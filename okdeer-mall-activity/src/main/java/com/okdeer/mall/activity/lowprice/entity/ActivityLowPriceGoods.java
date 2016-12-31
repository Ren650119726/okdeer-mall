/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityLowPriceGoods.java
 * @Date 2016-12-29 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.lowprice.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * ClassName: ActivityLowPriceGoods 
 * @Description: 低价抢购商品
 * @author tangy
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2016年12月29日                               tangy
 */
public class ActivityLowPriceGoods {

    private String id;
    /**
     * 店铺ID
     */
    private String storeId;
    /**
     * 低价活动ID
     */
    private String activityLowPriceId;
    /**
     * 店铺商品ID
     */
    private String storeSkuId;
    /**
     * 商品款数购买限制:0不限制，大于0表示限制数量
     */
    private Integer limit;
    /**
     * 商品库存
     */
    private Integer stock;
    /**
     * 商品价格
     */
    private BigDecimal price;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 低价抢购状态(1：进行中，2已关闭)
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private String createUserId;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 更新人
     */
    private String updateUserId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getActivityLowPriceId() {
        return activityLowPriceId;
    }

    public void setActivityLowPriceId(String activityLowPriceId) {
        this.activityLowPriceId = activityLowPriceId;
    }

    public String getStoreSkuId() {
        return storeSkuId;
    }

    public void setStoreSkuId(String storeSkuId) {
        this.storeSkuId = storeSkuId;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
}