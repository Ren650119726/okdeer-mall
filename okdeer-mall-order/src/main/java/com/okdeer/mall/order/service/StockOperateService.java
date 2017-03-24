package com.okdeer.mall.order.service;

import java.util.List;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderRefunds;

/**
 * ClassName: StockOperateService 
 * @Description: 库存操作接口
 * @author zengjizu
 * @date 2016年11月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface StockOperateService {
	
	/**
	 * @Description: 根据订单回收库存
	 * @param tradeOrder
	 * @param rpcIdList
	 * @return
	 * @author zengjizu
	 * @date 2016年11月11日
	 */
	void recycleStockByOrder(TradeOrder tradeOrder,List<String> rpcIdList) throws Exception ;
	
	/**
	 * @Description: 根据退款单回收库存
	 * @param tradeOrder 订单信息
	 * @param orderRefunds 退款单信息
	 * @param rpcIdList
	 * @return
	 * @throws Exception
	 * @author zengjizu
	 * @date 2016年11月15日
	 */
	void recycleStockByRefund(TradeOrder tradeOrder,TradeOrderRefunds orderRefunds, List<String> rpcIdList) throws Exception ;
}
