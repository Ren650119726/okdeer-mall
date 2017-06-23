package com.okdeer.mall.order.service.impl;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.service.TradeOrderRefundsService;

@RunWith(Parameterized.class)
public class TradeOrderRefundsServiceImplTest extends BaseServiceTest{
	
	@Resource
	private TradeOrderRefundsService tradeOrderRefundsService;
	
	private String refundsId;
	
	private RefundsStatusEnum status;
	
	private String userId;
	
	public TradeOrderRefundsServiceImplTest(String refundsId, RefundsStatusEnum status, String userId) {
		this.refundsId = refundsId;
		this.status = status;
		this.userId = userId;
	}
	
	@Parameters
	public static Collection<Object[]> initParam(){
		return Arrays.asList(new Object[][]{
			{"40289d645cc40316015cc566c0b103ec",RefundsStatusEnum.FORCE_SELLER_REFUND,"14527626891242d4d00a207c4d69bd80"}
		});
	}

	@Test
	public void testUpdateByCustomer() {
		try {
			tradeOrderRefundsService.updateByCustomer(refundsId, status, userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
