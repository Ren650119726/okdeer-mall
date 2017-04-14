/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.api.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.advert.entity.ActivityAdvert;
import com.okdeer.mall.activity.advert.service.ActivityAdvertApi;
import com.okdeer.mall.activity.advert.service.ActivityAdvertService;

/**
 * ClassName: ActivityAdvertServiceImpl 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version="1.0.0")
public class ActivityAdvertApiImpl implements ActivityAdvertApi {
	
	/**
	 * H5活动Service
	 */
	@Autowired
	ActivityAdvertService activityAdvertService;
	
	@Override
	public ActivityAdvert findById(String id) throws Exception {
		return activityAdvertService.findById(id);
	}

	@Override
	public int findCountByName(ActivityAdvert activityAdvert) {
		return activityAdvertService.findCountByName(activityAdvert);
	}

	@Override
	public PageUtils<ActivityAdvert> findActivityAdvertList(ActivityAdvert activityAdvert, int pageNumber,
			int pageSize) {
		return activityAdvertService.findActivityAdvertList(activityAdvert, pageNumber, pageSize);
	}

	@Override
	public void addActivityAdvert(ActivityAdvert activityAdvert) throws Exception {
		Date date = new Date();
		activityAdvert.setId(UuidUtils.getUuid());
		activityAdvert.setCreateTime(date);
		activityAdvert.setUpdateTime(date);
		activityAdvert.setDisabled(Disabled.valid);
		activityAdvertService.add(activityAdvert);
		
	}

}
