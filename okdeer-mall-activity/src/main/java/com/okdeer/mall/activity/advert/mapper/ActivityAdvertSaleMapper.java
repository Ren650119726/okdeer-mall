/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityAdvertSaleMapper.java
 * @Date 2017-04-12 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.advert.mapper;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertSale;
/**
 * ClassName: ActivityAdvertSaleMapper 
 * @Description: 销售活动及广告活动关联持久化类
 * @author tuzhd
 * @date 2017年4月13日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 		V2.2.0			2017-4-13			tuzhd			 销售活动及广告活动关联持久化类
 */
public interface ActivityAdvertSaleMapper extends IBaseMapper {

	/**
	 * @Description: 根据活动id及模板编号查询关联的销售类型 
	 * @return ActivityAdvertSale  
	 * @author tuzhd
	 * @date 2017年4月13日
	 */
    public ActivityAdvertSale findSaleByIdNo(String modelNo,String activityAdvertId);
}