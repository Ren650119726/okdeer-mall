/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysBuyerRankRecord.java
 * @Date 2016-12-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.member.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员等级成长记录
 * 
 * @author null
 * @version 1.0 2016-12-30
 */
public class SysBuyerRankRecord {

    private String id;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 消费金额
     */
    private BigDecimal consumeAmount;
    /**
     * 成长值(整数表示增加，负数表示扣除)
     */
    private Integer growthVal;
    /**
     * 业务类型(1订单，2退货)
     */
    private Integer businessType;
    /**
     * 业务id(订单id等)
     */
    private String businessId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 备注
     */
    private String remark;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getConsumeAmount() {
        return consumeAmount;
    }

    public void setConsumeAmount(BigDecimal consumeAmount) {
        this.consumeAmount = consumeAmount;
    }

    public Integer getGrowthVal() {
        return growthVal;
    }

    public void setGrowthVal(Integer growthVal) {
        this.growthVal = growthVal;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}