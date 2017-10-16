
package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsLog;
import com.okdeer.mall.order.mapper.TradeOrderRefundsCertificateMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsLogMapper;
import com.okdeer.mall.order.service.TradeOrderRefundsListener;
import com.okdeer.mall.order.service.TradeOrderRefundsTraceService;

@Service
public class TradeOrderRefundRecordLogProcess implements TradeOrderRefundsListener {

	@Autowired
	private TradeOrderRefundsTraceService tradeOrderRefundsTraceService;

	@Resource
	private TradeOrderRefundsLogMapper tradeOrderRefundsLogMapper;

	@Autowired
	private TradeOrderRefundsCertificateMapper tradeOrderRefundsCertificateMapper;
	
	@Override
	public void beforeTradeOrderRefundsCrteate(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		// do nothing

	}

	@Override
	public void afterTradeOrderRefundsCrteated(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		processLog(tradeOrderRefundContext);
	}

	@Override
	public void beforeTradeOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		// do nothing

	}

	@Override
	public void afterOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		processLog(tradeOrderRefundContext);
	}

	private void processLog(TradeOrderRefundContextBo tradeOrderRefundContext) {
		Assert.notNull(tradeOrderRefundContext.getTradeOrderRefunds(), "退款单信息不能为空");
		tradeOrderRefundsTraceService.saveRefundTrace(tradeOrderRefundContext.getTradeOrderRefunds());
		tradeOrderRefundsLogMapper
				.insertSelective(createTradeOrderRefundsLog(tradeOrderRefundContext.getTradeOrderRefunds()));
		// 添加凭证信息
		addTradeOrderRefundsCertificate(tradeOrderRefundContext);
	}

	private void addTradeOrderRefundsCertificate(TradeOrderRefundContextBo tradeOrderRefundContext) {
		if (tradeOrderRefundContext.getTradeOrderRefundsCertificate() != null) {
			tradeOrderRefundsCertificateMapper
					.insertSelective(tradeOrderRefundContext.getTradeOrderRefundsCertificate());
		}
	}

	private TradeOrderRefundsLog createTradeOrderRefundsLog(TradeOrderRefunds orderRefunds) {
		return new TradeOrderRefundsLog(orderRefunds.getId(), orderRefunds.getOperator(),
				orderRefunds.getRefundsStatus().getName(), orderRefunds.getRefundsStatus().getValue());
	}
}
