
package com.okdeer.mall.activity.coupons.job;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.enums.ActivitySaleStatus;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;

/**
 * @pr mall
 * @desc 修改特惠活动状态job
 * @author zhangkn
 * @date 2016年1月28日 下午1:59:59
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
@Service
public class ActivitySaleJob extends AbstractSimpleElasticJob {

	private static final Logger logger = LoggerFactory.getLogger(ActivitySaleJob.class);

	@Autowired
	private ActivitySaleService activitySaleService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void process(JobExecutionMultipleShardingContext arg0) {
		try {
			List<ActivitySale> list = activitySaleService.listByTask();

			if (list != null && list.size() > 0) {
				for (ActivitySale a : list) {
					try {
						if (a.getStatus() == ActivitySaleStatus.noStart.getValue()) {
							List<String> idList = new ArrayList<String>();
							idList.add(a.getId());
							activitySaleService.updateBatchStatus(idList, ActivitySaleStatus.ing.getValue(),
									a.getStoreId(), "0");
						} else if (a.getStatus() == ActivitySaleStatus.ing.getValue()) {
							List<String> idList = new ArrayList<String>();
							idList.add(a.getId());
							activitySaleService.updateBatchStatus(idList, ActivitySaleStatus.end.getValue(),
									a.getStoreId(), "0");
						}
					} catch (Exception e) {
						logger.error("特惠活动定时器异常" + a.getId(), e);
					}

				}
			}
		} catch (Exception e) {
			logger.error("特惠活动定时器异常", e);
		}
	}
}
