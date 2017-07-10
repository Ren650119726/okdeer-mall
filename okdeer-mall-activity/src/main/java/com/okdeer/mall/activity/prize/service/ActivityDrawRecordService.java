/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service;

import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.prize.entity.ActivityDrawRecordVo;

/**
 * ClassName: ActivityDrawRecordService 
 * @Description: 活动抽奖记录Service
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动抽奖记录Service
 */

public interface ActivityDrawRecordService extends IBaseService {
	/**
	 * @Description: 根据用户id及活动id查询抽奖次数
	 * @param userId 用户id
	 * @param luckDrawId 模板后为抽奖活动id
	 * @date 2017年1月12日
	 */
	int findCountByUserIdAndActivityId(String userId,String luckDrawId);
	/**
	 * @Description: 根据用户id及活动id查询抽奖次数
	 * @param userId 用户id
	 * @param ids 模板后为抽奖活动id
	 * @date 2017年1月12日
	 */
	int findCountByUserIdAndIds(String userId, List<String> ids);
	
	/**
	 * @Description:插入用户抽奖记录
	 * @param userId
	 * @param activityId
	 * @return int  
	 * @author tuzhd
	 * @date 2016年12月14日
	 */
	public int addDrawRecord(String userId,String activityId);

}
