/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.prize.entity.ActivityAdvertDraw;
import com.okdeer.mall.activity.prize.mapper.ActivityAdvertDrawMapper;
import com.okdeer.mall.activity.prize.service.ActivityAdvertDrawService;


/**
 * ClassName: ActivityAdvertDrawServiceImpl 
 * @Description:  抽奖活动及H5活动关联持久化类
 * @author tuzhd
 * @date 2017年4月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		V2.2.0			2017-4-13			tuzhd			 抽奖活动及H5活动关联持久化类
 */
@Service
public class ActivityAdvertDrawServiceImpl extends BaseServiceImpl implements ActivityAdvertDrawService {

	/**
	 * H5活动与特惠或低价关联mapper
	 */
	@Autowired
	ActivityAdvertDrawMapper activityAdvertDrawMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityAdvertDrawMapper;
	}
	
	/**
	 * @Description: 根据活动id及模板编号查询关联的抽奖活动
	 * @return ActivityAdvertSale  
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
    public ActivityAdvertDraw findAdvertDrawByIdNo(String modelNo,String activityAdvertId){
    	return activityAdvertDrawMapper.findAdvertDrawByIdNo(modelNo, activityAdvertId);
    }
	
}
