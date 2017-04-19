/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityAdvertModelMapper.java
 * @Date 2017-04-12 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.advert.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertModel;
/**
 * ClassName: ActivityAdvertModelMapper 
 * @Description: 活动模块存储类
 * @author tuzhd
 * @date 2017年4月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		V2.2.0			2017-4-13			tuzhd			 活动模块实现类
 */
public interface ActivityAdvertModelMapper extends IBaseMapper {
	
	/**
	 * @Description: 根据模块序号及活动id查询模块信息
	 * @param modelNo 模块序号
	 * @param activityAdvertId 活动id
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
	public ActivityAdvertModel findModelByIdNo(int modelNo,String activityAdvertId);

	/**
	 * @Description: TODO
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