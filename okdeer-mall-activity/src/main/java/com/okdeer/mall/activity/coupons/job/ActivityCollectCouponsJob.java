package com.okdeer.mall.activity.coupons.job;

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
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.enums.ActivityCollectCouponsApprovalStatus;
import com.okdeer.mall.activity.coupons.enums.ActivityCollectCouponsStatus;
import com.okdeer.mall.common.utils.RobotUserUtil;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;

/**
 * @pr mall
 * @desc 修改代金券活动活动状态job
 * @author zhangkn
 * @date 2016年1月28日 下午1:59:59
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
@Service
public class ActivityCollectCouponsJob extends AbstractSimpleElasticJob {

	private static final Logger log = LoggerFactory.getLogger(ActivityCollectCouponsJob.class);

	@Autowired
	private ActivityCollectCouponsService activityCollectCouponsService;
	
	
	//本来放在service事务里面,后来改为单个活动为一个事务,所以把循环放在这里
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void process(JobExecutionMultipleShardingContext arg0) {
		try{
			log.info("运营商后台代金券活动定时器开始");
			
			List<ActivityCollectCoupons> accList = activityCollectCouponsService.listByJob();
			if(accList != null && accList.size() > 0){
				
				List<String> listIdNoStart = new ArrayList<String>();
				List<String> listIdIng = new ArrayList<String>();
				List<String> listIdFail = new ArrayList<String>();
				
				for(ActivityCollectCoupons a : accList){
					//未开始的 
					if(a.getStatus() == ActivityCollectCouponsStatus.noStart.getValue()){
						//代理商提交过来的活动超时没审核就是改为已经失效
						if(!"0".equals(a.getBelongType()) && 
							(a.getApprovalStatus() == null || a.getApprovalStatus() == ActivityCollectCouponsApprovalStatus.noApproval.ordinal() )
						){
							listIdFail.add(a.getId());
						}
						//改为进行中的
						else{
							listIdNoStart.add(a.getId());
						}
					}
					//进行中的改为已结束的
					else if(a.getStatus() == ActivityCollectCouponsStatus.ing.getValue()){
						listIdIng.add(a.getId());
					}
				}
				
				String updateUserId = RobotUserUtil.getRobotUser().getId();
				Date updateTime = new Date();
				
				//改为进行中
				if(listIdNoStart != null && listIdNoStart.size() > 0){
					for(String id : listIdNoStart){
						try{
							activityCollectCouponsService.updateBatchStatus(id,  ActivityCollectCouponsStatus.ing.getValue(), updateUserId, updateTime,"job");
						}catch(Exception e){
							log.error("代金券活动"+id+"job异常 改为进行中:",e);
						}
						
					}
				}
				//改为已经结束
				if(listIdIng != null && listIdIng.size() > 0){
					for(String id : listIdIng){
						try{
							activityCollectCouponsService.updateBatchStatus(id,  ActivityCollectCouponsStatus.end.getValue(), updateUserId, updateTime,"job");
						}catch(Exception e){
							log.error("代金券活动"+id+"job异常 改为已经结束:",e);
						}
					}
				}
				//改为已失效
				if(listIdFail != null && listIdFail.size() > 0){
					for(String id : listIdFail){
						try{
							activityCollectCouponsService.updateBatchStatus(id,  ActivityCollectCouponsStatus.disabled.getValue(), updateUserId, updateTime,"job");
						}catch(Exception e){
							log.error("代金券活动"+id+"job异常改为已失效:",e);
						}
					}
				}
			}
			log.info("运营商后台代金券活动定时器结束");
		}catch(Exception e){
			log.error("代金券活动job异常",e);
		}
		
	}
}
