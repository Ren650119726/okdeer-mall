package com.okdeer.mall.ele.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.ele.entity.ExpressCallback;
import com.okdeer.mall.ele.entity.ExpressOrderInfo;
import com.okdeer.mall.ele.service.ExpressService;
import com.okdeer.mall.express.api.ExpressApi;
import com.okdeer.mall.express.dto.ExpressCallbackDto;
import com.okdeer.mall.express.dto.ExpressCarrierDto;
import com.okdeer.mall.express.dto.ExpressOrderInfoDto;
import com.okdeer.mall.express.dto.ResultMsgDto;
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

    @Override
    public ResultMsgDto<ExpressOrderInfoDto> findExpressOrderInfo(String orderNo) throws Exception {
        ResultMsgDto<ExpressOrderInfoDto> resultMsgDto = new ResultMsgDto<ExpressOrderInfoDto>();
        ResultMsgDto<ExpressOrderInfo> resultMsgBo = expressService.findExpressOrderInfo(orderNo);
        resultMsgDto.setCode(resultMsgBo.getCode());
        resultMsgDto.setMsg(resultMsgBo.getMsg());
        resultMsgDto.setData(BeanMapper.map(resultMsgBo.getData(), ExpressOrderInfoDto.class));
        return resultMsgDto;
    }

    @Override
    public ResultMsgDto<ExpressCarrierDto> findExpressCarrier(String orderNo) throws Exception {
        return expressService.findExpressCarrier(orderNo);
    }
}
