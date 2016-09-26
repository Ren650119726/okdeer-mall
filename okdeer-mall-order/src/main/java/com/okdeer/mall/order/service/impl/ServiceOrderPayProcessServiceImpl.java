/** 
 *@Project: yschome-mall-order 
 *@Author: zengj
 *@Date: 2016年7月16日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.api.pay.enums.TradeErrorEnum;
import com.okdeer.api.pay.pay.dto.PayResponseDto;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.order.constant.ExceptionConstant;
import com.okdeer.mall.order.pay.entity.ResponseResult;
import com.okdeer.mall.order.service.ServiceOrderPayProcessService;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.timer.TradeOrderTimer;

/**
 * ClassName: ServiceOrderPayProcess 
 * @Description: 服务订单支付处理
 * @author zengj
 * @date 2016年7月16日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月16日                               zengj				新建类
 */

@Service
public class ServiceOrderPayProcessServiceImpl implements ServiceOrderPayProcessService {

	private static final Logger logger = LoggerFactory.getLogger(ServiceOrderPayProcessServiceImpl.class);

	/**
	 * 订单支付Service
	 */
	@Autowired
	private TradeOrderPayService tradeOrderPayService;

	/**
	 * 订单Service
	 */
	@Autowired
	private TradeOrderService tradeOrderService;
	
	/**
	 * 订单超时计时器
	 */
	@Autowired
	private TradeOrderTimer tradeOrderTimer;

	/**
	 * 
	 * @Description: 第三方支付回调
	 * @param tradeOrder 订单信息
	 * @param thirdPay   支付结果
	 * @author zengj
	 * @throws Exception 异常处理
	 * @date 2016年7月16日
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public void updateThirdPayResult(TradeOrder tradeOrder, PayResponseDto thirdPay) throws Exception {
		payResultProcess(tradeOrder, thirdPay, null);
	}

	/**
	 * 
	 * @Description: 余额支付回调
	 * @param tradeOrder 订单信息
	 * @param yuePay   支付结果
	 * @author zengj
	 * @throws Exception 异常处理
	 * @date 2016年7月16日
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public void updateYuePayResult(TradeOrder tradeOrder, ResponseResult yuePay) throws Exception {
		payResultProcess(tradeOrder, null, yuePay);
	}

	/**
	 * 
	 * @Description: 支付完成处理订单
	 * @param tradeOrder 订单对象
	 * @param thirdPay 第三方支付结果
	 * @param yuePay 余额支付结果
	 * @throws Exception   处理异常
	 * @author zengj
	 * @date 2016年7月16日
	 */
	private void payResultProcess(TradeOrder tradeOrder, PayResponseDto thirdPay, ResponseResult yuePay)
			throws Exception {
		// 查询订单是否产生过交易记录
		int count = tradeOrderPayService.selectTradeOrderPayByOrderId(tradeOrder.getId());
		// 说明该订单已经消费过该支付结果了，直接忽略
		if (count > 0) {
			return;
		}

		// 是否第三方支付,0为余额支付，1为第三方支付
		int payType = 0;
		if (thirdPay != null) {
			payType = 1;
		}

		// 交易流水号
		String tradeNum = null;
		// 交易金额
		BigDecimal tradeAmount = null;
		// 交易对账号
		String flowNo = null;
		// 订单支付方式
		PayTypeEnum payTypeEnum = PayTypeEnum.WALLET;

		// 余额支付
		if (payType == 0) {
			if (TradeErrorEnum.SUCCESS.getName().equals(yuePay.getCode())) {
				return;
			} else {
				logger.error(ExceptionConstant.ORDER_PAY_FAIL, tradeOrder.getOrderNo());
			}
			tradeNum = tradeOrder.getTradeNum();
			tradeAmount = tradeOrder.getActualAmount();
		} else {
			// 第三方支付
			tradeNum = thirdPay.getTradeNum();
			tradeAmount = thirdPay.getTradeAmount();
			flowNo = thirdPay.getFlowNo();

			// 订单支付方式，第三方回调
			int orderPayType = thirdPay.getPayType().ordinal();

			if (orderPayType == 1) {
				payTypeEnum = PayTypeEnum.ALIPAY;
			} else if (orderPayType == 2) {
				payTypeEnum = PayTypeEnum.WXPAY;
			} else if (orderPayType == 3) {
				payTypeEnum = PayTypeEnum.JDPAY;
			}
		}

		// 构建订单支付信息
		buildTradeOrderPay(tradeOrder, tradeAmount, payTypeEnum, tradeNum, flowNo);

		Date serviceTime = DateUtils.parseDate(tradeOrder.getPickUpTime(), "yyyy-MM-dd HH:mm");
		
		// 更新订单状态
		tradeOrderService.updateMyOrderStatus(tradeOrder);
		
		// 预约服务时间过后2小时未派单的自动取消订单
		tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_server_timeout, tradeOrder.getId(),
				(DateUtils.addHours(serviceTime, 2).getTime() - DateUtils.getSysDate().getTime()) / 1000);
	}

	/**
	 * 
	 * @Description: 构建订单支付信息
	 * @param tradeOrder 订单信息
	 * @param tradeAmount 交易金额
	 * @param tradeNum 交易号
	 * @param flowNo 对账流水号
	 * 
	 * @return TradeOrderPay 订单支付信息  
	 * @author zengj
	 * @date 2016年7月16日
	 */
	private void buildTradeOrderPay(TradeOrder tradeOrder, BigDecimal tradeAmount, PayTypeEnum payType, String tradeNum,
			String flowNo) {
		// 构建支付对象
		TradeOrderPay tradeOrderPay = new TradeOrderPay();
		tradeOrderPay.setId(UuidUtils.getUuid());
		tradeOrderPay.setCreateTime(new Date());
		tradeOrderPay.setPayTime(new Date());
		tradeOrderPay.setPayAmount(tradeAmount);
		tradeOrderPay.setOrderId(tradeOrder.getId());
		tradeOrderPay.setPayType(payType);

		tradeOrderPay.setReturns(flowNo);
		tradeOrder.setStatus(OrderStatusEnum.DROPSHIPPING);
		tradeOrder.setTradeNum(tradeNum);
		tradeOrder.setTradeOrderPay(tradeOrderPay);
	}
}
