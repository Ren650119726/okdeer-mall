package com.okdeer.mall.order.api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.core.io.ClassPathResource;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.okdeer.ca.common.mapper.JsonMapper;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.service.PlaceOrderApi;

@RunWith(Parameterized.class)
public class PlaceOrderApiImplTest extends BaseServiceTest {

	@Resource
	private PlaceOrderApi placeOrderApi;

	private Request<PlaceOrderParamDto> confirmReq;

	private Request<PlaceOrderParamDto> submitReq;

	public PlaceOrderApiImplTest(Request<PlaceOrderParamDto> confirmReq, Request<PlaceOrderParamDto> submitReq) {
		this.confirmReq = confirmReq;
		this.submitReq = submitReq;
	}

	@Parameters
	public static Collection<Object[]> initParam() throws Exception {
		Map<String, List<String>> dataMap = readReqData();
		Collection<Object[]> initParams = new ArrayList<Object[]>();
		List<String> confirmList = dataMap.get("confirm");
		List<String> submitList = dataMap.get("submit");
		int size = confirmList.size() > submitList.size() ? submitList.size() : confirmList.size();
		for (int i = 0; i < size; i++) {
			initParams.add(new Object[] { parseObject(confirmList.get(i)), parseObject(submitList.get(i)) });
		}
		return initParams;
	}

	private static Map<String, List<String>> readReqData() throws Exception {
		Map<String, List<String>> reqDataMap = new HashMap<String, List<String>>();
		reqDataMap.put("confirm", new ArrayList<String>());
		reqDataMap.put("submit", new ArrayList<String>());

		ClassPathResource resource = new ClassPathResource("/com/okdeer/mall/order/api/orderReq.txt");
		BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()));
		StringBuilder sb = null;
		String line = null;
		while ((line = reader.readLine()) != null) {
			if ("confirm start---".equals(line) || "submit start---".equals(line)) {
				sb = new StringBuilder();
			} else if ("confirm end---".equals(line)) {
				reqDataMap.get("confirm").add(sb.toString());
			} else if ("submit start---".equals(line)) {
				sb = new StringBuilder();
			} else if ("submit end---".equals(line)) {
				reqDataMap.get("submit").add(sb.toString());
			} else {
				sb.append(line);
			}
		}
		return reqDataMap;
	}

	private static Request<PlaceOrderParamDto> parseObject(String reqData) {
		return JSONObject.parseObject(reqData, new TypeReference<Request<PlaceOrderParamDto>>() {
		});
	}

	@Test
	public void testConfirmOrder() throws Exception {
		Response<PlaceOrderDto> resp = placeOrderApi.confirmOrder(confirmReq);
		System.out.println(JsonMapper.nonDefaultMapper().toJson(resp));
	}

	@Test
	public void testSubmitOrder() throws Exception {
		Response<PlaceOrderDto> resp = placeOrderApi.submitOrder(submitReq);
		System.out.println(JSONObject.toJSON(resp));
	}

}
