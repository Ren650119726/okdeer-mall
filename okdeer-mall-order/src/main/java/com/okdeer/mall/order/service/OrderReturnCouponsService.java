package com.okdeer.mall.order.service;

/**
 * ClassName: OrderReturnCouponsService 
 * @Description: 订单返券service
 * @author wushp
 * @date 2016年10月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.1.0			2016-10-18		wushp				订单返券
 */
public interface OrderReturnCouponsService {
	
	/**
	 * 
	 * @Description: 邀请注册首单送券
	 * @param tradeNum 订单交易号
	 * @throws Exception 异常
	 * @author wushp
	 * @date 2016年10月18日
	 */
	void firstOrderReturnCoupons(String tradeNum) throws Exception;
}
