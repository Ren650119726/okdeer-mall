package com.okdeer.mall.activity.coupons.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCouponsRecordVo;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecordVo;
import com.okdeer.mall.activity.coupons.enums.ActivityCollectCouponsStatus;
import com.okdeer.mall.order.enums.RefundType;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;

/**
 * 代金卷活动 退款定时任务
 * @project yschome-mall
 * @author zhulq
 * @date 2016年4月15日 下午3:06:42
 */
@Service
public class ActivityCollectRefundJob extends AbstractSimpleElasticJob {

	private static final Logger logger = LoggerFactory.getLogger(ActivityCollectRefundJob.class);

	@Autowired
	private ActivityCouponsRecordService activityCouponsRecordService;
	
	@Autowired
	private ActivityCollectCouponsService activityCollectCouponsService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void process(JobExecutionMultipleShardingContext arg0) {
		Date date = new Date();
		Map<String,Object> params = new HashMap<>();
		// 将活动的退款状态改为 未领取的已经退款
		params.put("closed", ActivityCollectCouponsStatus.closed.ordinal());
		params.put("end", ActivityCollectCouponsStatus.end.ordinal());
		params.put("refundType", RefundType.UNREFUND);	
		try {
			//所以关闭 结束 领取了的代金卷 没有退款的  
			List<ActivityCollectCouponsRecordVo> activityCollectCouponsList = activityCollectCouponsService.findByUnusedOrExpires(params);
			if (activityCollectCouponsList != null && activityCollectCouponsList.size() > 0) {
				for(ActivityCollectCouponsRecordVo activityCollectCouponsRecordVo : activityCollectCouponsList){
					List<ActivityCouponsRecordVo> activityCouponsRecordVoList = null;
					activityCouponsRecordVoList = activityCollectCouponsRecordVo.getActivityCouponsRecordVo();
					String id = activityCollectCouponsRecordVo.getId();
					if (activityCouponsRecordVoList != null && activityCouponsRecordVoList.size() > 0) {
						Date validTime = activityCouponsRecordVoList.get(0).getValidTime();
						int res = validTime.compareTo(date);
						if ((res == 0) || (res == -1)) {
							try{
								activityCouponsRecordService.updateRefundStatus(activityCouponsRecordVoList,id);
							}catch(Exception e){
								logger.error("更改代金卷活动退款状态异常",e);
							}							
					    }
			      }
				}
		   }
		} catch (Exception e) {
			logger.error("代金卷活动退款job异常",e);
		}
	}
}
