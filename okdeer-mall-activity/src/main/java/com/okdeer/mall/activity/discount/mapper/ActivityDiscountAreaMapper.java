/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年8月23日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.discount.mapper;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountArea;

/**
 * ClassName: ActivityDiscountAreaMapper 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年8月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface ActivityDiscountAreaMapper extends IBaseMapper {

	/**
	 * @Description: 通过区域id和区域类型查询
	 * @param areaId
	 * @param discountId   
	 * @author xuzq01
	 * @date 2017年8月23日
	 */
	ActivityDiscountArea findByAreaId(@Param("areaId") String areaId, @Param("discountId") String discountId);

}
