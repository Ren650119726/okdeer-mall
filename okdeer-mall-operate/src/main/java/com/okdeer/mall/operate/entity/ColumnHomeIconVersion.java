/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnHomeIconVersion.java
 * @Date 2017-04-07 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.entity;

import java.io.Serializable;

/**
 * ICON版本关系表
 * 
 * @author zhaoqc
 * @version 1.0 2017-04-07
 */
public class ColumnHomeIconVersion implements Serializable {
    /**
     * 序列化器
     */
    private static final long serialVersionUID = -986198282173844748L;
    
    /**
     * 主键ID
     */
    private String id;
    /**
     * ICON主键
     */
    private String iconId;
    /**
     * 版本
     */
    private String version;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}