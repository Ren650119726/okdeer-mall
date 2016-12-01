/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * TradeOrderRefundsItemDetail.java
 * @Date 2016-11-28 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.order.entity;

/**
 * 退款单项明细表
 * 
 * @author zengjz
 * @version 1.0 2016-11-28
 */
public class TradeOrderRefundsItemDetail {

    /**
     * 主键id
     */
    private String id;
    /**
     * 退款单项表
     */
    private String refundItemId;
    /**
     * 订单项明细id
     */
    private String orderItemDetailId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRefundItemId() {
        return refundItemId;
    }

    public void setRefundItemId(String refundItemId) {
        this.refundItemId = refundItemId;
    }

    public String getOrderItemDetailId() {
        return orderItemDetailId;
    }

    public void setOrderItemDetailId(String orderItemDetailId) {
        this.orderItemDetailId = orderItemDetailId;
    }
}