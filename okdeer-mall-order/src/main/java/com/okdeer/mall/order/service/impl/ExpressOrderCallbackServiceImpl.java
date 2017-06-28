package com.okdeer.mall.order.service.impl;

import com.google.common.collect.Maps;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.ele.entity.ExpressCallback;
import com.okdeer.mall.ele.service.ExpressService;
import com.okdeer.mall.ele.util.ExpressOrderStatus;
import com.okdeer.mall.express.dto.ExpressCallbackDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderCarrier;
import com.okdeer.mall.order.service.ExpressOrderCallbackService;
import com.okdeer.mall.order.service.TradeOrderCarrierService;
import com.okdeer.mall.order.service.TradeOrderService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * ClassName: ExpressOrderCallbackServiceImpl
 *
 * @author wangf01
 * @Description: 订单快递配送信息回调-service-impl
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service
public class ExpressOrderCallbackServiceImpl implements ExpressOrderCallbackService {

    /**
     * 注入配送-service
     */
    @Autowired
    private ExpressService expressService;

    /**
     * 注入订单骑手信息-service
     */
    @Autowired
    private TradeOrderCarrierService tradeOrderCarrierService;

    /**
     * 注入订单信息-service
     */
    @Autowired
    private TradeOrderService tradeOrderService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveExpressCallback(ExpressCallbackDto data) throws Exception {
        ExpressCallback callback = new ExpressCallback();
        BeanMapper.copy(data, callback);
        expressService.saveCallback(callback);
        //保存骑手信息
        if (data.getOrderStatus() != null && data.getOrderStatus() == Integer.valueOf(ExpressOrderStatus.STATUS_20.getValue())) {
            TradeOrderCarrier entity = new TradeOrderCarrier();
            entity.setOrderNo(data.getPartnerOrderCode());
            entity.setCarrierDriverName(data.getCarrierDriverName());
            entity.setCarrierDriverPhone(data.getCarrierDriverPhone());
            Map<String, Object> map = Maps.newHashMap();
            map.put("orderNo", data.getPartnerOrderCode());
            List<TradeOrder> tradeOrderList = tradeOrderService.selectByParams(map);
            if (CollectionUtils.isNotEmpty(tradeOrderList)) {
                entity.setOrderId(tradeOrderList.get(0).getId());
            }
            tradeOrderCarrierService.insert(entity);
        } else if (data.getOrderStatus() != null && data.getOrderStatus() == Integer.valueOf(ExpressOrderStatus.STATUS_3.getValue())) {
            TradeOrderCarrier entity = new TradeOrderCarrier();
            entity.setOrderNo(data.getPartnerOrderCode());
            entity.setCarrierDriverName(data.getCarrierDriverName());
            entity.setCarrierDriverPhone(data.getCarrierDriverPhone());
            entity.setPushTime(data.getPushTime());
            tradeOrderCarrierService.update(entity);
        }
    }
}
