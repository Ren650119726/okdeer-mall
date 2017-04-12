/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.prize.entity.ActivityDrawRecordVo;

/**
 * ClassName: ActivityDrawRecordService 
 * @Description: TODO
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动抽奖记录mapper
 */

public interface ActivityDrawRecordService extends IBaseService {
	/**
	 * @Description: 根据用户id及活动id查询抽奖次数
	 * @param userId 用户id
	 * @param activityId 活动id
	 * @date 2017年1月12日
	 */
	int findCountByUserIdAndActivityId(String userId,String activityId);
	
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
