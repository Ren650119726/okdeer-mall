package com.okdeer.mall.label.job;

import javax.annotation.Resource;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.okdeer.mall.activity.label.job.ActivityLabelJob;
import com.okdeer.mall.activity.label.service.ActivityLabelApi;
import com.okdeer.mall.base.BaseServiceTest;

/**
 * 
 * ClassName: ActivityLabelJobTest 
 * @Description: 服务标签jos单元测试类
 * @author tuzhd
 * @date 2016年11月12日
 *
 * =================================================================================================
 *     Task ID			  Date			  Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.2		  2016年11月12日		tuzhiding			服务标签jos单元测试类
 */
//@RunWith(Parameterized.class)
public class ActivityLabelJobTest extends BaseServiceTest {
	@Autowired
	private ActivityLabelApi activityLabelService;

	@Test
	public void testProcess() {
		activityLabelService.processLabelJob();
	}
}
