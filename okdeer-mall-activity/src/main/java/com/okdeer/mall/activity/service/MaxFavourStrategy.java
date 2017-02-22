package com.okdeer.mall.activity.service;

import java.math.BigDecimal;

import com.okdeer.mall.order.vo.Favour;

/**
 * ClassName: MaxFavourStrategy 
 * @Description: 最大优惠策略
 * @author maojj
 * @date 2017年2月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.1 			2017年2月15日				maojj			最大优惠策略
 */
public interface MaxFavourStrategy {

	/**
	 * @Description: 计算最大优惠规则
	 * @return   
	 * @author maojj
	 * @date 2017年2月15日
	 */
	String calMaxFavourRule(Favour favour,BigDecimal totalAmount);
}
