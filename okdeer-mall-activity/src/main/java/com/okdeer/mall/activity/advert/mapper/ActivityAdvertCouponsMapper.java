/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityAdvertCouponsMapper.java
 * @Date 2017-04-12 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.advert.mapper;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertCoupons;
/**
 * ClassName: ActivityAdvertCouponsMapper 
 * @Description: 代金券活动及H5活动持久化类
 * @author tuzhd
 * @date 2017年4月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		V2.2.0			2017-4-13			tuzhd			 代金券活动及H5活动实现类
 */
public interface ActivityAdvertCouponsMapper extends IBaseMapper {
	/**
	 * @Description: 根据活动id及模板编号查询关联的代金券活动
	 * @return ActivityAdvertSale  
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
    public ActivityAdvertCoupons findAdvertCouponsByIdNo(String modelNo,String activityAdvertId);
    
    /**
	 * @Description: 删除关联代金券信息by活动id
	 * @param activityAdvertId 活动id
	 * @return int  
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月19日
	 */
	public int deleteByActivityAdvertId(String activityAdvertId);
}