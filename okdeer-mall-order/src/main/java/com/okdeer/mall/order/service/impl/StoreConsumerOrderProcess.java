
package com.okdeer.mall.order.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.entity.TradeOrderRefundsItemDetail;
import com.okdeer.mall.order.enums.CompainStatusEnum;
import com.okdeer.mall.order.enums.ConsumeStatusEnum;
import com.okdeer.mall.order.enums.ConsumerCodeStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemDetailMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsItemDetailMapper;
import com.okdeer.mall.order.service.TradeOrderRefundsListener;

@Service("storeConsumerOrderProcess")
public class StoreConsumerOrderProcess implements TradeOrderRefundsListener {

	@Autowired
	private TradeOrderRefundsItemDetailMapper tradeOrderRefundsItemDetailMapper;

	@Autowired
	private TradeOrderItemDetailMapper tradeOrderItemDetailMapper;
	
	
	@Autowired
	private TradeOrderMapper tradeOrderMapper;

	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoServiceApi;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void beforeTradeOrderRefundsCrteate(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		Assert.notNull(tradeOrderRefundContext.getTradeOrder());
		if (tradeOrderRefundContext.getTradeOrder().getType() != OrderTypeEnum.STORE_CONSUME_ORDER) {
			return;
		}
		if (tradeOrderRefundContext.getTradeOrderRefunds() != null) {
			TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
			tradeOrderRefunds.setRefundsStatus(RefundsStatusEnum.SELLER_REFUNDING);
			// tradeOrderRefunds.setRefundsReason("消费码未消费退款");
			// tradeOrderRefunds.setMemo("消费码未消费退款");
		}
		try {
			String bossSysUserId = storeInfoServiceApi
					.getBossIdByStoreId(tradeOrderRefundContext.getTradeOrder().getStoreId());
			tradeOrderRefundContext.setSotreUserId(bossSysUserId);
		} catch (ServiceException e) {
			throw new MallApiException("查询店老板用户id出错", e);
		}
	}

	private void updateTradeItemDetailStatus(TradeOrderRefundContextBo tradeOrderRefundContext) {
		for (TradeOrderRefundsItem tradeOrderRefundsItem : tradeOrderRefundContext.getTradeOrderRefundsItemList()) {
			for (TradeOrderItemDetail tradeOrderItemDetail : tradeOrderRefundContext.getTradeOrderItemDetail()) {
				if (!tradeOrderItemDetail.getOrderItemId().equals(tradeOrderRefundsItem.getOrderItemId())) {
					break;
				}
				tradeOrderItemDetail.setStatus(ConsumeStatusEnum.refund);
				tradeOrderItemDetail.setUpdateTime(new Date());
				tradeOrderItemDetailMapper.updateByPrimaryKeySelective(tradeOrderItemDetail);
			}
		}
	}

	private void addTradeOrderRefundsItemDetail(TradeOrderRefundContextBo tradeOrderRefundContext) {
		if (CollectionUtils.isNotEmpty(tradeOrderRefundContext.getTradeOrderRefundsItemList())
				&& CollectionUtils.isNotEmpty(tradeOrderRefundContext.getTradeOrderItemDetail())) {
			for (TradeOrderRefundsItem tradeOrderRefundsItem : tradeOrderRefundContext.getTradeOrderRefundsItemList()) {

				for (TradeOrderItemDetail tradeOrderItemDetail : tradeOrderRefundContext.getTradeOrderItemDetail()) {
					if (!tradeOrderItemDetail.getOrderItemId().equals(tradeOrderRefundsItem.getOrderItemId())) {
						break;
					}
					TradeOrderRefundsItemDetail tradeOrderRefundsItemDetail = new TradeOrderRefundsItemDetail();
					tradeOrderRefundsItemDetail.setId(UuidUtils.getUuid());
					tradeOrderRefundsItemDetail.setRefundItemId(tradeOrderRefundsItem.getId());
					tradeOrderRefundsItemDetail.setOrderItemDetailId(tradeOrderItemDetail.getId());
					tradeOrderRefundsItemDetailMapper.add(tradeOrderRefundsItemDetail);
				}
			}
		}
	}
	
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void afterTradeOrderRefundsCrteated(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		// 添加退款单明细
		addTradeOrderRefundsItemDetail(tradeOrderRefundContext);
		// 更新退款单明细状态
		updateTradeItemDetailStatus(tradeOrderRefundContext);
		// 更新consumer_code_status
		updateTradeOrder(tradeOrderRefundContext);
	}

	private void updateTradeOrder(TradeOrderRefundContextBo tradeOrderRefundContext) {
		TradeOrder tradeOrder = tradeOrderRefundContext.getTradeOrder();
		TradeOrder updateTradeOrder = new TradeOrder();
		updateTradeOrder.setId(tradeOrder.getId());
		updateTradeOrder.setConsumerCodeStatus(calculationConsumerCodeStatus(tradeOrderRefundContext));
		tradeOrderMapper.updateByPrimaryKeySelective(updateTradeOrder);
	}

	@Override
	public void beforeTradeOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		// do nothing
	}

	@Override
	public void afterOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		// do nothing
	}
	
	
	
	private ConsumerCodeStatusEnum calculationConsumerCodeStatus(TradeOrderRefundContextBo tradeOrderRefundContext) {
		// 更新订单状态
		List<TradeOrderItemDetail> detailList = tradeOrderItemDetailMapper
				.selectByOrderItemDetailByOrderId(tradeOrderRefundContext.getTradeOrder().getId());
		// 是否有过期的消费码
		boolean isHasExpired = false;
		// 是否有未消费的
		boolean isHasWaitConsume = false;
		// 是否有已经消费的
		boolean isHasConsumed = false;
		if (CollectionUtils.isNotEmpty(detailList)) {
			
			List<String> refundDetailIds = Lists.newArrayList();
			List<TradeOrderItemDetail> tradeOrderItemDetailList =  tradeOrderRefundContext.getTradeOrderItemDetail();
			for (TradeOrderItemDetail tradeOrderItemDetail : tradeOrderItemDetailList) {
				refundDetailIds.add(tradeOrderItemDetail.getId());
			}
			
			for (TradeOrderItemDetail tradeOrderItemDetail : detailList) {
				if (refundDetailIds.contains(tradeOrderItemDetail.getId())) {
					// 如果是这次退款的消费码直接继续判断
					continue;
				}
				if (tradeOrderItemDetail.getStatus() == ConsumeStatusEnum.expired) {
					isHasExpired = true;
				}
				if (tradeOrderItemDetail.getStatus() == ConsumeStatusEnum.noConsume) {
					isHasWaitConsume = true;
				}
				if (tradeOrderItemDetail.getStatus() == ConsumeStatusEnum.consumed) {
					isHasConsumed = true;
				}
			}
		}

		if (isHasExpired) {
			// 如果已经有过期的就将消费码状态改为已过期
			return ConsumerCodeStatusEnum.EXPIRED;
		}

		if (isHasWaitConsume) {
			// 有待消费的就改为待消费
			return ConsumerCodeStatusEnum.WAIT_CONSUME;
		}
		if (isHasConsumed) {
			// 变成已经消费
			return ConsumerCodeStatusEnum.WAIT_EVALUATE;
		} else {
			// 全部退款
			return ConsumerCodeStatusEnum.REFUNDED;
		}
	}
}
