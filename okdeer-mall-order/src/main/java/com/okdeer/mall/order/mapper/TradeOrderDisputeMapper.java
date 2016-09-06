package com.okdeer.mall.order.mapper;

import com.okdeer.mall.order.entity.TradeOrderDispute;
import com.yschome.base.dal.IBaseCrudMapper;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderDisputeMapper extends IBaseCrudMapper {
	
	/**
	 * 新增纠纷单
	 * @author yangq
	 * @param tradeOrderDispute
	 */
	void insertDispute(TradeOrderDispute tradeOrderDispute);
	
	/**
	 * 修改纠纷单状态
	 * @author yangq
	 * @param tradeOrderDispute
	 */
	void updateDispute(TradeOrderDispute tradeOrderDispute);
	
}