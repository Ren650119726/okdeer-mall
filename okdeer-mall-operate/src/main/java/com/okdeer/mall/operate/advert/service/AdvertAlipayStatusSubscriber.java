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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.base.Charsets;
import com.okdeer.mall.advert.entity.AdvertResponseResult;
import com.okdeer.mall.advert.entity.ColumnAdvert;
import com.okdeer.mall.advert.enums.AdvertIsPayEnum;
import com.yschome.api.pay.pay.dto.PayResponseDto;
import com.yschome.base.framework.mq.AbstractRocketMQSubscriber;
import com.yschome.base.framework.mq.RocketMQProducer;
import com.okdeer.mall.operate.advert.constant.AdvertMessageConstant;
import com.okdeer.mall.system.utils.mapper.JsonMapper;

/**
 * 广告缴费 状态同步
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年3月30日 下午3:50:50
 */
@Service
public class AdvertAlipayStatusSubscriber extends AbstractRocketMQSubscriber  implements AdvertMessageConstant{
	private static final Logger logger = LoggerFactory.getLogger(AdvertAlipayStatusSubscriber.class);
	/**
	 * 广告service
	 */
	@Resource
	private ColumnAdvertService columnAdvertService;
	
	@Override
	public String getTopic() {
		return TOPIC_ADVERT_ALIPAY_RESULT;
	}

	@Override
	public String getTags() {
		return "tag_ad";
	}

	@Override
	public ConsumeConcurrentlyStatus subscribeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		try {
			String msg = new String(msgs.get(0).getBody(), Charsets.UTF_8);
			logger.info("广告支付支付状态消息:" + msg);
			PayResponseDto result = JsonMapper.nonEmptyMapper().fromJson(msg, PayResponseDto.class);
			ColumnAdvert advert = this.columnAdvertService.getAdvertByTradeNum(result.getTradeNum());
			if (result.getPayStatus().equals(AdvertResponseResult.SUCCESS_CODE)) {
				//更改广告缴费状态
				advert.setIsPay(AdvertIsPayEnum.HAS_PAID);
				advert.setUpdateTime(new Date());
			}
			int num = this.columnAdvertService.updateAdvertInfo(advert);
			
			//添加交易记录
/*			JSONObject data = new JSONObject();
			data.put("tag_pay_trade_result", "tag_ad");
			JSONArray list = new JSONArray();
			JSONObject bean = new JSONObject();
			bean.put("amount", result.getTradeAmount().multiply(new BigDecimal(-1)));
			bean.put("amountUpdateType", AmountUpdateType.UPDATE_AVAILABLE);
	        bean.put("payType", PayTypeEnum.WALLET);
	        bean.put("serviceFkId", advert.getId());
	        bean.put("serviceType", PayServiceTypeEnum.AD);
	        bean.put("title", "广告费支出");
	        bean.put("tradeNum", result.getTradeNum());
	        bean.put("userId", advert.getCreateUserId());
	        list.add(bean);*/
			
	      //----友门鹿记录
/*	        bean = new JSONObject();
	        bean.put("amount", result.getTradeAmount().multiply(new BigDecimal(-1)));
	        bean.put("amountUpdateType", AmountUpdateType.UPDATE_AVAILABLE);
	        bean.put("payType", PayTypeEnum.WALLET);
	        bean.put("serviceFkId", advert.getId());
	        bean.put("serviceType", PayServiceTypeEnum.AD);
	        bean.put("title", "广告费收入");
	        bean.put("tradeNum", result.getTradeNum());
	        bean.put("userId","1");
	        list.add(bean);
	        data.put("list", list);
	        logger.info("余额支付：");
	        logger.info(data.toString());*/
	        //Message message = new Message("topic_pay_trade", "tag_ad", data.toString().getBytes(Charsets.UTF_8));
			//SendResult sendResult = rocketMQProducer.send(message);
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
	
}
