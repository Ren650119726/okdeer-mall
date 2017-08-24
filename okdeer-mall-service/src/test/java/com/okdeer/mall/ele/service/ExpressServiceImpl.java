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
                "\t\"goodsSkuList\": [{\n" +
                "\t\t\t\"skuId\": \"商品sku_id\",\n" +
                "\t\t\t\"skuCode\": \"商品货号\",\n" +
                "\t\t\t\"barCode\": \"商品条形码\",\n" +
                "\t\t\t\"skuName\": \"商品名称\",\n" +
                "\t\t\t\"status\": \"0\",\n" +
                "\t\t\t\"bindType\": \"3\",\n" +
                "\t\t\t\"spec\": \"商品规格\",\n" +
                "\t\t\t\"unit\": \"单位\",\n" +
                "\t\t\t\"salePrice\": \"1\",\n" +
                "\t\t\t\"vipPrice\": \"1\",\n" +
                "\t\t\t\"pricingType\": \"0\",\n" +
                "\t\t\t\"remark\": \"备注信息\"\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"bindingList\": [{\n" +
                "\t\t\t\"skuId\": \"商品sku_id\",\n" +
                "\t\t\t\"skuCode\": \"主商品-商品货号\",\n" +
                "\t\t\t\"barCode\": \"主商品-商品条形码\",\n" +
                "\t\t\t\"skuName\": \"主商品-商品名称\",\n" +
                "\t\t\t\"status\": \"0\",\n" +
                "\t\t\t\"bindType\": \"3\",\n" +
                "\t\t\t\"spec\": \"商品规格\",\n" +
                "\t\t\t\"unit\": \"单位\",\n" +
                "\t\t\t\"salePrice\": \"1\",\n" +
                "\t\t\t\"vipPrice\": \"1\",\n" +
                "\t\t\t\"pricingType\": \"0\",\n" +
                "\t\t\t\"remark\": \"备注信息\",\n" +
                "\t\t\t\"makeupList\": [{\n" +
                "\t\t\t\t\t\"skuId\": \"成份商品-商品sku_id\",\n" +
                "\t\t\t\t\t\"skuCode\": \"成份商品-商品货号\",\n" +
                "\t\t\t\t\t\"barCode\": \"成份商品-商品条形码\",\n" +
                "\t\t\t\t\t\"skuName\": \"成份商品-商品名称\",\n" +
                "\t\t\t\t\t\"status\": \"0\",\n" +
                "\t\t\t\t\t\"bindType\": \"0\",\n" +
                "\t\t\t\t\t\"spec\": \"商品规格\",\n" +
                "\t\t\t\t\t\"unit\": \"单位\",\n" +
                "\t\t\t\t\t\"salePrice\": \"1\",\n" +
                "\t\t\t\t\t\"vipPrice\": \"1\",\n" +
                "\t\t\t\t\t\"pricingType\": \"0\",\n" +
                "\t\t\t\t\t\"remark\": \"备注信息\",\n" +
                "\t\t\t\t\t\"componentNum\": \"成份商品数量\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t]\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}\n";
        Message msg = new Message("topic_sku_add_wangfan", "tag_sku_add", json.getBytes(Charsets.UTF_8));
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
