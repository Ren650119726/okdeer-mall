package com.okdeer.mall.order.handler;

import static org.junit.Assert.*;
import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.mall.Application;
import com.okdeer.mall.order.service.impl.ServOrderAddServiceImpl;
import com.okdeer.mall.order.thread.SeckillQueue;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class RequestHandlerChainTest {
	

	@Resource
	private RequestHandlerChain<ServiceOrderReq, ServiceOrderResp> confirmSeckillOrderChain;
	
	@Resource
	private RequestHandlerChain<ServiceOrderReq, ServiceOrderResp> submitSeckillOrderChain;
	
	@Resource RedisTemplate<String, Object> redisTemplate;
	
	@Resource
	private SeckillQueue seckillQueue;
	
	@Test
	public void test() {
		assertNotNull(confirmSeckillOrderChain);
		assertNotNull(submitSeckillOrderChain);
		assertNotEquals(confirmSeckillOrderChain, submitSeckillOrderChain);
		assertNotNull(seckillQueue);
	}

	@Test
	public void testDeleteKey(){
		redisTemplate.delete("");
	}
}
