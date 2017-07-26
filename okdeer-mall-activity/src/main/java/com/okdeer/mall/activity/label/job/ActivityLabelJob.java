package com.okdeer.mall.activity.label.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.activity.label.service.ActivityLabelApi;

/**
 * 
 * ClassName: 执行服务标签活动的定时任务处理 
 * @Description: 
 * @author tuzhd
 * @date 2016年11月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.2		  2016年11月12日		tuzhiding				编写服务标签活动的定时任务处理 
 */
@Service
public class ActivityLabelJob extends AbstractSimpleElasticJob {

	private static final Logger log = LoggerFactory.getLogger(ActivityLabelJob.class);

	@Autowired
	private ActivityLabelApi activityLabelService;
	
	/**
	 * 
	 * 执行服务标签活动的定时任务处理，处理以下两种状态
	 * 1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
	 * @author tuzhd
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void process(JobExecutionMultipleShardingContext arg0) {
		//执行服务标签的JOB 任务
		activityLabelService.processLabelJob();
	}
}
