package com.okdeer.mall.activity.discount.service.impl;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountRecord;
import com.okdeer.mall.activity.discount.service.ActivityDiscountRecordServiceApi;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountRecordMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountRecordService;

/**
 * @DESC: 
 * @author yangq
 * @date  2016-03-25 20:00:26
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.discount.service.ActivityDiscountRecordServiceApi")
class ActivityDiscountRecordServiceImpl implements ActivityDiscountRecordServiceApi, ActivityDiscountRecordService {

	@Resource
	private ActivityDiscountRecordMapper activityDiscountRecordMapper;

	@Override
	public void insertRecord(ActivityDiscountRecord record) throws Exception {
		activityDiscountRecordMapper.insertRecord(record);
	}

}