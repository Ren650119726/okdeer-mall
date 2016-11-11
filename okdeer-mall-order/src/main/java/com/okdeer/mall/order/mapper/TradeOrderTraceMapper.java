package com.okdeer.mall.order.mapper;


import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.order.entity.TradeOrderTrace;

/**
 * ClassName: TradeOrderTraceMapper 
 * @Description: 订单轨迹Mapper
 * @author maojj
 * @date 2016年11月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿1.2			2016年11月7日				maojj		      订单轨迹Mapper
 */
public interface TradeOrderTraceMapper extends IBaseMapper {

	/**
	 * @Description: 根据订单ID查询轨迹列表
	 * @param orderId
	 * @return   
	 * @author maojj
	 * @date 2016年11月7日
	 */
	List<TradeOrderTrace> findTraceList(String orderId);
}
