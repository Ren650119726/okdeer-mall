/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityDiscountMultiItem.java
 * @Date 2017-12-06 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.discount.entity;

import java.math.BigDecimal;

/**
 * N件X元明细表
 * 
 * @author YSCGD
 * @version 1.0 2017-12-06
 */
public class ActivityDiscountMultiItem implements java.io.Serializable{

    /**
     * 主键id
     */
    private String id;
    /**
     * 平台活动id
     */
    private String activityId;
    /**
     * activity_discount_item表id
     */
    private String activityItemId;
    /**
     * 件数
     */
    private Integer piece;
    /**
     * 多少元
     */
    private BigDecimal price;
    /**
     * 梯度优惠名称
     */
    private String name;
    /**
     * 排序值
     */
    private Integer tinyint;


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

    public Integer getPiece() {
        return piece;
    }

    public void setPiece(Integer piece) {
        this.piece = piece;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTinyint() {
        return tinyint;
    }

    public void setTinyint(Integer tinyint) {
        this.tinyint = tinyint;
    }
}