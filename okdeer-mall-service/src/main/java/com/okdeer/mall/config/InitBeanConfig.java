package com.okdeer.mall.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.handler.RequestHandlerChain;
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
	 * 库存
	 */
	@Resource
	private RequestHandler<ServiceOrderReq, ServiceOrderResp> servStockCheckService;
	
	/**
	 * 活动查询
	 */
	@Resource
	private RequestHandler<ServiceOrderReq, ServiceOrderResp> servActivityQueryService;
	
	/**
	 * 活动校验
	 */
	@Resource
	private RequestHandler<ServiceOrderReq, ServiceOrderResp> servActivityCheckService;
	
	/**
	 * 新增服务订单（上门服务，到店消费）
	 */
	@Resource
	private RequestHandler<ServiceOrderReq, ServiceOrderResp> servOrderSubmitService;
	
	/**
	 * 商品校验（上门服务，到店消费）
	 */
	@Resource
	private RequestHandler<ServiceOrderReq, ServiceOrderResp> serviceGoodsCheckService;
	
	/**
	 * 服务地址查询（上门服务，到店消费）
	 */
	@Resource
	private RequestHandler<ServiceOrderReq, ServiceOrderResp> servAddressSearchService;

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
	
	// begin add by wushp 20160927 V1.1.0
	/**
	 * 
	 * @Description: 创建服务订单确认的处理链
	 * @return RequestHandlerChain
	 * @author wushp
	 * @date 2016年9月27日
	 */
	@Bean
	public RequestHandlerChain<ServiceOrderReq, ServiceOrderResp> confirmServiceOrderChain() {
		RequestHandlerChain<ServiceOrderReq, ServiceOrderResp> chain = new RequestHandlerChain<ServiceOrderReq, ServiceOrderResp>();
		// 第一步 ：服务栏目校验
		chain.addHandlerChain(servColumnCheckService);
		// 第二步：服务店铺校验
		chain.addHandlerChain(servStoreCheckService);
		// 第三步：商品校验
		chain.addHandlerChain(serviceGoodsCheckService);
		// 第四步：库存校验
		chain.addHandlerChain(servStockCheckService);
		// 第五步：获取服务地址地址
		chain.addHandlerChain(servAddressSearchService);
		// 第六步：活动查询
		chain.addHandlerChain(servActivityQueryService);
		return chain;
	}
	
	/**
	 * 
	 * @Description: 创建服务订单确认的处理链
	 * @return RequestHandlerChain
	 * @author wushp
	 * @date 2016年9月27日
	 */
	@Bean
	public RequestHandlerChain<ServiceOrderReq, ServiceOrderResp> submitServiceOrderChain() {
		RequestHandlerChain<ServiceOrderReq, ServiceOrderResp> chain = new RequestHandlerChain<ServiceOrderReq, ServiceOrderResp>();
		// 第一步 ：服务栏目校验
		chain.addHandlerChain(servColumnCheckService);
		// 第二步：服务店铺校验
		chain.addHandlerChain(servStoreCheckService);
		// 第三步：商品校验
		chain.addHandlerChain(serviceGoodsCheckService);
		// 第四步：库存校验
		chain.addHandlerChain(servStockCheckService);
		// 第五步：活动校验
		chain.addHandlerChain(servActivityCheckService);
		// 第六步：添加服务订单
		chain.addHandlerChain(servOrderSubmitService);
		
		return chain;
	}
	// end add by wushp 20160927 V1.1.0
	
}
