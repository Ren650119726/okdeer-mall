/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: CoumnAdevertJob.java 
 * @Date: 2016年4月15日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 
package com.okdeer.mall.operate.advert.job;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.operate.advert.service.ColumnAdvertService;

/**
 * 广告管理定时任务
 * @project yschome-mall
 * @author zhaoqc
 * @date 2016年4月15日 下午3:14:15
 */         
@Service
public class CoumnAdevertJob extends AbstractSimpleElasticJob {

	private static final Logger log = LoggerFactory.getLogger(CoumnAdevertJob.class);
	
	@Resource
	private ColumnAdvertService columnAdvertService;

	/**
	 * @desc 广告定时任务， 根据当前时间扫描
	 * 		  将未开始广告状态改为进行中
	 * 		  将进行中广告状态改为已结束
	 *
	 * @param shardingContext 上下文
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void process(JobExecutionMultipleShardingContext shardingContext) {
		log.info("Start  运营商后台广告定时任务定时器");
		this.columnAdvertService.updateAdvertStatusByJob();
		log.info("End  运营商后台广告定时任务定时器");
	}
	
}
