
package com.okdeer.mall.order.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.utils.RobotUserUtil;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.builder.TradeOrderRefundBuilder;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundResultDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.OrderComplete;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsMapper;
import com.okdeer.mall.order.service.TradeOrderRefundProcessCallback;
import com.okdeer.mall.order.service.TradeOrderRefundProcessService;
import com.okdeer.mall.order.service.TradeOrderRefundsListener;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;

public abstract class AbstractTradeOrderRefundProcessService implements TradeOrderRefundProcessService, DisposableBean {

	@Autowired
	protected TradeOrderRefundBuilder tradeOrderRefundBuilder;

	@Autowired
	private TradeOrderRefundsService tradeOrderRefundsService;

	@Autowired
	private TradeOrderRefundsMapper tradeOrderRefundsMapper;

	@Autowired
	private TradeOrderItemMapper tradeOrderItemMapper;

	@Autowired
	private RedisLockRegistry redisLockRegistry;

	private List<TradeOrderRefundsListener> tradeOrderRefundsListenerList = Lists.newArrayList();

	public boolean checkApplyRefund(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			Response<TradeOrderApplyRefundResultDto> response, TradeOrderRefundContextBo tradeOrderRefundContext) {
		TradeOrder tradeOrder = tradeOrderApplyRefundParamDto.getTradeOrder();
		Assert.notNull(tradeOrder);
		TradeOrderItem tradeOrderItem = tradeOrderApplyRefundParamDto.getTradeOrderItem();
		if (tradeOrderItem == null) {
			tradeOrderItem = tradeOrderItemMapper.selectOrderItemById(tradeOrderApplyRefundParamDto.getOrderItemId());
		}

		if (tradeOrderItem == null) {
			// 订单项不存在
			response.setResult(ResultCodeEnum.ILLEGAL_PARAM);
			return false;
		}
		List<TradeOrderItem> tradeOrderItemList = Lists.newArrayList();
		tradeOrderItemList.add(tradeOrderItem);
		tradeOrderRefundContext.setTradeOrderItemList(tradeOrderItemList);
		if (!RobotUserUtil.getRobotUser().getId().equals(tradeOrderApplyRefundParamDto.getUserId())
				&& !tradeOrder.getUserId().equals(tradeOrderApplyRefundParamDto.getUserId())) {
			response.setResult(ResultCodeEnum.ILLEGAL_PARAM);
			return false;
		}
		if (!tradeOrder.getId().equals(tradeOrderItem.getOrderId())) {
			response.setResult(ResultCodeEnum.ILLEGAL_PARAM);
			return false;
		}

		// 订单已过售后期
		if (tradeOrder.getIsComplete() == OrderComplete.YES) {
			response.setResult(ResultCodeEnum.ORDER_REFUND_EXPIRE);
			return false;
		}

		// 校验订单状态
		if (tradeOrder.getStatus() != OrderStatusEnum.HAS_BEEN_SIGNED) {
			response.setResult(ResultCodeEnum.ORDER_NOT_FINSH);
			return false;
		}
		
		if (tradeOrderItem.getServiceAssurance() == null || tradeOrderItem.getServiceAssurance() == 0) {
			response.setResult(ResultCodeEnum.ORDER_NOT_SUPPORT_REFUND);
			return false;
		}
		if ((tradeOrder.getType() == OrderTypeEnum.PHYSICAL_ORDER || tradeOrder.getType() == OrderTypeEnum.SERVICE_EXPRESS_ORDER) && tradeOrderRefundsService.isRefundOrderItemId(tradeOrderItem.getId())) {
			response.setResult(ResultCodeEnum.ORDER_HAS_BEAN_REFUND);
			return false;
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void createRefundInfo(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		// 创建退款单前的处理
		TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundBuilder
				.createTradeOrderRefund(tradeOrderApplyRefundParamDto, tradeOrderRefundContext);
		List<TradeOrderRefundsItem> refundsItems = tradeOrderRefundBuilder
				.createTradeOrderRefundsItem(tradeOrderApplyRefundParamDto, tradeOrderRefundContext);
		tradeOrderRefunds.setTradeOrderRefundsItem(refundsItems);
		TradeOrderRefundsCertificateVo tradeOrderRefundsCertificateVo = tradeOrderRefundBuilder
				.createCertificate(tradeOrderApplyRefundParamDto, tradeOrderRefundContext);
		beforeCreateTradeOrderRefundsProcess(tradeOrderApplyRefundParamDto, tradeOrderRefundContext);
		try {
			// 插入退款订单信息
			tradeOrderRefundsService.insertRefunds(tradeOrderRefunds, tradeOrderRefundsCertificateVo);
		} catch (Exception e) {
			throw new MallApiException(e);
		}
		// 退款单创建后处理
		atterCreateTradeOrderRefundsProcess(tradeOrderRefundContext);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateTradeOrderRefund(TradeOrderRefundContextBo tradeOrderRefundContext, RefundsStatusEnum checkStatus,
			TradeOrderRefundProcessCallback tradeOrderRefundProcessCallback) throws MallApiException {
		Assert.notNull(tradeOrderRefundContext.getTradeOrderRefunds(), "退款单不能为空");
		Lock lock = redisLockRegistry.obtain(tradeOrderRefundContext.getTradeOrderRefunds().getId());
		try {
			boolean result = lock.tryLock(10, TimeUnit.SECONDS);
			if (result) {
				checkStatus(tradeOrderRefundContext.getTradeOrderRefunds().getRefundsStatus(), checkStatus);
				// 更新退单前的操作
				beforeTradeOrderRefundsChangedProcess(tradeOrderRefundContext);
				// 回调处理退款单信息
				tradeOrderRefundProcessCallback.doProcess(tradeOrderRefundContext);
				int count = tradeOrderRefundsMapper
						.updateByPrimaryKeySelective(tradeOrderRefundContext.getTradeOrderRefunds());
				if (count < 1) {
					throw new MallApiException("更新退款单信息失败");
				}
				TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundsMapper
						.selectByPrimaryKey(tradeOrderRefundContext.getTradeOrderRefunds().getId());
				tradeOrderRefundContext.setTradeOrderRefunds(tradeOrderRefunds);
				// 更新退单后的操作
				afterTradeOrderRefundsChangedProcess(tradeOrderRefundContext);
			} else {
				throw new MallApiException("处理超时");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new MallApiException("线程处理出错");
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void addTradeOrderRefundsListener(TradeOrderRefundsListener lister) {
		tradeOrderRefundsListenerList.add(lister);
	}

	@Override
	public void setTradeOrderRefundsListener(List<TradeOrderRefundsListener> listerList) {
		tradeOrderRefundsListenerList.clear();
		tradeOrderRefundsListenerList.addAll(listerList);
	}

	@Override
	public void destroy() throws Exception {
		tradeOrderRefundsListenerList.clear();
		tradeOrderRefundsListenerList = null;
	}

	protected void checkStatus(RefundsStatusEnum currentRefundsStatus, RefundsStatusEnum checkStatus)
			throws MallApiException {
		String exceptionMsg = "当前状态:" + currentRefundsStatus + "不符合修改的条件,需求修改成的状态为:" + checkStatus;
		switch (checkStatus) {
			case APPLY_CUSTOMER_SERVICE_INTERVENE:
				// 申请客服介入
				if (!(currentRefundsStatus == RefundsStatusEnum.SELLER_REJECT_REFUND
						|| currentRefundsStatus == RefundsStatusEnum.SELLER_REJECT_APPLY)) {
					throw new MallApiException(exceptionMsg);
				}
				break;
			case BUYER_REPEAL_REFUND:
				if (!(currentRefundsStatus == RefundsStatusEnum.WAIT_SELLER_VERIFY
						|| currentRefundsStatus == RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS
						|| currentRefundsStatus == RefundsStatusEnum.WAIT_SELLER_REFUND
						|| currentRefundsStatus == RefundsStatusEnum.SELLER_REJECT_APPLY
						|| currentRefundsStatus == RefundsStatusEnum.SELLER_REJECT_REFUND
						|| currentRefundsStatus == RefundsStatusEnum.APPLY_CUSTOMER_SERVICE_INTERVENE)) {
					throw new MallApiException(exceptionMsg);
				}
				break;
			default:
				RefundsStatusEnum conditionStatus = getConditionStatus(checkStatus);
				if (conditionStatus == null) {
					throw new MallApiException("未知的状态校验:" + checkStatus);
				}
				if (currentRefundsStatus != conditionStatus) {
					throw new MallApiException(exceptionMsg);
				}
				break;
		}

	}

	private void atterCreateTradeOrderRefundsProcess(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		for (TradeOrderRefundsListener tradeOrderRefundsLister : tradeOrderRefundsListenerList) {
			tradeOrderRefundsLister.afterTradeOrderRefundsCrteated(tradeOrderRefundContext);
		}
	}

	private void beforeCreateTradeOrderRefundsProcess(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		for (TradeOrderRefundsListener tradeOrderRefundsLister : tradeOrderRefundsListenerList) {
			tradeOrderRefundsLister.beforeTradeOrderRefundsCrteate(tradeOrderApplyRefundParamDto,
					tradeOrderRefundContext);
		}
	}

	private void beforeTradeOrderRefundsChangedProcess(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		for (TradeOrderRefundsListener tradeOrderRefundsLister : tradeOrderRefundsListenerList) {
			tradeOrderRefundsLister.beforeTradeOrderRefundsChanged(tradeOrderRefundContext);
		}
	}

	private void afterTradeOrderRefundsChangedProcess(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		for (TradeOrderRefundsListener tradeOrderRefundsLister : tradeOrderRefundsListenerList) {
			tradeOrderRefundsLister.afterOrderRefundsChanged(tradeOrderRefundContext);
		}
	}

	protected RefundsStatusEnum getConditionStatus(RefundsStatusEnum updateStaus) {
		switch (updateStaus) {
			case SELLER_REFUNDING:
				return RefundsStatusEnum.WAIT_SELLER_REFUND;
			case FORCE_SELLER_REFUND_SUCCESS:
				return RefundsStatusEnum.FORCE_SELLER_REFUND;
			case YSC_REFUND_SUCCESS:
				return RefundsStatusEnum.YSC_REFUND;
			case FORCE_SELLER_REFUND:
				return RefundsStatusEnum.APPLY_CUSTOMER_SERVICE_INTERVENE;
			case YSC_REFUND:
				return RefundsStatusEnum.APPLY_CUSTOMER_SERVICE_INTERVENE;
			case CUSTOMER_SERVICE_CANCEL_INTERVENE:
				return RefundsStatusEnum.APPLY_CUSTOMER_SERVICE_INTERVENE;
			case SELLER_REJECT_APPLY:
				return RefundsStatusEnum.WAIT_SELLER_VERIFY;
			case REFUND_SUCCESS:
				return RefundsStatusEnum.SELLER_REFUNDING;
			case SELLER_REJECT_REFUND:
				return RefundsStatusEnum.WAIT_SELLER_REFUND;
			case WAIT_SELLER_REFUND:
				return RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS;
			case WAIT_BUYER_RETURN_GOODS:
				return RefundsStatusEnum.WAIT_SELLER_VERIFY;
			default:
				return null;
		}
	}
}
