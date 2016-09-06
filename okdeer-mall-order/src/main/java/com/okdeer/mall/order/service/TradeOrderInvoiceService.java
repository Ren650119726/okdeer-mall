package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrderInvoice;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderInvoiceService {

	/**
	 * 
	 * @desc 根据订单查询发票信息
	 *
	 * @param orderId 订单ID
	 * @return
	 */
	public TradeOrderInvoice selectByOrderId(String orderId);
}