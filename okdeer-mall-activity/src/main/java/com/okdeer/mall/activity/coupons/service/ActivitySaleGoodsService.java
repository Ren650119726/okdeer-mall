package com.okdeer.mall.activity.coupons.service;

import java.util.Map;

import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;

/**
 * 
 * TODO 特惠活动商品
 * @project yschome-mall
 * @author zhulq
 * @date 2016年4月27日 上午9:57:59
 */
public interface ActivitySaleGoodsService {
	
	ActivitySaleGoods selectByObject(ActivitySaleGoods activitySaleGoods);
	
	ActivitySaleGoods selectActivitySaleByParams(Map<String, Object> params);

}
