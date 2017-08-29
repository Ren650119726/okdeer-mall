package com.okdeer.mall.ele.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.rocketmq.common.message.Message;
import com.google.common.base.Charsets;
import com.okdeer.archive.store.dto.StoreInfoDto;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.express.api.ExpressApi;
import com.okdeer.mall.express.dto.ExpressCallbackDto;
import com.okdeer.mall.express.dto.ExpressCarrierDto;
import com.okdeer.mall.express.dto.ExpressOrderInfoDto;
import com.okdeer.mall.express.dto.ResultMsgDto;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class ExpressServiceImpl extends BaseServiceTest {

    @Autowired
    private ExpressService expressService;

    @Reference(version = "1.0.0", check = false)
    private ExpressApi expressApi;

    @Autowired
    private RocketMQProducer rocketMQProducer;

    @Test
    public void rocaketTest() throws Exception{
        String json = "{\n" +
                "\t\"updateSkuPutawayStatusList\": [{\n" +
                "\t\t\t\"storeSkuId\": \"ff8080815e2d6a3b015e2d701da3000a\",\n" +
                "\t\t\t\"status\": 0,\n" +
                "\t\t\t\"branchId\": \"5592971b276511e6aaff00163e010eb1\",\n" +
                "\t\t\t\"skuId\": \"39eb1ba783fe11e6823127f894e357cf\",\n" +
                "\t\t\t\"skuCode\": \"240297\",\n" +
                "\t\t\t\"barCode\": \"6909460001319\",\n" +
                "\t\t\t\"skuName\": \"马林甘甜话梅90g  \",\n" +
                "\t\t\t\"bindType\": 0,\n" +
                "\t\t\t\"spec\": \"90g\",\n" +
                "\t\t\t\"unit\": \"袋\",\n" +
                "\t\t\t\"salePrice\": 4.5,\n" +
                "\t\t\t\"vipPrice\": 4.5,\n" +
                "\t\t\t\"pricingType\": 0,\n" +
                "\t\t\t\"remark\": \"马林甘甜话梅90g  \"\n" +
                "\t\t}, {\n" +
                "\t\t\t\"storeSkuId\": \"ff8080815e2d6a3b015e2d701dad000c\",\n" +
                "\t\t\t\"status\": 0,\n" +
                "\t\t\t\"branchId\": \"5592971b276511e6aaff00163e010eb1\",\n" +
                "\t\t\t\"skuId\": \"39f6404783fe11e6823127f894e357cf\",\n" +
                "\t\t\t\"skuCode\": \"240684\",\n" +
                "\t\t\t\"barCode\": \"6921701420246\",\n" +
                "\t\t\t\"skuName\": \"老福麻花  \",\n" +
                "\t\t\t\"bindType\": 0,\n" +
                "\t\t\t\"spec\": \" 0.0\",\n" +
                "\t\t\t\"unit\": \"\",\n" +
                "\t\t\t\"salePrice\": 0.5,\n" +
                "\t\t\t\"vipPrice\": 0.5,\n" +
                "\t\t\t\"pricingType\": 0,\n" +
                "\t\t\t\"remark\": \"老福麻花  \"\n" +
                "\t\t}, {\n" +
                "\t\t\t\"storeSkuId\": \"ff8080815e2d6a3b015e2d701db3000e\",\n" +
                "\t\t\t\"status\": 0,\n" +
                "\t\t\t\"branchId\": \"5592971b276511e6aaff00163e010eb1\",\n" +
                "\t\t\t\"skuId\": \"ff8080815e2d6a3b015e2d6df4950001\",\n" +
                "\t\t\t\"skuCode\": \"110346\",\n" +
                "\t\t\t\"barCode\": \"110346\",\n" +
                "\t\t\t\"skuName\": \"捆绑-测试-1\",\n" +
                "\t\t\t\"bindType\": 3,\n" +
                "\t\t\t\"spec\": \"\",\n" +
                "\t\t\t\"unit\": \"个\",\n" +
                "\t\t\t\"salePrice\": 5,\n" +
                "\t\t\t\"vipPrice\": 4,\n" +
                "\t\t\t\"pricingType\": 0,\n" +
                "\t\t\t\"remark\": \"\"\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"bindingList\": [{\n" +
                "\t\t\t\"storeSkuId\": \"ff8080815e2d6a3b015e2d701db3000e\",\n" +
                "\t\t\t\"status\": 0,\n" +
                "\t\t\t\"branchId\": \"5592971b276511e6aaff00163e010eb1\",\n" +
                "\t\t\t\"skuId\": \"ff8080815e2d6a3b015e2d6df4950001\",\n" +
                "\t\t\t\"skuCode\": \"110346\",\n" +
                "\t\t\t\"barCode\": \"110346\",\n" +
                "\t\t\t\"skuName\": \"捆绑-测试-1\",\n" +
                "\t\t\t\"bindType\": 3,\n" +
                "\t\t\t\"spec\": \"\",\n" +
                "\t\t\t\"unit\": \"个\",\n" +
                "\t\t\t\"salePrice\": 5,\n" +
                "\t\t\t\"vipPrice\": 4,\n" +
                "\t\t\t\"pricingType\": 0,\n" +
                "\t\t\t\"remark\": \"\",\n" +
                "\t\t\t\"makeupList\": [{\n" +
                "\t\t\t\t\t\"storeSkuId\": \"ff8080815e2d6a3b015e2d701da3000a\",\n" +
                "\t\t\t\t\t\"status\": 0,\n" +
                "\t\t\t\t\t\"branchId\": \"5592971b276511e6aaff00163e010eb1\",\n" +
                "\t\t\t\t\t\"skuId\": \"39eb1ba783fe11e6823127f894e357cf\",\n" +
                "\t\t\t\t\t\"skuCode\": \"240297\",\n" +
                "\t\t\t\t\t\"barCode\": \"6909460001319\",\n" +
                "\t\t\t\t\t\"skuName\": \"马林甘甜话梅90g  \",\n" +
                "\t\t\t\t\t\"bindType\": 0,\n" +
                "\t\t\t\t\t\"spec\": \"90g\",\n" +
                "\t\t\t\t\t\"unit\": \"袋\",\n" +
                "\t\t\t\t\t\"salePrice\": 4.5,\n" +
                "\t\t\t\t\t\"vipPrice\": 4.5,\n" +
                "\t\t\t\t\t\"pricingType\": 0,\n" +
                "\t\t\t\t\t\"remark\": \"马林甘甜话梅90g  \",\n" +
                "\t\t\t\t\t\"componentNum\": 3\n" +
                "\t\t\t\t}, {\n" +
                "\t\t\t\t\t\"storeSkuId\": \"ff8080815e2d6a3b015e2d701dad000c\",\n" +
                "\t\t\t\t\t\"status\": 0,\n" +
                "\t\t\t\t\t\"branchId\": \"5592971b276511e6aaff00163e010eb1\",\n" +
                "\t\t\t\t\t\"skuId\": \"39f6404783fe11e6823127f894e357cf\",\n" +
                "\t\t\t\t\t\"skuCode\": \"240684\",\n" +
                "\t\t\t\t\t\"barCode\": \"6921701420246\",\n" +
                "\t\t\t\t\t\"skuName\": \"老福麻花  \",\n" +
                "\t\t\t\t\t\"bindType\": 0,\n" +
                "\t\t\t\t\t\"spec\": \" 0.0\",\n" +
                "\t\t\t\t\t\"unit\": \"\",\n" +
                "\t\t\t\t\t\"salePrice\": 0.5,\n" +
                "\t\t\t\t\t\"vipPrice\": 0.5,\n" +
                "\t\t\t\t\t\"pricingType\": 0,\n" +
                "\t\t\t\t\t\"remark\": \"老福麻花  \",\n" +
                "\t\t\t\t\t\"componentNum\": 1\n" +
                "\t\t\t\t}\n" +
                "\t\t\t]\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}\n";
        Message msg = new Message("topic_store_sku_status_update", "tag_store_sku_status_update", json.getBytes(Charsets.UTF_8));
        rocketMQProducer.send(msg);
    }

    @Test
    public void expressPushTest() {
        try {
            //expressService.saveExpressOrder("8a94e7545cd93f77015cd940590d0003");
            assertEquals(2,2);
        } catch (Exception e) {
            System.out.println("推送订单异常" + e);
        }
    }

    @Test
    public void expressCarrierTest() {
        try {
            ResultMsgDto<ExpressCarrierDto> resultMsgDto = expressService.findExpressCarrier("XS990170017070400022");
            System.out.println(JsonMapper.nonDefaultMapper().toJson(resultMsgDto));
        } catch (Exception e) {
            System.out.println("获取骑手信息异常" + e);
        }
    }

    @Test
    public void expressOrderInfoTest() {
        try {
            ResultMsgDto<ExpressOrderInfoDto> resultMsgDto = expressApi.findExpressOrderInfo("XS100010017062600008");
            System.out.println(JsonMapper.nonDefaultMapper().toJson(resultMsgDto));
        } catch (Exception e) {
            System.out.println("获取第三方订单信息异常" + e);
        }
    }

    @Test
    public void expressCancelTest() {
        try {
            ResultMsgDto<String> resultMsgDto = expressService.cancelExpressOrder("XS100010017050400012");
            System.out.println(JsonMapper.nonDefaultMapper().toJson(resultMsgDto));
        } catch (Exception e) {
            System.out.println("取消第三方订单信息异常" + e);
        }
    }

    @Test
    public void expressCallbackLogTest() {
        try {
            List<ExpressCallbackDto> list = expressApi.findExpressCallbackLogByOrderNo("XS990170017070400011");
            System.out.println(JsonMapper.nonDefaultMapper().toJson(list));
        } catch (Exception e) {
            System.out.println("取消第三方订单信息异常" + e);
        }
    }

    @Test
    public void expressChainStoreTest(){
        StoreInfoDto dto = new StoreInfoDto();
        dto.setStoreName("王胜测试店");
        dto.setAddress("广东省深圳市南山区");
        dto.setArea("东方科技大厦");
        dto.setMobile("15107180089");
        dto.setLongitude(Double.valueOf("113.952547"));
        dto.setLatitude(Double.valueOf("22.553397"));
        try {
            ResultMsgDto<String> resultMsgDto = expressService.saveChainStore(dto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
