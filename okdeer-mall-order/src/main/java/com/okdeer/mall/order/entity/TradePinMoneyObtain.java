/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * TradePinMoneyObtain.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.order.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 零花钱领取
 * 
 * @author guocp
 * @version 1.0 2017-08-10
 */
public class TradePinMoneyObtain {

    /**
     * 主键ID
     */
    private String id;
    /**
     * 来源交易订单ID
     */
    private String orderId;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户设备id
     */
    private String deviceId;
    
    /**
     * 状态：0未使用，1已使用
     */
    private Integer status;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 剩余金额
     */
    private BigDecimal remainAmount;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 有效时间
     */
    private Date validTime;


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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(BigDecimal remainAmount) {
        this.remainAmount = remainAmount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
    
}