package com.okdeer.mall.order.service.impl;

import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.Test;

import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.order.service.TradeOrderCompleteProcessService;


public class TradeOrderCompleteProcessServiceImplTest extends BaseServiceTest{

	@Resource
	private TradeOrderCompleteProcessService tradeOrderCompleteProcessService;
	
	@Test
	public void testOrderCompleteSyncToJxc() {
		try {
			tradeOrderCompleteProcessService.orderCompleteSyncToJxc("8a94e71759833f9c015983412eb70006");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
