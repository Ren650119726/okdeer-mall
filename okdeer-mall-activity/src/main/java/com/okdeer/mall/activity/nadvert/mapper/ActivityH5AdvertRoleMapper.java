/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityH5AdvertRoleMapper.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.nadvert.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5AdvertRole;

public interface ActivityH5AdvertRoleMapper extends IBaseMapper {
	
	/**
	 * @Description: 批量保存h5活动规则
	 * @param entitys void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月11日
	 */
	void batchSave(@Param("list")List<ActivityH5AdvertRole> entitys);
	
	
	/**
	 * @Description: 通过h5活动id查询活动规则
	 * @param activityId
	 * @return List<ActivityH5AdvertRole>
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	List<ActivityH5AdvertRole> findByActId(@Param("activityId")String activityId);
	
	/**
	 * @Description: 删除h5活动规则
	 * @param activityId
	 * @param contentId void
	 * @throws
	 * @author mengsj
	 * @date 2017年8月12日
	 */
	void deleteByActId(@Param("activityId")String activityId);

}