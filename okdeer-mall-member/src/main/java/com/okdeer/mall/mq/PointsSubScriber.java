
package com.okdeer.mall.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.annotation.RocketMQListener;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.common.consts.PointConstants;
import com.okdeer.mall.member.points.dto.AddPointsParamDto;
import com.okdeer.mall.member.points.dto.ConsumPointParamDto;
import com.okdeer.mall.member.points.dto.RefundPointParamDto;
import com.okdeer.mall.points.service.PointsService;

/**
 * ClassName: PointsSubScriber 
 * @Description: 积分消息订阅
 * @author zengjizu
 * @date 2016年12月31日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class PointsSubScriber {

	private static final Logger logger = LoggerFactory.getLogger(PointsSubScriber.class);

	@Autowired
	private PointsService pointsService;
	
	@RocketMQListener(topic = PointConstants.POINT_TOPIC, tag = "*")
	public ConsumeConcurrentlyStatus addPoint(MQMessage enMessage) {

		AddPointsParamDto addPointsParamDto = (AddPointsParamDto) enMessage.getContent();
		logger.debug("添加积分请求参数：{}", JsonMapper.nonEmptyMapper().toJson(addPointsParamDto));
		try {
			//添加积分
			pointsService.addPoints(addPointsParamDto);
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		} catch (Exception e) {
			logger.error("添加积分处理异常：{}", JsonMapper.nonEmptyMapper().toJson(addPointsParamDto), e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
	}
	
	/**
	 * @Description: 消费积分
	 * @param enMessage 消费积分消息
	 * @return
	 * @author zengjizu
	 * @date 2017年1月6日
	 */
	@RocketMQListener(topic = PointConstants.CONSUM_POINT_TOPIC, tag = "*")
	public ConsumeConcurrentlyStatus consumPoint(MQMessage enMessage) {

		ConsumPointParamDto consumPointParamDto = (ConsumPointParamDto) enMessage.getContent();
		logger.debug("消费积分请求参数：{}", JsonMapper.nonEmptyMapper().toJson(consumPointParamDto));
		try {
			//消费积分积分
			pointsService.consumPoint(consumPointParamDto);
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		} catch (Exception e) {
			logger.error("消费积分处理异常：{}", JsonMapper.nonEmptyMapper().toJson(consumPointParamDto), e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
	}
	
	@RocketMQListener(topic = PointConstants.REFUND_POINT_TOPIC, tag = "*")
	public ConsumeConcurrentlyStatus refundPoint(MQMessage enMessage) {

		RefundPointParamDto refundPointParamDto = (RefundPointParamDto) enMessage.getContent();
		logger.debug("消费积分请求参数：{}", JsonMapper.nonEmptyMapper().toJson(refundPointParamDto));
		try {
			//消费积分积分
			pointsService.refundPoint(refundPointParamDto);
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		} catch (Exception e) {
			logger.error("消费积分处理异常：{}", JsonMapper.nonEmptyMapper().toJson(refundPointParamDto), e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
	}
}
