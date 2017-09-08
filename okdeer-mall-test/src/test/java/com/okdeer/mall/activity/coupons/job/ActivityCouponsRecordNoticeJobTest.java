package com.okdeer.mall.activity.coupons.job;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.base.BaseServiceTest;

/**
 * 
 * ClassName: ActivityLabelJobTest 
 * @Description: 代金劵到期提测单元测试类
 * @author tuzhd
 * @date 2016年11月21日
 *
 * =================================================================================================
 *     Task ID			  Date			  Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.2		  2016年11月21日		tuzhiding			服务标签job单元测试类
 */
//@RunWith(Parameterized.class)
public class ActivityCouponsRecordNoticeJobTest extends BaseServiceTest {
	@Autowired
	private ActivityCouponsRecordService activityCouponsRecordService;

	@Test
	public void testProcess() {
		activityCouponsRecordService.procesRecordNoticeJob();
	}
}
