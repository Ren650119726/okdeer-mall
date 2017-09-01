package com.okdeer.mall.activity.coupons.service;

import java.util.List;
import java.util.Map;

import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;

/**
 * 
 * 特惠活动商品
 * @project yschome-mall
 * @author zhulq
 * @date 2016年4月27日 上午9:57:59
 */
public interface ActivitySaleGoodsService {
	
	ActivitySaleGoods selectByObject(ActivitySaleGoods activitySaleGoods);
	
	ActivitySaleGoods selectActivitySaleByParams(Map<String, Object> params);
	
	void saveBatch(List<ActivitySaleGoods> list);
	
	/**
	 * @Description: 给正在进行中的活动添加商品
	 * @author zhangkn
	 * @date 2017年9月1日
	 */
	void addActivitySaleGoodsList(ActivitySale sale,List<ActivitySaleGoods> activitySaleGoodsList) throws Exception;

}
