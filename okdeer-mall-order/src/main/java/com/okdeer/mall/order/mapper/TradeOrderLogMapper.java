package com.okdeer.mall.order.mapper;

import com.okdeer.mall.order.entity.TradeOrderLog;

/**
 * @DESC: 
 * @author yangq
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderLogMapper{
	
	/**
	 * @desc 新增订单日志记录
	 * @author yangq
	 * @param tradeOrderLog
	 */
	void insertSelective(TradeOrderLog tradeOrderLog);
	
}