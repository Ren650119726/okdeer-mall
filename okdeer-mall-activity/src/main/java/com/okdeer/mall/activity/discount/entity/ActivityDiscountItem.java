/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityDiscountItem.java
 * @Date 2017-12-06 Created
 * ע�⣺�����ݽ���������¹��˾�ڲ����ģ���ֹ��й�Լ�������������ҵĿ��
 */
package com.okdeer.mall.activity.discount.entity;

import java.math.BigDecimal;

/**
 * 店铺活动梯度表（二级）
 * 
 * @author YSCGD
 * @version 1.0 2017-12-06
 */
public class ActivityDiscountItem implements java.io.Serializable{

    /**
     * 主键id
     */
    private String id;
    /**
     * 平台活动id
     */
    private String activityId;
    /**
     * 梯度优惠名称
     */
    private String name;
    /**
     * 订单金额满多少元
     */
    private BigDecimal limitOrderAmount;
    /**
     * 商品限制：0:不限，1：指定导航分类，2：指定商品
     */
    private Integer limitSku;
    /**
     * 导航分类是否包含 0包含 1不包含
     */
    private Integer categoryInvert;
    /**
     * 订单最多换购/赠送
     */
    private Integer orderMaxCount;
    /**
     * 用户限购总数
     */
    private Integer userCountLimit;
    /**
     * 用户每日限数
     */
    private Integer userDayCountLimit;
    /**
     * 排序值
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLimitOrderAmount() {
        return limitOrderAmount;
    }

    public void setLimitOrderAmount(BigDecimal limitOrderAmount) {
        this.limitOrderAmount = limitOrderAmount;
    }

    public Integer getLimitSku() {
        return limitSku;
    }

    public void setLimitSku(Integer limitSku) {
        this.limitSku = limitSku;
    }

    public Integer getCategoryInvert() {
        return categoryInvert;
    }

    public void setCategoryInvert(Integer categoryInvert) {
        this.categoryInvert = categoryInvert;
    }

    public Integer getOrderMaxCount() {
        return orderMaxCount;
    }

    public void setOrderMaxCount(Integer orderMaxCount) {
        this.orderMaxCount = orderMaxCount;
    }

    public Integer getUserCountLimit() {
        return userCountLimit;
    }

    public void setUserCountLimit(Integer userCountLimit) {
        this.userCountLimit = userCountLimit;
    }

    public Integer getUserDayCountLimit() {
        return userDayCountLimit;
    }

    public void setUserDayCountLimit(Integer userDayCountLimit) {
        this.userDayCountLimit = userDayCountLimit;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}