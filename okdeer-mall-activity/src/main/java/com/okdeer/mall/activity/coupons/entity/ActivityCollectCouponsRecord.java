/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityCollectCouponsRecord.java
 * @Date 2017-08-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.coupons.entity;

import java.util.Date;

/**
 * 代金卷活动领取记录表
 * 
 * @author null
 * @version 1.0 2017-08-30
 */
public class ActivityCollectCouponsRecord {

    /**
     * 主键id
     */
    private String id;
    /**
     * 代金卷活动id
     */
    private String couponsCollectId;
    /**
     * 手机号码
     */
    private String phoneNo;
    /**
     * 领取时间
     */
    private Date collectTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouponsCollectId() {
        return couponsCollectId;
    }

    public void setCouponsCollectId(String couponsCollectId) {
        this.couponsCollectId = couponsCollectId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Date getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Date collectTime) {
        this.collectTime = collectTime;
    }
}