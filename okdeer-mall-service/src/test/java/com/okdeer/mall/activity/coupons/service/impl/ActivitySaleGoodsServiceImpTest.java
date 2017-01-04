package com.okdeer.mall.activity.coupons.service.impl;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.ApplicationTests;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoodsBo;
import com.okdeer.mall.activity.coupons.service.ActivitySaleGoodsServiceApi;
import com.okdeer.mall.activity.dto.ActivitySaleGoodsParamDto;

public class ActivitySaleGoodsServiceImpTest extends ApplicationTests {
	@Autowired
	private ActivitySaleGoodsServiceApi service;
	
	@Test
	public void testFindSaleGoodsByParams() {
		try{
			ActivitySaleGoodsParamDto param = new ActivitySaleGoodsParamDto();
			param.setActivityId("8a94e71f5968724c015968724fbd0002");
			List<ActivitySaleGoodsBo> list = service.findSaleGoodsByParams(param);
			System.out.println(list.get(0).getSkuName());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Test
	public void testFindSaleGoodsByParams1() {
		fail("Not yet implemented");
	}
}
