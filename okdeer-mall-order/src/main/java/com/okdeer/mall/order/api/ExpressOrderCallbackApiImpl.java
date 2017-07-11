package com.okdeer.mall.order.api;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.mall.express.dto.ExpressCallbackDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.service.ExpressOrderCallbackApi;
import com.okdeer.mall.order.service.ExpressOrderCallbackService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.vo.TradeOrderOperateParamVo;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Reference(version = "1.0.0", check = false)
    private StoreInfoServiceApi storeInfoServiceApi;

    @Override
    public void saveExpressCallback(ExpressCallbackDto data) throws Exception {
        expressOrderCallbackService.saveExpressCallback(data);
        switch (data.getOrderStatus()) {
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
