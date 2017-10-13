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
	 * 优惠信息校验
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  checkFavourService;
	
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
	
	/**
	 * 校验服务商品
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  checkServSkuService;
	
	/**
	 * 校验服务商品库存
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  checkServStockService;
	
	/**
	 * 校验服务商品库存
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  checkSecKillService;
	
	/**
	 * 校验服务商品库存
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  placeSeckillOrderService;
	
	/**
	 * 查询零花钱信息
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto> findPinMoneyService;
	
	/**
	 * 检测零花钱
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto> checkPinMoneyService;
	
	// Begin V2.6.3 added by maojj 2017-10-12
	/**
	 * 检查团购商品信息
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto> checkGroupSkuService;
	
	/**
	 * 检查团购活动信息
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto> checkGroupActivityService;
	
	/**
	 * 生成团购订单
	 */
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto> placeGroupOrderService;
	// End V2.6.3 added by maojj 2017-10-12
	
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
		// 查询零花钱信息
		chain.addHandlerChain(findPinMoneyService);
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
	    chain.addHandlerChain(checkFavourService);
		// 校验零花钱信息
		chain.addHandlerChain(checkPinMoneyService);
		// 第五步：生成订单
		chain.addHandlerChain(placeOrderService);
		return chain;
	}
	
	@Bean(name="confirmServOrderService")
	public RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> confirmServOrderService() {
		RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> chain = new RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto>();
		// 第一步 ：校验店铺
		chain.addHandlerChain(checkStoreService);
		// 第二步：校验商品
		chain.addHandlerChain(checkServSkuService);
		// 第三步：校验商品库存
		chain.addHandlerChain(checkServStockService);
		// 第四步：查询最优用户地址
		chain.addHandlerChain(findUserAddrService);
		// 查询零花钱信息
		chain.addHandlerChain(findPinMoneyService);
		// 第五步：查询用户有效的优惠信息
		chain.addHandlerChain(findFavourService);
		return chain;
	}
	
	@Bean(name="submitServOrderService")
	public RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> submitServOrderService() {
		RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> chain = new RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto>();
		// 第一步 ：校验店铺
		chain.addHandlerChain(checkStoreService);
		// 第二步：校验商品
		chain.addHandlerChain(checkServSkuService);
		// 第三步：校验商品库存
		chain.addHandlerChain(checkServStockService);
		// 第四步：校验用户优惠信息
        chain.addHandlerChain(checkFavourService);
		// 校验零花钱信息
		chain.addHandlerChain(checkPinMoneyService);
		// 第五步：生成订单
		chain.addHandlerChain(placeOrderService);
		return chain;
	}
	
	@Bean(name="confirmSeckillOrderService")
	public RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> confirmSeckillOrderService() {
		RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> chain = new RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto>();
		// 第一步 ：校验店铺
		chain.addHandlerChain(checkStoreService);
		// 第二步：校验秒杀
		chain.addHandlerChain(checkSecKillService);
		// 第三步：检查秒杀商品
		chain.addHandlerChain(checkServSkuService);
		// 第四步：校验商品库存
		chain.addHandlerChain(checkServStockService);
		// 查询零花钱信息
//		chain.addHandlerChain(findPinMoneyService);
		// 第五步：查询最优用户地址
		chain.addHandlerChain(findUserAddrService);
		return chain;
	}
	
	@Bean(name="submitSeckillOrderService")
	public RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> submitSeckillOrderService() {
		RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> chain = new RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto>();
		// 第一步 ：校验店铺
		chain.addHandlerChain(checkStoreService);
		// 第二步：校验秒杀
		chain.addHandlerChain(checkSecKillService);
		// 第三步：检查秒杀商品
		chain.addHandlerChain(checkServSkuService);
		// 第四步：校验商品库存
		chain.addHandlerChain(checkServStockService);
		// 校验零花钱信息
//		chain.addHandlerChain(checkPinMoneyService);
		// 第五步：生成订单
		chain.addHandlerChain(placeSeckillOrderService);
		return chain;
	}
	
	@Bean(name="confirmGroupOrderService")
	public RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> confirmGroupOrderService() {
		RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> chain = new RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto>();
		// 第一步 ：校验店铺
		chain.addHandlerChain(checkStoreService);
		// 第二步：检查团购商品
		chain.addHandlerChain(checkGroupSkuService);
		// 第三步：检查团购活动
		chain.addHandlerChain(checkGroupActivityService);
		// 第四步：查询最优用户地址
		chain.addHandlerChain(findUserAddrService);
		return chain;
	}
	
	@Bean(name="submitGroupOrderService")
	public RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> submitGroupOrderService() {
		RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> chain = new RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto>();
		// 第一步 ：校验店铺
		chain.addHandlerChain(checkStoreService);
		// 第二步：检查团购商品
		chain.addHandlerChain(checkGroupSkuService);
		// 第三步：检查团购活动
		chain.addHandlerChain(checkGroupActivityService);
		// 第四步：生成订单
		chain.addHandlerChain(placeGroupOrderService);
		return chain;
	}
}
