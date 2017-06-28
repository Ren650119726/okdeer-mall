package com.okdeer.mall.order.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.express.dto.ExpressCallbackDto;
import com.okdeer.mall.order.service.ExpressOrderCallbackApi;
import com.okdeer.mall.order.service.ExpressOrderCallbackService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ClassName: ExpressOrderCallbackApiImpl
 *
 * @author wangf01
 * @Description: 订单快递配送信息回调-api-impl
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.ExpressOrderCallbackApi")
public class ExpressOrderCallbackApiImpl implements ExpressOrderCallbackApi {

    /**
     * 注入-service
     */
    @Autowired
    private ExpressOrderCallbackService expressOrderCallbackService;

    @Override
    public void saveExpressCallback(ExpressCallbackDto data) throws Exception {
        expressOrderCallbackService.saveExpressCallback(data);
    }
}
