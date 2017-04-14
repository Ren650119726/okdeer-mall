/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月11日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service;

import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDraw;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDrawVo;

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
	 * @param activityLuckDrawVo
	 * @param pageNumber
	 * @param pageSize
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月11日
	 */
	PageUtils<ActivityLuckDraw> findLuckDrawList(ActivityLuckDrawVo activityLuckDrawVo, int pageNumber, int pageSize);

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
	 * @param list   
	 * @author xuzq01
	 * @date 2017年4月11日
	 */
	void closedLuckDraw(List<String> list);


}
