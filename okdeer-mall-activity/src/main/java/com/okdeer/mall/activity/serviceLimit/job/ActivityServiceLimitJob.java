package com.okdeer.mall.activity.serviceLimit.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.activity.serviceLimit.service.ActivityServiceLimitApi;

/**
 * @pr mall
 * @desc 修改限购活动状态job
 * @author zhangkn
 * @date 2016年12月13日 下午1:59:59
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
@Service
public class ActivityServiceLimitJob extends AbstractSimpleElasticJob {

	private static final Logger logger = LoggerFactory.getLogger(ActivityServiceLimitJob.class);

	@Autowired
	private ActivityServiceLimitApi limitService;

	@Override
	public void process(JobExecutionMultipleShardingContext arg0) {
		try {
			limitService.processJob();
		} catch (Exception e) {
			logger.error("促销限购活动定时器异常", e);
		}
	}
}
