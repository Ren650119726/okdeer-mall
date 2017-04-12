/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月11日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDraw;

/**
 * ClassName: ActivityLuckDrawService 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年4月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface ActivityLuckDrawService extends IBaseService {

	/**
	 * @Description: TODO
	 * @param activityLuckDraw
	 * @param pageNumber
	 * @param pageSize
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月11日
	 */
	PageUtils<ActivityLuckDraw> findLuckDrawList(ActivityLuckDraw activityLuckDraw, int pageNumber, int pageSize);

	/**
	 * @Description: TODO
	 * @param activityLuckDraw
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月11日
	 */
	int findCountByName(ActivityLuckDraw activityLuckDraw);

	/**
	 * @Description: TODO
	 * @param ids   
	 * @author xuzq01
	 * @date 2017年4月11日
	 */
	void closedLuckDraw(String ids);


}
