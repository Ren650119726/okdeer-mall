
package com.okdeer.mall.order.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.google.common.collect.Maps;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.common.consts.PointConstants;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRecordService;
import com.okdeer.mall.member.points.dto.RefundPointParamDto;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.service.TradeOrderRefundsListener;

/**
 * ClassName: ActivityProcess 
 * @Description: 活动处理
 * @author zengjizu
 * @date 2017年10月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service("activityProcess")
public class ActivityProcess implements TradeOrderRefundsListener {

	private static final Logger logger = LoggerFactory.getLogger(ActivityProcess.class);

	/**
	 * 特惠活动记录信息mapper
	 */
	@Autowired
	private ActivitySaleRecordService activitySaleRecordService;

	@Autowired
	private RocketMQProducer rocketMQProducer;

	@Autowired
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;

	@Autowired
	private ActivityCouponsMapper activityCouponsMapper;

	@Override
	public void beforeTradeOrderRefundsCrteate(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		// do nothing

	}

	@Override
	public void afterTradeOrderRefundsCrteated(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		// do nothing

	}

	@Override
	public void beforeTradeOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		// do nothing

	}

	@Override
	public void afterOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		// do nothing
		TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
		TradeOrder tradeOrder = tradeOrderRefundContext.getTradeOrder();
		Assert.notNull(tradeOrderRefunds);
		Assert.notNull(tradeOrder);
		if (tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.YSC_REFUND
				|| tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.FORCE_SELLER_REFUND
				|| tradeOrderRefunds.getRefundsStatus() == RefundsStatusEnum.SELLER_REFUNDING) {

			// 特惠活动释放限购数量
			returnActivitySale(tradeOrderRefundContext);
			// 积分退还
			try {
				reduceUserPoint(tradeOrder, tradeOrderRefunds);
			} catch (Exception e) {
				logger.error("发送扣减积分信息出错", e);
				throw new MallApiException(e);
			}
			// 退换优惠券
			returnVoncher(tradeOrderRefundContext);
		}
	}
	
	private void returnActivitySale(TradeOrderRefundContextBo tradeOrderRefundContext) {
		TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
		for (TradeOrderRefundsItem refundsItem : tradeOrderRefundContext.getTradeOrderRefundsItemList()) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("orderId", tradeOrderRefunds.getOrderId());
			params.put("storeSkuId", refundsItem.getStoreSkuId());
			activitySaleRecordService.updateDisabledByOrderId(params);
		}
	}

	private void returnVoncher(TradeOrderRefundContextBo tradeOrderRefundContext) {
		TradeOrder tradeOrder = tradeOrderRefundContext.getTradeOrder();
		// 如果有优惠，则返还优惠信息
		if (tradeOrder.getActivityType() == ActivityTypeEnum.VONCHER) {
			Map<String, Object> params = Maps.newHashMap();
			String orderId = tradeOrder.getId();
			params.put("orderId", orderId);
			List<ActivityCouponsRecord> records = activityCouponsRecordMapper.selectByParams(params);
			if (records != null && records.size() == 1) {
				if (records.get(0).getValidTime().compareTo(DateUtils.getSysDate()) > 0) {
					activityCouponsRecordMapper.updateUseStatus(orderId);
					activityCouponsMapper.updateReduceUseNum(records.get(0).getCouponsId());
				} else {
					activityCouponsRecordMapper.updateUseStatusAndExpire(orderId);
				}
			}
		}
	}

	/**
	 * @Description: 扣减用户积分
	 * @param order 订单信息
	 * @param refunds 退款信息
	 * @author zengjizu
	 * @throws Exception 
	 * @date 2017年1月7日
	 */
	private void reduceUserPoint(TradeOrder order, TradeOrderRefunds refunds) throws Exception {
		// 线上支付需要扣减积分与成长值
		RefundPointParamDto refundPointParamDto = new RefundPointParamDto();
		refundPointParamDto.setAmount(refunds.getTotalAmount());
		refundPointParamDto.setBusinessId(refunds.getId());
		refundPointParamDto.setDescription("商品退款");
		refundPointParamDto.setUserId(order.getUserId());
		MQMessage<RefundPointParamDto> anMessage = new MQMessage<>(PointConstants.TOPIC_POINT_REFUND,
				refundPointParamDto);
		SendResult sendResult = rocketMQProducer.sendMessage(anMessage);
		if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
			logger.info("扣减积分消息发送成功，发送数据为：{},topic:{}", JsonMapper.nonDefaultMapper().toJson(refundPointParamDto),
					PointConstants.TOPIC_POINT_REFUND);
		} else {
			logger.info("扣减积分消息发送失败，topic：{}", PointConstants.TOPIC_POINT_REFUND);
		}
	}

}
