/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeWeight;
import com.okdeer.mall.activity.prize.mapper.ActivityPrizeWeightMapper;
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
@Service
public class ActivityPrizeWeightServiceImpl extends BaseServiceImpl implements ActivityPrizeWeightService{

	/**
	 * 活动奖品权重表mapper
	 */
	@Autowired
	ActivityPrizeWeightMapper activityPrizeWeightMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityPrizeWeightMapper;
	}

	@Override
	public List<ActivityPrizeWeight> findAll() {
		return activityPrizeWeightMapper.findAll();
	}

}
