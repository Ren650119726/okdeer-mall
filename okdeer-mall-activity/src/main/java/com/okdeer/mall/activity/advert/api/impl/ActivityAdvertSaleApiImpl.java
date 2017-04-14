/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertSale;
import com.okdeer.mall.activity.advert.service.ActivityAdvertSaleApi;
import com.okdeer.mall.activity.advert.service.ActivityAdvertSaleService;

/**
 * ClassName: ActivityAdvertSaleApiImpl 
 * @Description: 销售活动及H5活动关联对外实现类
 * @author tuzhd
 * @date 2017年4月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		V2.2.0			2017-4-13			tuzhd			 销售活动及H5活动关联对外实现类
 */
@Service(version="1.0.0")
public class ActivityAdvertSaleApiImpl implements ActivityAdvertSaleApi {
	@Autowired
	public ActivityAdvertSaleService activityAdvertSaleService;
	/**
	 * @Description: 根据活动id及模板编号查询关联的销售类型 
	 * @return ActivityAdvertSale  
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
    public ActivityAdvertSale findSaleByIdNo(String modelNo,String activityAdvertId){
    	return activityAdvertSaleService.findSaleByIdNo(modelNo, activityAdvertId);
    }
}
