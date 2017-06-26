package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrderCarrier;

import java.util.List;

/**
 * ClassName: TradeOrderCarrierService
 *
 * @author wangf01
 * @Description: 订单快递配送信息-service
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface TradeOrderCarrierService {

    /**
     * 根据id查询符合的数据
     *
     * @param id String
     * @return TradeOrderCarrier
     */
    TradeOrderCarrier selectCarrierById(String id) throws Exception;

    /**
     * 根据条件查询符合的数据
     *
     * @param param TradeOrderCarrier 查询数据的条件
     * @return TradeOrderCarrier
     */
    TradeOrderCarrier selectCarrierByParam(TradeOrderCarrier param) throws Exception;

    /**
     * 根据条件查询符合的数据
     *
     * @param param TradeOrderCarrier 查询数据的条件
     * @return TradeOrderCarrier
     */
    List<TradeOrderCarrier> selectCarrierListByParam(TradeOrderCarrier param) throws Exception;

    /**
     * 保存数据
     *
     * @param param TradeOrderCarrier 查询数据的条件
     * @return int
     */
    int insert(TradeOrderCarrier param) throws Exception;
}