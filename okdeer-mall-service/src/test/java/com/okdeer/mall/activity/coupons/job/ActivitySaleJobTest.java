package com.okdeer.mall.activity.coupons.job;

import javax.annotation.Resource;

import org.junit.Test;

import com.okdeer.mall.base.BaseServiceTest;


public class ActivitySaleJobTest extends BaseServiceTest{

	@Resource
	private ActivitySaleJob activitySaleJob;
	
	@Test
	public void testProcess() {
		activitySaleJob.process(null);
	}

}
