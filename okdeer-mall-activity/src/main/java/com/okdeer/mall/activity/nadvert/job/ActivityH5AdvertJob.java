/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2017年4月20日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.nadvert.job;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.activity.nadvert.entity.ActivityH5Advert;
import com.okdeer.mall.activity.nadvert.service.ActivityH5AdvertService;
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
public class ActivityH5AdvertJob extends AbstractSimpleElasticJob {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActivityH5AdvertJob.class);

	@Autowired
	private ActivityH5AdvertService service;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void process(JobExecutionMultipleShardingContext arg0) {
		try{
			LOGGER.info("广告活动定时器开始");
			
			List<ActivityH5Advert> accList = service.listByJob(new Date());
			if(CollectionUtils.isNotEmpty(accList)){
				String updateUserId = RobotUserUtil.getRobotUser().getId();
				Date updateTime = new Date();
				accList.forEach(advert -> {
					if(advert.getStatus() == SeckillStatusEnum.noStart.ordinal()){
						//未开始的 
						advert.setStatus(SeckillStatusEnum.ing.ordinal());
					}else if(advert.getStatus() == SeckillStatusEnum.ing.ordinal()){
						//进行中的改为已结束的
						advert.setStatus(SeckillStatusEnum.end.ordinal());
					}
					advert.setUpdateUserId(updateUserId);
					advert.setUpdateTime(updateTime);
					try {
						service.updateBatchStatus(advert);
					} catch (Exception e) {
						LOGGER.error("定时修改h5活动:{}状态为{}异常{}",advert.getId(),advert.getStatus(),e);
					}
				});
			}
			LOGGER.info("广告活动定时器结束");
		}catch(Exception e){
			LOGGER.error("广告活动定时器异常",e);
		}
		
	}

}
