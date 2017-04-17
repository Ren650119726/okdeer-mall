/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.staticFile.api.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.staticFile.entity.ActivityStaticFile;
import com.okdeer.mall.activity.staticFile.entity.ActivityStaticFileVo;
import com.okdeer.mall.activity.staticFile.service.ActivityStaticFileApi;
import com.okdeer.mall.activity.staticFile.service.ActivityStaticFileService;


/**
 * ClassName: ActivityStaticFileApiImpl 
 * @Description: 
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version="1.0.0")
public class ActivityStaticFileApiImpl implements ActivityStaticFileApi {
	
	/**
	 * 活动奖品权重表Service
	 */
	@Autowired
	private ActivityStaticFileService activityStaticFileService;
	
	@Override
	public PageUtils<ActivityStaticFile> findStaticFileList(ActivityStaticFileVo activityStaticFileVo, int pageNumber,
			int pageSize) {
		
		return activityStaticFileService.findStaticFileList(activityStaticFileVo, pageNumber, pageSize);
	}

	@Override
	public int findCountByName(ActivityStaticFile activityStaticFile) {
		
		return activityStaticFileService.findCountByName(activityStaticFile);
		
	}

	@Override
	public void addStaticFile(ActivityStaticFile activityStaticFile) throws Exception {
		Date date = new Date();
		activityStaticFile.setId(UuidUtils.getUuid());
		activityStaticFile.setCreateTime(date);
		activityStaticFile.setUpdateTime(date);
		activityStaticFile.setDisabled(Disabled.valid);
		activityStaticFileService.add(activityStaticFile);
		
	}

	@Override
	public ActivityStaticFile findById(String id) throws Exception {
		return activityStaticFileService.findById(id);
	}

	@Override
	public void associateActivity(String id, String activityId) {
		activityStaticFileService.associateActivity(id, activityId);
		
	}

	@Override
	public void updateStaticFile(ActivityStaticFile staticFile) throws Exception {
		Date date = new Date();
		staticFile.setUpdateTime(date);
		activityStaticFileService.update(staticFile);
	}

}
