package com.okdeer.mall.order.mapper;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.order.entity.TradeOrderCarrier;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TradeOrderCarrierTest extends BaseServiceTest {

    @Autowired
    private TradeOrderCarrierMapper tradeOrderCarrierMapper;

    @Test
    public void insertCarrierTest() {
        TradeOrderCarrier param = new TradeOrderCarrier();
        param.setCarrierDriverName("王凡");
        param.setId(UuidUtils.getUuid());
        param.setOrderId("CS0000000000000002");
        param.setOrderNo("CS0000000000000002");
        param.setCarrierDriverName("王凡");
        param.setCarrierDriverPhone("13971676734");
        int flag = tradeOrderCarrierMapper.insert(param);
        System.out.println(flag);
    }

    @Test
    public void selectCarrierTest() {
        TradeOrderCarrier param = new TradeOrderCarrier();
        param.setCarrierDriverName("王凡");
        /*TradeOrderCarrier entity = tradeOrderCarrierMapper.selectCarrierByParam(param);
        System.out.println(JsonMapper.nonDefaultMapper().toJson(entity));*/

        /*List<TradeOrderCarrier> list = tradeOrderCarrierMapper.selectCarrierListByParam(param);
        System.out.println(JsonMapper.nonDefaultMapper().toJson(list));*/
    }
}
