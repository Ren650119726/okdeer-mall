
package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.google.common.base.Charsets;
import com.okdeer.api.pay.enums.BusinessTypeEnum;
import com.okdeer.api.pay.enums.RefundTypeEnum;
import com.okdeer.api.pay.pay.dto.PayRefundDto;
import com.okdeer.api.pay.tradeLog.dto.BalancePayTradeDto;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.order.bo.PayTradeExt;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.constant.mq.PayMessageConstant;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.service.TradeOrderRefundsListener;

@Service("refundmentProcess")
public class RefundmentProcess implements TradeOrderRefundsListener {

	private static Logger logger = LoggerFactory.getLogger(RefundmentProcess.class);

	private static final String REFUND_REMARK = "关联订单号【%s】";

	@Autowired
	private RocketMQProducer rocketMQProducer;

	@Override
	public void beforeTradeOrderRefundsCrteate(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		// do nothing
	}

	@Override
	public void afterTradeOrderRefundsCrteated(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		// do nothing
		Assert.notNull(tradeOrderRefundContext.getTradeOrder());
		if (tradeOrderRefundContext.getTradeOrder().getType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
			// 到店消费订单、充值订单创建退款单后立马退钱給用户
			sellerRefund(tradeOrderRefundContext);
		} else if (tradeOrderRefundContext.getTradeOrder().getType() == OrderTypeEnum.PHONE_PAY_ORDER
				|| tradeOrderRefundContext.getTradeOrder().getType() == OrderTypeEnum.TRAFFIC_PAY_ORDER) {
			if (isOldWayBack(tradeOrderRefundContext.getTradeOrderRefunds().getPaymentMethod())) {
				// 退还用户金额
				this.refundUserAmount(tradeOrderRefundContext);
			}
		}
	}

	@Override
	public void beforeTradeOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		// do nothing
	}

	@Override
	public void afterOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		Assert.notNull(tradeOrderRefundContext.getTradeOrder(), "订单信息不能为空");
		Assert.notNull(tradeOrderRefundContext.getTradeOrderRefunds(), "退款单信息不能为空");

		if (PayWayEnum.PAY_ONLINE != tradeOrderRefundContext.getTradeOrder().getPayWay()) {
			// 非线上支付
			return;
		}

		switch (tradeOrderRefundContext.getTradeOrderRefunds().getRefundsStatus()) {
			case YSC_REFUND:
				// 运营商退款
				this.updateSellerWallet(tradeOrderRefundContext);
				break;
			case FORCE_SELLER_REFUND:
				// 强制买家退款
			case SELLER_REFUNDING:
				// 买家退款中
				sellerRefund(tradeOrderRefundContext);
				break;
			case CUSTOMER_SERVICE_CANCEL_INTERVENE:
			case BUYER_REPEAL_REFUND:
				// 取消客服借介入，解冻商家冻结金额
				unfreezeSellerAmount(tradeOrderRefundContext);
				break;
			default:
				break;
		}
	}

	/**
	 * @Description: 商家退款
	 * @param tradeOrderRefundContext
	 * @throws MallApiException
	 * @author zengjizu
	 * @date 2017年10月15日
	 */
	private void sellerRefund(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
		if (isOldWayBack(tradeOrderRefunds.getPaymentMethod())) {
			this.updateSellerWallet(tradeOrderRefundContext);
			// 退还用户金额
			this.refundUserAmount(tradeOrderRefundContext);
		} else {
			// 余额退款
			this.updateSellerWallet(tradeOrderRefundContext);
		}
	}

	private void refundUserAmount(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		TradeOrder order = tradeOrderRefundContext.getTradeOrder();
		TradeOrderRefunds orderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
		PayRefundDto payRefundDto = new PayRefundDto();
		payRefundDto.setTradeAmount(orderRefunds.getTotalAmount());
		payRefundDto.setServiceId(orderRefunds.getId());
		payRefundDto.setServiceNo(orderRefunds.getOrderNo());
		payRefundDto.setRemark(String.format(REFUND_REMARK, orderRefunds.getOrderNo()));
		payRefundDto.setRefundType(convert(orderRefunds.getType(), orderRefunds.getRefundsStatus()));
		payRefundDto.setTradeNum(order.getTradeNum());
		payRefundDto.setRefundNum(orderRefunds.getRefundNo());
		MQMessage<PayRefundDto> msg = new MQMessage<>(PayMessageConstant.TOPIC_REFUND, payRefundDto);
		msg.setKey(orderRefunds.getId());
		try {
			rocketMQProducer.sendMessage(msg);
		} catch (Exception e) {
			logger.error("发送MQ消息失败", e);
			throw new MallApiException(e);
		}
	}

	private void updateSellerWallet(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		String sendJson = buildBalancePayTrade(tradeOrderRefundContext);
		sendMqMsg(PayMessageConstant.TOPIC_BALANCE_PAY_TRADE, PayMessageConstant.TAG_PAY_TRADE_MALL, sendJson);
	}

	/**
	 * @Description: 解冻商家金额
	 * @param tradeOrderRefundContext
	 * @throws MallApiException
	 * @author zengjizu
	 * @date 2017年10月15日
	 */
	private void unfreezeSellerAmount(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		try {
			String sendMsg = buildBalanceFinish(tradeOrderRefundContext);
			sendMqMsg(PayMessageConstant.TOPIC_BALANCE_PAY_TRADE, PayMessageConstant.TAG_PAY_TRADE_MALL, sendMsg);
		} catch (Exception e) {
			throw new MallApiException(e);
		}
	}

	private void sendMqMsg(String topic, String tag, String content) throws MallApiException {
		try {
			Message msg = new Message(topic, tag, content.getBytes(Charsets.UTF_8));
			SendResult sendResult = rocketMQProducer.send(msg);
			if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
				throw new MallApiException("发送消息到云钱包失败，错误原因：" + sendResult.getSendStatus());
			}
		} catch (Exception e) {
			logger.error("发送MQ消息失败", e);
			throw new MallApiException(e);
		}
	}

	/**
	 * 构建支付对象
	 */
	private String buildBalancePayTrade(TradeOrderRefundContextBo tradeOrderRefundContext) {
		TradeOrder order = tradeOrderRefundContext.getTradeOrder();
		TradeOrderRefunds orderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
		Assert.hasText(tradeOrderRefundContext.getSotreUserId());
		BalancePayTradeDto payTradeVo = new BalancePayTradeDto();
		payTradeVo.setAmount(orderRefunds.getTotalAmount());
		payTradeVo.setIncomeUserId(orderRefunds.getUserId());
		payTradeVo.setPayUserId(tradeOrderRefundContext.getSotreUserId());
		payTradeVo.setBatchNo(TradeNumUtil.getTradeNum());
		payTradeVo.setTitle("订单退款(余额支付)，退款交易号：" + orderRefunds.getRefundNo());
		payTradeVo.setTradeNum(order.getTradeNum());
		// 退款单号
		payTradeVo.setRefundNo(orderRefunds.getRefundNo());
		if (isOldWayBack(orderRefunds.getPaymentMethod())) {
			// 原路退回
			payTradeVo.setBusinessType(BusinessTypeEnum.AGREEN_REFUND);
		} else {
			if (orderRefunds.getRefundsStatus() == RefundsStatusEnum.YSC_REFUND) {
				payTradeVo.setBusinessType(BusinessTypeEnum.YSC_REFUND);
			} else {
				payTradeVo.setBusinessType(BusinessTypeEnum.REFUND_ORDER);
			}
		}
		payTradeVo.setServiceFkId(orderRefunds.getId());
		payTradeVo.setServiceNo(orderRefunds.getOrderNo());
		payTradeVo.setRemark(String.format(REFUND_REMARK, orderRefunds.getOrderNo()));
		// 优惠额退款
		// 判断是否有平台优惠
		BigDecimal platformFavour = orderRefunds.getTotalPreferentialPrice()
				.subtract(orderRefunds.getStorePreferential());

		if (platformFavour.compareTo(BigDecimal.valueOf(0.00)) > 0) {
			// 如果平台优惠>0.则标识有平台优惠
			payTradeVo.setPrefeAmount(platformFavour);
			payTradeVo.setActivitier("1");
		}
		if (orderRefunds.getRefundsStatus() == RefundsStatusEnum.YSC_REFUND) {
			// 平台退款，需要扣除佣金
			PayTradeExt payTradeExt = buildCommissionInfo(order, orderRefunds);
			payTradeVo.setExt(JsonMapper.nonDefaultMapper().toJson(payTradeExt));
		}
		// 接受返回消息的tag
		if (!isOldWayBack(orderRefunds.getPaymentMethod())) {
			// 原路返回
			payTradeVo.setTag(PayMessageConstant.TAG_PAY_RESULT_REFUND);
		}
		return JsonMapper.nonEmptyMapper().toJson(payTradeVo);
	}

	private PayTradeExt buildCommissionInfo(TradeOrder order, TradeOrderRefunds orderRefunds) {
		BigDecimal platformFavour = orderRefunds.getTotalPreferentialPrice()
				.subtract(orderRefunds.getStorePreferential());
		PayTradeExt payTradeExt = new PayTradeExt();
		payTradeExt.setCommissionRate(order.getCommisionRatio());
		BigDecimal totalCommision = orderRefunds.getTotalAmount().add(platformFavour)
				.multiply(order.getCommisionRatio()).setScale(2, BigDecimal.ROUND_HALF_UP);
		if (order.getCommisionRatio().compareTo(BigDecimal.ZERO) > 0
				&& orderRefunds.getTotalAmount().add(platformFavour).compareTo(BigDecimal.ZERO) > 0
				&& totalCommision.compareTo(BigDecimal.ZERO) == 0) {
			// 如果佣金比例>0,且需要收佣金额>0，当收佣金额*佣金比例四舍五入之后结果为0，则将需要收取的佣金金额设置为0.01元
			totalCommision = BigDecimal.valueOf(0.01);
		}
		payTradeExt.setCommission(totalCommision);
		return payTradeExt;
	}

	/**
	 * 是否资金原来返回
	 */
	private boolean isOldWayBack(PayTypeEnum payType) {
		return PayTypeEnum.ALIPAY == payType || PayTypeEnum.WXPAY == payType;
	}

	private RefundTypeEnum convert(OrderTypeEnum orderType, RefundsStatusEnum refundsStatus) {
		RefundTypeEnum refundType = null;
		if (refundsStatus == RefundsStatusEnum.YSC_REFUND) {
			return RefundTypeEnum.YSC_REFUND;
		}
		switch (orderType) {
			case PHYSICAL_ORDER:
			case SERVICE_ORDER:
			case SERVICE_STORE_ORDER:
			case STORE_CONSUME_ORDER:
				refundType = RefundTypeEnum.REFUND_ORDER;
				break;
			case PHONE_PAY_ORDER:
			case TRAFFIC_PAY_ORDER:
				refundType = RefundTypeEnum.RECHARGE_ORDER_REFUND;
				break;
			default:
				refundType = RefundTypeEnum.REFUND_ORDER;
				break;
		}
		return refundType;
	}

	private String buildBalanceFinish(TradeOrderRefundContextBo tradeOrderRefundContext) {
		TradeOrder order = tradeOrderRefundContext.getTradeOrder();
		TradeOrderRefunds orderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
		Assert.notNull(order);
		Assert.notNull(orderRefunds);
		Assert.hasText(tradeOrderRefundContext.getSotreUserId(), "店铺老板用户id不能为空");
		// 是否店铺优惠
		BigDecimal totalAmount = orderRefunds.getTotalAmount();
		// 平台优惠金额
		BigDecimal preferentialPrice = orderRefunds.getTotalPreferentialPrice()
				.subtract(orderRefunds.getStorePreferential());
		PayTradeExt payTradeExt = buildCommissionInfo(order, orderRefunds);
		BalancePayTradeDto payTradeVo = new BalancePayTradeDto();
		payTradeVo.setAmount(totalAmount);
		payTradeVo.setIncomeUserId(tradeOrderRefundContext.getSotreUserId());
		payTradeVo.setTradeNum(order.getTradeNum());
		payTradeVo.setBatchNo(TradeNumUtil.getTradeNum());
		payTradeVo.setTitle("解冻余额，交易号：" + orderRefunds.getTradeNum());
		payTradeVo.setBusinessType(BusinessTypeEnum.COMPLETE_ORDER);
		payTradeVo.setServiceFkId(order.getId());
		payTradeVo.setServiceNo(order.getOrderNo());
		// 优惠金额
		if (preferentialPrice != null && preferentialPrice.compareTo(BigDecimal.ZERO) > 0) {
			// 优化活动发起人，比如代理商id或者运营商id
			payTradeVo.setActivitier("1");
			payTradeVo.setPrefeAmount(preferentialPrice);
		}
		// 接受返回消息的tag
		payTradeVo.setTag(null);
		payTradeVo.setExt(JsonMapper.nonDefaultMapper().toJson(payTradeExt));
		return JsonMapper.nonEmptyMapper().toJson(payTradeVo);
	}
}
