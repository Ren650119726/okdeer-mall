package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrderDispute;
import com.yschome.base.common.exception.ServiceException;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderDisputeService {

	/**
	 * 买家申请客服操作
	 * 纠纷单状态 init(申请客服介入)
	 * @author yangq
	 * @param tradeOrderDispute
	 * @throws ServiceException
	 */
	public void updateApplyDispute(TradeOrderDispute tradeOrderDispute) throws ServiceException;

	/**
	 * 纠纷单操作
	 * 纠纷单状态 from(客服介入中)----to(取消纠纷单)
	 * @author yangq
	 * @param tradeOrderDispute
	 * @throws ServiceException
	 */
	public void updateCancelDispute(TradeOrderDispute tradeOrderDispute) throws ServiceException;

	/**
	 * 纠纷单操作
	 * 纠纷单状态from(客服介入中)----to(强制卖家退款)
	 * @author yangq
	 * @param tradeOrderDispute
	 * @throws ServiceException
	 */
	public void updateForceRefund(TradeOrderDispute tradeOrderDispute) throws ServiceException;

	/**
	 * 纠纷单操作
	 * 纠纷单状态from(客服介入中)----to(友门鹿退款)
	 * @author yangq
	 * @param tradeOrderDispute
	 * @throws ServiceException
	 */
	public void updateYscRefund(TradeOrderDispute tradeOrderDispute) throws ServiceException;

	/**
	 * 买家申请客服介入- 纠纷单插入数据
	 * @author luosm
	 * @param tradeOrderDispute
	 * @throws ServiceException
	 */
	public void updateByApplyDispute(TradeOrderDispute tradeOrderDispute) throws ServiceException;

	/**
	 * 通过id查询纠纷单信息
	 * @param id
	 * @return TradeOrderDispute
	 * @throws ServiceException
	 */
	TradeOrderDispute findById(String id) throws ServiceException;

}