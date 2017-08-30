/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityCollectCouponsRecordMapper.java
 * @Date 2017-08-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.coupons.mapper;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.coupons.bo.ActivityCollectCouponsRecordParamBo;

public interface ActivityCollectCouponsRecordMapper extends IBaseMapper {

	int findCountByParams(ActivityCollectCouponsRecordParamBo activityCollectCouponsRecordParamBo);
	
}