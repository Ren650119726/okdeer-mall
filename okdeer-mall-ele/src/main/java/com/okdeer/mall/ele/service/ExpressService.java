package com.okdeer.mall.ele.service;

import com.okdeer.archive.store.dto.StoreInfoDto;
import com.okdeer.mall.ele.entity.ExpressCallback;
import com.okdeer.mall.ele.entity.ExpressCallbackLog;
import com.okdeer.mall.ele.entity.ExpressOrderInfo;
import com.okdeer.mall.express.dto.ExpressCallbackParamDto;
import com.okdeer.mall.express.dto.ExpressCarrierDto;
import com.okdeer.mall.express.dto.ResultMsgDto;
import com.okdeer.mall.order.entity.TradeOrder;

import java.util.List;

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
     * 推送门店信息
     *
     * @param storeInfoDto StoreInfoDto
     * @return ResultMsgDto<String>
     * @throws Exception
     */
    ResultMsgDto<String> saveChainStore(StoreInfoDto storeInfoDto) throws Exception;

    /**
     * 取消蜂鸟订单
     *
     * @param orderNo String
     * @return ResultMsgDto<String>
     * @throws Exception
     */
    ResultMsgDto<String> cancelExpressOrder(String orderNo) throws Exception;

    /**
     * 根据条件查询符合的记录
     *
     * @param paramDto ExpressCallbackParamDto
     * @return List<ExpressCallback>
     * @throws Exception
     */
    List<ExpressCallback> findByParam(ExpressCallbackParamDto paramDto) throws Exception;

    /**
     * 处理回调信息
     *
     * @param data ExpressCallback 回调信息
     * @throws Exception
     */
    void saveCallback(ExpressCallback data) throws Exception;

    /**
     * 查询第三方订单信息
     *
     * @param orderNo String 商户订单号（orderNo）
     * @return ResultMsgDto<ExpressOrderInfo>
     * @throws Exception
     */
    ResultMsgDto<ExpressOrderInfo> findExpressOrderInfo(String orderNo) throws Exception;

    /**
     * 查询骑手位置信息
     *
     * @param orderNo String 商户订单号（orderNo）
     * @return ResultMsgDto<ExpressCarrierDto>
     * @throws Exception
     */
    ResultMsgDto<ExpressCarrierDto> findExpressCarrier(String orderNo) throws Exception;

    /**
     * 根据订单编号查询第三方配送日志轨迹
     *
     * @param orderNo String 商户订单号（orderNo）
     * @return List<ExpressCallbackLog>
     * @throws Exception
     */
    List<ExpressCallbackLog> findExpressCallbackLogByOrderNo(String orderNo) throws Exception;
}
