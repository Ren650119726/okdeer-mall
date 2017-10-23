
package com.okdeer.mall.order.service;


/**
 * ClassName: TradeOrderChangeListeners 
 * @Description: 订单变更监听
 * @author zengjizu
 * @date 2017年10月20日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface TradeOrderChangeListeners extends TradeOrderChangeListener{

	/**
	 * @Description:添加监听
	 * @param tradeOrderChangeListener
	 * @author zengjizu
	 * @date 2017年10月20日
	 */
	void addTradeOrderChangeListener(TradeOrderChangeListener tradeOrderChangeListener);

}
