/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertModel;
import com.okdeer.mall.activity.advert.service.ActivityAdvertModelApi;
import com.okdeer.mall.activity.advert.service.ActivityAdvertModelService;

/**
 * ClassName: ActivityAdvertModelServiceImpl 
 * @Description:  活动模块实现类
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V2.2.0			2017-4-13			tuzhd			 活动模块实现类
 */
@Service(version="1.0.0")
public class ActivityAdvertModelApiImpl implements ActivityAdvertModelApi {
	
	@Autowired
	public ActivityAdvertModelService activityAdvertModelService;
	/**
	 * @Description: 根据模块序号及活动id查询模块信息
	 * @param modelNo 模块序号
	 * @param activityAdvertId 活动id
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
	public ActivityAdvertModel findModelByIdNo(String modelNo,String activityAdvertId){
		return activityAdvertModelService.findModelByIdNo(modelNo, activityAdvertId);
	}
	
	/**
	 * @Description: 新增模块信息
	 * @param modelNo 模块序号
	 * @param activityAdvertId 活动id
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
	public int addModel(ActivityAdvertModel model){
		return activityAdvertModelService.addModel(model);
	}
}
