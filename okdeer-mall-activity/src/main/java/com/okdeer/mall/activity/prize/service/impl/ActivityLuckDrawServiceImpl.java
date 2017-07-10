/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月11日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDraw;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDrawVo;
import com.okdeer.mall.activity.prize.mapper.ActivityLuckDrawMapper;
import com.okdeer.mall.activity.prize.service.ActivityLuckDrawService;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.common.utils.RobotUserUtil;


/**
 * ClassName: ActivityLuckDrawServiceImpl 
 * @Description: 抽奖模板service
 * @author xuzq01
 * @date 2017年4月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	   V2.2.0         2017年4月11日                       xuzq01          	    抽奖模型管理service
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
	public PageUtils<ActivityLuckDraw> findLuckDrawList(ActivityLuckDrawVo activityLuckDrawVo, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityLuckDraw> result = activityLuckDrawMapper.findPrizeRecordList(activityLuckDrawVo);
		if(result == null){
			result = new ArrayList<ActivityLuckDraw>();
		}
		return new PageUtils<ActivityLuckDraw>(result);
	}
	
	@Override
	public List<ActivityLuckDraw> findLuckDrawList(ActivityLuckDrawVo activityLuckDrawVo) {
		List<ActivityLuckDraw> result = activityLuckDrawMapper.findPrizeRecordList(activityLuckDrawVo);
		if(result == null){
			result = new ArrayList<ActivityLuckDraw>();
		}
		return result;
	}

	@Override
	public int findCountByName(String name) {
		return activityLuckDrawMapper.findCountByName(name);
	}

	@Override
	public void updateLuckDrawStatus(List<String> ids,int status) {
		Date updateTime = new Date();
		String updateUserId = RobotUserUtil.getRobotUser().getId();
		activityLuckDrawMapper.updateLuckDrawStatus(ids,status,updateTime,updateUserId);
		
	}

	@Override
	public List<ActivityLuckDraw> listByJob() {
		return activityLuckDrawMapper.listByJob();
	}

	@Override
	public void updateBatchStatus(String id, SeckillStatusEnum status, String updateUserId, Date updateTime) {
		ActivityLuckDraw draw = new ActivityLuckDraw();
		draw.setId(id);
		draw.setStatus(status);
		draw.setUpdateTime(updateTime);
		draw.setUpdateUserId(updateUserId);
		activityLuckDrawMapper.updateBatchStatus(draw);
	}

	@Override
	public ActivityLuckDraw findLuckDrawByModelId(String modelId, String activityAdvertId) {
		return activityLuckDrawMapper.findLuckDrawByModelId(modelId,activityAdvertId);
	}

	@Override
	public PageUtils<ActivityLuckDraw> findLuckDrawSelectList(ActivityLuckDrawVo activityLuckDrawVo, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityLuckDraw> result = activityLuckDrawMapper.findLuckDrawSelectList(activityLuckDrawVo);
		return new PageUtils<ActivityLuckDraw>(result);
	}

}
