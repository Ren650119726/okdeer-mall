package com.okdeer.mall.order.mapper;

import com.okdeer.mall.order.entity.TradeOrderLogistics;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;

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
	
	//Begin V2.1.0 added by luosm 20170215
	/***
	 * 
	 * @Description: 根据订单ID集合查询物流信息
	 * @param orderIds
	 * @return
	 * @author luosm
	 * @date 2017年2月14日
	 */
	List<TradeOrderLogistics> selectByOrderIds(@Param("orderIds") List<String> orderIds);
	
	/***
	 * 
	 * @Description: 根据城市id查询订单id集合
	 * @param cityId
	 * @return
	 * @author luosm
	 * @date 2017年2月15日
	 */
	List<String> selectByCityId(String cityId);
	
	/***
	 * 根据订单id更新物流信息
	 * @param tradeOrderLogistics
	 */
	void updateByOrderId(TradeOrderLogistics tradeOrderLogistics);
	//End V2.1.0 added by luosm 20170215
}