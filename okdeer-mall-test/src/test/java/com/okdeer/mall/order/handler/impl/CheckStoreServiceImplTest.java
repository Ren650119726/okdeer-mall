package com.okdeer.mall.order.handler.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.api.mock.StoreMock;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;

@RunWith(Parameterized.class)
public class CheckStoreServiceImplTest extends BaseServiceTest{
	
	private static final Logger logger = LoggerFactory.getLogger(CheckStoreServiceImplTest.class);
	
	private static final String MOCK_FILE_PATH = BASE_CLASS_PATH + "order/handler/impl/mock-order-param.json";
	
	private static final JsonMapper jsonMapper = JsonMapper.nonDefaultMapper();
	
	@Resource
	private RequestHandler<PlaceOrderParamDto, PlaceOrderDto>  checkStoreService;
	
	@Mock
	private StoreInfoServiceApi storeInfoServiceApi;
	
	/**
	 * 下单请求Dto
	 */
	private Request<PlaceOrderParamDto> req;
	
	public CheckStoreServiceImplTest(Request<PlaceOrderParamDto> req){
		this.req = req;
	}
	
	@Override
	public void initMocks(){
		CheckStoreServiceImpl checkStoreService = this.ac.getBean(CheckStoreServiceImpl.class);
		ReflectionTestUtils.setField(checkStoreService, "storeInfoServiceApi", storeInfoServiceApi);
	}
	
	@Parameters
	public static Collection<Object[]> initParam() throws Exception {
		// 申明变量
		Collection<Object[]> initParams = new ArrayList<Object[]>();
		
		// 获取mock的json数据列表
		List<String> mockStrList = MockUtils.getMockData(MOCK_FILE_PATH);
		// 解析JSON
		for (String mockStr : mockStrList) {
			initParams.add(
					new Object[] { jsonMapper.fromJson(mockStr, new TypeReference<Request<PlaceOrderParamDto>>() {
					}) }
			);
		}
	
		return initParams;
	}

	@Test
	public void testProcess() throws Exception {
		// 模拟店铺不存在
		Response<PlaceOrderDto> resp = initRespInstance();
		given(storeInfoServiceApi.getStoreInfoById(anyString())).willReturn(null);
		this.checkStoreService.process(req, resp);
		assertEquals(ResultCodeEnum.SERVER_STORE_NOT_EXISTS.getCode(), resp.getCode());
		
		// 模拟店铺已关闭
		resp = initRespInstance();
		given(storeInfoServiceApi.getStoreInfoById(anyString())).willReturn(StoreMock.mockClosed());
		this.checkStoreService.process(req, resp);
		assertEquals(ResultCodeEnum.STORE_IS_CLOSED.getCode(), resp.getCode());
		
		// 模拟店铺已暂停营业
		resp = initRespInstance();
		given(storeInfoServiceApi.getStoreInfoById(anyString())).willReturn(StoreMock.mockNotBusiness());
		this.checkStoreService.process(req, resp);
		assertEquals(ResultCodeEnum.CVS_IS_PAUSE.getCode(), resp.getCode());
		
		// 模拟店铺不跨天营业且非营业时间不接单
		resp = initRespInstance();
		req.getData().setOrderOptType(OrderOptTypeEnum.ORDER_SETTLEMENT);
		given(storeInfoServiceApi.getStoreInfoById(anyString())).willReturn(StoreMock.mockInDay());
		this.checkStoreService.process(req, resp);
		assertEquals(ResultCodeEnum.CVS_IS_PAUSE.getCode(), resp.getCode());
		
		// 模拟店铺跨天营业且非营业时间不接单
		resp = initRespInstance();
		req.getData().setOrderOptType(OrderOptTypeEnum.ORDER_SETTLEMENT);
		given(storeInfoServiceApi.getStoreInfoById(anyString())).willReturn(StoreMock.mockInterDay());
		this.checkStoreService.process(req, resp);
		assertEquals(ResultCodeEnum.CVS_IS_PAUSE.getCode(), resp.getCode());
		
		// 模拟店铺非营业时间接单
		resp = initRespInstance();
		req.getData().setOrderOptType(OrderOptTypeEnum.ORDER_SETTLEMENT);
		given(storeInfoServiceApi.getStoreInfoById(anyString())).willReturn(StoreMock.mockIsAccept());
		this.checkStoreService.process(req, resp);
		assertEquals(ResultCodeEnum.SUCCESS.getCode(), resp.getCode());
	}

	private Response<PlaceOrderDto> initRespInstance(){
		Response<PlaceOrderDto> resp = new Response<PlaceOrderDto>();
		resp.setData(new PlaceOrderDto());
		return resp;
	}
}
