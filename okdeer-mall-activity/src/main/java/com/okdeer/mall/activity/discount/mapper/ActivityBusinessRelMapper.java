/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityBusinessRelMapper.java
 * @Date 2017-04-17 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.discount.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.discount.entity.ActivityBusinessRel;

public interface ActivityBusinessRelMapper extends IBaseMapper {

	/**
	 * @Description: 批量新增活动业务关联关系
	 * @param relList   
	 * @author maojj
	 * @date 2017年4月18日
	 */
	void batchAdd(@Param("relList")List<ActivityBusinessRel> relList);
	
	/**
	 * @Description: 查询活动业务关联关系
	 * @param activityId
	 * @return   
	 * @author maojj
	 * @date 2017年4月19日
	 */
	List<ActivityBusinessRel> findByActivityId(String activityId);
	
	/**
	 * @Description: 根据活动ID删除业务关联关系
	 * @param activityId   
	 * @author maojj
	 * @date 2017年4月20日
	 */
	void deleteByActivityId(String activityId);
}