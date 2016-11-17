package com.okdeer.mall.order.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.mall.Application;
import com.okdeer.mall.order.dto.PhysicalOrderApplyDto;
import com.okdeer.mall.order.dto.PhysicalOrderApplyParamDto;
import com.okdeer.mall.order.dto.StoreConsumerApplyDto;
import com.okdeer.mall.order.dto.StoreConsumerApplyParamDto;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.service.TradeOrderRefundsApi;

/**
 * ClassName: TradeOrderRefundsApiTest 
 * @Description: TradeOrderRefundsApi测试类
 * @author zengjizu
 * @date 2016年11月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     v1.2.0             2016-11-15        zengjz            增加退款测试方法
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class TradeOrderRefundsApiTest {

	@Autowired
	private TradeOrderRefundsApi tradeOrderRefundsApi;
	
	@Test
	public void testStoreConsumerApplyRefunds(){
		StoreConsumerApplyParamDto storeConsumerApplyParamDto = new StoreConsumerApplyParamDto();
		List<String> consumerIds = new ArrayList<>();
		consumerIds.add("8a80808d57ad46ca0157ad478a63000c");
		storeConsumerApplyParamDto.setConsumerIds(consumerIds);
		storeConsumerApplyParamDto.setOrderId("8a80808d57ad46ca0157ad46ca0a0000");
		storeConsumerApplyParamDto.setOrderItemId("8a80808d57ad46ca0157ad46cabc0002");
		storeConsumerApplyParamDto.setOrderResource(OrderResourceEnum.YSCAPP);
		storeConsumerApplyParamDto.setUserId("145873225909e79f954d5de54caba9b5");
		StoreConsumerApplyDto respDto = tradeOrderRefundsApi.storeConsumerApplyRefunds(storeConsumerApplyParamDto);
		Assert.assertTrue(respDto.getStatus() != 1);
	}
	
	/**
	 * @Description: 测试实物订单申请退款
	 * @author zengjizu
	 * @date 2016年11月15日
	 */
	@Test
	public void testPhysicalOrderApplyRefunds(){
		PhysicalOrderApplyParamDto physicalOrderApplyDto = new PhysicalOrderApplyParamDto();
		physicalOrderApplyDto.setOrderId("f8855f29276511e6aaff00163e010eb1");
		physicalOrderApplyDto.setOrderItemId("0000008e276611e6aaff00163e010eb1");
		physicalOrderApplyDto.setOrderResource(OrderResourceEnum.YSCAPP);
		physicalOrderApplyDto.setUserId("1452762514109e0dd565fa894b498051");
		physicalOrderApplyDto.setMemo("谁是谁");
		physicalOrderApplyDto.setReason("不喜欢");
		PhysicalOrderApplyDto respDto = tradeOrderRefundsApi.physicalOrderApplyRefunds(physicalOrderApplyDto);
		Assert.assertNotNull(respDto);
//		Assert.assertTrue(respDto.getStatus() != 1);
	}
	
}
