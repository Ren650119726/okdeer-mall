package com.okdeer.mall.operate.skinmanager.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.operate.service.SkinManagerApi;

/**
 * 
 * APPTAB换肤定时任务
 * ClassName: SkinManagerJob 
 * @Description: TODO
 * @author tuzhd
 * @date 2016年11月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.2			2016-11-17			tuzhd			  APPTAB换肤定时任务
 */
@Service
public class SkinManagerJob extends AbstractSimpleElasticJob {
	@Autowired
	SkinManagerApi skinManagerApi;

	@Override
	public void process(JobExecutionMultipleShardingContext shardingContext) {
		// TODO Auto-generated method stub
		skinManagerApi.processSkinActivityJob();
	}

}
