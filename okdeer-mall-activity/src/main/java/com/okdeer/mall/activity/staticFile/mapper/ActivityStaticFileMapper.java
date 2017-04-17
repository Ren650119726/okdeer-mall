/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityStaticFileMapper.java
 * @Date 2017-04-12 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.staticFile.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.staticFile.entity.ActivityStaticFile;
import com.okdeer.mall.activity.staticFile.entity.ActivityStaticFileVo;

public interface ActivityStaticFileMapper extends IBaseMapper {

	/**
	 * @Description: TODO
	 * @param activityStaticFileVo
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月12日
	 */
	List<ActivityStaticFile> findStaticFileList(ActivityStaticFileVo activityStaticFileVo);

	/**
	 * @Description: TODO
	 * @param activityStaticFile
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月12日
	 */
	int findCountByName(ActivityStaticFile activityStaticFile);

	/**
	 * @Description: TODO
	 * @param id
	 * @param activityId   
	 * @author xuzq01
	 * @date 2017年4月17日
	 */
	void associateActivity(@Param("id") String id, @Param("activityId") String activityId);

}