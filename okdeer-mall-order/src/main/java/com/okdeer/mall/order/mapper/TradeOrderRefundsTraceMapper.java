package com.okdeer.mall.order.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.order.entity.TradeOrderRefundsTrace;

/**
 * TradeOrderRefundsTrace 数据访问接口
 * @author YSCERP CODE GENERATOR
 * @Copyright: ©2005-2013 yschome.com Inc. All rights reserved
 */
public interface TradeOrderRefundsTraceMapper extends IBaseCrudMapper{
	
	/**
	 * 批量新增
	 * @param tradeOrderRefundsTraceList
	 */
	void batchInsert(List<TradeOrderRefundsTrace> tradeOrderRefundsTraceList);
	
	List<TradeOrderRefundsTrace> findRefundsTrace(String refundsId);
}
