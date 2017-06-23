package com.okdeer.mall.ele.mapper;

import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.ele.entity.ExpressCallback;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExpressCallbackTest extends BaseServiceTest{

    @Autowired
    private ExpressCallbackMapper expressCallbackMapper;

    @Test
    public void insertTest(){
        ExpressCallback param = new ExpressCallback();
        param.setId(UuidUtils.getUuid());
        param.setOpenOrderCode("DSF000000000000001");
        param.setPartnerOrderCode("CS000000000000001");
        param.setOrderStatus(1);
        param.setPushTime(DateUtils.getSysDate());
        param.setCarrierDriverName("王凡");
        param.setCarrierDriverPhone("13971676734");
        int flag = expressCallbackMapper.insert(param);
        System.out.println(flag);
    }

    @Test
    public void updateTest(){
        ExpressCallback param = new ExpressCallback();
        param.setOpenOrderCode("DSF000000000000001");
        param.setCarrierDriverName("wangf01");
        int flag = expressCallbackMapper.update(param);
        System.out.println(flag);
    }

    @Test
    public void selectTest(){
        ExpressCallback param = new ExpressCallback();
        param.setCarrierDriverName("王凡");
        ExpressCallback entity = expressCallbackMapper.selectExpressCallbackByParam(param);
        System.out.println(JsonMapper.nonDefaultMapper().toJson(entity));
    }
}
