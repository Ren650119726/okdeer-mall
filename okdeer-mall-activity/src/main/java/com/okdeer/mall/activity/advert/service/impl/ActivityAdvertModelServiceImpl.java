/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertModel;
import com.okdeer.mall.activity.advert.mapper.ActivityAdvertModelMapper;
import com.okdeer.mall.activity.advert.service.ActivityAdvertModelService;

/**
 * ClassName: ActivityAdvertModelServiceImpl 
 * @Description: 活动模块实现类
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V2.2.0			2017-4-13			tuzhd			 活动模块实现类
 */
@Service
public class ActivityAdvertModelServiceImpl extends BaseServiceImpl implements ActivityAdvertModelService {
	
	@Autowired
	public ActivityAdvertModelMapper activityAdvertModelMapper;
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return activityAdvertModelMapper;
	}
	
	/**
	 * @Description: 根据模块序号及活动id查询模块信息
	 * @param modelNo 模块序号
	 * @param activityAdvertId 活动id
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
	public ActivityAdvertModel findModelByIdNo(int modelNo,String activityAdvertId){
		return activityAdvertModelMapper.findModelByIdNo(modelNo, activityAdvertId);
	}
	
	/**
	 * @Description: 新增模块信息
	 * @param modelNo 模块序号
	 * @param activityAdvertId 活动id
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
	@Transactional(rollbackFor = Exception.class)
	public int addModel(ActivityAdvertModel model){
		return activityAdvertModelMapper.add(model);
	}

	@Override
	public List<ActivityAdvertModel> findModelList(ActivityAdvertModel model) {
		
		return activityAdvertModelMapper.findModelList(model);
	}
	
	/**
	 * @Description: 删除关联模块信息by活动id
	 * @param activityAdvertId 活动id
	 * @return int  
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月19日
	 */
	public int deleteByActivityAdvertId(String activityAdvertId){
		return activityAdvertModelMapper.deleteByActivityAdvertId(activityAdvertId);
	}
}
