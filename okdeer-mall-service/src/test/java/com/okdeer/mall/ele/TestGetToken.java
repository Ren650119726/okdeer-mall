package com.okdeer.mall.ele;

import com.okdeer.mall.Application;
import com.okdeer.mall.ele.service.ExpressService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class TestGetToken {

    private static final Log logger = LogFactory.getLog(TestGetToken.class);

    @Autowired
    private ExpressService expressService;

    @Test
    public void testGetToken() throws Exception {

    }

}
