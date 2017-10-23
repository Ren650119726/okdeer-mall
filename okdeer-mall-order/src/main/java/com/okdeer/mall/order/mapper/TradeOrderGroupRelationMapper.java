/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * TradeOrderGroupRelationMapper.java
 * @Date 2017-10-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.order.entity.TradeOrderGroupRelation;

/**
 * ClassName: TradeOrderGroupRelationMapper 
 * @Description: 团单订单关联关系
 * @author maojj
 * @date 2017年10月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年10月12日				maojj
 */
public interface TradeOrderGroupRelationMapper extends IBaseMapper {

	/**
	 * @Description: 根据订单id查询团单订单关联关系
	 * @param orderId
	 * @return   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	TradeOrderGroupRelation findByOrderId(String orderId);
	
	/**
	 * @Description: 根据订单id列表查询团购关联关系
	 * @param orderIds
	 * @return   
	 * @author maojj
	 * @date 2017年10月19日
	 */
	List<TradeOrderGroupRelation> findByOrderIds(@Param("orderIds")List<String> orderIds);
	
	/**
	 * @Description: 查询已成功入团的团单关联关系
	 * @param groupOrderId
	 * @return   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	List<TradeOrderGroupRelation> findByGroupOrderId(String groupOrderId);
	
	/**
	 * @Description:根据团单id统计成功入团的总数 
	 * @param groupOrderId
	 * @return   
	 * @author maojj
	 * @date 2017年10月12日
	 */
	int countSuccessJoinNum(String groupOrderId);
}