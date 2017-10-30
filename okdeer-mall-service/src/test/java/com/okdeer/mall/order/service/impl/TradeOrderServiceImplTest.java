package com.okdeer.mall.order.service.impl;

import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.Test;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.order.service.TradeOrderService;


public class TradeOrderServiceImplTest extends BaseServiceTest{
	
	@Resource 
	private TradeOrderService tradeOrderService;

	@Test
	public void testSelectById() throws ServiceException {
		tradeOrderService.selectById("1231231");
		
	}

}
