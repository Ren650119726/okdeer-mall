/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: RefundsPayStatusSubscriberServiceImpl.java 
 * @Date: 2016年3月23日 
 * 注意：本内容仅限于云上城公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.order.pay;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.base.Charsets;
import com.okdeer.mall.order.constant.mq.OrderMessageConstant;
import com.okdeer.mall.order.constant.mq.PayMessageConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.service.TradeOrderCompleteProcessService;
import com.okdeer.mall.order.service.TradeOrderPayServiceApi;
import com.okdeer.mall.order.service.TradeOrderServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.framework.mq.AbstractRocketMQSubscriber;

/**
 * pos支付支付结果消息订阅处理
 * 
 * @pr yschome-mall
 * @author zhangkn
 * @date 2016年3月23日 下午7:25:11
 */
@Service
public class PosPayResultStatusSubscriber extends AbstractRocketMQSubscriber
		implements PayMessageConstant, OrderMessageConstant {

	private static final Logger logger = LoggerFactory.getLogger(PosPayResultStatusSubscriber.class);

	@Resource
	private TradeOrderServiceApi tradeOrderService;

	@Resource
	private TradeOrderPayServiceApi tradeOrderPayService;

	// Begin 1.0.Z POS支付完成后，订单状态会变成已完成，需要同步商业系统 zengj
	/**
	 * 订单完成后同步商业系统
	 */
	@Resource
	private TradeOrderCompleteProcessService tradeOrderCompleteProcessService;
	// End 1.0.Z POS支付完成后，订单状态会变成已完成，需要同步商业系统 zengj

	@Override
	public String getTopic() {
		return "topic_pos_pay_result";
	}

	@Override
	public String getTags() {
		return "tag_pos_pay_result";
	}

	@Override
	public ConsumeConcurrentlyStatus subscribeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		MessageExt message = msgs.get(0);
		String msg = new String(message.getBody(), Charsets.UTF_8);
		logger.info("pos支付结果消息:" + msg);

		/**返回值格式
		msgMap.put("tradeNum", result.get("outTradeNo"));
		msgMap.put("flowNo", result.get("transactionId"));
		msgMap.put("payStatus", 0);
		msgMap.put("payType", PayTypeEnum.WXPAY.getName());
		msgMap.put("payWay", "1");
		*/
		try {
			Map<String, Object> map = (Map<String, Object>) com.alibaba.fastjson.JSON.parseObject(msg, Map.class);

			if (0 == Integer.valueOf(map.get("payStatus").toString())) {
				// 修改订单状态
				TradeOrder editOrder = tradeOrderService.getByTradeNum(map.get("tradeNum").toString());
				editOrder.setId(editOrder.getId());
				editOrder.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
				editOrder.setUpdateTime(new Date());
				//add by zhangkn 2016-12-12
				editOrder.setDisabled(Disabled.valid);
				//end by zhangkn 2016-12-12
				tradeOrderService.updateByPrimaryKeySelective(editOrder);

				// pay表插入数据
				TradeOrderPay oldOrderPay = tradeOrderPayService.selectByOrderId(editOrder.getId());
				if (oldOrderPay == null) {
					TradeOrderPay orderPay = new TradeOrderPay();
					orderPay.setId(UuidUtils.getUuid());
					orderPay.setCreateTime(new Date());
					orderPay.setOrderId(editOrder.getId());
					orderPay.setPayAmount(editOrder.getIncome());
					orderPay.setPayTime(new Date());

					String payWay = map.get("payWay").toString();
					// 0:云钱包,1:支付宝支付,2:微信支付,3:京东支付,4:现金支付,5:云上城垫付,6:网银支付,7:银行转账
					if (payWay.equals("1")) {
						orderPay.setPayType(PayTypeEnum.ALIPAY);
					} else if (payWay.equals("2")) {
						orderPay.setPayType(PayTypeEnum.WXPAY);
					}
					orderPay.setReturns(map.get("flowNo").toString());
					tradeOrderPayService.insertSelective(orderPay);

					tradeOrderCompleteProcessService.orderCompleteSyncToJxc(editOrder.getId());
				}

			}
		} catch (Exception e) {
			logger.error("pos支付结果同步消息处理失败", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
}