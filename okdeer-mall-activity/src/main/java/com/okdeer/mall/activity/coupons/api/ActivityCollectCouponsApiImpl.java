
package com.okdeer.mall.activity.coupons.api;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsApi;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;
import com.okdeer.mall.activity.dto.ActivityCollectCouponsDto;
import com.okdeer.mall.activity.dto.TakeActivityCouponParamDto;
import com.okdeer.mall.activity.dto.TakeActivityCouponResultDto;

@Service(version = "1.0.0")
public class ActivityCollectCouponsApiImpl implements ActivityCollectCouponsApi {

	@Autowired
	private ActivityCollectCouponsService activityCollectCouponsService;
	
	@Override
	public ActivityCollectCouponsDto findById(String id) {
		ActivityCollectCoupons activityCollectCoupons = activityCollectCouponsService.get(id);
		return BeanMapper.map(activityCollectCoupons, ActivityCollectCouponsDto.class);
	}

	@Override
	public TakeActivityCouponResultDto takeActivityCoupon(TakeActivityCouponParamDto activityCouponParamDto) {
		
		return activityCollectCouponsService.takeActivityCoupon(activityCouponParamDto);
	}

}
