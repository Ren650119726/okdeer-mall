/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityCouponsStore.java
 * @Date 2017-06-23 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.coupons.entity;

/**
 * 代金券关联店铺表
 * 
 * @author maojj
 * @version 1.0 2017-06-23
 */
public class ActivityCouponsStore {

    /**
     * ID
     */
    private String id;
    /**
     * 代金券ID
     */
    private String couponsId;
    /**
     * 店铺ID
     */
    private String storeId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouponsId() {
        return couponsId;
    }

    public void setCouponsId(String couponsId) {
        this.couponsId = couponsId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}