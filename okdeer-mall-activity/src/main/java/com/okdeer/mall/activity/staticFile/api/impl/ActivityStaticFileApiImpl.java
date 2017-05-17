/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.staticFile.api.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.staticFile.dto.ActivityStaticFileDto;
import com.okdeer.mall.activity.staticFile.dto.ActivityStaticFileParamDto;
import com.okdeer.mall.activity.staticFile.entity.ActivityStaticFile;
import com.okdeer.mall.activity.staticFile.service.ActivityStaticFileApi;
import com.okdeer.mall.activity.staticFile.service.ActivityStaticFileService;


/**
 * ClassName: ActivityStaticFileApiImpl 
 * @Description:  前端模块管理Api实现
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * V2.2.0          2017年4月12日                       xuzq01          	    前端模块管理Api实现
 */
@Service(version="1.0.0")
public class ActivityStaticFileApiImpl implements ActivityStaticFileApi {
	
	/**
	 * 活动奖品权重表Service
	 */
	@Autowired
	private ActivityStaticFileService activityStaticFileService;
	
	@Override
	public PageUtils<ActivityStaticFileDto> findStaticFileList(ActivityStaticFileParamDto activityStaticFileParamDto, int pageNumber,
			int pageSize) {
		List<ActivityStaticFileDto>  file = activityStaticFileService.findStaticFileList(activityStaticFileParamDto, pageNumber, pageSize);
		
		return new PageUtils<ActivityStaticFileDto>(file);
	}

	@Override
	public int findCountByName(String title) {
		
		return activityStaticFileService.findCountByName(title);
		
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
	public void updateStaticFile(ActivityStaticFile staticFile) throws Exception {
		Date date = new Date();
		staticFile.setUpdateTime(date);
		activityStaticFileService.update(staticFile);
	}

}
