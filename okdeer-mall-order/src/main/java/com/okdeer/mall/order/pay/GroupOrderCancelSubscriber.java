package com.okdeer.mall.order.pay;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.okdeer.base.framework.mq.annotation.RocketMQListener;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.order.dto.CancelOrderDto;
import com.okdeer.mall.order.dto.CancelOrderParamDto;
import com.okdeer.mall.order.enums.OrderCancelType;
import com.okdeer.mall.order.pay.constant.GroupOrderTopicConst;
import com.okdeer.mall.order.service.CancelOrderApi;

/**
 * ClassName: GroupOrderCancelSubscriber 
 * @Description: 团购订单取消订阅。处理成团失败或者团购活动关闭相关团购订单取消的处理
 * @author maojj
 * @date 2017年11月3日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年11月3日				maojj
 */
@Service
public class GroupOrderCancelSubscriber {
	
	private static final Logger LOG = LoggerFactory.getLogger(GroupOrderCancelSubscriber.class);
	
	@Resource
	private CancelOrderApi cancelOrderApi;
	
	@RocketMQListener(topic = GroupOrderTopicConst.TOPIC_GROUP_ORDER_CANCEL, tag = "*")
	public ConsumeConcurrentlyStatus trigger(MQMessage<String> enMessage) throws Exception {
		try {
			String orderId = enMessage.getContent();
			CancelOrderParamDto cancelParamDto = new CancelOrderParamDto();
			cancelParamDto.setOrderId(orderId);
			cancelParamDto.setReason("成团失败");
			cancelParamDto.setCancelType(OrderCancelType.CANCEL_BY_SYSTEM);
			CancelOrderDto cancelResult = cancelOrderApi.cancelOrder(cancelParamDto);
			if(cancelResult.getStatus() == 0){
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}else{
				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}
		} catch (Exception e) {
			LOG.error("团购订单取消发生异常：",e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
	}
}
