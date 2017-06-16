package com.okdeer.mall.ele;

import com.okdeer.mall.Application;
import com.okdeer.mall.ele.config.ElemeOpenConfig;
import com.okdeer.mall.ele.config.RequestConstant;
import com.okdeer.mall.ele.request.OrderComplaintRequest;
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
 * 投诉订单
 * 
 * @author wengangzheng
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class TestOrderComplaint {
    private static final Log logger = LogFactory.getLog(TestOrderComplaint.class);

    @Test
    public void testCancelOrder() throws IOException {
        String appId = ElemeOpenConfig.appId;
        String url = ElemeOpenConfig.API_URL;
        String partner_order_code = "44451479289481540";  //推单时 第三方订单号
        String token = "bdc94751-c82a-4cb8-b09a-e438ea062a71";

        OrderComplaintRequest.OrderComplaintRequstData data = new OrderComplaintRequest.OrderComplaintRequstData();
        data.setPartner_order_code(partner_order_code);
        data.setOrder_complaint_code(150);
        //data.setOrder_complaint_desc("");
        data.setOrder_complaint_time(new Date().getTime());

        OrderComplaintRequest orderComplaintRequest = new OrderComplaintRequest();
        orderComplaintRequest.setData(data);

        int salt = RandomUtils.getInstance().generateValue(1000, 10000);

        Map<String, Object> sigStr = new LinkedHashMap<>();      // 注意添加的顺序, 应该如下面一样各个key值顺序一致
        sigStr.put("app_id", appId);
        sigStr.put("access_token", token);        // 需要使用前面请求生成的token
        sigStr.put("data", orderComplaintRequest.getData());
        sigStr.put("salt", salt);

        // 生成签名
        String sig = OpenSignHelper.generateBusinessSign(sigStr);
        orderComplaintRequest.setSignature(sig);

        orderComplaintRequest.setApp_id(appId);
        orderComplaintRequest.setSalt(salt);

        String requestJson = JsonUtils.getInstance().objectToJson(orderComplaintRequest);
        url = url + RequestConstant.orderComplaint;
        try {
            String res = HttpClient.postBody(url, requestJson);
            logger.info(String.format("^_^, reponse data: %s", res));
        } catch (Exception e) {
            throw new HttpClientRuntimeException("投诉订单出现异常", e);
        }
    }
}
