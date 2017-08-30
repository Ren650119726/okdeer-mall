/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年8月23日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.discount.mapper;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountStore;

/**
 * ClassName: ActivityDiscountStoreMapper 
 * @Description: 此接口和表已经没有使用 活动关联店铺由activity_business_rel替代
 * @author xuzq01
 * @date 2017年8月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Deprecated
public interface ActivityDiscountStoreMapper extends IBaseMapper {

	/**
	 * @Description: 通过活动id和店铺id查询对象
	 * @param id
	 * @param storeId   
	 * @author xuzq01
	 * @date 2017年8月23日
	 */
	ActivityDiscountStore findDiscountStore(@Param("discountId") String discountId,@Param("storeId") String storeId);

}
