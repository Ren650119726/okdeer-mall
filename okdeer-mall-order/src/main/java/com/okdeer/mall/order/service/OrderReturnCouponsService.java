package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrder;

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
	 * @param tradeOrder 订单
	 * @throws Exception 异常
	 * @author wushp
	 * @date 2016年10月18日
	 */
	void firstOrderReturnCoupons(TradeOrder tradeOrder) throws Exception;
}
