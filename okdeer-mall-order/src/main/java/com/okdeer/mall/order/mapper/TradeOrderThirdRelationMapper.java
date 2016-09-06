package com.okdeer.mall.order.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yschome.base.dal.IBaseCrudMapper;
/**
 *订单与第三方手机充值平台关系Mapper
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年7月20日 下午1:53:15
 * 
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	         重构4.1		    2016-7-20			 zhaoqc	                                     新增
 */
@Repository
public interface TradeOrderThirdRelationMapper extends IBaseCrudMapper {
	
	/**
	 * 根据订单ID删除关系
	 * @param order 订单Id
	 */
	void deleteById(@Param("orderId") String orderId);
}
