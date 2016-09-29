package com.okdeer.mall.order.service.impl;


import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.okdeer.mall.Application;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.RefundsLogisticsEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.vo.RefundsTraceResp;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class TradeOrderRefundsTraceServiceImplTest {

	@Resource
	private TradeOrderRefundsTraceServiceImpl tradeOrderRefundsTraceService; 
	
	
	@Test
	public void testSaveRefundApplyTrace() {
		// 发起申请
		TradeOrderRefunds refundsOrder = new TradeOrderRefunds();
		refundsOrder.setId("8a98681b56115c7401561169e9ba000c");
		refundsOrder.setType(OrderTypeEnum.PHYSICAL_ORDER);
		refundsOrder.setRefundNo("XT16072215322751");
		tradeOrderRefundsTraceService.saveRefundApplyTrace(refundsOrder);
	}

	@Test
	public void testSaveSellerDealTrace() {
		// 卖家同意
		TradeOrderRefunds refundsOrder = new TradeOrderRefunds();
		refundsOrder.setId("8a98681b56115c7401561169e9ba000c");
		refundsOrder.setRefundsStatus(RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS);
		tradeOrderRefundsTraceService.saveSellerDealTrace(refundsOrder);
	}

	
	@Test
	public void testSaveBuyerReturnTrace() {
		tradeOrderRefundsTraceService.saveBuyerReturnTrace("8a98681b56115c7401561169e9ba000c", RefundsLogisticsEnum.DOOR_PICK_UP);
	}
	
	@Test
	public void testSellerRefund() {
		// 卖家同意退款
		TradeOrderRefunds refundsOrder = new TradeOrderRefunds();
		refundsOrder.setId("8a98681b56115c7401561169e9ba000c");
		refundsOrder.setRefundsStatus(RefundsStatusEnum.REFUND_SUCCESS);
		tradeOrderRefundsTraceService.saveSellerRefundTrace(refundsOrder);
	}
	
	@Test
	public void findRefundsTrace(){
		Response<RefundsTraceResp> resp = tradeOrderRefundsTraceService.findRefundsTrace("12345678");
		System.out.println(JSONObject.toJSONString(resp));
	}
}
