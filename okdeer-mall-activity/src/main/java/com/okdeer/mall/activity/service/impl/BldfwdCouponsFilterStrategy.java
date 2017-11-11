package com.okdeer.mall.activity.service.impl;

import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;

@Service("bldfwdCouponsFilterStrategy")
public class BldfwdCouponsFilterStrategy extends GenericCouponsFilterStrategy {

	@Override
	public boolean isOutOfLimitOrderType(FavourParamBO paramBo,
			ActivityCoupons couponsInfo) {
		OrderResourceEnum orderChannel = paramBo.getChannel();
		return (paramBo.getOrderType() != OrderTypeEnum.PHYSICAL_ORDER 
				|| orderChannel == OrderResourceEnum.MEMCARD
				|| orderChannel == OrderResourceEnum.SWEEP 
				|| orderChannel == OrderResourceEnum.WECHAT_MIN)
				&& paramBo.getOrderType() != OrderTypeEnum.SERVICE_ORDER
				&& paramBo.getOrderType() != OrderTypeEnum.SERVICE_STORE_ORDER
				&& paramBo.getOrderType() != OrderTypeEnum.STORE_CONSUME_ORDER;
	}
}
