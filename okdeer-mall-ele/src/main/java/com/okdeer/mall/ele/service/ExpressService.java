package com.okdeer.mall.ele.service;

import com.okdeer.mall.ele.entity.ExpressCallback;
import com.okdeer.mall.ele.util.ResultMsg;

/**
 * ClassName: ExpressService
 *
 * @author wangf01
 * @Description: 蜂鸟配送-service
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface ExpressService {

    /**
     * 推送蜂鸟订单
     *
     * @param orderId String 业务订单id
     * @return ResultMsg
     * @throws Exception
     */
    ResultMsg saveExpressOrder(String orderId) throws Exception;

    /**
     * 处理回调信息
     *
     * @param data ExpressCallback 回调信息
     * @throws Exception
     */
    void saveCallback(ExpressCallback data) throws Exception;
}