/** 
 *@Project: okdeer-mall-order 
 *@Author: zengj
 *@Date: 2016年9月9日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.order.service;

/**
 * ClassName: TradeOrderCompleteProcessService 
 * @Description: 订单或退款单完成后同步处理Service
 * @author zengj
 * @date 2016年9月9日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.0.Z          2016年9月9日                               zengj				订单或退款单完成后同步处理Service
 */

public interface TradeOrderCompleteProcessService {

	/**
	 * 
	 * @Description: 订单完成时发送MQ消息同步到商业管理系统
	 * @param orderId 订单ID
	 * @author zengj
	 * @throws Exception 异常处理
	 * @date 2016年9月6日
	 */
	void orderCompleteSyncToJxc(String orderId) throws Exception;

	/**
	 * 
	 * @Description: 退款单完成时发送MQ消息同步到商业管理系统
	 * @param refundsId 退款单ID
	 * @author zengj
	 * @throws Exception 异常处理
	 * @date 2016年9月6日
	 */
	void orderRefundsCompleteSyncToJxc(String refundsId) throws Exception;
}
