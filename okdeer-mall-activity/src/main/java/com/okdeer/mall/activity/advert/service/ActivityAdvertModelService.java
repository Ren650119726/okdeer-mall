/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertModel;

/**
 * ClassName: ActivityAdvertModelService 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V2.2.0			2017-4-13			tuzhd			 活动模块实现类
 */

public interface ActivityAdvertModelService extends IBaseService {
	/**
	 * @Description: 根据模块序号及活动id查询模块信息
	 * @param modelNo 模块序号
	 * @param activityAdvertId 活动id
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
	public ActivityAdvertModel findModelByIdNo(String modelNo,String activityAdvertId);
	/**
	 * @Description: 新增模块信息
	 * @param modelNo 模块序号
	 * @param activityAdvertId 活动id
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
	public int addModel(ActivityAdvertModel model);
	/**
	 * @Description: 获取活动模板列表
	 * @param model
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月19日
	 */
	public List<ActivityAdvertModel> findModelList(ActivityAdvertModel model);
	
	/**
	 * @Description: 删除关联模块信息by活动id
	 * @param activityAdvertId 活动id
	 * @return int  
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月19日
	 */
	public int deleteByActivityAdvertId(String activityAdvertId);
	
}
