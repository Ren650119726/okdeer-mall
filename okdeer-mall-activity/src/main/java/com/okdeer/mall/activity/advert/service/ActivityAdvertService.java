/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.service;

import java.util.Date;
import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.advert.entity.ActivityAdvert;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;

/**
 * ClassName: ActivityAdvertService 
 * @Description: 广告活动service
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *	V2.2.2			2017年4月12日				xuzq01			广告活动service
 */

public interface ActivityAdvertService extends IBaseService {

	/**
	 * @Description: 名称查询 用于校验
	 * @param activityAdvert
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月12日
	 */
	int findCountByName(String advertName);

	/**
	 * @Description: 获取广告列表
	 * @param activityAdvert
	 * @param pageNumber
	 * @param pageSize
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月12日
	 */
	PageUtils<ActivityAdvert> findActivityAdvertList(ActivityAdvert activityAdvert, int pageNumber, int pageSize);
	
	/**
	 * @Description: 保存新增活动信息
	 * @param activityAdvertDto 活动对象
	 * @param userId    用户id
	 * @return void  
	 * @author tuzhd
	 * @date 2017年4月18日
	 */
	public void addActivityAdvert(ActivityAdvert activityAdvert)throws Exception;

	/**
	 * @Description: 根据状态获取列表
	 * @param asList
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月17日
	 */
	List<ActivityAdvert> findActivityListByStatus(List<String> statusList);

	/**
	 * @Description: 定时器查询未开始和进行中状态的广告活动列表
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月20日
	 */
	List<ActivityAdvert> listByJob();

	/**
	 * @Description: 定时器批量更新广告活动状态
	 * @param id
	 * @param ing
	 * @param updateUserId
	 * @param updateTime   
	 * @author xuzq01
	 * @date 2017年4月20日
	 */
	void updateBatchStatus(String id, SeckillStatusEnum status, String updateUserId, Date updateTime);

}
