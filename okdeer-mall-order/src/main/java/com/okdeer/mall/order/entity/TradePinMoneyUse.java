/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * TradePinMoneyUse.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.order.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 零花钱使用
 * 
 * @author guocp
 * @version 1.0 2017-08-10
 */
public class TradePinMoneyUse {

    /**
     * 主键ID
     */
    private String id;
    /**
     * 使用订单ID
     */
    private String orderId;
    /**
     * 零用钱领取记录ID,多个用“,”隔开
     */
    private String sourceId;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 使用金额
     */
    private BigDecimal useAmount;
    /**
     * 使用金额
     */
    private BigDecimal orderAmount;
    /**
     * 创建时间
     */
    private Date createTime;


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

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getUseAmount() {
        return useAmount;
    }

    public void setUseAmount(BigDecimal useAmount) {
        this.useAmount = useAmount;
    }
    
	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	
	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}