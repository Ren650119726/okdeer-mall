package com.okdeer.mall.order.service.impl;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.order.entity.TradeOrderCarrier;
import com.okdeer.mall.order.mapper.TradeOrderCarrierMapper;
import com.okdeer.mall.order.service.TradeOrderCarrierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: TradeOrderCarrierServiceImpl
 *
 * @author wangf01
 * @Description: 订单快递配送信息-service-impl
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service
public class TradeOrderCarrierServiceImpl implements TradeOrderCarrierService {

    /**
     * 注入-mapper
     */
    @Autowired
    private TradeOrderCarrierMapper tradeOrderCarrierMapper;

    @Override
    public TradeOrderCarrier selectCarrierById(String id) throws Exception {
        TradeOrderCarrier param = new TradeOrderCarrier();
        param.setId(id);
        TradeOrderCarrier entity = tradeOrderCarrierMapper.selectCarrierByParam(param);
        return entity;
    }

    @Override
    public TradeOrderCarrier selectCarrierByParam(TradeOrderCarrier param) throws Exception {
        TradeOrderCarrier entity = tradeOrderCarrierMapper.selectCarrierByParam(param);
        return entity;
    }

    @Override
    public List<TradeOrderCarrier> selectCarrierListByParam(TradeOrderCarrier param) throws Exception {
        List<TradeOrderCarrier> list = tradeOrderCarrierMapper.selectCarrierListByParam(param);
        return list;
    }

    @Override
    public int insert(TradeOrderCarrier param) throws Exception {
        param.setId(UuidUtils.getUuid());
        int flag = tradeOrderCarrierMapper.insert(param);
        return flag;
    }

    @Override
    public int update(TradeOrderCarrier param) throws Exception {
        int flag = tradeOrderCarrierMapper.update(param);
        return flag;
    }
}
