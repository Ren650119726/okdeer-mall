/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月11日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDraw;
import com.okdeer.mall.activity.prize.mapper.ActivityLuckDrawMapper;
import com.okdeer.mall.activity.prize.service.ActivityLuckDrawService;


/**
 * ClassName: ActivityLuckDrawServiceImpl 
 * @Description: 抽奖模板service
 * @author xuzq01
 * @date 2017年4月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class ActivityLuckDrawServiceImpl extends BaseServiceImpl implements ActivityLuckDrawService {

	/**
	 * 活动抽奖记录mapper
	 */
	@Autowired
	ActivityLuckDrawMapper activityLuckDrawMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityLuckDrawMapper;
	}
	
	@Override
	public PageUtils<ActivityLuckDraw> findLuckDrawList(ActivityLuckDraw activityLuckDraw, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityLuckDraw> result = activityLuckDrawMapper.findPrizeRecordList(activityLuckDraw);
		return new PageUtils<ActivityLuckDraw>(result);
	}

	@Override
	public int findCountByName(ActivityLuckDraw activityLuckDraw) {
		return activityLuckDrawMapper.findCountByName(activityLuckDraw);
	}

	@Override
	public void closedLuckDraw(String ids) {
		activityLuckDrawMapper.closedLuckDraw(ids);
		
	}

}
