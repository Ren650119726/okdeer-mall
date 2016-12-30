/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivitySelectAreaMapper.java
 * @Date 2016-12-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.entity.ActivitySelectArea;

public interface ActivitySelectAreaMapper extends IBaseMapper {

	/**
	 * @Description: 根据活动ID查询数据
	 * @param activityId 活动ID
	 * @return List<ActivitySelectArea>  
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	List<ActivitySelectArea> findListByActivityId(String activityId);

	/**
	 * @Description: 根据活动ID删除数据
	 * @param activityId 活动ID   
	 * @return int 删除记录  
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	int deleteByActivityId(String activityId);

	/**
	 * @Description: 批量插入数据
	 * @param list   
	 * @return 成功插入记录数  
	 * @author tangzj02
	 * @date 2016年12月28日
	 */
	int insertMore(@Param("list") List<ActivitySelectArea> list);
}