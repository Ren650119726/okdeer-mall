/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.api.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.advert.dto.ActivityAdvertDto;
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
	public int findCountByName(ActivityAdvertDto activityAdvertDto) {
		return activityAdvertService.findCountByName(activityAdvertDto);
	}

	@Override
	public PageUtils<ActivityAdvert> findActivityAdvertList(ActivityAdvert activityAdvert, int pageNumber,
			int pageSize) {
		return activityAdvertService.findActivityAdvertList(activityAdvert, pageNumber, pageSize);
	}

	/**
	 * @Description: 保存新增活动信息
	 * @param activityAdvertDto 活动对象
	 * @param userId    用户id
	 * @return void  
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年4月18日
	 */
	public void addActivityAdvert(ActivityAdvertDto activityAdvertDto) throws Exception{
		activityAdvertService.addActivityAdvert(activityAdvertDto);
	}

	@Override
	public List<ActivityAdvert> findActivityListByStatus(String status) {
		String[] statusList = status.split(",");
		return activityAdvertService.findActivityListByStatus(Arrays.asList(statusList));
	}

}
