/**   
* @Title: AlipayStatusSubscriber.java 
* @Package com.okdeer.mall.trade.order.pay 
* @Description: TODO(用一句话描述该文件做什么) 
* @author A18ccms A18ccms_gmail_com   
* @date 2016年3月30日 下午7:39:54 
* @version V1.0   
*/
package com.okdeer.mall.activity.coupons.listen;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.base.Charsets;

import com.okdeer.base.framework.mq.AbstractRocketMQSubscriber;
import com.okdeer.mall.activity.coupons.service.MessageConstant;

/** 
* @ClassName: AlipayStatusSubscriber 
* @Description: 代理商添加代金券活动写入消息
* @author yangq
* @date 2016年3月30日 下午7:39:54 
*  
*/
public class ActivityCollectCouponsAgentSubscriber extends AbstractRocketMQSubscriber {

	private static final Logger logger = LoggerFactory.getLogger(ActivityCollectCouponsAgentSubscriber.class);

//	@Autowired
//	public TradeOrderRefundsService tradeOrderRefundsService;
	
	@Override
	public String getTopic() {
		return "topic_pay_trade_result";
	}

	@Override
	public String getTags() {
		return MessageConstant.TAG_ACTIVITY_COLLECT_COUPONS_AGENT_ADD;
	}

	@Override
	public ConsumeConcurrentlyStatus subscribeMessage (List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

			try {
					String msg = new String(msgs.get(0).getBody(), Charsets.UTF_8);
					logger.info("监听代理商保存活动后mq回调的状态消息:" + msg);
			} catch (Exception e) {
				logger.error("订单支付状态消息处理失败", e);
				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
}	
