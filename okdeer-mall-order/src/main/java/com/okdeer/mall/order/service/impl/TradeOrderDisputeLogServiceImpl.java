package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderDisputeLog;
import com.okdeer.mall.order.service.TradeOrderDisputeLogServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.order.mapper.TradeOrderDisputeLogMapper;
import com.okdeer.mall.order.service.TradeOrderDisputeLogService;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-03-06 15:19:16
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version="1.0.0", interfaceName="com.okdeer.mall.order.service.TradeOrderDisputeLogServiceApi")
class TradeOrderDisputeLogServiceImpl implements TradeOrderDisputeLogService, TradeOrderDisputeLogServiceApi {
    @Resource
    private TradeOrderDisputeLogMapper tradeOrderDisputeLogMapper;


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(TradeOrderDisputeLog tradeOrderDisputeLog)
			throws ServiceException {
		// TODO Auto-generated method stub
		tradeOrderDisputeLogMapper.insertSelective(tradeOrderDisputeLog);
	}
}