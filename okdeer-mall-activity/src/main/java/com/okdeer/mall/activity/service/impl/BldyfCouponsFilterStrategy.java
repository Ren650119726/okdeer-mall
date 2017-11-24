package com.okdeer.mall.activity.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;

@Service
public class BldyfCouponsFilterStrategy extends GenericCouponsFilterStrategy {

	@Override
	public boolean isOutOfLimitOrderType(FavourParamBO paramBo, ActivityCoupons couponsInfo) {
		OrderResourceEnum orderChannel = paramBo.getChannel();
		return paramBo.getOrderType() != OrderTypeEnum.PHYSICAL_ORDER || orderChannel == OrderResourceEnum.MEMCARD
				|| orderChannel == OrderResourceEnum.SWEEP 
				|| orderChannel == OrderResourceEnum.WECHAT_MIN;
	}

	@Override
	public boolean isArriveOrderAmount(BigDecimal orderAmount,BigDecimal limitAmount){
		// 运费券无金额限制
		return true;
	}
}
