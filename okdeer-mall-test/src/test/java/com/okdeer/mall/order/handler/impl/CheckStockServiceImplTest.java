package com.okdeer.mall.order.handler.impl;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.mock.MockFilePath;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.RequestHandler;

public class CheckStockServiceImplTest extends AbstractHandlerTest implements MockFilePath{
	
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto> checkStockService;
	
	@Test
	public void testProcess() throws Exception{
		StoreSkuParserBo parserBo = MockStoreSkuParserBo.mockFromFile();
		Request<PlaceOrderParamDto> req = new Request<PlaceOrderParamDto>();
		PlaceOrderParamDto paramDto = new PlaceOrderParamDto();
		paramDto.put("parserBo", parserBo);
		req.setData(paramDto);
		Response<PlaceOrderDto> resp = initRespInstance();
		checkStockService.process(req, resp);
		assertEquals(0, resp.getCode());
	}
	
	/**
	 * @Description: 测试是否超出限款   
	 * @author maojj
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @date 2017年9月7日
	 */
	@Test
	public void testIsOutOfLimitKind() throws Exception{
		Method method = Arrays.asList(CheckStockServiceImpl.class.getDeclaredMethods()).stream()
				.filter(e -> "isOutOfLimitKind".equals(e.getName())).findFirst().get();
		//method.setAccessible(true);
		StoreSkuParserBo parserBo = MockStoreSkuParserBo.mock();
		Response<PlaceOrderDto> respMock = Mockito.mock(Response.class);
		// 1.不存在活动商品
		boolean isOutOfLimitKind = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(false, isOutOfLimitKind);
		// 2.存在活动商品。活动不限款
		MockStoreSkuParserBo.initUnLimitKind(parserBo);
		isOutOfLimitKind = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(false, isOutOfLimitKind);
		// 3.存在活动商品，活动限款但不超出限款
		MockStoreSkuParserBo.initInLimitKind(parserBo);
		isOutOfLimitKind = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(false, isOutOfLimitKind);
		// 3.存在活动商品，超出限款
		MockStoreSkuParserBo.initOutLimitKind(parserBo);
		isOutOfLimitKind = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(true, isOutOfLimitKind);
	}
	
	/**
	 * @Description: 测试是否超出限购
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年9月7日
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testIsOutOfLimitBuy() throws Exception{
		Method method = Arrays.asList(CheckStockServiceImpl.class.getDeclaredMethods()).stream()
				.filter(e -> "isOutOfLimitBuy".equals(e.getName())).findFirst().get();
		method.setAccessible(true);
		// 数据模拟
		StoreSkuParserBo parserBo = Mockito.mock(StoreSkuParserBo.class);
		List<PlaceOrderItemDto> skuList = Lists.newArrayList();
		Response<PlaceOrderDto> respMock = Mockito.mock(Response.class);
		// 1.不存在参加活动的商品
		boolean isOutOfLimitBuy = (boolean) method.invoke(checkStockService, parserBo,skuList,respMock);
		assertEquals(false, isOutOfLimitBuy);
		// 2.特价不限购
		parserBo = MockStoreSkuParserBo.mock();
		MockStoreSkuParserBo.initLowUnLimit(parserBo);
		isOutOfLimitBuy = (boolean) method.invoke(checkStockService, parserBo,skuList,respMock);
		// 特价购买数量
		int lowBuyNum = parserBo.getCurrentSkuMap().get("8a8080835e50ec28015e50f2f799000a").getSkuActQuantity();
		assertEquals(false, isOutOfLimitBuy);
		assertEquals(2,lowBuyNum);
		// 3.特价限购2件已购1件
		MockStoreSkuParserBo.initLowInLimit(parserBo);
		isOutOfLimitBuy = (boolean) method.invoke(checkStockService, parserBo,skuList,respMock);
		lowBuyNum = parserBo.getCurrentSkuMap().get("8a8080835e50ec28015e50f7b2750011").getSkuActQuantity();
		assertEquals(false, isOutOfLimitBuy);
		assertEquals(1,lowBuyNum);
		// 4.特价限购2件，且已购2件
		MockStoreSkuParserBo.initLowOutLimit(parserBo);
		isOutOfLimitBuy = (boolean) method.invoke(checkStockService, parserBo,skuList,respMock);
		lowBuyNum = parserBo.getCurrentSkuMap().get("8a8080835e50ec28015e50f2f799000a").getSkuActQuantity();
		assertEquals(false, isOutOfLimitBuy);
		assertEquals(0,lowBuyNum);
		// 5.特惠限购5件，且已购3件
		MockStoreSkuParserBo.initFavourInLimit(parserBo);
		isOutOfLimitBuy = (boolean) method.invoke(checkStockService, parserBo,skuList,respMock);
		assertEquals(false, isOutOfLimitBuy);
		// 6.特惠限购6件，且已购3件
		MockStoreSkuParserBo.initFavourOutLimit(parserBo);
		isOutOfLimitBuy = (boolean) method.invoke(checkStockService, parserBo,skuList,respMock);
		assertEquals(true, isOutOfLimitBuy);
	}

	@Test
	public void testIsOutOfStock() throws Exception{
		Method method = Arrays.asList(CheckStockServiceImpl.class.getDeclaredMethods()).stream()
				.filter(e -> "isOutOfStock".equals(e.getName())).findFirst().get();
		method.setAccessible(true);
		StoreSkuParserBo parserBo = MockStoreSkuParserBo.mockFromFile();
		Response<PlaceOrderDto> respMock = new Response<PlaceOrderDto>();
		// 1.特价商品购买特价数量>活动库存，且特价商品价格发生变化
		// 指定的特价商品
		CurrentStoreSkuBo storeSkuBo = parserBo.getCurrentSkuMap().get("8a8080f65e50f996015e55f1e99c0013");
		storeSkuBo.setLocked(2);
		storeSkuBo.setActPrice(BigDecimal.valueOf(6));
		boolean isOutOfStock = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(true, isOutOfStock);
		assertEquals(2,storeSkuBo.getSkuActQuantity());
		assertEquals(ResultCodeEnum.PART_GOODS_IS_CHANGE.getCode(),respMock.getCode());
		// 2.特价商品原价购买限购，且超出限购
		storeSkuBo.setLocked(50);
		storeSkuBo.setActPrice(BigDecimal.valueOf(3));
		storeSkuBo.setTradeMax(2);
		isOutOfStock = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(true, isOutOfStock);
		assertEquals(ResultCodeEnum.LOW_STOCK_NOT_ENOUGH.getCode(),respMock.getCode());
		
		// 3.组合商品参与特价，且特价购买数量超过活动库存
		storeSkuBo.setLocked(2);
		storeSkuBo.setTradeMax(0);
		isOutOfStock = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(true, isOutOfStock);
		assertEquals(ResultCodeEnum.LOW_STOCK_NOT_ENOUGH.getCode(),respMock.getCode());
		
		// 4.商品总共购买数量超过可售库存
		storeSkuBo.setSellable(2);
		storeSkuBo.setLocked(50);
		storeSkuBo.setSkuActQuantity(5);
		isOutOfStock = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(true, isOutOfStock);
		assertEquals(ResultCodeEnum.PART_GOODS_STOCK_NOT_ENOUGH.getCode(),respMock.getCode());
		
		//TODO 4.特价商品购买数量重新分配低于请求数量
		storeSkuBo.setSellable(100);
//		isOutOfStock = (boolean)method.invoke(checkStockService, parserBo,respMock);
//		assertEquals(false, isOutOfStock);
		
		// 5.特惠商品可售库存不足
		storeSkuBo = parserBo.getCurrentSkuMap().get("8a8080835e50ec28015e50f7b27b0015");
		storeSkuBo.setSellable(2);
		isOutOfStock = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(true, isOutOfStock);
		assertEquals(ResultCodeEnum.PART_GOODS_STOCK_NOT_ENOUGH.getCode(),respMock.getCode());
		
		// 6.特惠商品活动库存不足
		storeSkuBo.setSellable(100);
		storeSkuBo.setLocked(2);
		isOutOfStock = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(true, isOutOfStock);
		assertEquals(ResultCodeEnum.PART_GOODS_STOCK_NOT_ENOUGH.getCode(),respMock.getCode());
		
		// 7.单品库存不足
		storeSkuBo.setLocked(50);
		storeSkuBo = parserBo.getCurrentSkuMap().get("8a8080db5e55e692015e5602bac2000e");
		storeSkuBo.setSellable(2);
		isOutOfStock = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(true, isOutOfStock);
		assertEquals(ResultCodeEnum.PART_GOODS_STOCK_NOT_ENOUGH.getCode(),respMock.getCode());
		
		// 8.单品限购且超出限购
		storeSkuBo.setSellable(100);
		storeSkuBo.setTradeMax(1);
		isOutOfStock = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(true, isOutOfStock);
		assertEquals(ResultCodeEnum.TRADE_LIMIT_OVERFLOW.getCode(),respMock.getCode());
		
		// 9.捆绑商品明细不足
		storeSkuBo.setTradeMax(0);
		GoodsStoreSkuStock bindStock = parserBo.getBindStockMap().get("8a8080db5e55e692015e5602babf000c");
		bindStock.setSellable(1);
		isOutOfStock = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(true, isOutOfStock);
		assertEquals(ResultCodeEnum.PART_GOODS_STOCK_NOT_ENOUGH.getCode(),respMock.getCode());
		
		// 10.库存检查正常
		bindStock.setSellable(100);
		isOutOfStock = (boolean)method.invoke(checkStockService, parserBo,respMock);
		assertEquals(false, isOutOfStock);
	}
}
