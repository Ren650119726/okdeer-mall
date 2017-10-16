package com.okdeer.mall.order.service;

import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;

/**
 * ClassName: TradeOrderRefundProcessCallback 
 * @Description: 退款处理回调
 * @author zengjizu
 * @date 2017年10月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface TradeOrderRefundProcessCallback {
	
	void doProcess(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException;
	
}
