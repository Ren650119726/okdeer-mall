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
	

	
}
