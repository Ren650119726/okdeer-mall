/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.advert.entity.ActivityAdvert;
import com.okdeer.mall.activity.advert.mapper.ActivityAdvertMapper;
import com.okdeer.mall.activity.advert.service.ActivityAdvertApi;
import com.okdeer.mall.activity.advert.service.ActivityAdvertService;
import com.okdeer.mall.activity.staticFile.entity.ActivityStaticFile;

/**
 * ClassName: ActivityAdvertServiceImpl 
 * @Description: 
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class ActivityAdvertServiceImpl extends BaseServiceImpl implements ActivityAdvertService {
	
	/**
	 * 广告活动Service
	 */
	@Autowired
	ActivityAdvertMapper activityAdvertMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityAdvertMapper;
	}

	@Override
	public int findCountByName(ActivityAdvert activityAdvert) {
		return activityAdvertMapper.findCountByName(activityAdvert);
	}

	@Override
	public PageUtils<ActivityAdvert> findActivityAdvertList(ActivityAdvert activityAdvert, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityAdvert> result = activityAdvertMapper.findActivityAdvertList(activityAdvert);
		return new PageUtils<ActivityAdvert>(result);
	}

}
