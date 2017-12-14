/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.prize.entity.ActivityAdvertDraw;

/**
 * ClassName: ActivityAdvertDrawServiceImpl 
 * @Description:  抽奖活动及H5活动关联持久化类
 * @author tuzhd
 * @date 2017年4月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		V2.2.0			2017-4-13			tuzhd			 抽奖活动及H5活动关联持久化类
 */

public interface ActivityAdvertDrawService extends IBaseService {
	/**
	 * @Description: 根据活动id及模板编号查询关联的抽奖活动
	 * @return ActivityAdvertSale  
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
    ActivityAdvertDraw findAdvertDrawByIdNo(int modelNo,String activityAdvertId);
    
	/**
	 * @Description: 删除关联抽奖信息by活动id
	 * @param activityAdvertId 活动id
	 * @return int  
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月19日
	 */
	int deleteByActivityAdvertId(String activityAdvertId);

	/**
	 * @Description: 条件查询广告活动与优惠关联条
	 * @param activityAdvertDraw
	 * @return   
	 * @author xuzq01
	 * @date 2017年12月13日
	 */
	List<ActivityAdvertDraw> findList(ActivityAdvertDraw activityAdvertDraw);
    
}
