/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * HomeIconGoods.java
 * @Date 2016-12-29 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.entity;

/**
 * 首页ICON商品关联表(指定商品推荐才有关联)
 * 
 * @author tangzj02
 * @version 1.0 2016-12-29
 */
public class HomeIconGoods {

    private String id;
    /**
     * 首页ICON记录ID(home_icon)
     */
    private String iconId;
    /**
     * 商品ID（关联goods_store_sku表）
     */
    private String goodsId;


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

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }
}