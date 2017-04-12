/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.api.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeRecordVo;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeWeight;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeWeightVo;
import com.okdeer.mall.activity.prize.service.ActivityPrizeWeightApi;
import com.okdeer.mall.activity.prize.service.ActivityPrizeWeightService;

/**
 * ClassName: ActivityPrizeWeightApiImpl 
 * @Description: 活动奖品权重表Service实现类
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动奖品权重表Service实现类
 */
@Service(version="1.0.0")
public class ActivityPrizeWeightApiImpl implements ActivityPrizeWeightApi{

	/**
	 * 活动奖品权重表Service
	 */
	@Autowired
	ActivityPrizeWeightService activityPrizeWeightService;

	/**
	 * 根据活动id查询所有奖品的比重信息 按顺序查询 顺序与奖品对应 
	 * @param activityId 活动id
	 * @return List<ActivityPrizeWeight>  
	 * @author tuzhd
	 * @date 2016年12月14日
	 */
	@Override
	public List<ActivityPrizeWeight> findPrizesByactivityId(String activityId){
		return activityPrizeWeightService.findPrizesByactivityId(activityId);
	}

	@Override
	public PageUtils<ActivityPrizeWeightVo> findPrizeWeightList(ActivityPrizeWeightVo activityPrizeWeightVo,
			int pageNumber, int pageSize) {
		return activityPrizeWeightService.findPrizeWeightList(activityPrizeWeightVo, pageNumber, pageSize);
	}

}
