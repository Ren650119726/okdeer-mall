package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.service.TradeOrderInvoiceServiceApi;
import com.okdeer.mall.order.mapper.TradeOrderInvoiceMapper;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderInvoiceServiceApi")
class TradeOrderInvoiceServiceImpl implements TradeOrderInvoiceServiceApi {

	@Resource
	private TradeOrderInvoiceMapper tradeOrderInvoiceMapper;

	/**
	 * 
	 * @desc 根据订单查询发票信息
	 *
	 * @param orderId 订单ID
	 * @return
	 */
	public TradeOrderInvoice selectByOrderId(String orderId) {
		return tradeOrderInvoiceMapper.selectByOrderId(orderId);
	}
}