/** 
 *@Project: yschome-mall-activity 
 *@Author: lijun
 *@Date: 2016年7月15日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    

package com.okdeer.mall.activity.seckill.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillService;

/**
 * ClassName: ActivitySeckillJob 
 * @Description: 秒杀活动相关Job
 * @author lijun
 * @date 2016年7月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      重构 4.1         2016年7月15日                                 lijun               新增
 * 
 */
@Service
public class ActivitySeckillJob extends AbstractSimpleElasticJob {
	
	/**
	 * 日志输出
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActivitySeckillJob.class);

	/**
	 * 注入秒杀活动service
	 */
	@Autowired
	ActivitySeckillService activitySeckillService;
	
	@Override
	public void process(JobExecutionMultipleShardingContext shardingContext) {
		try {
			logger.info("秒杀活动定时任务修改活动状态开始-----" + DateUtils.getDateTime());
			
			// 查询条件map param
			Map<String, Object> param = new HashMap<String, Object>();
			
			// 未开始活动，时间开始之后变更状态为已开始 start
			param.put("seckillStatus", SeckillStatusEnum.noStart);
			param.put("disabled", Disabled.valid);
			List<ActivitySeckill> noStartSeckillList = activitySeckillService.findActivitySeckillByStatus(param);
			if (noStartSeckillList != null && noStartSeckillList.size() > 0) {
				for (ActivitySeckill activitySeckill : noStartSeckillList) {
					Date nowTime = new Date();
					Date startTime = activitySeckill.getStartTime();
					//当前时间已超过活动开始时间，修改活动状态已失效
					if (nowTime.getTime() >= startTime.getTime()) {
						// 更新活动状态
						activitySeckillService.updateSeckillStatus(activitySeckill.getId(), SeckillStatusEnum.ing);
					}
				}
			}
			// 未开始活动，时间开始之后变更状态为已开始 end
			
			// 已开始活动，时间到期之后变更状态为已结束 start
			param.put("seckillStatus", SeckillStatusEnum.ing);
			param.put("disabled", Disabled.valid);
			List<ActivitySeckill> startSeckillList = activitySeckillService.findActivitySeckillByStatus(param);
			if (startSeckillList != null && startSeckillList.size() > 0) {
				for (ActivitySeckill activitySeckill : startSeckillList) {
					Date nowTime = new Date();
					Date endTime = activitySeckill.getEndTime();
					//当前时间已超过活动结束时间，修改活动状态已失效
					if (nowTime.getTime() >= endTime.getTime()) {
						// 更新活动状态（修改状态、解除商品关系、释放库存）
						activitySeckillService.updateSeckillByEnd(activitySeckill);
					}
				}
			}
			// 已开始活动，时间到期之后变更状态为已结束  end
			
		} catch (Exception e) {
			logger.warn("秒杀活动定时任务修改活动状态异常", e);
		} finally {
			logger.info("秒杀活动定时任务修改活动状态結束-----" + DateUtils.getDateTime());
		}
	}

}
