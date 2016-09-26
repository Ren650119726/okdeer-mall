package com.okdeer.mall.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.handler.RequestHandlerChain;
import com.okdeer.mall.order.service.impl.SecKillCheckServiceImpl;
import com.okdeer.mall.order.service.impl.SeckillAddressSearchServiceImpl;
import com.okdeer.mall.order.service.impl.ServColumnCheckServiceImpl;
import com.okdeer.mall.order.service.impl.ServGoodsCheckServiceImpl;
import com.okdeer.mall.order.service.impl.ServStoreCheckServiceImpl;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;

/**
 * ClassName: InitBeanConfig 
 * @Description: 初始化对象配置
 * @author maojj
 * @date 2016年9月22日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构1.1			2016年9月22日				maojj		初始化对象配置
 */
@Configuration
public class InitBeanConfig {

	/**
	 * 服务栏目校验的Service
	 */
	@Resource
	private RequestHandler<ServiceOrderReq, ServiceOrderResp>  servColumnCheckService;

	/**
	 * 服务店铺校验的Service
	 */
	@Resource
	private RequestHandler<ServiceOrderReq, ServiceOrderResp>  servStoreCheckService;

	/**
	 * 秒杀活动校验的Service
	 */
	@Resource
	private RequestHandler<ServiceOrderReq, ServiceOrderResp>  secKillCheckService;

	/**
	 * 秒杀商品校验的Service
	 */
	@Resource
	private RequestHandler<ServiceOrderReq, ServiceOrderResp>  servGoodsCheckService;

	/**
	 * 秒杀地址查询的Service
	 */
	@Resource
	private RequestHandler<ServiceOrderReq, ServiceOrderResp>  seckillAddressSearchService;
	
	/**
	 * 新增秒杀订单
	 */
	@Resource
	private RequestHandler<ServiceOrderReq, ServiceOrderResp> servOrderAddService;

	/**
	 * @Description: 创建服务栏目确认的处理链
	 * @return   
	 * @author maojj
	 * @date 2016年9月22日
	 */
	@Bean
	public RequestHandlerChain<ServiceOrderReq, ServiceOrderResp> confirmSeckillOrderChain() {
		RequestHandlerChain<ServiceOrderReq, ServiceOrderResp> chain = new RequestHandlerChain<ServiceOrderReq, ServiceOrderResp>();
		// 第一步 ：服务栏目校验
		chain.addHandlerChain(servColumnCheckService);
		// 第二步：服务店铺校验
		chain.addHandlerChain(servStoreCheckService);
		// 第三步：秒杀活动校验
		chain.addHandlerChain(secKillCheckService);
		// 第四步：秒杀商品校验
		chain.addHandlerChain(servGoodsCheckService);
		// 第五步：获取秒杀地址
		chain.addHandlerChain(seckillAddressSearchService);
		return chain;
	}

	@Bean
	public RequestHandlerChain<ServiceOrderReq, ServiceOrderResp> submitSeckillOrderChain() {
		RequestHandlerChain<ServiceOrderReq, ServiceOrderResp> chain = new RequestHandlerChain<ServiceOrderReq, ServiceOrderResp>();
		// 第一步 ：服务栏目校验
		chain.addHandlerChain(servColumnCheckService);
		// 第二步：服务店铺校验
		chain.addHandlerChain(servStoreCheckService);
		// 第三步：秒杀活动校验
		chain.addHandlerChain(secKillCheckService);
		// 第四步：秒杀商品校验
		chain.addHandlerChain(servGoodsCheckService);
		// 第五步：添加秒杀订单
		chain.addHandlerChain(servOrderAddService);
		return chain;
	}
}
