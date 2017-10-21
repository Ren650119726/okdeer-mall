
package com.okdeer.mall.order.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.order.bo.TradeOrderContext;
import com.okdeer.mall.order.service.TradeOrderChangeListener;
import com.okdeer.mall.order.service.TradeOrderChangeListeners;

/**
 * ClassName: DefaultTradeOrderChangeListeners 
 * @Description: 订单变更监听
 * @author zengjizu
 * @date 2017年10月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class DefaultTradeOrderChangeListeners implements TradeOrderChangeListeners {

	private List<TradeOrderChangeListener> listeners = Lists.newArrayList();

	@Override
	public void tradeOrderCreated(TradeOrderContext tradeOrderContext) throws MallApiException {
		for (TradeOrderChangeListener tradeOrderChangeListener : listeners) {
			tradeOrderChangeListener.tradeOrderCreated(tradeOrderContext);
		}
	}

	@Override
	public void tradeOrderChanged(TradeOrderContext tradeOrderContext) throws MallApiException {
		for (TradeOrderChangeListener tradeOrderChangeListener : listeners) {
			tradeOrderChangeListener.tradeOrderChanged(tradeOrderContext);
		}
	}

	@Override
	public void addTradeOrderChangeListener(TradeOrderChangeListener tradeOrderChangeListener) {
		if (listeners.contains(tradeOrderChangeListener)) {
			throw new RuntimeException("该监听器已经添加过了..");
		}
		listeners.add(tradeOrderChangeListener);
	}
}
