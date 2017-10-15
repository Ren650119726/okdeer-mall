
package com.okdeer.mall.order.service;

import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;

public interface TradeOrderRefundsListener {

	/**
	 * @Description: 退款单创建前
	 * @param tradeOrderApplyRefundParamDto
	 * @param tradeOrderRefundContext
	 * @author zengjizu
	 * @date 2017年10月13日
	 */
	void beforeTradeOrderRefundsCrteate(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException;

	/**
	 * @Description: 退款单创建后
	 * @param tradeOrderRefundContext
	 * @author zengjizu
	 * @date 2017年10月13日
	 */
	void afterTradeOrderRefundsCrteated(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException;

	/**
	 * @Description: 退款单发状态变化前
	 * @param tradeOrderRefundContext
	 * @author zengjizu
	 * @date 2017年10月13日
	 */
	void beforeTradeOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException;

	/**
	 * @Description: 退款单发状态变化后
	 * @param tradeOrderRefundContext
	 * @author zengjizu
	 * @date 2017年10月13日
	 */
	void afterOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException;

}
