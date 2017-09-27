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
import com.okdeer.mall.activity.staticFile.bo.ActivityStaticFileBo;
import com.okdeer.mall.activity.staticFile.dto.ActivityStaticFileDto;
import com.okdeer.mall.activity.staticFile.dto.ActivityStaticFileParamDto;
import com.okdeer.mall.activity.staticFile.mapper.ActivityStaticFileMapper;
import com.okdeer.mall.activity.staticFile.service.ActivityStaticFileService;

/**
 * ClassName: ActivityStaticFileServiceImpl 
 * @Description: 前端静态模块service
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      V2.2.2			2017年4月12日 		xuzq01				前端静态模块service
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

	@SuppressWarnings("unchecked")
	@Override
	public PageUtils<ActivityStaticFileDto> findStaticFileList(ActivityStaticFileParamDto activityStaticFileParamDto, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityStaticFileBo> fileBoList = activityStaticFileMapper.findStaticFileList(activityStaticFileParamDto);
		return new PageUtils<ActivityStaticFileBo>(fileBoList).toBean(ActivityStaticFileDto.class);
	}

	@Override
	public int findCountByName(String title) {
		return activityStaticFileMapper.findCountByName(title);
	}

}
