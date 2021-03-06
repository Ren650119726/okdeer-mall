package com.okdeer.mall.member.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.member.service.SysBuyerExtService;
/**
 * 重置用户抽奖机会定时JOB
 * ClassName: SysBuyerPrizeCountResetJob 
 * @author tuzhd
 * @date 2016年11月22日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.1.5		   2016-11-22			tuzhiding		  重置用户抽奖机会定时JOB
 */
@Service
public class SysBuyerPrizeCountResetJob extends AbstractSimpleElasticJob {
	@Autowired
	SysBuyerExtService sysBuyerExtService;
	
	@Override
	public void process(JobExecutionMultipleShardingContext shardingContext) {
		//执行批量重置用户抽奖次数  邀新活动启动后屏蔽该活动
		//sysBuyerExtService.updateUserPrizeCount();
	}

}
