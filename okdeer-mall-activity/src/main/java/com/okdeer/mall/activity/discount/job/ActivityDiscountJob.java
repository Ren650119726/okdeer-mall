package com.okdeer.mall.activity.discount.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.yschome.base.common.utils.DateUtils;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;

/**
 * @pr mall
 * @desc 修改满减满折活动状态job
 * @author zengj
 * @date 2016年1月28日 下午1:59:59
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
@Service
public class ActivityDiscountJob extends AbstractSimpleElasticJob {

	private static final Logger logger = LoggerFactory.getLogger(ActivityDiscountJob.class);

	@Autowired
	private ActivityDiscountService activityDiscountService;
	
	
	@Override
	public void process(JobExecutionMultipleShardingContext arg0) {
		try {
			logger.info("满减满折活动定时任务开始-----"+DateUtils.getDateTime());
			activityDiscountService.updateStatus();
		} catch (Exception e) {
			logger.error("定时修改满减满折活动状态失败", e);
		}finally{
			logger.info("满减满折活动定时任务结束-----"+DateUtils.getDateTime());
		}
	}
}
