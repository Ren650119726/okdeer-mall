
package com.okdeer.mall.order.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.google.common.collect.Maps;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
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
import com.okdeer.mall.activity.share.bo.ActivityShareOrderRecordParamBo;
import com.okdeer.mall.activity.share.bo.ActivityShareRecordNumParamBo;
import com.okdeer.mall.activity.share.dto.ActivityShareRecordParamDto;
import com.okdeer.mall.activity.share.entity.ActivityShareOrderRecord;
import com.okdeer.mall.activity.share.entity.ActivityShareRecord;
import com.okdeer.mall.activity.share.service.ActivityShareOrderRecordService;
import com.okdeer.mall.activity.share.service.ActivityShareRecordService;
import com.okdeer.mall.member.points.dto.RefundPointParamDto;
import com.okdeer.mall.order.bo.TradeOrderContext;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.service.TradeOrderChangeListener;
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
public class ActivityProcess implements TradeOrderRefundsListener, TradeOrderChangeListener {

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

	@Autowired
	private ActivityShareOrderRecordService activityShareOrderRecordService;

	@Autowired
	private ActivityShareRecordService activityShareRecordService;

	@Override
	public void beforeTradeOrderRefundsCrteate(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		// do nothing

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void afterTradeOrderRefundsCrteated(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		Assert.notNull(tradeOrderRefundContext.getTradeOrderRefunds(), "退款单信息不能为空");
		addRefundActivityShareOrderRecord(tradeOrderRefundContext);
	}

	@Override
	public void beforeTradeOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		// do nothing

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
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
			
			if(tradeOrder.getType() == OrderTypeEnum.PHONE_PAY_ORDER|| tradeOrder.getType() == OrderTypeEnum.TRAFFIC_PAY_ORDER){
				//如果是充值订单，则退换优惠卷
				// 退换优惠券
				returnVoncher(tradeOrderRefundContext);
			}
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void tradeOrderCreated(TradeOrderContext tradeOrderContext) throws MallApiException {
		Assert.notNull(tradeOrderContext.getTradeOrder(), "订单信息不能为空");
		// 添加订单分享记录
		addActivityShareOrderRecord(tradeOrderContext);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void tradeOrderChanged(TradeOrderContext tradeOrderContext) throws MallApiException {
		Assert.notNull(tradeOrderContext.getTradeOrder(), "订单信息不能为空");
		// 更新分享记录
		updateActivityShareOrderRecord(tradeOrderContext);
	}

	/**
	 * @Description: 添加活动下单
	 * @param tradeOrderContext
	 * @author zengjizu
	 * @date 2017年10月24日
	 */
	private void addActivityShareOrderRecord(TradeOrderContext tradeOrderContext) throws MallApiException {
		TradeOrder tradeOrder = tradeOrderContext.getTradeOrder();
		List<TradeOrderItem> tradeOrderItems = tradeOrderContext.getItemList();
		Assert.notEmpty(tradeOrderItems, "订单项信息不能为空");
		if (tradeOrder.getType() != OrderTypeEnum.GROUP_ORDER || StringUtils.isEmpty(tradeOrder.getShareUserId())) {
			return;
		}
		try {
			ActivityShareRecordParamDto activityShareRecordParamDto = new ActivityShareRecordParamDto();
			activityShareRecordParamDto.setActivityId(tradeOrder.getActivityId());
			activityShareRecordParamDto.setStoreSkuId(tradeOrderItems.get(0).getStoreSkuId());
			activityShareRecordParamDto.setSysUserId(tradeOrder.getShareUserId());
			activityShareRecordParamDto.setSort(false);
			List<ActivityShareRecord> activityShareRecordList = activityShareRecordService
					.findList(activityShareRecordParamDto);

			if (CollectionUtils.isEmpty(activityShareRecordList) || activityShareRecordList.size() > 1) {
				logger.error("用户分享记录有误，有多条同样的分享记录或者无分享记录，无法增加统计信息");
				return;
			}
			ActivityShareRecord activityShareRecord = activityShareRecordList.get(0);
			addActivityShareOrderRecord(tradeOrder.getId(), activityShareRecord.getId(), 0);
		} catch (Exception e) {
			throw new MallApiException(e);
		}
	}

	private void addActivityShareOrderRecord(String orderId, String shareId, int type) throws MallApiException {
		ActivityShareOrderRecord activityShareOrderRecord = new ActivityShareOrderRecord();
		activityShareOrderRecord.setCreateTime(new Date());
		activityShareOrderRecord.setId(UuidUtils.getUuid());
		activityShareOrderRecord.setOrderId(orderId);
		activityShareOrderRecord.setShareId(shareId);
		activityShareOrderRecord.setType(type);
		activityShareOrderRecord.setStatus(0);
		try {
			activityShareOrderRecordService.add(activityShareOrderRecord);
		} catch (Exception e) {
			logger.error("保存分享订单记录出错", e);
			throw new MallApiException(e);
		}
	}

	private void updateActivityShareOrderRecord(TradeOrderContext tradeOrderContext) throws MallApiException {
		TradeOrder tradeOrder = tradeOrderContext.getTradeOrder();
		if (!(tradeOrder.getStatus() == OrderStatusEnum.TO_BE_SIGNED
				|| tradeOrder.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED)
				|| tradeOrder.getType() != OrderTypeEnum.SERVICE_EXPRESS_ORDER) {
			return;
		}
		try {
			ActivityShareOrderRecordParamBo activityShareOrderRecordParam = new ActivityShareOrderRecordParamBo();
			activityShareOrderRecordParam.setOrderId(tradeOrder.getId());
			activityShareOrderRecordParam.setSort(false);
			List<ActivityShareOrderRecord> list = activityShareOrderRecordService
					.findList(activityShareOrderRecordParam);
			if (CollectionUtils.isEmpty(list) || list.size() > 1) {
				logger.debug("团购商品分享下单记录没有");
				return;
			}
			ActivityShareOrderRecord activityShareOrderRecord = list.get(0);

			ActivityShareRecord activityShareRecord = activityShareRecordService
					.findById(activityShareOrderRecord.getShareId());
			if (activityShareRecord == null) {
				logger.error("订单分享记录对应的分享记录不存在");
				return;
			}

			ActivityShareRecordNumParamBo activityShareRecordNumParamBo = new ActivityShareRecordNumParamBo();
			activityShareRecordNumParamBo.setId(activityShareRecord.getId());
			if (tradeOrder.getStatus() == OrderStatusEnum.TO_BE_SIGNED) {
				activityShareOrderRecord.setStatus(1);
				activityShareRecordNumParamBo.setDeliveryNum(1);
				activityShareRecordService.updateNum(activityShareRecordNumParamBo);
			} else if (tradeOrder.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED) {
				activityShareOrderRecord.setStatus(2);
				
				activityShareRecordNumParamBo.setCompleteNum(1);
				activityShareRecordService.updateNum(activityShareRecordNumParamBo);
			}
			int result = activityShareOrderRecordService.update(activityShareOrderRecord);
			if(result <1){
				throw new MallApiException("重复更新状态");
			}
		} catch (Exception e) {
			logger.error("修改分享记录订单信息出错", e);
			throw new MallApiException(e);
		}
	}

	private void addRefundActivityShareOrderRecord(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
		if (tradeOrderRefunds.getType() != OrderTypeEnum.SERVICE_EXPRESS_ORDER) {
			return;
		}
		ActivityShareOrderRecordParamBo activityShareOrderRecordParam = new ActivityShareOrderRecordParamBo();
		activityShareOrderRecordParam.setOrderId(tradeOrderRefunds.getOrderId());
		activityShareOrderRecordParam.setSort(false);
		List<ActivityShareOrderRecord> list = activityShareOrderRecordService.findList(activityShareOrderRecordParam);
		if (CollectionUtils.isEmpty(list) || list.size() > 1) {
			logger.debug("团购商品分享下单记录没有");
			return;
		}
		ActivityShareOrderRecord activityShareOrderRecord = list.get(0);
		addActivityShareOrderRecord(tradeOrderRefunds.getId(), activityShareOrderRecord.getShareId(), 1);
		ActivityShareRecordNumParamBo activityShareRecordNumParamBo = new ActivityShareRecordNumParamBo();
		activityShareRecordNumParamBo.setId(activityShareOrderRecord.getShareId());
		activityShareRecordNumParamBo.setRefundNum(1);
		activityShareRecordService.updateNum(activityShareRecordNumParamBo);
	}
}
