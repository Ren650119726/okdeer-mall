
package com.okdeer.mall.order.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.builder.TradeOrderRefundBuilder;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.entity.TradeOrderDispute;
import com.okdeer.mall.order.entity.TradeOrderDisputeLog;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.enums.DisputeStatusEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.service.TradeOrderDisputeLogService;
import com.okdeer.mall.order.service.TradeOrderDisputeService;
import com.okdeer.mall.order.service.TradeOrderRefundsListener;

/**
 * ClassName: DefaultTradeOrderRefundsLister 
 * @Description: 默认退款监听处理
 * @author zengjizu
 * @date 2017年10月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

@Service("defaultTradeOrderRefundsLister")
public class DefaultTradeOrderRefundsLister implements TradeOrderRefundsListener {

	@Autowired
	private TradeOrderDisputeService tradeOrderDisputeService;
	
	@Autowired
	private TradeOrderDisputeLogService tradeOrderDisputeLogService;
	
	@Autowired
	private TradeOrderRefundBuilder tradeOrderRefundBuilder;
	
	@Override
	public void beforeTradeOrderRefundsCrteate(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) {
	}

	@Override
	public void afterTradeOrderRefundsCrteated(TradeOrderRefundContextBo tradeOrderRefundContext) {
		// do nothing
	}

	@Override
	public void beforeTradeOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext) {
		// do nothing
	}

	@Override
	public void afterOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		// do nothing
		TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
		Assert.notNull(tradeOrderRefunds);
		if(tradeOrderRefundContext.getTradeOrderRefunds().getRefundsStatus() == RefundsStatusEnum.APPLY_CUSTOMER_SERVICE_INTERVENE){
			//申请客服介入，插入纠风单日志
			TradeOrderDispute tradeOrderDispute  = tradeOrderRefundBuilder.createTradeOrderDispute(tradeOrderRefundContext);
			try {
				tradeOrderDisputeService.updateByApplyDispute(tradeOrderDispute);
				//添加纠纷单日志
				addTradeOrderDisputeLog(tradeOrderRefunds,tradeOrderDispute);
			} catch (ServiceException e) {
				throw new MallApiException(e);
			}
		}

	}

	private void addTradeOrderDisputeLog(TradeOrderRefunds tradeOrderRefunds, TradeOrderDispute tradeOrderDispute) throws ServiceException {
		TradeOrderDisputeLog tradeOrderDisputeLog = new TradeOrderDisputeLog();
		tradeOrderDisputeLog.setId(UuidUtils.getUuid());
		tradeOrderDisputeLog.setDisputeId(tradeOrderDispute.getId());
		tradeOrderDisputeLog.setOperateUser(tradeOrderRefunds.getUserId());
		tradeOrderDisputeLog.setStatus(DisputeStatusEnum.UNPROCESSED);
		tradeOrderDisputeLog.setRecordTime(new Date());
		tradeOrderDisputeLogService.update(tradeOrderDisputeLog);
	}

}
