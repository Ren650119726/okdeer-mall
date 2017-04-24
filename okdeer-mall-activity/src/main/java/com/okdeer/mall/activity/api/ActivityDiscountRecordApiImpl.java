package com.okdeer.mall.activity.api;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountRecord;
import com.okdeer.mall.activity.discount.service.ActivityDiscountRecordApi;
import com.okdeer.mall.activity.discount.service.ActivityDiscountRecordService;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.discount.service.ActivityDiscountRecordApi")
public class ActivityDiscountRecordApiImpl implements ActivityDiscountRecordApi {
	
	@Resource
	private ActivityDiscountRecordService activityDiscountRecordService;

	@Override
	public void add(ActivityDiscountRecord record) throws Exception {
		activityDiscountRecordService.add(record);
	}

}
