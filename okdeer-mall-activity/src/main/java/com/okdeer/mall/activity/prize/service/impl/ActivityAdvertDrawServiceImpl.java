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
import com.okdeer.mall.activity.prize.mapper.ActivityAdvertDrawMapper;
import com.okdeer.mall.activity.prize.mapper.ActivityDrawRecordMapper;
import com.okdeer.mall.activity.prize.service.ActivityAdvertDrawService;


/**
 * ClassName: ActivityAdvertDrawServiceImpl 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年4月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class ActivityAdvertDrawServiceImpl extends BaseServiceImpl implements ActivityAdvertDrawService {

	/**
	 * 广告活动与特惠或低价关联mapper
	 */
	@Autowired
	ActivityAdvertDrawMapper activityAdvertDrawMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityAdvertDrawMapper;
	}

}
