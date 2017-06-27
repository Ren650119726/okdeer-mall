package com.okdeer.mall.ele.service;

import com.okdeer.mall.ele.entity.ExpressCallback;
import com.okdeer.mall.express.dto.ExpressCarrierDto;
import com.okdeer.mall.express.dto.ResultMsgDto;
import com.okdeer.mall.order.entity.TradeOrder;

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
     * @param tradeOrder TradeOrder 业务订单id
     * @return ResultMsgDto<String>
     * @throws Exception
     */
    ResultMsgDto<String> saveExpressOrder(TradeOrder tradeOrder) throws Exception;

    /**
     * 处理回调信息
     *
     * @param data ExpressCallback 回调信息
     * @throws Exception
     */
    void saveCallback(ExpressCallback data) throws Exception;

    /**
     * 查询骑手位置信息
     * @param orderNo String 商户订单号（orderNo）
     * @return ResultMsgDto<ExpressCarrierDto>
     * @throws Exception
     */
    ResultMsgDto<ExpressCarrierDto> findExpressCarrier(String orderNo) throws Exception;
}
