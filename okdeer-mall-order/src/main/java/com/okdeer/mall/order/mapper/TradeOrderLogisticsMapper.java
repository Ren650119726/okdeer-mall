package com.okdeer.mall.order.mapper;

import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderLogisticsMapper extends IBaseCrudMapper {
	
	/**
	 * 根据orderId查询收货人信息
	 *
	 * @param orderId 订单orderId
	 * @return 返回查询结果集
	 */
	TradeOrderLogistics selectByOrderId(String orderId);
}