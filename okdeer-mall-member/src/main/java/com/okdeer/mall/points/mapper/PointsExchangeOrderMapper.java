package com.okdeer.mall.points.mapper;

import com.okdeer.mall.member.points.entity.PointsExchangeOrder;
import com.yschome.base.dal.IBaseCrudMapper;

/**
 * @DESC: 
 * @author luosm
 * @date  2016-03-16 10:13:06
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface PointsExchangeOrderMapper extends IBaseCrudMapper {
	
	/**
	 * 根据兑吧订单号查询积分兑换订单记录表
	 * @param duibaOrderNo
	 * @return PointsExchangeOrder
	 */
	PointsExchangeOrder selectByDuibaOrderNo(String duibaOrderNo);
	
	
}