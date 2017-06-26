package com.okdeer.mall.order.service;

import com.okdeer.mall.order.bo.TradeOrderContext;

/**
 * 
 * ClassName: TradeorderProcessLister 
 * @Description:订单退款处理监听
 * @author zhangkn
 * @date 2017年6月5日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.3			 2017年6月5日 			zhagnkn
 */
public interface TradeorderRefundProcessLister {

	
	/**
	 * @Description: 改变订单状态
	 * @param tradeOrderContext
	 * @author zhangkn
	 * @date 2017年6月5日
	 */
	void tradeOrderStatusChange(TradeOrderContext tradeOrderContext) throws Exception;
	
}
