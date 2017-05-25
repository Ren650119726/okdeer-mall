/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月20日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.job;

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
import com.okdeer.mall.activity.advert.entity.ActivityAdvert;
import com.okdeer.mall.activity.advert.service.ActivityAdvertService;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.common.utils.RobotUserUtil;

/**
 * ClassName: ActivityAdvertJob 
 * @Description: 广告活动状态job
 * @author xuzq01
 * @date 2017年4月20日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v2.2.0 			2017年4月20日			xuzq01				广告活动状态job
 */
@Service
public class ActivityAdvertJob extends AbstractSimpleElasticJob {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActivityAdvertJob.class);

	@Autowired
	private ActivityAdvertService activityAdvertService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void process(JobExecutionMultipleShardingContext arg0) {
		try{
			LOGGER.info("广告活动定时器开始");
			
			List<ActivityAdvert> accList = activityAdvertService.listByJob();
			if(!accList.isEmpty() && accList.size()>0 ){
				
				List<String> listIdNoStart = new ArrayList<String>();
				List<String> listIdIng = new ArrayList<String>();
				
				for(ActivityAdvert advert : accList){
					//未开始的 
					if(advert.getStatus() == SeckillStatusEnum.noStart){
						listIdNoStart.add(advert.getId());
					}
					//进行中的改为已结束的
					else if(advert.getStatus() == SeckillStatusEnum.ing){
						listIdIng.add(advert.getId());
					}
				}
				
				String updateUserId = RobotUserUtil.getRobotUser().getId();
				Date updateTime = new Date();
				
				//改为进行中
				if(!listIdNoStart.isEmpty() && listIdNoStart.size() > 0){
					for(String id : listIdNoStart){
						try{
							activityAdvertService.updateBatchStatus(id,  SeckillStatusEnum.ing, updateUserId, updateTime);
						}catch(Exception e){
							LOGGER.error("抽奖模板"+id+"job异常 改为进行中:",e);
						}
						
					}
				}
				//改为已经结束
				if(listIdIng != null && listIdIng.size() > 0){
					for(String id : listIdIng){
						try{
							activityAdvertService.updateBatchStatus(id,  SeckillStatusEnum.end, updateUserId, updateTime);
						}catch(Exception e){
							LOGGER.error("抽奖模板"+id+"job异常 改为已经结束:",e);
						}
					}
				}
				
			}
			LOGGER.info("广告活动定时器结束");
		}catch(Exception e){
			LOGGER.error("广告活动定时器异常",e);
		}
		
	}

}
