/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysBuyerSignRecord.java
 * @Date 2016-12-31 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.member.entity;

import java.util.Date;

/**
 * 买家用户签到记录表
 * 
 * @author null
 * @version 1.0 2016-12-31
 */
public class SysBuyerSignRecord {

    private String id;
    /**
     * 买家用户id，关联sys_buyer_user
     */
    private String userId;
    /**
     * 买家用户签到时间
     */
    private Date signTime;


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

    public Date getSignTime() {
        return signTime;
    }

    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }
}