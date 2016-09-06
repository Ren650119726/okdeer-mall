package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.service.TradeOrderLogisticsServiceApi;
import com.yschome.base.common.exception.ServiceException;
import com.okdeer.mall.order.mapper.TradeOrderLogisticsMapper;
import com.okdeer.mall.order.service.TradeOrderLogisticsService;

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
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderLogisticsServiceApi")
class TradeOrderLogisticsServiceImpl implements TradeOrderLogisticsService, TradeOrderLogisticsServiceApi {

	@Resource
	private TradeOrderLogisticsMapper tradeOrderLogisticsMapper;

	/**
	 * @desc TODO Add a description 
	 *
	 * @param orderId
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public TradeOrderLogistics findByOrderId(String orderId) throws ServiceException {
		return tradeOrderLogisticsMapper.selectByOrderId(orderId);
	}
	
	// Begin 重构4.1 add by zengj
	/**
	 * 
	 * @Description: 新增物流信息
	 * @param logistics   物流信息
	 * @author zengj
	 * @date 2016年7月29日
	 */
	@Override
	public void addTradeOrderLogistics(TradeOrderLogistics logistics) {
		tradeOrderLogisticsMapper.insertSelective(logistics);
	}
	// End 重构4.1 add by zengj
}