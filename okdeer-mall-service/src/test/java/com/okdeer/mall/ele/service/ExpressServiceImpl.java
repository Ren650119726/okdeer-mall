package com.okdeer.mall.ele.service;

import com.okdeer.mall.base.BaseServiceTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExpressServiceImpl extends BaseServiceTest {

    @Autowired
    private ExpressService expressService;

    @Test
    public void expressPushTest() {
        try {
            expressService.saveExpressOrder("40289d675cca2ea5015cced58b40032a");
        } catch (Exception e) {
            System.out.println("推送订单异常" + e);
        }
    }
}
