package com.okdeer.mall.activity.serviceGoodsRecommend.job;

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
import com.okdeer.mall.activity.serviceGoodsRecommend.entity.ActivityServiceGoodsRecommend;
import com.okdeer.mall.activity.serviceGoodsRecommend.enums.ActivityServiceGoodsRecommendStatus;
import com.okdeer.mall.activity.serviceGoodsRecommend.service.ActivityServiceGoodsRecommendApi;
import com.okdeer.mall.common.utils.RobotUserUtil;

/**
 * 
 * ClassName: 服务商品推荐定时器处理 
 * @Description: TODO
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
		try{
			log.info("服务商品推荐定时器开始");
			
			Map<String,Object> map = new HashMap<String,Object>();
			Date nowTime = new Date();
			map.put("nowTime", nowTime);
			//1、查询活动未开始，开始时间小于当前的数据 即为要设置开始，2、活动开始、结束时间小于当前的数据 即为要设置结束
			List<ActivityServiceGoodsRecommend> accList = recommendService.listByJob(map);
			//获得系统当前系统用户id
			String updateUserId = RobotUserUtil.getRobotUser().getId();
			
			
			//需要更新状态的活动新不为空进行定时任务处理
			if(CollectionUtils.isNotEmpty(accList)){
				for(ActivityServiceGoodsRecommend a : accList){
					try{
						//未开始的 
						if(a.getStatus() == ActivityServiceGoodsRecommendStatus.noStart.getValue()){
							//根据id修改服务商品活动状态
							recommendService.updateStatusById(a.getId(), ActivityServiceGoodsRecommendStatus.ing.getValue(), updateUserId, nowTime);
						
						//进行中的改为已结束的
						}else if(a.getStatus() == ActivityServiceGoodsRecommendStatus.ing.getValue()){
							//根据id修改服务商品活动状态
							recommendService.updateStatusById(a.getId(), ActivityServiceGoodsRecommendStatus.end.getValue(), updateUserId, nowTime);
						}
					}catch(Exception e){
						log.error(a.getStatus()+"状态服务标签管理"+a.getId()+"job修改异常 :",e);
					}
				}
			}
			
			log.info("服务商品推荐定时器结束");
		}catch(Exception e){
			log.error("服务商品推荐job异常",e);
		}
		
	}
}
