
package com.okdeer.mall.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.annotation.RocketMQListener;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.constant.MessageConstant;
import com.okdeer.mall.member.points.dto.AddPointsParamDto;
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
	
	@RocketMQListener(topic = MessageConstant.POINT_TOPIC, tag = "*")
	public ConsumeConcurrentlyStatus addPoint(MQMessage enMessage) {

		AddPointsParamDto addPointsParamDto = (AddPointsParamDto) enMessage.getContent();
		logger.debug("订单完成后处理开始：{}", JsonMapper.nonEmptyMapper().toJson(addPointsParamDto));
		try {
			//添加积分
			pointsService.addPoints(addPointsParamDto);
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		} catch (Exception e) {
			logger.error("添加积分处理异常：{}", JsonMapper.nonEmptyMapper().toJson(addPointsParamDto), e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
	}
}
