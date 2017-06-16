package com.okdeer.mall.ele;

import com.okdeer.mall.Application;
import com.okdeer.mall.ele.config.ElemeOpenConfig;
import com.okdeer.mall.ele.config.RequestConstant;
import com.okdeer.mall.ele.request.CancelOrderRequest;
import com.okdeer.mall.ele.sign.OpenSignHelper;
import com.okdeer.mall.ele.util.HttpClient;
import com.okdeer.mall.ele.util.HttpClientRuntimeException;
import com.okdeer.mall.ele.util.JsonUtils;
import com.okdeer.mall.ele.util.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 取消订单例子
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class TestCancelOrder {
    private static final Log logger = LogFactory.getLog(TestCancelOrder.class);

    @Test
    public void testCancelOrder() throws IOException {
        String appId = ElemeOpenConfig.appId;
        String url = ElemeOpenConfig.API_URL;
        String partner_order_code = "44451479289481540";  //推单时 第三方订单号
        String token = "bdc94751-c82a-4cb8-b09a-e438ea062a71";

        CancelOrderRequest.CancelOrderRequstData data = new CancelOrderRequest.CancelOrderRequstData();
        data.setOrder_cancel_description("货品不新鲜");
        data.setOrder_cancel_reason_code(2);
        //data.setOrder_cancel_code(0);
        data.setPartner_order_code(partner_order_code);
        data.setOrder_cancel_time(new Date().getTime());

        CancelOrderRequest cancelOrderRequest = new CancelOrderRequest();
        cancelOrderRequest.setData(data);

        int salt = RandomUtils.getInstance().generateValue(1000, 10000);

        Map<String, Object> sigStr = new LinkedHashMap<>();      // 注意添加的顺序, 应该如下面一样各个key值顺序一致
        sigStr.put("app_id", appId);
        sigStr.put("access_token", token);        // 需要使用前面请求生成的token
        sigStr.put("data", cancelOrderRequest.getData());
        sigStr.put("salt", salt);

        // 生成签名
        String sig = OpenSignHelper.generateBusinessSign(sigStr);
        cancelOrderRequest.setSignature(sig);

        cancelOrderRequest.setApp_id(appId);
        cancelOrderRequest.setSalt(salt);

        String requestJson = JsonUtils.getInstance().objectToJson(cancelOrderRequest);
        url = url + RequestConstant.orderCancel;
        try {
            String res = HttpClient.postBody(url, requestJson);
            logger.info(String.format("^_^, reponse data: %s", res));
        } catch (Exception e) {
            throw new HttpClientRuntimeException("取消订单出现异常", e);
        }
    }
}
