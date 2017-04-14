/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.api.impl;


import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.prize.mapper.ActivityAdvertDrawMapper;
import com.okdeer.mall.activity.prize.service.ActivityAdvertDrawApi;
import com.okdeer.mall.activity.prize.service.ActivityAdvertDrawService;

/**
 * ClassName: ActivityAdvertDrawApiImpl 
 * @Description: 广告活动与特惠或低价关联api实现
 * @author xuzq01
 * @date 2017年4月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version="1.0.0")
public class ActivityAdvertDrawApiImpl implements ActivityAdvertDrawApi {
	
	/**
	 * 广告活动与特惠或低价关联service
	 */
	@Autowired
	ActivityAdvertDrawService activityAdvertDrawService;
	
}
