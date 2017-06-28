package com.okdeer.mall.order.service;

import com.okdeer.mall.express.dto.ExpressCallbackDto;

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
     * 处理配送回调信息
     *
     * @param data String
     * @throws Exception
     */
    void saveExpressCallback(ExpressCallbackDto data) throws Exception;
}
