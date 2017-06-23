package com.okdeer.mall.order.mapper;


import com.okdeer.mall.order.entity.TradeOrderCarrier;

import java.util.List;

/**
 * ClassName: TradeOrderCarrierMapper
 *
 * @author wangf01
 * @Description: 订单快递配送信息
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
public interface TradeOrderCarrierMapper {

    /**
     * 根据条件查询符合的数据
     *
     * @param param TradeOrderCarrier 查询数据的条件
     * @return TradeOrderCarrier
     */
    TradeOrderCarrier selectCarrierByParam(TradeOrderCarrier param);

    /**
     * 根据条件查询符合的数据
     *
     * @param param TradeOrderCarrier 查询数据的条件
     * @return List<TradeOrderCarrier>
     */
    List<TradeOrderCarrier> selectCarrierListByParam(TradeOrderCarrier param);

    /**
     * 保存数据
     *
     * @param param TradeOrderCarrier 查询数据的条件
     * @return int
     */
    int insert(TradeOrderCarrier param);
}
