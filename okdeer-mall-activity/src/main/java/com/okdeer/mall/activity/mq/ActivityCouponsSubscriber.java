package com.okdeer.mall.activity.mq;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.okdeer.base.framework.mq.annotation.RocketMQListener;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.activity.coupons.bo.ActivityCouponsBo;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.mq.constants.ActivityCouponsTopic;

public class ActivityCouponsSubscriber {
	
	private static final Logger log = LoggerFactory.getLogger(ActivityCouponsSubscriber.class);

	@Resource
	private ActivityCouponsMapper activityCouponsMapper;
	
	@RocketMQListener(topic = ActivityCouponsTopic.TOPIC_COUPONS_COUNT, tag = "*")
	public ConsumeConcurrentlyStatus trigger(MQMessage enMessage) {
		ActivityCouponsBo couponsBo = (ActivityCouponsBo)enMessage.getContent();
		try {
			activityCouponsMapper.updateCouponsNum(couponsBo);
		} catch (Exception e) {
			log.error("修改代金券使用或剩余数量时失败，{}",e);
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
}
