package com.okdeer.mall.order.api;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.order.service.TradeOrderTraceService;
import com.okdeer.mall.order.service.TradeOrderTraceServiceApi;
import com.okdeer.mall.order.vo.RefundsTraceResp;

/**
 * ClassName: TradeOrderTraceServiceApiImpl 
 * @Description: 交易订单轨迹服务
 * @author maojj
 * @date 2016年11月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿1.2			2016年11月10日				maojj		交易订单轨迹服务
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderTraceServiceApi")
public class TradeOrderTraceServiceApiImpl implements TradeOrderTraceServiceApi {

	@Resource
	private TradeOrderTraceService tradeOrderTraceService;
	
	@Override
	public Response<RefundsTraceResp> findOrderTrace(String orderId) {
		return tradeOrderTraceService.findOrderTrace(orderId);
	}

}
