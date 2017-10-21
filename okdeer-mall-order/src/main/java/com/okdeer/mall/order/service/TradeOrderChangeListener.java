
package com.okdeer.mall.order.service;

import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.order.bo.TradeOrderContext;

/**
 * ClassName: TradeOrderChangeListener 
 * @Description: 订单监听
 * @author zengjizu
 * @date 2017年10月20日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface TradeOrderChangeListener {

	/**
	 * @Description: 订单创建
	 * @param tradeOrderContext
	 * @author zengjizu
	 * @date 2017年10月20日
	 */
	void tradeOrderCreated(TradeOrderContext tradeOrderContext) throws MallApiException;

	/**
	 * @Description: 订单变更后
	 * @param tradeOrderContext
	 * @author zengjizu
	 * @date 2017年10月20日
	 */
	void tradeOrderChanged(TradeOrderContext tradeOrderContext) throws MallApiException;
}
