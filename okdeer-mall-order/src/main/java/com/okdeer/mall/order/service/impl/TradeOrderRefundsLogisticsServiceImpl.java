package com.okdeer.mall.order.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Maps;
import com.okdeer.mall.order.entity.TradeOrderRefundsLogistics;
import com.okdeer.mall.order.service.TradeOrderRefundsLogisticsServiceApi;
import com.yschome.base.common.exception.ServiceException;
import com.okdeer.mall.order.mapper.TradeOrderRefundsLogisticsMapper;
import com.okdeer.mall.order.service.TradeOrderRefundsLogisticsService;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderRefundsLogisticsServiceApi")
class TradeOrderRefundsLogisticsServiceImpl implements TradeOrderRefundsLogisticsService,
		TradeOrderRefundsLogisticsServiceApi {

	@Resource
	private TradeOrderRefundsLogisticsMapper tradeOrderRefundsLogisticsMapper;

	/**
	 * @desc 根据退单ID查询物流信息
	 *
	 * @param refundsId
	 * @return
	 */
	@Override
	public TradeOrderRefundsLogistics findByRefundsId(String refundsId) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("refundsId", refundsId);
		List<TradeOrderRefundsLogistics> logisticsList = tradeOrderRefundsLogisticsMapper.selectByParams(params);
		if (!CollectionUtils.isEmpty(logisticsList)) {
			return logisticsList.get(0);
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void modifyById(TradeOrderRefundsLogistics logistics) {
		tradeOrderRefundsLogisticsMapper.updateByPrimaryKeySelective(logistics);

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void add(TradeOrderRefundsLogistics logistics) {
		tradeOrderRefundsLogisticsMapper.insertSelective(logistics);

	}

	@Override
	public List<TradeOrderRefundsLogistics> findByParams(Map<String, Object> params) throws ServiceException {
		// TODO Auto-generated method stub
		return tradeOrderRefundsLogisticsMapper.selectByParams(params);
	}
}