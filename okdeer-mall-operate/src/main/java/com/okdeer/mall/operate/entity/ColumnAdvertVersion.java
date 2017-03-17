/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnAdvertVersion.java
 * @Date 2017-03-14 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.entity;

/**
 * 广告版本关联表
 * 
 * @author tangzj02
 * @version 1.0 2017-03-14
 */
public class ColumnAdvertVersion {

    /**
     * 主键ID
     */
    private String id;
    /**
     * 广告ID
     */
    private String advertId;
    /**
     * APP类型  0:管家版 3:便利店版
     */
    private Integer type;
    /**
     * APP版本
     */
    private String version;


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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}