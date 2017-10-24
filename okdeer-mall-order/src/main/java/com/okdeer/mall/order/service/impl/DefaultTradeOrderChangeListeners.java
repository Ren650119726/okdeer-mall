
package com.okdeer.mall.order.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
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
public class DefaultTradeOrderChangeListeners implements TradeOrderChangeListeners, InitializingBean, DisposableBean {

	@Resource(name = "activityProcess")
	private TradeOrderChangeListener activityProcess;

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

	@Override
	public void afterPropertiesSet() throws Exception {
		listeners.add(activityProcess);
	}

	@Override
	public void destroy() throws Exception {
		listeners.clear();
		listeners = null;
	}
}
