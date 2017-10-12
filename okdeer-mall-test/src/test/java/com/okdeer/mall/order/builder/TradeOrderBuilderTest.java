package com.okdeer.mall.order.builder;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

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

}
