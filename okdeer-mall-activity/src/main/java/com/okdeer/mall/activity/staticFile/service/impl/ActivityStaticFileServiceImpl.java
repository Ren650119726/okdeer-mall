/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.staticFile.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.staticFile.entity.ActivityStaticFile;
import com.okdeer.mall.activity.staticFile.entity.ActivityStaticFileVo;
import com.okdeer.mall.activity.staticFile.mapper.ActivityStaticFileMapper;
import com.okdeer.mall.activity.staticFile.service.ActivityStaticFileService;

/**
 * ClassName: ActivityStaticFileServiceImpl 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class ActivityStaticFileServiceImpl extends BaseServiceImpl implements ActivityStaticFileService {

	
	/**
	 * 静态记录mapper
	 */
	@Autowired
	ActivityStaticFileMapper activityStaticFileMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		
		return activityStaticFileMapper;
	}

	@Override
	public PageUtils<ActivityStaticFile> findStaticFileList(ActivityStaticFileVo activityStaticFileVo, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityStaticFile> result = activityStaticFileMapper.findStaticFileList(activityStaticFileVo);
		return new PageUtils<ActivityStaticFile>(result);
	}

	@Override
	public int findCountByName(ActivityStaticFile activityStaticFile) {
		return activityStaticFileMapper.findCountByName(activityStaticFile);
	}

	@Override
	public void associateActivity(String id, String activityId) {
		
		activityStaticFileMapper.associateActivity(id, activityId);
		
	}

}
