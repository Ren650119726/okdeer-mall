package com.okdeer.mall.activity.coupons.job;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;

/**
 * 代金卷领取记录
 * @project yschome-mall
 * @author zhulq
 * @date 2016年4月15日 下午3:06:42
 */
@Service
public class ActivityCouponsRecordJob extends AbstractSimpleElasticJob {

	private static final Logger logger = LoggerFactory.getLogger(ActivityCouponsRecordJob.class);

	@Autowired
	private ActivityCouponsRecordService activityCouponsRecordService;
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void process(JobExecutionMultipleShardingContext arg0) {
		logger.info("代金券领取记录修改状态开始---{}",new Date());
		try {
			activityCouponsRecordService.updateStatusByJob();
		} catch (Exception e) {
			logger.error("更改代金券领取记录状态job异常",e);
		}
	}
}
