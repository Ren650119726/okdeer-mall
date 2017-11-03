package com.okdeer.mall.order.builder;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;


public class TradeOrderBuilderTest {
	
	private TradeOrderBuilder builder = new TradeOrderBuilder();

	@Test
	public void testAllocatePinMoney() throws Exception {
		Method method = Arrays.asList(TradeOrderBuilder.class.getDeclaredMethods()).stream()
				.filter(e -> "allocatePinMoney".equals(e.getName())).findFirst().get();
		method.setAccessible(true);
		
		TradeOrderItem orderItem = new TradeOrderItem();
		orderItem.setActualAmount(BigDecimal.valueOf(0.10));
		orderItem.setPreferentialPrice(BigDecimal.valueOf(9.9));
		
		TradeOrderItem orderItem1 = new TradeOrderItem();
		orderItem1.setActualAmount(BigDecimal.valueOf(0.01));
		orderItem1.setPreferentialPrice(BigDecimal.valueOf(0));
		
		TradeOrderItem orderItem2 = new TradeOrderItem();
		orderItem2.setActualAmount(BigDecimal.valueOf(0.01));
		orderItem2.setPreferentialPrice(BigDecimal.valueOf(0));
		
		List<TradeOrderItem> itemList = Arrays.asList(new TradeOrderItem[]{orderItem,orderItem1,orderItem2});
		
		BigDecimal pinMoney = BigDecimal.valueOf(0.11);
		
		method.invoke(builder, pinMoney,itemList);
	}
	
	@Test
	public void testSetActualAmount() throws Exception{
		Method method = Arrays.asList(TradeOrderBuilder.class.getDeclaredMethods()).stream()
				.filter(e -> "setActualAmount".equals(e.getName())).findFirst().get();
		method.setAccessible(true);
		
		TradeOrder tradeOrder = new TradeOrder();
		tradeOrder.setTotalAmount(BigDecimal.valueOf(0.01));
		tradeOrder.setPreferentialPrice(BigDecimal.ZERO);
		tradeOrder.setStorePreferential(BigDecimal.ZERO);
		
		PlaceOrderParamDto paramDto = new PlaceOrderParamDto();
		paramDto.setOrderType("1");
		paramDto.put("pinMoneyAmount",BigDecimal.ZERO);
		
		method.invoke(builder, tradeOrder,paramDto);
		assertEquals(BigDecimal.valueOf(0.01), tradeOrder.getActualAmount());
	}

}
