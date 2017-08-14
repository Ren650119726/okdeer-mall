/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年8月9日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.service.TradePinMoneyObtainService;

/**
 * ClassName: FindPinMoneyServiceImpl 
 * @Description: 订单零花钱余额处理
 * @author guocp
 * @date 2017年8月9日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service("findPinMoneyService")
public class FindPinMoneyServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto>{

	@Autowired
	private TradePinMoneyObtainService tradePinMoneyObtainService;
	
	/**
	 * 零花钱余额处理
	 */
	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		BigDecimal myUsable = tradePinMoneyObtainService.findMyUsableTotal(req.getData().getUserId(),new Date());
		//设置我的可用零花钱总额
		resp.getData().setUsablePinMoney(myUsable.toString());
	}

}
