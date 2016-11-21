 package com.okdeer.mall.activity.coupons.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;

/**
 * tuzhdiing 代金劵提醒
 * ClassName: ActivityCouponsRecordJob 
 * @Description: TODO
 * @author tuzhd
 * @date 2016年11月21日
 *
 * =================================================================================================
 *     Task ID			 Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.2			 2016-11-21			tuzhd			  代金劵提醒定时任务
 */
@Service
public class ActivityCouponsRecordNoticeJob extends AbstractSimpleElasticJob {

	@Autowired
	private ActivityCouponsRecordService activityCouponsRecordService;
	
	@Override
	public void process(JobExecutionMultipleShardingContext arg0) {
		activityCouponsRecordService.procesRecordNoticeJob();
	}
	
}
