package com.okdeer.mall.ele;

import com.okdeer.mall.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 订单查询例子
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class TestQueryOrder {

    @Test
    public void testQueryOrder() throws Exception {
        /*String appId = ElemeOpenConfig.appId;
        String url = ElemeOpenConfig.API_URL;
        String partner_order_code = "1234567890jiaobuchong100";  //推单时 第三方订单号
        String token = "c0b192b5-0445-412a-a315-092be18055fc";

        ElemeQueryOrderRequest request = new ElemeQueryOrderRequest();
        ElemeQueryOrderRequest.ElemeQueryRequestData data = new ElemeQueryOrderRequest.ElemeQueryRequestData();
        data.setPartner_order_code(partner_order_code);
        request.setData(data);

        int salt = RandomUtils.getInstance().generateValue(1000, 10000);
        request.setApp_id(ElemeOpenConfig.appId);
        request.setSalt(salt);

        *//**
         * 生成签名
         *//*
        Map<String, Object> sigStr = new LinkedHashMap<>();      // 注意添加的顺序, 应该如下面一样各个key值顺序一致
        sigStr.put("app_id", appId);
        sigStr.put("access_token", token);        // 需要使用前面请求生成的token
        sigStr.put("data", request.getData());
        sigStr.put("salt", salt);
        // 生成签名
        String sig = OpenSignHelper.generateBusinessSign(sigStr);
        request.setSignature(sig);

        String requestJson = JsonUtils.getInstance().objectToJson(request);

        url = url + RequestConstant.orderQuery;
        try {
            HttpClient.postBody(url, requestJson);
        } catch (Exception e) {
            throw new HttpClientRuntimeException("查询订单出现异常", e);
        }*/
    }
}
