package com.okdeer.mall.order.api;

import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.order.dto.UserRefuseDto;
import com.okdeer.mall.order.dto.UserRefuseParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.service.CancelOrderApi;
import com.okdeer.mall.order.service.CancelOrderService;
import com.okdeer.mall.order.service.TradeOrderService;
import javax.annotation.Resource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class CancelOrderApiImplTest extends BaseServiceTest {

    @Resource
    private CancelOrderApi cancelOrderApi;

    @Mock
    private TradeOrderService tradeorderService;

    @Mock
    private CancelOrderService cancelOrderService;

    private UserRefuseParamDto paramDto = new UserRefuseParamDto();

    private TradeOrder tradeOrder = new TradeOrder();

    @Before
    public void setUp() throws Exception {
        // 初始化测试用例类中由Mockito的注解标注的所有模拟对象
        MockitoAnnotations.initMocks(this);
        paramDto.setOrderId("123");
        paramDto.setReason("测试拒收");
        paramDto.setUserId("111111");
        tradeOrder.setId(paramDto.getOrderId());
        tradeOrder.setUpdateTime(DateUtils.getSysDate());
        tradeOrder.setUpdateUserId(paramDto.getUserId());
        tradeOrder.setReason(paramDto.getReason());
        tradeOrder.setSellerId(paramDto.getUserId());
        ReflectionTestUtils.setField(cancelOrderApi, "tradeorderService", tradeorderService);
        ReflectionTestUtils.setField(cancelOrderApi, "cancelOrderService", cancelOrderService);
    }

    @Test
    public void userRefuseTest() throws Exception {
        when(tradeorderService.selectById(paramDto.getOrderId())).thenReturn(tradeOrder);
        UserRefuseDto refuseDto = cancelOrderApi.userRefuse(paramDto);
        assertEquals("拒收成功", 0, refuseDto.getStatus());
    }

    @Test
    public void userRefuseExceptionTest() throws Exception {
        doNothing().doThrow(new Exception()).when(cancelOrderService);
        UserRefuseDto refuseDto = cancelOrderApi.userRefuse(paramDto);
        assertNotEquals("拒收异常测试", 0, refuseDto.getStatus());
    }
}
