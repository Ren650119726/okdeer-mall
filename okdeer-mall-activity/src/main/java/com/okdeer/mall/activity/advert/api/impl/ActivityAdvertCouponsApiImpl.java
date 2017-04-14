/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertCoupons;
import com.okdeer.mall.activity.advert.service.ActivityAdvertCouponsApi;
import com.okdeer.mall.activity.advert.service.ActivityAdvertCouponsService;

/**
 * ClassName: ActivityAdvertCouponsMapper 
 * @Description: 代金券活动及H5活动对外实现类
 * @author tuzhd
 * @date 2017年4月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		V2.2.0			2017-4-13			tuzhd			 代金券活动及H5活动对外实现
 */

@Service(version="1.0.0")
public class ActivityAdvertCouponsApiImpl implements ActivityAdvertCouponsApi {
	@Autowired
	public ActivityAdvertCouponsService activityAdvertCouponsService;
	/**
	 * @Description: 根据活动id及模板编号查询关联的代金券活动
	 * @return ActivityAdvertSale  
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
    public ActivityAdvertCoupons findAdvertCouponsByIdNo(String modelNo,String activityAdvertId){
    	return  activityAdvertCouponsService.findAdvertCouponsByIdNo(modelNo, activityAdvertId);
    }
}
