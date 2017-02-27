package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderRefunds;

/**
 * ClassName: TradeOrderSendMessageService 
 * @Description: 便利店订单消息发送service
 * @author zhaoqc
 * @date 2017年2月18日
 *
 * =================================================================================================
 *     Task ID            Date               Author           Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     友门鹿2.1          2017年2月18日                        zhaoqc          便利店订单状态或者退款单状态发生改变时发送通知消息
 */
public interface TradeOrderSendMessageService {
    
    /**
     * 订单发送消息
     * @param tradeOrder 便利店订单
     * @return
     * @date 2017-2-18
     * @throws Exception
     * @author zhaoqc
     */
    void tradeSendMessage(TradeOrder tradeOrder, TradeOrderRefunds orderRefunds);
    
}
