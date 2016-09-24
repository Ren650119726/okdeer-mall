package com.okdeer.mall.activity.discount.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.discount.entity.ActivityDiscountCondition;
import com.okdeer.base.dal.IBaseCrudMapper;

/**
 * 店铺活动满减(满折)条件
 * @pr yscm
 * @desc 店铺活动满减(满折)条件
 * @author zengj
 * @date 2016年1月26日 下午2:16:27
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月18日                               zengj				新增增加活动项信息方法
 */
public interface ActivityDiscountConditionMapper extends IBaseCrudMapper {
	/**
	 * 
	 * @desc 添加满减(满折)活动条件
	 *
	 * @param activityDiscountConditionList 满减(满折)条件集合
	 */
	void insertActivityDiscountCondition(List<ActivityDiscountCondition> list);
	
	/**
	 * 
	 * @desc 删除满减(满折)活动下的条件
	 *
	 * @param discountId 满减(满折)活动ID
	 */
	void deleteActivityDiscountCondition(@Param("discountId") String discountId);
	
	/**
	 * 
	 * @desc 查询 满减(满折)活动下的条件
	 *
	 * @param discountId 满减(满折)活动ID
	 * @return  满减(满折)活动条件集合
	 */
	List<ActivityDiscountCondition> selectByDiscountId(@Param("discountId") String discountId);
	
	//重构4.1 add by zengj
	/**
	 * @Description: 根据主键查询活动项信息
	 * @param id主键ID
	 * @return ActivityDiscountCondition 活动项信息  
	 * @author zengj
	 * @date 2016年7月18日
	 */
	ActivityDiscountCondition findByPrimaryKey(@Param("id") String id);
	//重构4.1 add by zengj
}