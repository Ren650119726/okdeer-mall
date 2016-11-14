package com.okdeer.mall.activity.label.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.activity.label.entity.ActivityLabel;
import com.okdeer.mall.activity.label.enums.ActivityLabelStatus;
import com.okdeer.mall.activity.label.service.ActivityLabelApi;
import com.okdeer.mall.common.utils.RobotUserUtil;

/**
 * 
 * ClassName: 执行服务标签活动的定时任务处理 
 * @Description: TODO
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
		try{
			log.info("服务标签管理定时器开始");
			Map<String,Object> map = new HashMap<String,Object>();
			Date nowTime = new Date();
			map.put("nowTime", nowTime);
			//1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
			List<ActivityLabel> accList = activityLabelService.listByJob(map);
			//获得系统当前系统用户id
			String updateUserId = RobotUserUtil.getRobotUser().getId();
			//需要更新状态的活动新不为空进行定时任务处理
			if(CollectionUtils.isNotEmpty(accList)){
				for(ActivityLabel a : accList){
					try{
						//未开始的 
						if(a.getStatus() == ActivityLabelStatus.noStart.getValue()){
							//根据id修改服务标签活动状态
							activityLabelService.updateStatusById(a.getId(),  ActivityLabelStatus.ing.getValue(), updateUserId, nowTime);
						
						//进行中的改为已结束的
						}else if(a.getStatus() == ActivityLabelStatus.ing.getValue()){
							//根据id修改服务标签活动状态
							activityLabelService.updateStatusById(a.getId(),  ActivityLabelStatus.end.getValue(), updateUserId, nowTime);
						}
					}catch(Exception e){
						log.error(a.getStatus()+"状态服务标签管理"+a.getId()+"job修改异常 :",e);
					}
				}
			}
			log.info("服务标签管理定时器结束");
		}catch(Exception e){
			log.error("服务标签管理job异常",e);
		}
		
	}
}
