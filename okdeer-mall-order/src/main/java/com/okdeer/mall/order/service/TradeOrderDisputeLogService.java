package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrderDisputeLog;
import com.okdeer.base.common.exception.ServiceException;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-03-06 15:19:16
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderDisputeLogService {

	public void update(TradeOrderDisputeLog tradeOrderDisputeLog) throws ServiceException;
}