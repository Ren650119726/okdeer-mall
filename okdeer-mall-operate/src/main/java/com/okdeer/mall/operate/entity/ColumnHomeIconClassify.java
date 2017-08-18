/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnHomeIconClassifyDto.java
 * @Date 2017-08-15 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.entity;

/**
 * 首页ICON导航分类关联表(V2.6)
 * 
 * @author xuzq01
 * @version 1.0 2017-08-15
 */
public class ColumnHomeIconClassify {

    /**
     * 主键id
     */
    private String id;
    /**
     * 首页ICON记录ID(column_home_icon)
     */
    private String homeIconId;
    /**
     * 导航分类id（三级）
     */
    private String navigateCategoryId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHomeIconId() {
        return homeIconId;
    }

    public void setHomeIconId(String homeIconId) {
        this.homeIconId = homeIconId;
    }

    public String getNavigateCategoryId() {
        return navigateCategoryId;
    }

    public void setNavigateCategoryId(String navigateCategoryId) {
        this.navigateCategoryId = navigateCategoryId;
    }
}