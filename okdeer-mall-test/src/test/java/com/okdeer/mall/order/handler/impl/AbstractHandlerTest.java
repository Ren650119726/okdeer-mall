package com.okdeer.mall.order.handler.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;

public abstract class AbstractHandlerTest extends BaseServiceTest{

	protected static final JsonMapper JSONMAPPER = JsonMapper.nonDefaultMapper();

	protected static Collection<Object[]> initParam(String filePath) throws Exception {
		// 申明变量
		Collection<Object[]> initParams = new ArrayList<Object[]>();
		
		// 获取mock的json数据列表
		List<String> mockStrList = MockUtils.getMockData(filePath);
		// 解析JSON
		for (String mockStr : mockStrList) {
			initParams.add(
					new Object[] { JSONMAPPER.fromJson(mockStr, new TypeReference<Request<PlaceOrderParamDto>>() {
					}) }
			);
		}
	
		return initParams;
	}
	
	protected Response<PlaceOrderDto> initRespInstance(){
		Response<PlaceOrderDto> resp = new Response<PlaceOrderDto>();
		resp.setData(new PlaceOrderDto());
		return resp;
	}
}
