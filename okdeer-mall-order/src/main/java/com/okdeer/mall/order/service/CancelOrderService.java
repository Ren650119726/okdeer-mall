package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrder;

/**
 * ClassName: CancelOrderService 
 * @Description: 取消订单service接口
 * @author zengjizu
 * @date 2016年11月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface CancelOrderService {
	
	/**
	 * @Description: 取消订单
	 * @param order 订单
	 * @author zengjizu
	 * @date 2016年11月10日
	 */
	boolean cancelOrder(TradeOrder order,boolean isBuyerOperate)  throws Exception;
	
	/**
	 * @Description: 用户拒收订单
	 * @param order 订单信息
	 * @author zengjizu
	 * @date 2016年11月16日
	 */
	void updateWithUserRefuse(TradeOrder order) throws Exception;
	
	/**
	 * @Description: 判断是否收取违约金
	 * @param orderId 订单id
	 * @return true：收取违约金 false：不收取违约金
	 * @author zengjizu
	 * @date 2016年11月18日
	 */
	boolean isBreach(String orderId) throws Exception;
}
