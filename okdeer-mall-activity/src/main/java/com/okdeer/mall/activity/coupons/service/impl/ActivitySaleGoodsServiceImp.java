package com.okdeer.mall.activity.coupons.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoodsBo;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleGoodsMapper;
import com.okdeer.mall.activity.coupons.service.ActivitySaleGoodsService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleGoodsServiceApi;
import com.okdeer.mall.activity.dto.ActivitySaleGoodsParamDto;

/**
 * 
 * TODO 特惠活动的商品
 * @project yschome-mall
 * @author zhulq
 * @date 2016年4月27日 上午9:37:58
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivitySaleGoodsServiceApi")
public class ActivitySaleGoodsServiceImp implements ActivitySaleGoodsServiceApi, ActivitySaleGoodsService {

	private static final Logger log = Logger.getLogger(ActivitySaleServiceImpl.class);

	@Autowired
	private ActivitySaleGoodsMapper activitySaleGoodsMapper;

	@Override
	public ActivitySaleGoods selectByObject(ActivitySaleGoods activitySaleGoods) {
		return activitySaleGoodsMapper.selectByObject(activitySaleGoods);
	}

	@Override
	public ActivitySaleGoods selectActivitySaleByParams(Map<String, Object> params) {
		return activitySaleGoodsMapper.selectActivitySaleByParams(params);
	}

	@Override
	public List<ActivitySaleGoodsBo> findSaleGoodsByParams(
			ActivitySaleGoodsParamDto param) {
		return activitySaleGoodsMapper.findSaleGoodsByParams(param);
	}

}
