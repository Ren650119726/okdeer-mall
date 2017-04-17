package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsArea;

/**
 * ClassName: 代金券的区域关联关系
 * @Description: ActivityCouponsAreaMapper
 * @author zhulq
 * @date 2017年4月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2017年4月11日 			zhulq
 */
public interface ActivityCouponsAreaMapper extends IBaseCrudMapper{
	
	/**
	 * @Description: 根据区域类型获取省市信息
	 * @param activityCouponsArea 实体
	 * @return List<ActivityCouponsArea>
	 * @author zhulq
	 * @date 2017年4月11日
	 */
	List<ActivityCouponsArea> findListByType(ActivityCouponsArea activityCouponsArea);
}
