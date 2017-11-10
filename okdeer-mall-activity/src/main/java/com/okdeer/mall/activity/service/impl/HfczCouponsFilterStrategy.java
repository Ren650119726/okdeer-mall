package com.okdeer.mall.activity.service.impl;

import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.order.enums.OrderTypeEnum;

@Service("hfczCouponsFilterStrategy")
public class HfczCouponsFilterStrategy extends GenericCouponsFilterStrategy {

	@Override
	public boolean isOutOfLimitOrderType(FavourParamBO paramBo, ActivityCoupons couponsInfo) {
		return paramBo.getOrderType() != OrderTypeEnum.TRAFFIC_PAY_ORDER
				&& paramBo.getOrderType() != OrderTypeEnum.PHONE_PAY_ORDER;
	}

}
