package com.okdeer.mall.activity.coupons.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.ApplicationTests;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRemindApi;

/**
 * 
 * ClassName: ActivitySaleRemindApiImplTest 
 * @Description: 活动安全库存预警提醒
 * @author tangy
 * @date 2017年2月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2017年2月21日                               tangy
 */
public class ActivitySaleRemindApiImplTest extends ApplicationTests {

	@Autowired
	private ActivitySaleRemindApi activitySaleRemindApi;
	
	@Test
	public void sendSafetyWarningTest(){
		String storeSkuId = "8a98683a5669d21d01567e1a834d0604";
		List<String> storeSkuIds = new ArrayList<String>();
		storeSkuIds.add(storeSkuId);
		//activitySaleRemindApi.sendSafetyWarning(storeSkuIds);
	}
	
}
