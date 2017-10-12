/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityDiscountGroupMapper.java
 * @Date 2017-10-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.discount.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountGroup;
/**
 * ClassName: ActivityDiscountGroupMapper 
 * @Description: 团购商品关联表实体操作类
 * @author tuzhd
 * @date 2017年10月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	   2.6.3			2017-10-10			tuzhd			团购商品关联表实体操作类
 */
public interface ActivityDiscountGroupMapper extends IBaseMapper {
	
	/**
	 * @Description: 批量新增团购活动
	 * @param list   
	 * @author tuzhd
	 * @date 2017年10月12日
	 */
	void batchAdd(@Param("list")List<ActivityDiscountGroup> list);
	
	/**
	 * @Description: 根据活动id删除团购商品记录
	 * @param activityId
	 * @return int  
	 * @author tuzhd
	 * @date 2017年10月12日
	 */
	int deleteByActivityId(@Param("activityId")String activityId);

}