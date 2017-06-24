/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.api.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeRecord;
import com.okdeer.mall.activity.prize.entity.ActivityPrizeRecordVo;
import com.okdeer.mall.activity.prize.service.ActivityPrizeRecordApi;
import com.okdeer.mall.activity.prize.service.ActivityPrizeRecordService;

/**
 * ClassName: ActivityPrizeRecordApiImpl 
 * @Description: 中奖记录表Service实现类
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				中奖记录表Service实现类
 */
@Service(version="1.0.0")
public class ActivityPrizeRecordApiImpl implements ActivityPrizeRecordApi{
	
	/**
	 * 中奖记录表Service
	 */
	@Autowired
	ActivityPrizeRecordService activityPrizeRecordService;
	
	/**
	 * @Description: 用户id获奖奖品列表
	 * @param userId
	 * @param activityId 活动id H5活动id 以后会是对应
	 * @return   
	 * @return List<ActivityPrizeRecord>  
	 * @throws
	 * @author tuzhd
	 * @date 2016年12月15日
	 */
	@Override
	public List<ActivityPrizeRecordVo> findByUserId(String userId,String activityId) {
		return activityPrizeRecordService.findByUserId(userId,activityId);
	}

	@Override
	public List<ActivityPrizeRecord> findPrizeRecord() {
		return activityPrizeRecordService.findPrizeRecord();
	}
	
	/**
	 * @Description: 根据奖品记录id查询记录
	 * @param id 奖品记录id
	 * @return List<ActivityPrizeRecord>  
	 * @throws Exception 
	 * @throws
	 * @author tuzhd
	 * @date 2017年6月20日
	 */
	@Override
	public ActivityPrizeRecord findPrizeById(String id) throws Exception {
		return activityPrizeRecordService.findById(id);
	}

	@Override
	public int findCountByPrizeId(String prizeId) {
		return activityPrizeRecordService.findCountByPrizeId(prizeId);
	}

	@Override
	public PageUtils<ActivityPrizeRecordVo> findPrizeRecordList(ActivityPrizeRecordVo activityPrizeRecordVo,
			int pageNumber, int pageSize) {
		return activityPrizeRecordService.findPrizeRecordList(activityPrizeRecordVo, pageNumber, pageSize);
	}
	
	/**
	 * @Description: 更新中奖记录
	 * @param record  中奖对象
	 * @return void  
	 * @throws Exception 
	 * @throws
	 * @author tuzhd
	 * @date 2017年6月20日
	 */
	public void updatePrizeRecord(ActivityPrizeRecord record) throws Exception{
		activityPrizeRecordService.update(record);
	}
	
	/**
	 * @Description: 批量更新发放状态
	 * @param map   
	 * @return void  
	 * @author tuzhd
	 * @date 2017年6月20日
	 */
	public void updateBathOffer(Map<String,Object> map){
		activityPrizeRecordService.updateBathOffer(map);
	}

}
