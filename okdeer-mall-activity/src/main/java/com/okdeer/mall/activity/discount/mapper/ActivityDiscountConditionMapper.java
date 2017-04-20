package com.okdeer.mall.activity.discount.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountCondition;

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
 *     友门鹿V2.3 	   2017-04-18			 maojj				优化优惠条件
 */
public interface ActivityDiscountConditionMapper extends IBaseMapper {

	/**
	 * @Description: 批量新增优惠条件
	 * @param conditionList   
	 * @author maojj
	 * @date 2017年4月18日
	 */
	void batchAdd(@Param("conditionList")List<ActivityDiscountCondition> conditionList);
	
	/**
	 * @Description: 删除活动下的优惠条件
	 * @param activityId 满减(满折、零钱包)活动ID
	 * @return   
	 * @author maojj
	 * @date 2017年4月18日
	 */
	int deleteByActivityId(@Param("activityId")String activityId);
	
	/**
	 * @Description: 查询活动下的优惠条件
	 * @param activityId
	 * @return   
	 * @author maojj
	 * @date 2017年4月18日
	 */
	List<ActivityDiscountCondition> findByActivityId(@Param("activityId") String activityId);

}