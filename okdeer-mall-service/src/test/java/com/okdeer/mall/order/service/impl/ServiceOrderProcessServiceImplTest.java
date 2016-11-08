package com.okdeer.mall.order.service.impl;

import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.okdeer.mall.Application;
import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.order.exception.OrderException;
import com.okdeer.mall.order.service.ServiceOrderProcessServiceApi;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;
import com.okdeer.base.common.utils.mapper.JsonMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ServiceOrderProcessServiceImplTest {
	
	@Resource
	private ServiceOrderProcessServiceApi serviceOrderProcessService;
	
	

	@Test
	public void testConfirmSeckillOrder() {
		String content = "{\"data\": {\"columnServerId\": \"8a2863a556c28acc0156c2a885d901b3\",\"goodsUpdateTime\": \"2016-09-14 23:30:01\",\"seckillId\": \"8a286a4656eac84901570cf897f37521\",\"skuId\": \"8a284fd056c2991b0156c37795210302\",\"skuNum\": 1,\"storeId\": \"8a284fd056c2991b0156c365d2e802e8\",\"userId\": \"1427942320170590da77555e4eb4a314\"},\"token\": \"8a98681b57248b6a015750cafece04ac\"}";
		Request<ServiceOrderReq> req = JSONObject.parseObject(content, new TypeReference<Request<ServiceOrderReq>>(){});
		Response<ServiceOrderResp> resp = new Response<ServiceOrderResp>();
		try {
			serviceOrderProcessService.confirmSeckillOrder(req, resp);
			System.out.println(JSONObject.toJSON(resp));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSubmitSeckillOrder() {
		String content = "{\"token\":\"8a98681757248b99015755016288055c\",\"data\":{\"userId\":\"1427942320170590da77555e4eb4a314\",\"userPhone\":\"18589077958\",\"columnServerId\":\"8a2863a556c28acc0156c2a885d901b3\",\"storeId\":\"8a284fd056c2991b0156c365d2e802e8\",\"skuId\":\"8a284fd056c2991b0156c37795210302\",\"skuNum\":\"1\",\"goodsUpdateTime\":\"2016-09-14 23:30:01\",\"addressId\":\"8a98681756c37c000156d501220a0b49\",\"serviceTime\":\"2016-09-24 19:00\",\"payWay\":\"0\",\"isInvoice\":\"0\",\"invoiceHead\":\"\",\"seckillId\":\"8a286a4656eac84901570cf897f37521\",\"remark\":\"\"}}";
		Request<ServiceOrderReq> req = JSONObject.parseObject(content, new TypeReference<Request<ServiceOrderReq>>(){});
		Response<ServiceOrderResp> resp = new Response<ServiceOrderResp>();
		
		try {
			serviceOrderProcessService.submitSeckillOrder(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(JSONObject.toJSON(resp));
	}
}
