/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.advert.bo.ActivityAdvertStoreBo;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertStore;

/**
 * ClassName: ActivityAdvertStoreMapper 
 * @Description: 关联店铺信息实现接口类
 * @author tuzhd
 * @date 2017年4月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V2.2.2			2017-4-18			tuzhd				 关联店铺信息实现接口类
 */

public interface ActivityAdvertStoreService extends IBaseService {
	/**
	 * @Description: 批量添加关联店铺信息
	 * @param list   要插入的关联店铺集合
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月17日
	 */
	void saveBatch(List<ActivityAdvertStore> list);
	
	/**
	 * @Description:查询店铺信息根据活动id
	 * @param activityAdverId
	 * @return List<ActivityAdvertStoreDto>  
	 * @author tuzhd
	 * @date 2017年4月19日
	 */
	List<ActivityAdvertStoreBo> findShopByAdvertId(String activityAdverId);
	
	/**
	 * @Description: 删除关联店铺信息by活动id
	 * @param activityAdvertId 活动id
	 * @return int  
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月19日
	 */
	int deleteByActivityAdvertId(String activityAdvertId);
}
