package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrder;

/**
 * ClassName: TradeOrderSendMessageService 
 * @Description: 便利店订单消息发送service
 * @author zhaoqc
 * @date 2017年2月18日
 *
 * =================================================================================================
 *     Task ID            Date               Author           Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *      友门鹿1.2          2016年11月4日          maojj                 上门服务订单轨迹服务
 */
public interface TradeOrderSendMessageService {
    
    /**
     * 订单发送消息
     * @param tradeOrder 便利店订单
     * @return
     * @date 2017-2-18
     */
    void tradeSendMessage(TradeOrder tradeOrder);
    
}
