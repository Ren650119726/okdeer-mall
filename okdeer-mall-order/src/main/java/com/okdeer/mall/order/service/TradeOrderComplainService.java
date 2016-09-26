package com.okdeer.mall.order.service;

import java.util.List;

import com.okdeer.mall.order.entity.TradeOrderComplain;
import com.okdeer.mall.order.vo.TradeOrderComplainVo;
import com.okdeer.base.common.exception.ServiceException;

/**
 * @DESC: 投诉单service
 * @author zhongy
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderComplainService {

	/**
	 * 根据主键id查询投诉单
	 * @param id 请求参数
	 * @return 返回结果
	 * @throws ServiceException
	 */
	TradeOrderComplain findById(String id) throws ServiceException;

	/**
	 * 插入投诉单
	 * @author luosm
	 * @param tradeOrderComplain
	 * @throws ServiceException
	 */
	void update(TradeOrderComplain tradeOrderComplain) throws ServiceException;

	/***
	 * 通过orderId查询投诉单详情
	 * @author luosm
	 * @param orderId
	 * @return
	 * @throws ServiceException
	 */
	List<TradeOrderComplainVo> findByOrderId(String orderId) throws ServiceException;
}