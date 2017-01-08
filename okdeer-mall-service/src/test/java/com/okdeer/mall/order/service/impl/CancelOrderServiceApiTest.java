package com.okdeer.mall.order.service.impl;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.mall.Application;
import com.okdeer.mall.order.dto.CancelOrderDto;
import com.okdeer.mall.order.dto.CancelOrderParamDto;
import com.okdeer.mall.order.dto.UserRefuseDto;
import com.okdeer.mall.order.dto.UserRefuseParamDto;
import com.okdeer.mall.order.enums.OrderCancelType;
import com.okdeer.mall.order.service.CancelOrderApi;

/**
 * ClassName: CancelOrderServiceApiTest 
 * @Description: 取消订单服务测试类
 * @author zengjizu
 * @date 2016年11月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *   v1.2.0              2016-11-11         zengjz          增加testCancelOrder测试方法
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class CancelOrderServiceApiTest{
	
	
	@Autowired
	private CancelOrderApi cancelOrderApi;
	
	/**
	 * @Description: 取消订单
	 * @author zengjizu
	 * @date 2016年11月11日
	 */
	@Test
	@Rollback(value=true)
	public void testCancelOrder(){
		
		CancelOrderParamDto cancelOrderParamDto = new CancelOrderParamDto();
		cancelOrderParamDto.setCancelType(OrderCancelType.CANCEL_BY_BUYER);
		cancelOrderParamDto.setOrderId("8a94e4095884c533015886145e99000f");
		cancelOrderParamDto.setReason("商品不好");
		cancelOrderParamDto.setUserId("145312257950d7a66015194e478d8594");
		CancelOrderDto cancelOrderRespDto = cancelOrderApi.cancelOrder(cancelOrderParamDto);
		System.out.println(cancelOrderRespDto.getMsg());
		Assert.assertNotNull(cancelOrderRespDto);
		
	}
	
	@Test
	public void testUserRefuse(){
		UserRefuseParamDto userRefuseParamDto = new UserRefuseParamDto();
		userRefuseParamDto.setOrderId("8a94e4cb5862e252015862e378e70001");
		userRefuseParamDto.setReason("不想要了");
		userRefuseParamDto.setUserId("14527626891242d4d00a207c4d69bd80");
		UserRefuseDto userRefuseDto =  cancelOrderApi.userRefuse(userRefuseParamDto);
		Assert.assertNotNull(userRefuseDto);
	}

	@Test
	public void testIsBreach(){
		String orderId = "8a94e4095884c533015886145e99000f";
		try {
			boolean bln = cancelOrderApi.isBreach(orderId);
			System.out.println(bln);
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.assertTrue(false);
		}
	}
}
