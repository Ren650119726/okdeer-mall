/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityLowPriceMapper.java
 * @Date 2016-12-29 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.lowprice.mapper;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.lowprice.entity.ActivityLowPrice;

public interface ActivityLowPriceMapper extends IBaseMapper {
	
	/**
	 * 
	 * @Description: 根据店铺id查询低价活动
	 * @param storeId           店铺id
	 * @return ActivityLowPrice 低价活动信息
	 * @author tangy
	 * @date 2016年12月29日
	 */
	ActivityLowPrice findByStoreId(@Param("storeId") String storeId);

}