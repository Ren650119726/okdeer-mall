package com.okdeer.mall.order.service;

import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.vo.RefundsTraceResp;

/**
 * ClassName: TradeOrderTraceService 
 * @Description: 上门服务订单轨迹服务
 * @author maojj
 * @date 2016年11月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿1.2			2016年11月4日			maojj			      上门服务订单轨迹服务
 */
public interface TradeOrderTraceService {

	/**
	 * @Description: 保存上门服务订单轨迹
	 * @param tradeOrder 交易订单
	 * @author maojj
	 * @date 2016年11月4日
	 */
	void saveOrderTrace(TradeOrder tradeOrder);
	
	/**
	 * @Description: 查询订单轨迹
	 * @param orderId
	 * @return 响应对象，复用退款轨迹响应对象
	 * @author maojj
	 * @date 2016年11月7日
	 */
	public Response<RefundsTraceResp> findOrderTrace(String orderId);
}
