
package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.service.TradeOrderRefundProcessService;

@Service
public class TradeOrderRefundBuildFactory {

	@Resource(name = "defaultTradeOrderRefundProcessService")
	private TradeOrderRefundProcessService defaultTradeOrderRefundProcessService;

	@Resource(name = "storeConsumeOrderRefundProcessService")
	private TradeOrderRefundProcessService storeConsumeOrderRefundProcessService;

	public TradeOrderRefundProcessService getTradeOrderRefundProcessService(OrderTypeEnum orderTypeEnum) {
		if (orderTypeEnum == OrderTypeEnum.STORE_CONSUME_ORDER) {
			return storeConsumeOrderRefundProcessService;
		} else {
			return defaultTradeOrderRefundProcessService;
		}
	}

}
