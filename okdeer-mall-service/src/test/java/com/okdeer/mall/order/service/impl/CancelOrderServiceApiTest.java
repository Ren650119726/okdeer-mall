package com.okdeer.mall.order.service.impl;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.mall.Application;
import com.okdeer.mall.order.dto.CancelOrderReqDto;
import com.okdeer.mall.order.dto.CancelOrderRespDto;
import com.okdeer.mall.order.enums.OrderCancelType;
import com.okdeer.mall.order.service.CancelOrderServiceApi;

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
	private CancelOrderServiceApi cancelOrderServiceApi;
	
	/**
	 * @Description: 取消订单
	 * @author zengjizu
	 * @date 2016年11月11日
	 */
	@Test
	public void testCancelOrder(){
		
		CancelOrderReqDto cancelOrderReqDto = new CancelOrderReqDto();
		cancelOrderReqDto.setCancelType(OrderCancelType.CANCEL_BY_BUYER);
		cancelOrderReqDto.setOrderId("000048e2276611e6aaff00163e010eb1");
		cancelOrderReqDto.setReason("商品不好");
		cancelOrderReqDto.setUserId("14508634421718ccdd2bf2bb49f9836b");
		CancelOrderRespDto cancelOrderRespDto = cancelOrderServiceApi.cancelOrder(cancelOrderReqDto);
		System.out.println(cancelOrderRespDto.getMsg());
		Assert.assertNotNull(cancelOrderRespDto);
		
	}

}
