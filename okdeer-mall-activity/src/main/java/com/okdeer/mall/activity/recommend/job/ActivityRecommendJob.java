/** 
 *@Project: yschome-mall-activity 
 *@Author: zhongy
 *@Date: 2016年7月18日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.recommend.job;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.activity.recommend.entity.ActivityRecommend;
import com.okdeer.mall.activity.recommend.enums.ActivityRecommendStatus;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.recommend.mapper.ActivityRecommendMapper;
import com.okdeer.mall.activity.recommend.service.ActivityRecommendService;

/**
 * 
 * ClassName: ActivityRecommendJob 
 * @Description: 修改活动推荐状态job
 * @author zhongy
 * @date 2016年7月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1            2016年7月14日               zhongy           修改活动推荐状态job
 *     重构4.1            2016年7月30日               zhongy           修改job更新实体
 */

@Service
public class ActivityRecommendJob extends AbstractSimpleElasticJob {

	private static final Logger logger = LoggerFactory.getLogger(ActivityRecommendJob.class);

	@Autowired
	private ActivityRecommendService activityRecommendService;
	
	@Autowired
	private ActivityRecommendMapper activityRecommendMapper;
	
	
	public void checkActivityRecommendStatus(){
		logger.info("推荐活动JOB开始执行----"  + new Date());
		ActivityRecommend activityRecommend = new ActivityRecommend();
		List<ActivityRecommend> activityRecommends = null;
		Date nowTime = new Date();
		
		//****************检查未开始状态开始状态,修改状态进行中 start*******************
		activityRecommend.setDisabled(Disabled.valid);
		activityRecommend.setRecommendStatus(ActivityRecommendStatus.unStarted);
		activityRecommends = activityRecommendService.findList(activityRecommend);
		if(activityRecommends != null && activityRecommends.size() > 0){
			for (ActivityRecommend entity : activityRecommends) {
				Date startTime = entity.getStartTime();
				//当前时间已超过活动开始时间，修改活动状态进行中
				if(nowTime.getTime() >= startTime.getTime()){
				    ActivityRecommend recommend = new ActivityRecommend();
				    recommend.setId(entity.getId());
				    recommend.setRecommendStatus(ActivityRecommendStatus.starting);
					try {
					    activityRecommendMapper.updateByPrimaryKeySelective(recommend);
						logger.info("JOB--检查未开始状态开始状态,修改状态进行中成功");
					} catch (ServiceException e) {
						logger.info("JOB--检查未开始状态开始状态,修改状态进行中异常",e);
					}
				}
			}
		}
		//****************检查未开始状态开始状态,修改状态进行中 end*******************
		
		//****************检查进行中的状态,修改状态已结束start**********************
		activityRecommend.setDisabled(Disabled.valid);
        activityRecommend.setRecommendStatus(ActivityRecommendStatus.starting);
        activityRecommends = activityRecommendService.findList(activityRecommend);
        
		if(activityRecommends != null && activityRecommends.size() > 0){
			for (ActivityRecommend entity : activityRecommends) {
				Date endTime = entity.getEndTime();
				//活动结束时间已超过当前时间，修改活动状态已结束
				if(nowTime.getTime() >= endTime.getTime() ){
				    ActivityRecommend recommend = new ActivityRecommend();
                    recommend.setId(entity.getId());
                    recommend.setRecommendStatus(ActivityRecommendStatus.over);
					try {
					    activityRecommendMapper.updateByPrimaryKeySelective(recommend);
						logger.info("JOB--检查进行中的状态,修改状态已结束成功");
					} catch (ServiceException e) {
						logger.info("JOB--检查进行中的状态,修改状态已结束成功异常",e);
					}
				}
			}
		}
	}
		//****************检查进行中的状态,修改状态已结束 end***********************

	@Override
	public void process(JobExecutionMultipleShardingContext arg0) {
		this.checkActivityRecommendStatus();
	}
}
