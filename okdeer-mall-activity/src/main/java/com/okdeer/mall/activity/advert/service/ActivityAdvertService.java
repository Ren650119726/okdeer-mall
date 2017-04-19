/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.service;

import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.advert.dto.ActivityAdvertDto;
import com.okdeer.mall.activity.advert.entity.ActivityAdvert;

/**
 * ClassName: ActivityAdvertService 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface ActivityAdvertService extends IBaseService {

	/**
	 * @Description: TODO
	 * @param activityAdvert
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月12日
	 */
	int findCountByName(String advertName);

	/**
	 * @Description: TODO
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
	 * @Description: TODO
	 * @param asList
	 * @return   
	 * @author xuzq01
	 * @date 2017年4月17日
	 */
	List<ActivityAdvert> findActivityListByStatus(List<String> statusList);

}
