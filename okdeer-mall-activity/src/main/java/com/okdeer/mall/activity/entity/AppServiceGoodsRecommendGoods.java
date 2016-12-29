/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * AppServiceGoodsRecommendGoods.java
 * @Date 2016-12-29 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.entity;

/**
 * APP端服务商品推荐与商品关联表
 * 
 * @author tangzj02
 * @version 1.0 2016-12-29
 */
public class AppServiceGoodsRecommendGoods {

    private String id;
    /**
     * 服务商品推荐记录ID (activity_app_service_goods_recommend)
     */
    private String recommendId;
    /**
     * 店铺sku表（关联goods_store_sku表）
     */
    private String goodsId;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 是否展示 0:不展示  1:展示
     */
    private Integer isShow;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecommendId() {
        return recommendId;
    }

    public void setRecommendId(String recommendId) {
        this.recommendId = recommendId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getIsShow() {
        return isShow;
    }

	public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }
}