package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrderLogistics;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.common.exception.ServiceException;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月29日                               zengj				新增添加物流信息方法
 */
public interface TradeOrderLogisticsService {

	/**
	 * 根据orderId查询收货人信息
	 *
	 * @param orderId 订单orderId
	 * @return 返回查询结果集
	 */
	TradeOrderLogistics findByOrderId(String orderId) throws ServiceException;
	
	// Begin 重构4.1 add by zengj
	/**
	 * 
	 * @Description: 新增物流信息
	 * @param logistics   物流信息
	 * @author zengj
	 * @date 2016年7月29日
	 */
	void addTradeOrderLogistics(TradeOrderLogistics logistics);
	// End 重构4.1 add by zengj
	
	List<TradeOrderLogistics> selectByOrderIds(List<String> orderIds) throws ServiceException;
}