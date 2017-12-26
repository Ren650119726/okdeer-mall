/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年12月20日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.prize.entity.ActivityDrawRecord;
import com.okdeer.mall.activity.prize.service.ActivityDrawRecordApi;
import com.okdeer.mall.activity.prize.service.ActivityDrawRecordService;


/**
 * ClassName: ActivityDrawRecordApiImpl 
 * @Description: 抽奖记录表api实现类
 * @author xuzq01
 * @date 2017年12月20日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version="1.0.0")
public class ActivityDrawRecordApiImpl implements ActivityDrawRecordApi {
	/**
	 * 抽奖记录表service
	 */
	@Autowired
	ActivityDrawRecordService activityDrawRecordService;

	@Override
	public void add(ActivityDrawRecord activityDrawRecord) throws Exception {
		activityDrawRecordService.add(activityDrawRecord);
	}
	
}
