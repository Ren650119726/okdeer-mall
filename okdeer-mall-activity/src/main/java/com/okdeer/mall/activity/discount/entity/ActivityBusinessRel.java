/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityBusinessRel.java
 * @Date 2017-04-17 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.discount.entity;

import com.okdeer.mall.activity.discount.enums.ActivityBusinessType;

/**
 * 活动业务关联关系
 * 
 * @author maojj
 * @version 1.0 2017-04-17
 */
public class ActivityBusinessRel {

    /**
     * ID
     */
    private String id;
    /**
     * 活动ID
     */
    private String activityId;
    /**
     * 关联业务Id
     */
    private String businessId;
    /**
     * 关联业务类型，0：市，1：省，2：店铺，3：商品 ，4：商品分类，5:小区
     */
    private ActivityBusinessType businessType;
    /**
     * 排序(从小到大)
     */
    private Integer sort;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public ActivityBusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(ActivityBusinessType businessType) {
        this.businessType = businessType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}