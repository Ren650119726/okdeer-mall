package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrderLog;
import com.okdeer.base.common.exception.ServiceException;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderLogService {

	/**
	 * @desc 新增订单日志记录
	 * @author yangq
	 * @param tradeOrderLog
	 */
	void insertSelective(TradeOrderLog tradeOrderLog) throws ServiceException;

}