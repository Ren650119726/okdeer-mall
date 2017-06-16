package com.okdeer.mall.ele;

import com.okdeer.mall.Application;
import com.okdeer.mall.ele.config.ElemeOpenConfig;
import com.okdeer.mall.ele.config.RequestConstant;
import com.okdeer.mall.ele.request.ElemeCreateOrderRequest;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 获取token 请求创建订单 例子
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class TestCreateOrder {
    private static final Log logger = LogFactory.getLog(TestCreateOrder.class);
    private String token = "b63b07f6-39e6-4669-9043-0bfb7e0db43e";

    /**
     * 推送一个订单
     */
    @Test
    public void testCreateOrder() throws Exception {
        /**
         * 将参数构造成一个json
         */
        ElemeCreateOrderRequest.ElemeCreateRequestData data = new ElemeCreateOrderRequest.ElemeCreateRequestData();

        /**
         * transportInfo
         */
        ElemeCreateOrderRequest.TransportInfo transportInfo = new ElemeCreateOrderRequest.TransportInfo();
        //transportInfo.setTransport_name("饿了么BOD");
        transportInfo.setTransport_address("300弄亚都国际名园5号楼2003室");
        transportInfo.setTransport_tel("13900000000");

        /**
         * 31.2461955,121.3847637;31.2441408,121.3766956;31.2306375,121.3718891;
         * 31.2243255,121.3770389;31.2226374,121.3869094;31.2261605,121.391201;
         * 31.2403249,121.3933467;31.2461955,121.3847637
         */
        transportInfo.setTransport_longitude(new BigDecimal(121.5156496362));
        transportInfo.setTransport_latitude(new BigDecimal(31.2331643501));
        transportInfo.setTransport_remark("备注");
        transportInfo.setPosition_source(3);

        /**
         * receiverInfo
         */
        ElemeCreateOrderRequest.ReceiverInfo receiverInfo = new ElemeCreateOrderRequest.ReceiverInfo();
        receiverInfo.setReceiver_address("上海近铁广场");
        receiverInfo.setReceiver_name("jiabuchong");
        receiverInfo.setReceiver_primary_phone("13900000000");
        receiverInfo.setReceiver_second_phone("13911111111");
        receiverInfo.setReceiver_longitude(new BigDecimal(121.5156496362));
        receiverInfo.setReceiver_latitude(new BigDecimal(31.2331643501));
        receiverInfo.setPosition_source(1);

        /**
         * itemsJson
         */
        // items array
        ElemeCreateOrderRequest.ItemsJson[] itemsJsons = new ElemeCreateOrderRequest.ItemsJson[2];
        ElemeCreateOrderRequest.ItemsJson item = new ElemeCreateOrderRequest.ItemsJson();
        item.setItem_name("香蕉");
        item.setItem_quantity(5);
        item.setItem_actual_price(new BigDecimal(9.50));
        item.setItem_price(new BigDecimal(10.00));
        item.setIs_agent_purchase(1);
        item.setIs_need_package(1);

        ElemeCreateOrderRequest.ItemsJson item1 = new ElemeCreateOrderRequest.ItemsJson();
        item1.setItem_name("苹果");
        item1.setItem_quantity(5);
        item1.setItem_actual_price(new BigDecimal(9.50));
        item1.setItem_price(new BigDecimal(10.00));
        item1.setIs_agent_purchase(1);
        item1.setIs_need_package(1);

        itemsJsons[0] = item;
        itemsJsons[1] = item1;

        data.setTransport_info(transportInfo);
        data.setReceiver_info(receiverInfo);
        data.setItems_json(itemsJsons);

        data.setPartner_remark("hi, 咱们好好合作哦, 嘿嘿");
        String str = "test" + System.currentTimeMillis();
        System.out.println(str);
        data.setPartner_order_code(str);
        data.setNotify_url("http://192.168.104.133:5000");

        /**
         * 1: 蜂鸟配送, 未向饿了么物流平台查询过站点的订单，支持两小时送达
         * 2: 定点次日达, 提前向饿了么物流平台查询过配送站点的订单，支持次日送达
         */
        data.setOrder_type(1);    // 订单类型
        data.setOrder_total_amount(new BigDecimal(50.00));
        data.setOrder_actual_amount(new BigDecimal(48.00));
        data.setOrder_weight(new BigDecimal(3));
        data.setOrder_remark("一定送到哦");
        data.setIs_invoiced(0); // 是否需要发票0：不需要；1：需要
        data.setInvoice("伟大的公司");
        data.setOrder_payment_status(1);
        data.setOrder_payment_method(1);
        data.setIs_agent_payment(1); // 是否需要承运商代收货款 0：否 1：是
        data.setRequire_payment_pay(new BigDecimal(50.00));
        data.setGoods_count(4);
        data.setRequire_receive_time(LocalDateTime.now().plusHours(1).
                atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());   // 预计送达时间 要大于当前时间

        data.setOrder_add_time(new Date().getTime());

        ElemeCreateOrderRequest request = new ElemeCreateOrderRequest();
        request.setData(data);
        logger.info(String.format("urlencode string is %s", request.getData()));

        int salt = RandomUtils.getInstance().generateValue(1000, 10000);
        request.setApp_id(ElemeOpenConfig.appId);
        request.setSalt(salt);

        /**
         * 生成签名
         */
        Map<String, Object> sigStr = new LinkedHashMap<>();      // 注意添加的顺序, 应该如下面一样各个key值顺序一致
        sigStr.put("app_id", ElemeOpenConfig.appId);
        sigStr.put("access_token", token);        // 需要使用前面请求生成的token
        sigStr.put("data", request.getData());
        sigStr.put("salt", salt);
        // 生成签名
        String sig = OpenSignHelper.generateBusinessSign(sigStr);
        request.setSignature(sig);

        String requestJson = JsonUtils.getInstance().objectToJson(request);
        logger.info(String.format("request json is %s", requestJson));

        String url = ElemeOpenConfig.API_URL + RequestConstant.orderCreate;
        try {
            HttpClient.postBody(url, requestJson);
        } catch (Exception e) {
            logger.error("creating order request occurs an exception!");
            throw new HttpClientRuntimeException("推送订单出现异常", e);
        }
    }

}
