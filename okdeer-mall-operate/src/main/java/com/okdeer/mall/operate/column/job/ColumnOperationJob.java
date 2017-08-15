/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @Project: yschome-mall 
 * @File: ColumnOperationJob.java 
 * @Date: 2016年3月25日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */ 
package com.okdeer.mall.operate.column.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.okdeer.mall.operate.column.service.ColumnOperationService;

/**
 * @project yschome-mall
 * @author wusw
 * @date 2016年3月25日 下午7:41:30
 */
@Service
public class ColumnOperationJob extends AbstractSimpleElasticJob{
	
	private static final Logger logger = LoggerFactory.getLogger(ColumnOperationJob.class);
	
	@Autowired
	private ColumnOperationService columnOperationService;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void process(JobExecutionMultipleShardingContext arg0) {
		columnOperationService.updateByJob();
	}

}
