package com.okdeer.mall.activity.coupons.api;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsReceiveStrategy;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsReceiveStrategyApi;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityCouponsReceiveStrategyApi")
public class ActivityCouponsReceiveStrategyApiImpl implements ActivityCouponsReceiveStrategyApi {

	@Resource
	private ActivityCouponsReceiveStrategy activityCouponsReceiveStrategy;
	
	@Override
	public ActivityCouponsRecord process(ActivityCouponsRecord couponsRecord, ActivityCoupons coupons) {
		activityCouponsReceiveStrategy.process(couponsRecord, coupons);
		return couponsRecord;
	}

}
