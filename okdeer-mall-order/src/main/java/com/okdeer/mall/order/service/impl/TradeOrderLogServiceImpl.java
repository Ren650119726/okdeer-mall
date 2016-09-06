package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderLog;
import com.okdeer.mall.order.service.TradeOrderLogServiceApi;
import com.yschome.base.common.exception.ServiceException;
import com.okdeer.mall.order.mapper.TradeOrderLogMapper;
import com.okdeer.mall.order.service.TradeOrderLogService;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderLogServiceApi")
class TradeOrderLogServiceImpl implements TradeOrderLogService, TradeOrderLogServiceApi {

	@Resource
	private TradeOrderLogMapper tradeOrderLogMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.okdeer.mall.trade.order.serivce.TradeOrderLogService#insertSelective
	 * (com.okdeer.mall.trade.order.entity.TradeOrderLog)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertSelective(TradeOrderLog tradeOrderLog) throws ServiceException {
		tradeOrderLogMapper.insertSelective(tradeOrderLog);
	}

}