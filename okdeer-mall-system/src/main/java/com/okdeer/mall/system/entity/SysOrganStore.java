/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysOrganStore.java
 * @Date 2017-03-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.system.entity;

import java.io.Serializable;

/**
 * 组织关联店铺表
 * 
 * @author null
 * @version 1.0 2017-03-10
 */
@SuppressWarnings("serial")
public class SysOrganStore implements Serializable{

    /**
     * 主键id
     */
    private String id;
    /**
     * 组织id
     */
    private String orgId;
    /**
     * 店铺id
     */
    private String storeId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}