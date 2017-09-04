/** 
 *@Project: okdeer-mall-service 
 *@Author: xuzq01
 *@Date: 2017年8月30日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.ApplicationTests;
import com.okdeer.mall.activity.discount.service.ActivityDiscountServiceApi;

/**
 * ClassName: ActivityDiscountServiceApiTest 
 * @Description: TODO
 * @author xuzq01
 * @date 2017年8月30日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public class ActivityDiscountServiceApiTest extends ApplicationTests {
	@Autowired
	private ActivityDiscountServiceApi api;
	
	@Test
	public void TestIsJoinPinMoney(){
		
		//api.isJoinPinMoney(activityPinMoneyDto, storeId);
	}
	
	
}
