package com.okdeer.mall.ele.mapper;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.ele.entity.ExpressCallbackLog;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExpressCallbackLogTest extends BaseServiceTest {

    @Autowired
    private ExpressCallbackLogMapper expressCallbackLogMapper;

    @Test
    public void insertTest() {
        ExpressCallbackLog param = new ExpressCallbackLog();
        param.setId(UuidUtils.getUuid());
        param.setOpenOrderCode("DSF000000000000001");
        param.setPartnerOrderCode("CS0000000000000001");
        param.setCreateTime(DateUtils.getSysDate());
        param.setCallbackJson("{\n" +
                "    \"open_order_code\": \"160103\",\n" +
                "    \"partner_order_code\": \"BG658907200991\",\n" +
                "    \"order_status\": 1,\n" +
                "    \"push_time\": 1466095163344,\n" +
                "    \"carrier_driver_name\": \"\",\n" +
                "    \"carrier_driver_phone\": \"\",\n" +
                "    \"description\": \"\",\n" +
                "    \"error_code\":\"\"\n" +
                "}");
        int flag = expressCallbackLogMapper.insert(param);
        System.out.println(flag);
    }

    @Test
    public void selectTest() {

    }
}
