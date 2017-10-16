
package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.okdeer.mall.order.service.TradeOrderRefundProcessService;
import com.okdeer.mall.order.service.TradeOrderRefundsListener;

@Component
public class TradeOrderRefundsListenerBuilder implements InitializingBean {

	@Resource(name = "activityProcess")
	private TradeOrderRefundsListener activityProcess;

	@Resource(name = "defaultTradeOrderRefundsLister")
	private TradeOrderRefundsListener defaultTradeOrderRefundsLister;

	@Resource(name = "jxcSynTradeorderRefundProcessLister")
	private TradeOrderRefundsListener jxcSynTradeorderRefundProcessLister;

	@Resource(name = "rechargeOrderProcess")
	private TradeOrderRefundsListener rechargeOrderProcess;

	@Resource(name = "refundmentProcess")
	private TradeOrderRefundsListener refundmentProcess;

	@Resource(name = "sendTimerMessageProcess")
	private TradeOrderRefundsListener sendTimerMessageProcess;

	@Resource(name = "storeConsumerOrderProcess")
	private TradeOrderRefundsListener storeConsumerOrderProcess;

	@Resource(name = "tradeMessageServiceImpl")
	private TradeOrderRefundsListener tradeMessageServiceImpl;

	@Resource(name = "tradeOrderRefundRecordLogProcess")
	private TradeOrderRefundsListener tradeOrderRefundRecordLogProcess;

	@Resource(name = "defaultTradeOrderRefundProcessService")
	private TradeOrderRefundProcessService defaultTradeOrderRefundProcessService;

	@Resource(name = "storeConsumeOrderRefundProcessService")
	private TradeOrderRefundProcessService storeConsumeOrderRefundProcessService;

	@Override
	public void afterPropertiesSet() throws Exception {
		storeConsumeOrderRefundProcessService.addTradeOrderRefundsListener(storeConsumerOrderProcess);
		//充值订单处理
		defaultTradeOrderRefundProcessService.addTradeOrderRefundsListener(rechargeOrderProcess);
		addCommonTradeOrderRefundsListener(defaultTradeOrderRefundProcessService);
		addCommonTradeOrderRefundsListener(storeConsumeOrderRefundProcessService);
	}

	private void addCommonTradeOrderRefundsListener(TradeOrderRefundProcessService tradeOrderRefundProcessService) {
		//默认退款订单处理
		addTradeOrderRefundsListener(tradeOrderRefundProcessService, defaultTradeOrderRefundsLister);
		//活动处理
		addTradeOrderRefundsListener(tradeOrderRefundProcessService, activityProcess);
		// 发送定时消息
		addTradeOrderRefundsListener(tradeOrderRefundProcessService, sendTimerMessageProcess);
		// 记录日志信息
		addTradeOrderRefundsListener(tradeOrderRefundProcessService, tradeOrderRefundRecordLogProcess);
		// 同步进销存
		addTradeOrderRefundsListener(tradeOrderRefundProcessService, jxcSynTradeorderRefundProcessLister);
		// 通知短信监听
		addTradeOrderRefundsListener(tradeOrderRefundProcessService, tradeMessageServiceImpl);
		// 退款监听
		addTradeOrderRefundsListener(tradeOrderRefundProcessService, refundmentProcess);
	}

	private void addTradeOrderRefundsListener(TradeOrderRefundProcessService tradeOrderRefundProcessService,
			TradeOrderRefundsListener tradeOrderRefundsListener) {
		tradeOrderRefundProcessService.addTradeOrderRefundsListener(tradeOrderRefundsListener);
	}

}
