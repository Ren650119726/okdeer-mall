/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月14日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.api.impl;


import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertCoupons;
import com.okdeer.mall.activity.prize.entity.ActivityAdvertDraw;
import com.okdeer.mall.activity.prize.mapper.ActivityAdvertDrawMapper;
import com.okdeer.mall.activity.prize.service.ActivityAdvertDrawApi;
import com.okdeer.mall.activity.prize.service.ActivityAdvertDrawService;

/**
 * ClassName: ActivityAdvertDrawApiImpl 
 * @Description:  抽奖活动及H5活动关联 对外接口类
 * @author tuzhd
 * @date 2017年4月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		V2.2.0			2017-4-13			tuzhd			抽奖活动及H5活动关联 对外接口类
 */
@Service(version="1.0.0")
public class ActivityAdvertDrawApiImpl implements ActivityAdvertDrawApi {
	
	/**
	 * H5活动与特惠或低价关联service
	 */
	@Autowired
	ActivityAdvertDrawService activityAdvertDrawService;
	
	/**
     * @Description: 添加代金券关联信息
     * @param coupons 代金券信息
     * @return int  
     * @author tuzhd
	 * @throws Exception 
     * @date 2017年4月17日
     */
    public int addAdvertDraw(ActivityAdvertDraw draw) throws Exception{
    	return activityAdvertDrawService.add(draw);
    }
    
    /**
	 * @Description: 根据活动id及模板编号查询关联的抽奖活动
	 * @return ActivityAdvertSale  
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
    public ActivityAdvertDraw findAdvertDrawByIdNo(int modelNo,String activityAdvertId){
    	return activityAdvertDrawService.findAdvertDrawByIdNo(modelNo, activityAdvertId);
    }
	
}
