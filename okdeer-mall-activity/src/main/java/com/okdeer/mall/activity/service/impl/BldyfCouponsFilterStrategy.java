package com.okdeer.mall.activity.service.impl;

import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.order.enums.OrderTypeEnum;

@Service
public class BldyfCouponsFilterStrategy extends GenericCouponsFilterStrategy {

	@Override
	public boolean isOutOfLimitOrderType(FavourParamBO paramBo, ActivityCoupons couponsInfo) {
		return paramBo.getOrderType() != OrderTypeEnum.PHYSICAL_ORDER;
	}

}
