/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.service;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertCoupons;

/**
 * ClassName: ActivityAdvertCouponsMapper 
 * @Description: 代金券活动及H5活动实现类
 * @author tuzhd
 * @date 2017年4月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		V2.2.0			2017-4-13			tuzhd			 代金券活动及H5活动实现
 */

public interface ActivityAdvertCouponsService extends IBaseService {
	/**
	 * @Description: 根据活动id及模板编号查询关联的代金券活动
	 * @return ActivityAdvertSale  
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
    public ActivityAdvertCoupons findAdvertCouponsByIdNo(String modelNo,String activityAdvertId);
    
    /**
     * @Description: 添加代金券关联信息
     * @param coupons 代金券信息
     * @return int  
     * @author tuzhd
     * @date 2017年4月17日
     */
    public int addCoupons(ActivityAdvertCoupons coupons);
}
