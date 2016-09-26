package com.okdeer.mall.order.service;

import java.util.List;
import java.util.Map;

import com.okdeer.mall.order.entity.TradeOrderRefundsLogistics;
import com.okdeer.base.common.exception.ServiceException;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderRefundsLogisticsService {

	TradeOrderRefundsLogistics findByRefundsId(String refundsId);

	void modifyById(TradeOrderRefundsLogistics logistics);

	void add(TradeOrderRefundsLogistics logistics);

	/***
	 * 
	 * @Description: 根据参数条件查询
	 * @return List<TradeOrderRefundsLogistics>  
	 * @throws
	 * @author luosm
	 * @date 2016年7月4日
	 */
	public List<TradeOrderRefundsLogistics> findByParams(Map<String, Object> params) throws ServiceException;

}