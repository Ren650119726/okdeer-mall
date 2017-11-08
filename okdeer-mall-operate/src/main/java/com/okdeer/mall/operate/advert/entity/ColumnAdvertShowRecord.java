/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnAdvertShowRecord.java
 * @Date 2017-11-08 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.advert.entity;

import java.util.Date;

/**
 * 
 * 
 * @author null
 * @version 1.0 2017-11-08
 */
public class ColumnAdvertShowRecord {

    /**
     * 主键id
     */
    private String id;
    /**
     * 广告id
     */
    private String advertId;
    /**
     * 设备号
     */
    private String deviceNo;
    /**
     * 记录时间
     */
    private Date createTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdvertId() {
        return advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}