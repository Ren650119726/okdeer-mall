/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月11日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.service;

import java.util.Date;
import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDraw;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDrawVo;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;

/**
 * ClassName: ActivityLuckDrawService 
 * @Description: 活动抽奖记录Service
 * @author xuzq01
 * @date 2017年4月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v2.2.0			2017年4月11日		xuzq01				活动抽奖记录Service
 */

public interface ActivityLuckDrawService extends IBaseService {

	/**
	 * @Description: 获取抽奖模板列表
	 * @param activityLuckDrawVo
	 * @param pageNumber
	 * @param pageSize
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月11日
	 */
	PageUtils<ActivityLuckDraw> findLuckDrawList(ActivityLuckDrawVo activityLuckDrawVo, int pageNumber, int pageSize);

	/**
	 * @Description: 校验是否有重复名称
	 * @param name
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月11日
	 */
	int findCountByName(String name);

	/**
	 * @Description: 更新抽奖设置状态
	 * @param list   
	 * @author xuzq01
	 * @date 2017年4月11日
	 */
	void updateLuckDrawStatus(List<String> list,int status);

	/**
	 * @Description: 定时器查询抽奖设置列表
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月18日
	 */
	List<ActivityLuckDraw> listByJob();

	/**
	 * @Description: 定时器批量更新状态
	 * @param id
	 * @param end
	 * @param updateUserId
	 * @param updateTime   
	 * @author xuzq01
	 * @date 2017年4月18日
	 */
	void updateBatchStatus(String id, SeckillStatusEnum end, String updateUserId, Date updateTime);

	/**
	 * @Description: 通过模块id获取关联的抽奖设置
	 * @param id
	 * @param activityAdvertId
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月18日
	 */
	ActivityLuckDraw findLuckDrawByModelId(String id, String activityAdvertId);

	/**
	 * @Description: 关联广告活动获取抽奖设置列表
	 * @param activityLuckDrawVo
	 * @param pageNumber
	 * @param pageSize
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月20日
	 */
	PageUtils<ActivityLuckDraw> findLuckDrawSelectList(ActivityLuckDrawVo activityLuckDrawVo, int pageNumber,
			int pageSize);

}
