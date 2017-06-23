package com.okdeer.mall.order.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderCarrier;
import com.okdeer.mall.order.service.TradeOrderCarrierApi;
import com.okdeer.mall.order.service.TradeOrderCarrierService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * ClassName: TradeOrderCarrierApiImpl
 *
 * @author wangf01
 * @Description: 订单快递配送信息-api
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderCarrierApi")
public class TradeOrderCarrierApiImpl implements TradeOrderCarrierApi {

    /**
     * 注入-service
     */
    @Autowired
    private TradeOrderCarrierService tradeOrderCarrierService;

    @Override
    public TradeOrderCarrier selectCarrierByParam(String id) throws Exception {
        return tradeOrderCarrierService.selectCarrierById(id);
    }

    @Override
    public TradeOrderCarrier selectCarrierByParam(TradeOrderCarrier param) throws Exception {
        return tradeOrderCarrierService.selectCarrierByParam(param);
    }

    @Override
    public List<TradeOrderCarrier> selectCarrierListByParam(TradeOrderCarrier param) throws Exception {
        return tradeOrderCarrierService.selectCarrierListByParam(param);
    }

    @Override
    public int insert(TradeOrderCarrier param) throws Exception {
        return tradeOrderCarrierService.insert(param);
    }
}
