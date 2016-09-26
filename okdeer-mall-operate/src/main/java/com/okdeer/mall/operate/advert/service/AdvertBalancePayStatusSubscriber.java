/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: AdvertPayStatusSubscriber.java 
 * @Date: 2016年3月30日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 
package com.okdeer.mall.operate.advert.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.base.Charsets;
import com.okdeer.mall.advert.entity.ColumnAdvert;
import com.okdeer.mall.advert.enums.AdvertIsPayEnum;
import com.okdeer.api.pay.enums.TradeErrorEnum;
import com.okdeer.base.framework.mq.AbstractRocketMQSubscriber;
import com.okdeer.mall.operate.advert.constant.AdvertMessageConstant;

import net.sf.json.JSONObject;

/**	
 * 广告缴费 状态同步
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年3月30日 下午3:50:50
 */
@Service
public class AdvertBalancePayStatusSubscriber extends AbstractRocketMQSubscriber  implements AdvertMessageConstant{
	private static final Logger logger = LoggerFactory.getLogger(AdvertBalancePayStatusSubscriber.class);
	/**
	 * 广告service
	 */
	@Resource
	private ColumnAdvertService columnAdvertService;
	
	@Override
	public String getTopic() {
		return TOPIC_ADVERT_BALANCE_PAY;
	}

	@Override
	public String getTags() {
		return "tag_ad_balance";
	}

	@Override
	public ConsumeConcurrentlyStatus subscribeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		try {
			String msg = new String(msgs.get(0).getBody(), Charsets.UTF_8);
			logger.info("广告支付支付状态消息:" + msg);
			JSONObject json = JSONObject.fromObject(msg);
			
			ColumnAdvert advert = this.columnAdvertService.getAdvertByTradeNum(json.getString("tradeNum"));
			if (json.getString("code").equals(TradeErrorEnum.SUCCESS.toString())) {
				//更改广告缴费状态
				advert.setIsPay(AdvertIsPayEnum.HAS_PAID);
				advert.setUpdateTime(new Date());
			}
			int num = this.columnAdvertService.updateAdvertInfo(advert);
			if (num == 1) {
				return  ConsumeConcurrentlyStatus.CONSUME_SUCCESS;	
			} else {
				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}
		} catch (Exception e) {
			logger.error("广告缴费状态消息处理失败", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
	}
	
	
	public static void main(String[] args) {
		String msg = "{\"id\":\"你好\",\"name\":\"zhaoqc\",\"age\":18}";
		JSONObject json = JSONObject.fromObject(msg);
		System.out.println(json.getString("id"));
		System.out.println(json.getString("name"));
		System.out.println(json.getInt("age"));
	}
}
