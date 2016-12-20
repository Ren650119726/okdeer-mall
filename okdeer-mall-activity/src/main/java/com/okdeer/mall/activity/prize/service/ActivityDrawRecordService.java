/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service;

import com.okdeer.base.service.IBaseService;

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
