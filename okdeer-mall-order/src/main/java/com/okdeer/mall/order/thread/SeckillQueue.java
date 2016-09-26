package com.okdeer.mall.order.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.esotericsoftware.minlog.Log;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.common.consts.RedisKeyConstants;
import com.okdeer.mall.common.vo.OrderQueue;
import com.okdeer.mall.order.handler.RequestHandlerChain;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;

/**
 * ClassName: SeckillOrderConsumer 
 * @Description: 秒杀订单队列
 * @author maojj
 * @date 2016年9月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构1.1			2016年9月23日				maojj			秒杀订单队列
 */
@Service
public class SeckillQueue{

	private static final Logger LOG = LoggerFactory.getLogger(SeckillQueue.class);

	@Value("${seckill_queue_size}")
	private int queueMaxSize;

	@Value("${seckill_pool_size}")
	private int seckillPoolSize;
	
	@Value("${seckill_wait_time}")
	private int seckillWaitTime;
	
	@Resource
	private RedisTemplate<String,Integer> redisTemplate;

	private static ExecutorService executor = null;

	private static BlockingQueue<OrderQueue<ServiceOrderReq, ServiceOrderResp>> orderQueues = null;
	
	@Resource
	RequestHandlerChain<ServiceOrderReq, ServiceOrderResp> submitSeckillOrderChain;

	public void push(OrderQueue<ServiceOrderReq, ServiceOrderResp> orderQueue) {
		try {
			synchronized (orderQueue) {
				boolean isEnqueue = orderQueues.offer(orderQueue);
				if (isEnqueue) {
					String skuId = orderQueue.getReq().getData().getSkuId();
					redisTemplate.boundValueOps(RedisKeyConstants.SECKILL_QUEUE + skuId).increment(1L);
					orderQueue.wait(seckillWaitTime);
					if (!orderQueue.getReq().isComplete()) {
						orderQueue.getResp().setResult(ResultCodeEnum.PROCESS_TIME_OUT);
					}
				} else {
					orderQueue.getResp().setResult(ResultCodeEnum.SECKILL_QUEUE_IS_FULL);
				}
			}
		} catch (InterruptedException e) {
			LOG.error("PUSH到秒杀队列中发生异常{}", e.getMessage());
		}
	}

	@PostConstruct
	public void init() {
		executor = Executors.newFixedThreadPool(queueMaxSize);
		orderQueues = new ArrayBlockingQueue<OrderQueue<ServiceOrderReq, ServiceOrderResp>>(
				seckillPoolSize, true);
		for (int i = 0; i < seckillPoolSize; i++) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					OrderQueue<ServiceOrderReq, ServiceOrderResp> orderQueue = null;
					while (true) {
						try {
							orderQueue = orderQueues.take();
						} catch (InterruptedException e) {
							Log.error("获取消费队列时发生异常");
							continue;
						}
						synchronized (orderQueue) {
							try {
								String skuId = orderQueue.getReq().getData().getSkuId();
								redisTemplate.boundValueOps(RedisKeyConstants.SECKILL_QUEUE + skuId).increment(-1L);
								submitSeckillOrderChain.process(orderQueue.getReq(), orderQueue.getResp());
								orderQueue.notifyAll();

							} catch (Exception e) {
								LOG.error("消费订单队列时发生异常:{}", e);
								if (orderQueue != null) {
									orderQueue.getResp().setErrMsg("消费订单队列时发生异常" + e.getMessage());
									orderQueue.notify();
								}
								LOG.error("消费订单队列时发生异常");
							}
						}
					}
				}
			});
		}
	}
}
