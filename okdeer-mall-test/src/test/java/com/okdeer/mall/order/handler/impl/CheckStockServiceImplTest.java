package com.okdeer.mall.order.handler.impl;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.mockito.Mock;

import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.mock.MockFilePath;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.RequestHandler;


public class CheckStockServiceImplTest extends AbstractHandlerTest implements MockFilePath{
	
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto> checkStockService;
	
	@Mock
	private StoreSkuParserBo parserBo;
	
	@Mock
	private List<PlaceOrderItemDto> skuList;
	
	@Mock
	private Response<PlaceOrderDto> resp;

	/**
	 * @Description: 测试是否超出限款   
	 * @author maojj
	 * @date 2017年9月7日
	 */
	@Test
	public void testIsOutOfLimitKind(){
		
	}
	
	/**
	 * @Description: 测试是否超出限购
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年9月7日
	 */
	@Test
	public void testIsOutOfLimitBuy() throws Exception{
		Method method = Arrays.asList(CheckStockServiceImpl.class.getDeclaredMethods()).stream()
				.filter(e -> "isOutOfLimitBuy".equals(e.getName())).findFirst().get();
		method.setAccessible(true);
		boolean isOutOfLimitBuy = (boolean) method.invoke(checkStockService, parserBo,skuList,resp);
		assertEquals(false, isOutOfLimitBuy);
	}

}
