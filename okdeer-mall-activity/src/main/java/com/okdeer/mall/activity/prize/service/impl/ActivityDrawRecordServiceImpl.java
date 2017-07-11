/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.prize.entity.ActivityDrawRecord;
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
	/**
	 * 根据用户id及活动id查询抽奖次数
	 * (non-Javadoc)
	 */
	@Override
	public int findCountByUserIdAndActivityId(String userId, String luckDrawId) {
		//获取当前抽奖活动id集合
		List<String> ids = new ArrayList<String>();
		ids.add(luckDrawId);
		return activityDrawRecordMapper.findCountByUserIdAndActivityId(userId, ids);
	}
	
	/**
	 * 根据用户id及抽奖活动id查询抽奖次数
	 * (non-Javadoc)
	 */
	@Override
	public int findCountByUserIdAndIds(String userId, List<String> ids) {
		return activityDrawRecordMapper.findCountByUserIdAndActivityId(userId, ids);
	}
	
	
	
	
	
	/**
	 * @Description:插入用户抽奖记录
	 * @param userId
	 * @param activityId
	 * @return int  
	 * @author tuzhd
	 * @date 2016年12月14日
	 */
	@Override
	public int addDrawRecord(String userId,String activityId) {
		ActivityDrawRecord r = new ActivityDrawRecord();
		r.setId(UuidUtils.getUuid());
		r.setActivityAdvertId(activityId);
		r.setUserId(userId);
		r.setCreateTime(new Date());
		r.setDisabled(0);
		return activityDrawRecordMapper.add(r);
	}

}
