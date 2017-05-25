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
			{"40289d635c3e7c74015c3e98d3a50046",RefundsStatusEnum.FORCE_SELLER_REFUND,"8a94e8f152967d17015297323ada0011"}
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
