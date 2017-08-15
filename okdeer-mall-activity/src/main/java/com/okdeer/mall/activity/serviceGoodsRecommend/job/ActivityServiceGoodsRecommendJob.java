package com.okdeer.mall.activity.serviceGoodsRecommend.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.activity.serviceGoodsRecommend.service.ActivityServiceGoodsRecommendApi;

/**
 * 
 * ClassName: 服务商品推荐定时器处理 
 * @Description: 
 * @author tuzhd
 * @date 2016年11月12日
 *
 * =================================================================================================
 *     Task ID			  Date			    Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.2		  2016年11月12日		   tuzhiding		     服务商品推荐定时器
 */
@Service
public class ActivityServiceGoodsRecommendJob extends AbstractSimpleElasticJob {

	private static final Logger log = LoggerFactory.getLogger(ActivityServiceGoodsRecommendJob.class);

	@Autowired
	private ActivityServiceGoodsRecommendApi recommendService;
	
	
	//本来放在service事务里面,后来改为单个活动为一个事务,所以把循环放在这里
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void process(JobExecutionMultipleShardingContext arg0) {
		//服务商品推荐定时器 job 执行方法
		recommendService.processServiceGoodsJob();
	}
}
