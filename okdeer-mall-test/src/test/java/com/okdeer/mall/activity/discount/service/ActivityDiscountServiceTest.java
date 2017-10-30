package com.okdeer.mall.activity.discount.service;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;

import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.base.BaseServiceTest;


public class ActivityDiscountServiceTest extends BaseServiceTest{

	@Resource
	private ActivityDiscountService activityDiscountService;
	
	@Test
	public void test() throws Exception {
		ActivityDiscount actInfo = activityDiscountService.findById("8a8080e35f4d3750015f4d38ed890002");
		assertEquals("8a8080e35f4d3750015f4d38ed890002", actInfo.getId());
	}

}
