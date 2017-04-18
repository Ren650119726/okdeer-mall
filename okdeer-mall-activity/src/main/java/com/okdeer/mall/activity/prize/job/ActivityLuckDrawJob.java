/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月18日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.prize.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.activity.prize.entity.ActivityLuckDraw;
import com.okdeer.mall.activity.prize.service.ActivityLuckDrawService;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.common.utils.RobotUserUtil;

/**
 * ClassName: ActivityLuckDrawJob 
 * @Description: 抽奖设置模板job
 * @author xuzq01
 * @date 2017年4月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v2.2.2 			2017年4月18日			xuzq01				抽奖设置模板job
 */
@Service
public class ActivityLuckDrawJob extends AbstractSimpleElasticJob {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActivityLuckDrawJob.class);

	@Autowired
	private ActivityLuckDrawService activityLuckDrawService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void process(JobExecutionMultipleShardingContext arg0) {
		try{
			LOGGER.info("抽奖设置模板定时器开始");
			
			List<ActivityLuckDraw> accList = activityLuckDrawService.listByJob();
			if(!accList.isEmpty() && accList.size()>0 ){
				
				List<String> listIdNoStart = new ArrayList<String>();
				List<String> listIdIng = new ArrayList<String>();
				
				for(ActivityLuckDraw draw : accList){
					//未开始的 
					if(draw.getStatus() == SeckillStatusEnum.noStart){
						listIdNoStart.add(draw.getId());
					}
					//进行中的改为已结束的
					else if(draw.getStatus() == SeckillStatusEnum.ing){
						listIdIng.add(draw.getId());
					}
				}
				
				String updateUserId = RobotUserUtil.getRobotUser().getId();
				Date updateTime = new Date();
				
				//改为进行中
				if(!listIdNoStart.isEmpty() && listIdNoStart.size() > 0){
					for(String id : listIdNoStart){
						try{
							activityLuckDrawService.updateBatchStatus(id,  SeckillStatusEnum.ing, updateUserId, updateTime);
						}catch(Exception e){
							LOGGER.error("抽奖模板"+id+"job异常 改为进行中:",e);
						}
						
					}
				}
				//改为已经结束
				if(listIdIng != null && listIdIng.size() > 0){
					for(String id : listIdIng){
						try{
							activityLuckDrawService.updateBatchStatus(id,  SeckillStatusEnum.end, updateUserId, updateTime);
						}catch(Exception e){
							LOGGER.error("抽奖模板"+id+"job异常 改为已经结束:",e);
						}
					}
				}
				
			}
			LOGGER.info("抽奖设置模板定时器结束");
		}catch(Exception e){
			LOGGER.error("抽奖设置模板定时器异常",e);
		}
		
	}

}
