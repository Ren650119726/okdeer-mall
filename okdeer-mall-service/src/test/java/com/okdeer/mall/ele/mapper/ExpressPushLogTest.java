package com.okdeer.mall.ele.mapper;

import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.ele.entity.ExpressPushLog;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExpressPushLogTest extends BaseServiceTest {

    @Autowired
    private ExpressPushLogMapper expressPushLogMapper;

    @Test
    public void insertTest() {
        ExpressPushLog param = new ExpressPushLog();
        param.setId(UuidUtils.getUuid());
        param.setOrderId("CS0000000000000001");
        param.setOrderNo("CS0000000000000001");
        param.setPushJson("{\n" +
                "    \"app_id\": \"4cdbc040657a4847b2667e31d9e2c3d9\",\n" +
                "    \"data\": {\n" +
                "        \"partner_remark\": \"商户备注信息\",\n" +
                "        \"partner_order_code\": \"12345678\",\n" +
                "        \"notify_url\": \"http://123.100.120.22:8090\",\n" +
                "        \"order_type\": 1,\n" +
                "        \"transport_info\": {\n" +
                "            \"transport_name\": \"XXX烤鸭店\",\n" +
                "            \"transport_address\": \"上海市普陀区近铁城市广场5楼\",\n" +
                "            \"transport_longitude\": 120.00000,\n" +
                "            \"transport_latitude\": 30.11111,\n" +
                "            \"position_source\": 1,\n" +
                "            \"transport_tel\": \"13901232231\",\n" +
                "            \"transport_remark\": \"备注\"\n" +
                "        },\n" +
                "        \"order_add_time\": 1452570728594,\n" +
                "        \"order_total_amount\": 50.00,\n" +
                "        \"order_actual_amount\": 48.00,\n" +
                "        \"order_weight\": 3.5,\n" +
                "        \"order_remark\": \"用户备注\",\n" +
                "        \"is_invoiced\": 1,\n" +
                "        \"invoice\": \"xxx有限公司\",\n" +
                "        \"order_payment_status\": 1,\n" +
                "        \"order_payment_method\": 1,\n" +
                "        \"is_agent_payment\": 1,\n" +
                "        \"require_payment_pay\": 50.00,\n" +
                "        \"goods_count\": 4,\n" +
                "        \"require_receive_time\": 1452570728594,\n" +
                "        \"serial_number\": \"5678\",\n" +
                "        \"receiver_info\": {\n" +
                "            \"receiver_name\": \"李明\",\n" +
                "            \"receiver_primary_phone\": \"13900000000\",\n" +
                "            \"receiver_second_phone\": \"13911111111\",\n" +
                "            \"receiver_address\": \"上海市近铁广场\",\n" +
                "            \"receiver_longitude\": 130.0,\n" +
                "            \"receiver_latitude\": 30.0,\n" +
                "            \"position_source\": 1\n" +
                "        },\n" +
                "        \"items_json\": [\n" +
                "            {\n" +
                "                \"item_id\": \"fresh0001\",\n" +
                "                \"item_name\": \"苹果\",\n" +
                "                \"item_quantity\": 5,\n" +
                "                \"item_price\": 10.00,\n" +
                "                \"item_actual_price\": 9.50,\n" +
                "                \"item_size\": 1,\n" +
                "                \"item_remark\": \"苹果，轻放\",\n" +
                "                \"is_need_package\": 1,\n" +
                "                \"is_agent_purchase\": 1,\n" +
                "                \"agent_purchase_price\": 10.00\n" +
                "            },\n" +
                "            {\n" +
                "                \"item_id\": \"fresh0002\",\n" +
                "                \"item_name\": \"香蕉\",\n" +
                "                \"item_quantity\": 1,\n" +
                "                \"item_price\": 20.00,\n" +
                "                \"item_actual_price\": 19.00,\n" +
                "                \"item_size\": 2,\n" +
                "                \"item_remark\": \"香蕉，轻放\",\n" +
                "                \"is_need_package\": 1,\n" +
                "                \"is_agent_purchase\": 1,\n" +
                "                \"agent_purchase_price\": 10.00\n" +
                "            },\n" +
                "            ...\n" +
                "        ]\n" +
                "    },\n" +
                "    \"salt\": 1234,\n" +
                "    \"signature\": \"5fsh8a99xddf5asqw90v12a\"\n" +
                "}");
        param.setResultJson("{\n" +
                "    \"code\": 200,\n" +
                "    \"msg\": \"接收成功\",\n" +
                "    \"data\": {}\n" +
                "}");
        param.setCreateTime(DateUtils.getSysDate());
        int flag = expressPushLogMapper.insert(param);
    }

    @Test
    public void selectTest() {

    }
}
