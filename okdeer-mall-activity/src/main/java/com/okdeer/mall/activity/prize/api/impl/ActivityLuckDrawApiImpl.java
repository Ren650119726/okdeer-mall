/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月11日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.api.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDraw;
import com.okdeer.mall.activity.prize.service.ActivityLuckDrawApi;
import com.okdeer.mall.activity.prize.service.ActivityLuckDrawService;


/**
 * ClassName: ActivityLuckDrawApiImpl 
 * @Description: 抽奖活动设置表
 * @author xuzq01
 * @date 2017年4月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version="1.0.0")
public class ActivityLuckDrawApiImpl implements ActivityLuckDrawApi {
	/**
	 * 中奖记录表Service
	 */
	@Autowired
	ActivityLuckDrawService activityLuckDrawService;
	
	@Override
	public PageUtils<ActivityLuckDraw> findLuckDrawList(ActivityLuckDraw activityLuckDraw, int pageNumber,
			int pageSize) {
		return activityLuckDrawService.findLuckDrawList(activityLuckDraw, pageNumber, pageSize);
	}

	@Override
	public int findCountByName(ActivityLuckDraw activityLuckDraw) {
		
		return activityLuckDrawService.findCountByName(activityLuckDraw);
	}

	@Override
	public void addLuckDraw(ActivityLuckDraw activityLuckDraw) throws Exception {
		Date date = new Date();
		activityLuckDraw.setId(UuidUtils.getUuid());
		activityLuckDraw.setCreateTime(date);
		activityLuckDraw.setUpdateTime(date);
		activityLuckDraw.setDisabled(Disabled.valid);
		activityLuckDrawService.add(activityLuckDraw);
	}

	@Override
	public void closedLuckDraw(String ids) {
		activityLuckDrawService.closedLuckDraw(ids);
		
	}

	@Override
	public ActivityLuckDraw findById(String id) throws Exception {
		return activityLuckDrawService.findById(id);
	}

}
