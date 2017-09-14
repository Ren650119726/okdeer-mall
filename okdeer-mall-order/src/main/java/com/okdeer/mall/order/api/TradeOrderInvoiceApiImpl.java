package com.okdeer.mall.order.api;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.service.TradeOrderInvoiceService;
import com.okdeer.mall.order.service.TradeOrderInvoiceServiceApi;


@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderInvoiceServiceApi")
public class TradeOrderInvoiceApiImpl implements TradeOrderInvoiceServiceApi {

	
	@Autowired
	private TradeOrderInvoiceService tradeOrderInvoiceService;
	
	
	@Override
	public TradeOrderInvoice selectByOrderId(String orderId) {
		return tradeOrderInvoiceService.selectByOrderId(orderId);
	}

}
