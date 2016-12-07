/** 
 *@Project: okdeer-mall-system 
 *@Author: xuzq01
 *@Date: 2016年11月25日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.risk.job;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.mall.risk.service.RiskOrderRecordService;

/**
 * ClassName: RiskOrderRecordJob 
 * @Description: 删除风控动作记录job
 * @author xuzq01
 * @date 	2016年11月25日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2			2016年11月25日		xuzq01			删除风控动作记录job
 */
@Service
public class RiskOrderRecordJob extends AbstractSimpleElasticJob{

	/**
	 * 日志输出
	 */
	private static final Logger logger = LoggerFactory.getLogger(RiskOrderRecordJob.class);

	/**
	 * 注入动作记录service
	 */
	@Autowired
	RiskOrderRecordService riskOrderRecordService;
	
	@Override
	public void process(JobExecutionMultipleShardingContext shardingContext) {
		try {
			logger.info("定时清理动作记录开始-----" + DateUtils.getDateTime());
			Date createTime = getZeroTime();
			riskOrderRecordService.deleteByTime(createTime);
		} catch (Exception e) {
			logger.warn("清理动作记录异常", e);
		} finally {
			logger.info("清理动作记录結束-----" + DateUtils.getDateTime());
		}
	}
	
	/**
	 * 
	 * @Description: 获取当天的零点时间
	 * @return   
	 * @author xuzq01
	 * @date 2016年11月25日
	 */
	private static Date getZeroTime(){
		 Calendar cal = Calendar.getInstance();
	     cal.setTime(new Date());
	     cal.set(Calendar.HOUR_OF_DAY, 0);
	     cal.set(Calendar.MINUTE, 0);
	     cal.set(Calendar.SECOND, 0);
	     cal.set(Calendar.MILLISECOND, 0);
	     return  cal.getTime();	
	}
}
