package com.okdeer.mall.system.entity;

/**
 * 买家用户首单记录表
 * 
 * @author maojj
 * @version 1.0 2017-02-21
 */
public class SysBuyerFirstOrderRecord {

    private String id;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 买家用户id
     */
    private String userId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}