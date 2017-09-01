package com.okdeer.mall.order.service.impl;


import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.okdeer.mall.Application;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillService;
import com.okdeer.mall.common.dto.Response;
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
	
	@Resource
	private ActivitySeckillService activitySeckillService;
	
	@Test
	public void testQuery(){
		try {
			List<ActivitySeckill> activitySeckill = activitySeckillService.findByUserAppSecKillByCityId("291","0");
			System.out.println(activitySeckill.size());
		} catch (Exception e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSaveRefundApplyTrace() {
		// 发起申请
		TradeOrderRefunds refundsOrder = new TradeOrderRefunds();
		refundsOrder.setId("8a98681b56115c7401561169e9ba000c");
		refundsOrder.setType(OrderTypeEnum.PHYSICAL_ORDER);
		refundsOrder.setRefundNo("XT16072215322751");
		tradeOrderRefundsTraceService.saveRefundTrace(refundsOrder);
	}

	@Test
	public void testSaveSellerDealTrace() {
		// 卖家同意
		TradeOrderRefunds refundsOrder = new TradeOrderRefunds();
		refundsOrder.setId("8a98681b56115c7401561169e9ba000c");
		refundsOrder.setRefundsStatus(RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS);
		tradeOrderRefundsTraceService.saveRefundTrace(refundsOrder);
	}

	
	@Test
	public void testSaveBuyerReturnTrace() {
		TradeOrderRefunds refundsOrder = new TradeOrderRefunds();
		refundsOrder.setId("8a98681b56115c7401561169e9ba000c");
		refundsOrder.setRefundsStatus(RefundsStatusEnum.WAIT_SELLER_REFUND);
		refundsOrder.setLogisticsType(RefundsLogisticsEnum.DOOR_PICK_UP);
		tradeOrderRefundsTraceService.saveRefundTrace(refundsOrder);
	}
	
	@Test
	public void testSellerRefund() {
		// 卖家同意退款
		TradeOrderRefunds refundsOrder = new TradeOrderRefunds();
		refundsOrder.setId("8a98681b56115c7401561169e9ba000c");
		refundsOrder.setRefundsStatus(RefundsStatusEnum.REFUND_SUCCESS);
		tradeOrderRefundsTraceService.saveRefundTrace(refundsOrder);
	}
	
	@Test
	public void findRefundsTrace(){
		Response<RefundsTraceResp> resp = tradeOrderRefundsTraceService.findRefundsTrace("12345678");
		System.out.println(JSONObject.toJSONString(resp));
	}
}
