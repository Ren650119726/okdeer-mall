/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityCouponsStoreMapper.java
 * @Date 2017-06-23 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.coupons.mapper;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsStore;

public interface ActivityCouponsStoreMapper extends IBaseMapper {

	ActivityCouponsStore findByStoreIdAndCouponsId(@Param("storeId")String storeId,@Param("couponsId")String couponsId);
}