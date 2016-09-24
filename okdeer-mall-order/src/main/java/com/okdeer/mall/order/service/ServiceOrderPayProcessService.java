/** 
 *@Project: yschome-mall-order 
 *@Author: zengj
 *@Date: 2016年7月16日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.api.pay.pay.dto.PayResponseDto;
import com.okdeer.mall.order.pay.entity.ResponseResult;

/**
 * ClassName: ServiceOrderPayProcessService 
 * @Description: 服务订单支付结果处理
 * @author zengj
 * @date 2016年7月16日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月16日                               zengj				新建接口类
 */

public interface ServiceOrderPayProcessService {

	/**
	 * 
	 * @Description: 第三方支付回调
	 * @param tradeOrder 订单信息
	 * @param thirdPay   支付结果
	 * @author zengj
	 * @throws Exception 异常处理
	 * @date 2016年7月16日
	 */
	void updateThirdPayResult(TradeOrder tradeOrder, PayResponseDto thirdPay) throws Exception;

	/**
	 * 
	 * @Description: 余额支付回调
	 * @param tradeOrder 订单信息
	 * @param yuePay   支付结果
	 * @author zengj
	 * @throws Exception 异常处理
	 * @date 2016年7月16日
	 */
	void updateYuePayResult(TradeOrder tradeOrder, ResponseResult yuePay) throws Exception;

}