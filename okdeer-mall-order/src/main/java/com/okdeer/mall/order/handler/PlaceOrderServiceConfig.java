package com.okdeer.mall.order.handler;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;

@Configuration
public class PlaceOrderServiceConfig {

	/**
	 * 店铺校验的service
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  checkStoreService;

	/**
	 * 商品校验的Service
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  checkSkuService;

	/**
	 * 库存校验的Service
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  checkStockService;
	
	/**
	 * 用户地址查询的service
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  findUserAddrService;

	/**
	 * 查询优惠的Service
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  findFavourService;
	
	/**
	 * 下单的Service
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  placeOrderService;
	
	@Bean(name="confirmOrderService")
	public RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> confirmOrderService() {
		RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> chain = new RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto>();
		// 第一步 ：校验店铺
		chain.addHandlerChain(checkStoreService);
		// 第二步：校验商品
		chain.addHandlerChain(checkSkuService);
		// 第三步：校验商品库存
		chain.addHandlerChain(checkStockService);
		// 第四步：查询最优用户地址
		chain.addHandlerChain(findUserAddrService);
		// 第五步：查询用户有效的优惠信息
		chain.addHandlerChain(findFavourService);
		return chain;
	}
	
	@Bean(name="submitOrderService")
	public RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> submitOrderService() {
		RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> chain = new RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto>();
		// 第一步 ：校验店铺
		chain.addHandlerChain(checkStoreService);
		// 第二步：校验商品
		chain.addHandlerChain(checkSkuService);
		// 第三步：校验商品库存
		chain.addHandlerChain(checkStockService);
		// 第四步：校验用户优惠信息
		// chain.addHandlerChain(findFavourService);
		// 第五步：生成订单
		chain.addHandlerChain(placeOrderService);
		return chain;
	}
}
