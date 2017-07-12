package com.okdeer.mall.order.api;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Maps;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.message.MQMessage;
import com.okdeer.mall.express.dto.ExpressCallbackDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderCarrier;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.service.ExpressOrderCallbackApi;
import com.okdeer.mall.order.service.ExpressOrderCallbackService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.vo.TradeOrderOperateParamVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ClassName: ExpressOrderCallbackApiImpl
 *
 * @author wangf01
 * @Description: 订单快递配送信息回调-api-impl
 * @date 2017年6月21日
 * <p>
 * =================================================================================================
 * Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.ExpressOrderCallbackApi")
public class ExpressOrderCallbackApiImpl implements ExpressOrderCallbackApi {

    /**
     * 注入-service
     */
    @Autowired
    private ExpressOrderCallbackService expressOrderCallbackService;

    /**
     * 注入订单-service
     */
    @Autowired
    private TradeOrderService tradeOrderService;

    @Autowired
    private RocketMQProducer rocketMQProducer;

    @Reference(version = "1.0.0", check = false)
    private StoreInfoServiceApi storeInfoServiceApi;

    @Override
    public void saveExpressCallback(ExpressCallbackDto data) throws Exception {
        expressOrderCallbackService.saveExpressCallback(data);
        switch (data.getOrderStatus()) {
            case 20:
                TradeOrderCarrier carrier = new TradeOrderCarrier();
                carrier.setOrderNo(data.getPartnerOrderCode());
                carrier.setCarrierDriverName(data.getCarrierDriverName());
                carrier.setCarrierDriverPhone(data.getCarrierDriverPhone());
                Map<String, Object> map = Maps.newHashMap();
                map.put("orderNo", data.getPartnerOrderCode());
                List<TradeOrder> tradeOrderList = tradeOrderService.selectByParams(map);
                if (CollectionUtils.isNotEmpty(tradeOrderList)) {
                    carrier.setOrderId(tradeOrderList.get(0).getId());
                }
                MQMessage message = new MQMessage("topic_order_carrier_msg", (Serializable) carrier);
                //加一个key 订单id+状态,没有实际意义,方便查询定位错误
                message.setKey(carrier.getOrderNo());
                rocketMQProducer.sendMessage(message);
                break;
            case 80:
            case 2:
            case 3:
                TradeOrder tradeOrder = tradeOrderService.findByOrderNo(data.getPartnerOrderCode());
                if(tradeOrder.getStatus() == OrderStatusEnum.DROPSHIPPING){
                    TradeOrderOperateParamVo param = new TradeOrderOperateParamVo();
                    // 发货的订单ID
                    param.setOrderId(tradeOrder.getId());
                    // 根据店铺ID查询店主ID
                    String bossId = storeInfoServiceApi.getBossIdByStoreId(tradeOrder.getStoreId());
                    // 操作人ID
                    param.setUserId(bossId);
                    // 店铺ID
                    param.setStoreId(tradeOrder.getStoreId());
                    tradeOrderService.updateOrderShipment(param);
                }
                break;
            default:
                break;
        }
    }
}
