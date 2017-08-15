package com.okdeer.mall.order.api;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.express.dto.ResultMsgDto;
import com.okdeer.mall.order.dto.ExpressModeParamDto;
import com.okdeer.mall.order.service.ExpressOrderCallbackApi;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ExpressOrderCallBackApiTest extends BaseServiceTest {

    private ExpressModeParamDto paramDto;

    public ExpressOrderCallBackApiTest(ExpressModeParamDto paramDto) {
        this.paramDto = paramDto;
    }

    @Parameterized.Parameters
    public static List<ExpressModeParamDto> initParam() {
        List<ExpressModeParamDto> list = Lists.newArrayList();
        ExpressModeParamDto param6 = new ExpressModeParamDto("8a94e7185bd16f90015bd16fc0200003", 2, "143213006656192befa3a55b42eda438", "56583c03276511e6aaff00163e010eb1");
        list.add(param6);
        return list;
    }

    @Reference(version = "1.0.0", check = false)
    private ExpressOrderCallbackApi callbackApi;

    @Test
    public void saveModeOneFive() throws Exception {
        ResultMsgDto<String> resultMsgDto = callbackApi.saveExpressMode(paramDto);
        assertEquals(resultMsgDto.getMsg(), 200, resultMsgDto.getCode());
    }

}
