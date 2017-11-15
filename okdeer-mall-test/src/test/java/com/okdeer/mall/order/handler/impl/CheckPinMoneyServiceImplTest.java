package com.okdeer.mall.order.handler.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.service.TradePinMoneyObtainService;

@RunWith(MockitoJUnitRunner.class)  
public class CheckPinMoneyServiceImplTest {
	
	private CheckPinMoneyServiceImpl checkPinMoneyService;

	@Mock
	private TradePinMoneyObtainService tradePinMoneyObtainService;
	
	@Before
	public void setUp(){
		checkPinMoneyService = new CheckPinMoneyServiceImpl();
	    ReflectionTestUtils.setField(checkPinMoneyService, "tradePinMoneyObtainService", tradePinMoneyObtainService);
	}
	
	@Test
	public void testProcess() throws Exception {
		// 构建请求数据
		Request<PlaceOrderParamDto> req = new Request<PlaceOrderParamDto>();
		Response<PlaceOrderDto> resp = new Response<PlaceOrderDto>();
		PlaceOrderParamDto paramDto = new PlaceOrderParamDto();
		req.setData(paramDto);
		// 1.测试不使用零花钱
		paramDto.setIsUsePinMoney("0");
		checkPinMoneyService.process(req, resp);
		assertEquals(null, paramDto.get("pinMoneyAmount"));
		// 2.测试使用零花钱但是两花钱金额为null
		paramDto.setIsUsePinMoney("1");
		checkPinMoneyService.process(req, resp);
		assertEquals(null, paramDto.get("pinMoneyAmount"));
		// 3.测试使用零花钱，但是零花钱不足
		paramDto.setPinMoney("2.00");
		given(tradePinMoneyObtainService.findMyUsableTotal(anyString(), any())).willReturn(BigDecimal.valueOf(1.00));
		checkPinMoneyService.process(req, resp);
		assertEquals(ResultCodeEnum.TRADE_LIMIT_PIN_MONEY.getCode(),resp.getCode());
		// 4.测试使用零花钱，且零花钱足够
		paramDto.setPinMoney("0.50");
		checkPinMoneyService.process(req, resp);
		assertEquals(BigDecimal.valueOf(1.00), paramDto.get("pinMoneyAmount"));
	}

}
