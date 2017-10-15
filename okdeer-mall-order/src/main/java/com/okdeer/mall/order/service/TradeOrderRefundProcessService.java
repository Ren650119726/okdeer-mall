
package com.okdeer.mall.order.service;

import java.util.List;

import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundResultDto;
import com.okdeer.mall.order.enums.RefundsStatusEnum;

/**
 * ClassName: TraderOrderRefundProcessService 
 * @Description: 订单退款处理
 * @author zengjizu
 * @date 2017年10月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface TradeOrderRefundProcessService {

	/**
	 * @Description: 校验退款申请
	 * @return
	 * @author zengjizu
	 * @date 2017年10月12日
	 */
	boolean checkApplyRefund(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			Response<TradeOrderApplyRefundResultDto> response, TradeOrderRefundContextBo tradeOrderRefundContext);
	/**
	 * @Description: 创建退款信息
	 * @param tradeOrderApplyRefundParamDto
	 * @param tradeOrderRefundContext
	 * @throws MallApiException
	 * @author zengjizu
	 * @date 2017年10月12日
	 */
	void createRefundInfo(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException;
	
	/**
	 * @Description: 更新退款单
	 * @author zengjizu
	 * @throws MallApiException 
	 * @date 2017年10月13日
	 */
	void updateTradeOrderRefund(TradeOrderRefundContextBo tradeOrderRefundContext,RefundsStatusEnum checkStatus,TradeOrderRefundProcessCallback tradeOrderRefundProcessCallback) throws MallApiException;
	/**
	 * @Description: 添加退款监听
	 * @param lister
	 * @author zengjizu
	 * @date 2017年10月13日
	 */
	void addTradeOrderRefundsListener(TradeOrderRefundsListener lister);
	
	/**
	 * @Description: 设置监听
	 * @param listerList
	 * @author zengjizu
	 * @date 2017年10月13日
	 */
	void setTradeOrderRefundsListener(List<TradeOrderRefundsListener> listerList);
}
