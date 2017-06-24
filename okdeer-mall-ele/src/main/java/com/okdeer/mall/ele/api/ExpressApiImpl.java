package com.okdeer.mall.ele.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.ele.entity.ExpressCallback;
import com.okdeer.mall.ele.service.ExpressService;
import com.okdeer.mall.express.api.ExpressApi;
import com.okdeer.mall.express.dto.ExpressCallbackDto;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ClassName: ExpressApiImpl
 *
 * @author wangf01
 * @Description: 第三方配送回调接口-api-impl
 * @date 2017年6月23日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.express.api.ExpressApi")
public class ExpressApiImpl implements ExpressApi {

    @Autowired
    private ExpressService expressService;

    @Override
    public void saveExpressCallback(ExpressCallbackDto data) throws Exception {
        ExpressCallback callback = new ExpressCallback();
        BeanMapper.copy(data, callback);
        expressService.saveCallback(callback);
    }
}
