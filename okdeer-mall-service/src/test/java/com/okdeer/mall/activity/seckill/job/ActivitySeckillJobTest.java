package com.okdeer.mall.activity.seckill.job;

import javax.annotation.Resource;

import org.junit.Test;

import com.okdeer.mall.base.BaseServiceTest;


public class ActivitySeckillJobTest extends BaseServiceTest{
	
	@Resource
	private ActivitySeckillJob activitySeckillJob;

	@Test
	public void testProcessJobExecutionMultipleShardingContext() {
		activitySeckillJob.process(null);
	}

}
