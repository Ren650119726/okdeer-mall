package com.okdeer.mall.activity.serviceGoodsRecommend.job;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.activity.serviceGoodsRecommend.service.ActivityServiceGoodsRecommendApi;
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
public class ActivityServiceGoodsRecommendJobTest extends BaseServiceTest {
	@Autowired
	private ActivityServiceGoodsRecommendApi recommendService;

	@Test
	public void testProcess() {
		recommendService.processServiceGoodsJob();
	}
}
