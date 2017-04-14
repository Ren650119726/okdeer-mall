/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月11日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.api.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDraw;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDrawVo;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeWeight;
import com.okdeer.mall.activity.prize.service.ActivityLuckDrawApi;
import com.okdeer.mall.activity.prize.service.ActivityLuckDrawService;
import com.okdeer.mall.activity.prize.service.ActivityPrizeWeightService;


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
	 * 抽奖设置Service
	 */
	@Autowired
	ActivityLuckDrawService activityLuckDrawService;
	
	/**
	 * 奖品权重表Service
	 */
	@Autowired
	ActivityPrizeWeightService activityPrizeWeightService;
	
	@Override
	public PageUtils<ActivityLuckDraw> findLuckDrawList(ActivityLuckDrawVo activityLuckDrawVo, int pageNumber,
			int pageSize) {
		return activityLuckDrawService.findLuckDrawList(activityLuckDrawVo, pageNumber, pageSize);
	}

	@Override
	public int findCountByName(ActivityLuckDrawVo activityLuckDrawVo) {
		
		return activityLuckDrawService.findCountByName(BeanMapper.map(activityLuckDrawVo, ActivityLuckDraw.class));
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addLuckDraw(ActivityLuckDrawVo activityLuckDrawVo) throws Exception {
		Date date = new Date();
		activityLuckDrawVo.setId(UuidUtils.getUuid());
		activityLuckDrawVo.setCreateTime(date);
		activityLuckDrawVo.setUpdateTime(date);
		activityLuckDrawVo.setDisabled(Disabled.valid);
		BeanMapper.map(activityLuckDrawVo, ActivityLuckDraw.class);
		activityLuckDrawService.add(BeanMapper.map(activityLuckDrawVo, ActivityLuckDraw.class));
		
		//获取奖品权重数据 并添加到权重表
		String prize = activityLuckDrawVo.getPrizeWeight();
		List<ActivityPrizeWeight> prizeWeightList = JSONArray.parseArray(prize, ActivityPrizeWeight.class);
		for(ActivityPrizeWeight prizeWeight : prizeWeightList){
			prizeWeight.setId(UuidUtils.getUuid());
			prizeWeight.setLuckDrawId(activityLuckDrawVo.getId());
			prizeWeight.setCreateTime(date);
			prizeWeight.setUpdateTime(date);
			prizeWeight.setDisabled(Disabled.valid);
			activityPrizeWeightService.add(prizeWeight);
		}
	}

	@Override
	public void closedLuckDraw(String ids) {
		String[] idArray = ids.split(",");
		activityLuckDrawService.closedLuckDraw(Arrays.asList(idArray));
	}

	@Override
	public ActivityLuckDraw findById(String id) throws Exception {
		
		return  activityLuckDrawService.findById(id);
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateLuckDraw(ActivityLuckDrawVo activityLuckDrawVo) throws Exception {
		Date date = new Date();
		//直接更新抽奖设置
		activityLuckDrawService.update(BeanMapper.map(activityLuckDrawVo, ActivityLuckDraw.class));
		
		//获取奖品权重数据 并添加到权重表
		String prize = activityLuckDrawVo.getPrizeWeight();
		List<ActivityPrizeWeight> prizeWeightList = JSONArray.parseArray(prize, ActivityPrizeWeight.class);
		
		for(ActivityPrizeWeight prizeWeight : prizeWeightList){
			
			int result = activityPrizeWeightService.update(prizeWeight);
			//为零说明为新加奖品 新增数据 
			if(result==0){
				prizeWeight.setId(UuidUtils.getUuid());
				prizeWeight.setLuckDrawId(activityLuckDrawVo.getId());
				prizeWeight.setCreateTime(date);
				prizeWeight.setUpdateTime(date);
				prizeWeight.setDisabled(Disabled.valid);
				activityPrizeWeightService.add(prizeWeight);
			}
		}
	}

}
