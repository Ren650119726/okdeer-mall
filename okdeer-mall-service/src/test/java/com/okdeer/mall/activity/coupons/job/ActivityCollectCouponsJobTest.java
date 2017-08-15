/** 
 *@Project: yschome-mall-service 
 *@Author: lijun
 *@Date: 2016年7月29日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */    

package com.okdeer.mall.activity.coupons.job;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.Application;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectCouponsMapper;



/**
 * ClassName: ActivityCollectCouponsJobTest 
 * @Description: 
 * @author lijun
 * @date 2016年7月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      重构 4.1         2016年7月29日                                 lijun               新增
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ActivityCollectCouponsJobTest {
	
	@Autowired
	ActivityCollectCouponsMapper activityCollectCouponsMapper;

	@Test
	public void test() {
		List<ActivityCollectCoupons> couponsList = activityCollectCouponsMapper.listByJob();
	}

}
