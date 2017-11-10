package com.okdeer.mall.activity.service;

import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.bo.UserCouponsBo;
import com.okdeer.mall.activity.coupons.bo.UserCouponsFilterContext;

/**
 * ClassName: FavourFilter 
 * @Description: 代金券过滤策略
 * @author maojj
 * @date 2017年2月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.1 			2017年2月14日				maojj		      优惠过滤策略
 */
public interface CouponsFilterStrategy {

	/**
	 * @Description: 该优惠是否可用
	 * @param favour
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年2月15日
	 */
	boolean accept(UserCouponsBo userCouponsBo,FavourParamBO paramBo,UserCouponsFilterContext filterContext);
}
