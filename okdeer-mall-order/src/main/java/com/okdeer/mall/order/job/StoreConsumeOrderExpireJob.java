
package com.okdeer.mall.order.job;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.service.StoreConsumeOrderService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.vo.ExpireStoreConsumerOrderVo;

/**
 * ClassName: StoreConsumeOrderExpireJob 
 * @Description: 到店消费订单过期处理
 * @author zengjizu
 * @date 2016年9月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class StoreConsumeOrderExpireJob extends AbstractSimpleElasticJob {

	/**
	 * 日志管理器
	 */
	private static final Logger logger = LoggerFactory.getLogger(StoreConsumeOrderExpireJob.class);

	@Autowired
	private StoreConsumeOrderService storeConsumeOrderService;

	@Autowired
	private TradeOrderService tradeOrderService;

	@Override
	public void process(JobExecutionMultipleShardingContext shardingContext) {

		// 获取过期的到店消费订单列表
		List<ExpireStoreConsumerOrderVo> expireList = storeConsumeOrderService.findExpireOrder();

		if (CollectionUtils.isNotEmpty(expireList)) {

			for (ExpireStoreConsumerOrderVo expireStoreConsumerOrderVo : expireList) {
				try {
					TradeOrder order = tradeOrderService.findOrderDetail(expireStoreConsumerOrderVo.getOrderId());
					storeConsumeOrderService.handleExpireOrder(order, expireStoreConsumerOrderVo.getDetailList());
				} catch (Exception e) {
					logger.error("到店消费订单自动过期出现异常，订单id{}", expireStoreConsumerOrderVo.getOrderId());
				}
			}

		}

	}

}
