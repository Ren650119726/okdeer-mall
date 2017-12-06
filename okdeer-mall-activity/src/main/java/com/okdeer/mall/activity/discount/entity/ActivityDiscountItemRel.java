/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityDiscountItemRel.java
 * @Date 2017-12-06 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.discount.entity;

import java.math.BigDecimal;

/**
 * 平台活动二级表业务关联
 * 
 * @author YSCGD
 * @version 1.0 2017-12-06
 */
public class ActivityDiscountItemRel implements java.io.Serializable{

    /**
     * ID
     */
    private String id;
    /**
     * 活动ID
     */
    private String activityId;
    /**
     * 活动梯度ID（二级表id）
     */
    private String activityItemId;
    /**
     * 0商品 ，1 赠品，2换购
     */
    private Byte type;
    /**
     * 业务id
     */
    private String businessId;
    /**
     * 排序值
     */
    private Byte sort;
    /**
     * 换购价格
     */
    private BigDecimal price;


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

    public String getActivityItemId() {
        return activityItemId;
    }

    public void setActivityItemId(String activityItemId) {
        this.activityItemId = activityItemId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Byte getSort() {
        return sort;
    }

    public void setSort(Byte sort) {
        this.sort = sort;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}