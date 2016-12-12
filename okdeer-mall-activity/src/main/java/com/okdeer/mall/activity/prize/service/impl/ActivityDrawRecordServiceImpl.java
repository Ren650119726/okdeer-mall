/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.prize.mapper.ActivityDrawRecordMapper;
import com.okdeer.mall.activity.prize.service.ActivityDrawRecordService;

/**
 * ClassName: ActivityDrawRecordServiceImpl 
 * @Description: 活动抽奖记录Service
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动抽奖记录Service
 */
@Service
public class ActivityDrawRecordServiceImpl extends BaseServiceImpl implements ActivityDrawRecordService {
	
	/**
	 * 活动抽奖记录mapper
	 */
	@Autowired
	ActivityDrawRecordMapper activityDrawRecordMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityDrawRecordMapper;
	}
	
	@Override
	public int findCountByUserIdAndActivityId(String userId, String activityId) {
		return activityDrawRecordMapper.findCountByUserIdAndActivityId(userId, activityId);
	}

}
