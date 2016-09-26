package com.okdeer.mall.points.service;


import com.okdeer.mall.member.points.entity.PointsExchangeOrder;
import com.okdeer.base.common.exception.ServiceException;

/**
 * @DESC: 
 * @author luosm
 * @date  2016-03-16 10:13:06
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface PointsExchangeOrderService {
	
	/**
	 * 添加兑吧兑换信息
	 *@author luosm
	 * @param pointsExchangeOrder 请求参数
	 */
	String add(PointsExchangeOrder pointsExchangeOrder) throws ServiceException;
	
	
	/**
	 * 通过id获取积分兑换订单记录表
	 *@author luosm
	 * @param id 请求参数
	 */
	PointsExchangeOrder findById(String id) throws ServiceException;
	
	/**
	 * 通过duibaOrderNo获取积分兑换订单记录表
	 *@author luosm
	 * @param duibaOrderNo 请求参数
	 */
	PointsExchangeOrder findByDuibaOrderNo(String duibaOrderNo) throws ServiceException;
	
	/**
	 * 通过pointsExchangeOrder更新积分兑换订单记录表
	 *@author luosm
	 * @param pointsExchangeOrder 请求参数
	 */
	void updateByDuibaMap(PointsExchangeOrder pointsExchangeOrder) throws ServiceException;
	
}