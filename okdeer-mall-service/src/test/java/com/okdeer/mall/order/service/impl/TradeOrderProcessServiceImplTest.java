/** 
 *@Project: okdeer-mall-service 
 *@Author: yangq
 *@Date: 2016年10月31日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */

package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.base.BaseTest;
import com.okdeer.mall.order.service.TradeOrderProcessServiceApi;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderRespDto;

import net.sf.json.JSONObject;

/**
 * ClassName: TradeOrderProcessServiceImplTest 
 * @Description: TODO
 * @author yangq
 * @date 2016年10月31日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class TradeOrderProcessServiceImplTest extends BaseTest{

	@Resource
	private TradeOrderProcessServiceApi tradeOrderProcessServiceApi;
	
	/**
	 * 
	 * @Description: 结算单元测试 </p>
	 * @author yangq
	 * @date 2016年10月31日
	 */
	@Test
	public void testValidateStoreSkuStock() {

		// 结算入参
		String requestStr = "{\"data\": {\"storeId\": \"5646258e276511e6aaff00163e010eb1\",\"startMoney\": \"1.00\",\"list\": [{\"skuNum\": \"1\",\"skuPrice\": \"0.01\",\"skuId\": \"001f3c6e276511e6aaff00163e010eb1\",\"updateTime\": \"2016-10-07 19:08:03\",\"isPrivilege\": \"0\"}],\"userId\": \"14527626891242d4d00a207c4d69bd80\",\"fare\": \"5.00\"}}";
		try {
			TradeOrderRespDto respDto = tradeOrderProcessServiceApi.validateStoreSkuStock(JsonMapper.nonDefaultMapper().fromJson(requestStr, TradeOrderReqDto.class));
			//Assert.assertNull("should be null", respDto); // 查看对象是否为空
			Assert.assertNotNull("should not be null", respDto); // 查看对象是否不为空
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @Description: 确认订单单元测试</p>
	 * @author yangq
	 * @date 2016年10月31日
	 */
	@Test
	public void testAddTradeOrder() {

		String comfirmStr = "{\"data\": { \"recordId\": \"\",\"activityItemId\": \"\", \"storeName\":\"国际公馆一、二期快送店\",\"remark\":\"\",\"pickTime\":\"\",\"fare\":\"0.00\",\"list\":[{\"skuId\":\"001f3c6e276511e6aaff00163e010eb1\",\"skuNum\":\"1\",\"skuPrice\":\"440\",\"updateTime\":\"2015-12-25 08:44:32\",\"isPrivilege\": \"0\"}],\"couponsType\":\"\",\"type\":\"0\",\"orderResource\":\"0\",\"invoiceHead\":\"\",\"addressId\":\"000253b3276311e6aaff00163e010eb1\",\"activityId\":\"\",\"isInvoice\":\"0\",\"userId\":\"00a3c1a8a1b949e5b5e27efc6a377525\",\"receiveTime\": \"\",\"payType\": \"1\",\"userPhone\":\"13925296062\",\"activityType\":\"0\",\"storeId\":\"5646258e276511e6aaff00163e010eb1\",\"pickType\":\"0\",\"invoiceContent\":\"\"}}";

		try {
			TradeOrderRespDto respDto = tradeOrderProcessServiceApi.addTradeOrder(comfirmStr);
			Assert.assertNotNull("should not be null", respDto); // 查看对象是否不为空
			Assert.assertNull("should be null", respDto); // 查看对象是否为空
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	
}
