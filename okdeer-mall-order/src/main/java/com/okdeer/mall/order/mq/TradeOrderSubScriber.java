package com.okdeer.mall.order.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.annotation.RocketMQListener;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.mq.constants.TradeOrderTopic;


/**
 * ClassName: 订单消息接收处理新类 
 * @Description: TODO
 * @author tuzhd
 * @date 2016年12月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.2.3			2016-12-12			tuzhd			 完成订单消息处理 
 */
@Service
public class TradeOrderSubScriber {
	private static final Logger logger = LoggerFactory.getLogger(TradeOrderSubScriber.class);
	
	@Autowired
	ActivityCouponsRecordService activityCouponsRecordService;
	/**
	 * @Description: 接收完成订单消息，处理相关业务
	 * @param enMessage   
	 * @throws
	 * @author tuzhd
	 * @date 2016年12月12日
	 */
	@RocketMQListener(topic = TradeOrderTopic.ORDER_COMPLETE_TOCPIC, tag = "*")
	public void trigger(MQMessage enMessage) {

		TradeOrder tradeOrder = (TradeOrder) enMessage.getContent();
		logger.debug("订单完成后处理开始：{}", JsonMapper.nonEmptyMapper().toJson(tradeOrder));
		try {
			//处理订单完成后的业务功能之一  邀新活动 被邀用户下单完成后给 邀请人送代金劵及抽奖次数
			activityInviteHandler(tradeOrder);

		} catch (Exception e) {
			logger.error("订单完成后处理业务异常：{}",JsonMapper.nonEmptyMapper().toJson(tradeOrder), e);
		}
	}
	
	/**
	 * @Description: 邀新活动 被邀用户下单完成后给 邀请人送代金劵及抽奖次数   
	 * @param tradeOrder  订单信息
	 * @throws
	 * @author tuzhd
	 * @date 2016年12月12日
	 */
	private void activityInviteHandler(TradeOrder tradeOrder) throws Exception{
		//修改订单状态为完成时 进行业务处理
		if(tradeOrder.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED){
			//邀请人获得的代金劵奖励id, 每层id中逗号隔开
			String[] collectCouponsId ={"8a8080a358e6869c0159017a97a50021","8a8080a358e6869c0159017926ed001d,8a8080a358e6869c015901773ff0001a,8a8080a358e6869c0159017ad5cf0022",
					"8a8080a358e6869c0159017926ed001d,8a8080a358e6869c015901773ff0001a,8a8080a358e6869c0159017bbf5c0024","8a8080a358e6869c0159017a97a50021"};
			activityCouponsRecordService.addInviteUserHandler(tradeOrder.getUserId(),collectCouponsId);
		}
	}
	
}
