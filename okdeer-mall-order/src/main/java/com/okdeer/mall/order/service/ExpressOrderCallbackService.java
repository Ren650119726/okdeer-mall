package com.okdeer.mall.order.service;

import com.okdeer.mall.express.dto.ExpressCallbackDto;
import com.okdeer.mall.order.dto.ExpressModeParamDto;

/**
 * ClassName: ExpressOrderCallbackService
 *
 * @author wangf01
 * @Description: 订单快递配送信息回调-service
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface ExpressOrderCallbackService {

    /**
     * 配送方式：蜂鸟配送
     *
     * @param paramDto ExpressModeParamDto
     * @throws Exception
     */
    void saveExpressModePlanA(ExpressModeParamDto paramDto) throws Exception;

    /**
     * 配送方式：自行配送
     *
     * @param paramDto ExpressModeParamDto
     * @throws Exception
     */
    void saveExpressModePlanB(ExpressModeParamDto paramDto) throws Exception;

    /**
     * 处理配送回调信息
     *
     * @param data String
     * @throws Exception
     */
    void saveExpressCallback(ExpressCallbackDto data) throws Exception;
}
