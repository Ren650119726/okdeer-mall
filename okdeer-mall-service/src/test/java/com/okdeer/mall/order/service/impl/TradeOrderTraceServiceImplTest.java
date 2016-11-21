package com.okdeer.mall.order.service.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;

import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.order.constant.OrderTraceConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderTrace;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTraceEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.service.TradeOrderTraceService;
import com.okdeer.mall.order.vo.RefundsTraceResp;

/**
 * ClassName: TradeOrderTraceServiceImplTest 
 * @Description: 订单轨迹
 * @author maojj
 * @date 2016年11月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿1.2			2016年11月7日				maojj
 */
@RunWith(Parameterized.class)
public class TradeOrderTraceServiceImplTest extends BaseServiceTest{
	
	@Resource
	private TradeOrderTraceService tradeOrderTraceService;
	
	@Mock
	private TradeOrderMapper tradeOrderMapper;
	
	private String orderId;
	
	private List<TradeOrder> tradeOrderList;
	
	private TradeOrderTrace tradeOrderTrace;
	
	public TradeOrderTraceServiceImplTest(String orderId,List<TradeOrder> tradeOrder,TradeOrderTrace tradeOrderTrace){
		this.orderId = orderId;
		this.tradeOrderList = tradeOrder;
		this.tradeOrderTrace= tradeOrderTrace;
	}
	
	@Parameters
	public static Collection<Object[]> initParam(){
		List<TradeOrder> orderList = new ArrayList<TradeOrder>();
		// 提交订单
		TradeOrder order1 = new TradeOrder();
		order1.setId("8a8080a05793e3b501579406efe4008e");
		order1.setStatus(OrderStatusEnum.UNPAID);
		order1.setType(OrderTypeEnum.SERVICE_STORE_ORDER);
		order1.setActualAmount(BigDecimal.valueOf(9.00));
		order1.setOrderNo("FW0016100500032");
		
		// 订单完成支付
		TradeOrder order2 = new TradeOrder();
		order2.setId("8a8080a05793e3b501579406efe4008e");
		order2.setStatus(OrderStatusEnum.WAIT_RECEIVE_ORDER);
		order2.setType(OrderTypeEnum.SERVICE_STORE_ORDER);
		order2.setActualAmount(BigDecimal.valueOf(9.00));
		
		// 商家接单
		TradeOrder order3 = new TradeOrder();
		order3.setId("8a8080a05793e3b501579406efe4008e");
		order3.setStatus(OrderStatusEnum.DROPSHIPPING);
		order3.setType(OrderTypeEnum.SERVICE_STORE_ORDER);
		order3.setActualAmount(BigDecimal.valueOf(9.00));

		// 商家派单
		TradeOrder order4 = new TradeOrder();
		order4.setId("8a8080a05793e3b501579406efe4008e");
		order4.setStatus(OrderStatusEnum.TO_BE_SIGNED);
		order4.setType(OrderTypeEnum.SERVICE_STORE_ORDER);
		order4.setActualAmount(BigDecimal.valueOf(9.00));
		
		// 交易完成
		TradeOrder order5 = new TradeOrder();
		order5.setId("8a8080a05793e3b501579406efe4008e");
		order5.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
		order5.setType(OrderTypeEnum.SERVICE_STORE_ORDER);
		order5.setActualAmount(BigDecimal.valueOf(9.00));
		
		orderList.add(order1);
		orderList.add(order2);
		orderList.add(order3);
		orderList.add(order4);
		orderList.add(order5);
		
		TradeOrderTrace tradeTrace = new TradeOrderTrace();
		tradeTrace.setOrderId("8a94e4cb58867e4c0158868501510018");
		tradeTrace.setTraceStatus(OrderTraceEnum.COMPLETED);
		tradeTrace.setRemark(OrderTraceConstant.COMPLETED_APPRAISE_REMARK);
		
		return Arrays.asList(new Object[][]{
			{"8a8080a05793e3b501579406efe4008e",orderList,tradeTrace}
		});
		
		
	}
	
	@Test
	public void testSaveOrderTrace() {
		for(TradeOrder tradeOrder : tradeOrderList){
			this.tradeOrderTraceService.saveOrderTrace(tradeOrder);
		}
	}

	@Test
	public void testFindOrderTrace() {
		TradeOrder order = new TradeOrder();
		order.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
		given(this.tradeOrderMapper.selectByPrimaryKey("8a8080a05793e3b501579406efe4008e")).willReturn(order);
		Response<RefundsTraceResp> resp = tradeOrderTraceService.findOrderTrace(orderId);
		System.out.println(JsonMapper.nonDefaultMapper().toJson(resp));
	}
	

	@Test
	public void testUpdateRemarkAfterAppraise() {
		tradeOrderTraceService.updateRemarkAfterAppraise(tradeOrderTrace);
	}

}
