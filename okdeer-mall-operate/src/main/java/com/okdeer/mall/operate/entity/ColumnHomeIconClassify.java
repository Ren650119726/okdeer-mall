/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnHomeIconClassify.java
 * @Date 2017-08-29 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.entity;

/**
 * 首页ICON导航分类关联表(V2.6)
 * 
 * @author xuzq01
 * @version 1.0 2017-08-29
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
     * 导航分类id
     */
    private String navigateCategoryId;
    /**
     * 导航分类名称
     */
    private String navigateCategoryName;
    /**
     * 父导航菜单id
     */
    private String pidNavigateCategoryId;
    /**
     * 父导航菜单名称
     */
    private String pidNavigateCategoryName;
    /**
     * 类目级别：0:一级、1:二级、2:三级
     */
    private Boolean levelType;


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

    public String getNavigateCategoryName() {
        return navigateCategoryName;
    }

    public void setNavigateCategoryName(String navigateCategoryName) {
        this.navigateCategoryName = navigateCategoryName;
    }

    public String getPidNavigateCategoryId() {
        return pidNavigateCategoryId;
    }

    public void setPidNavigateCategoryId(String pidNavigateCategoryId) {
        this.pidNavigateCategoryId = pidNavigateCategoryId;
    }

    public String getPidNavigateCategoryName() {
        return pidNavigateCategoryName;
    }

    public void setPidNavigateCategoryName(String pidNavigateCategoryName) {
        this.pidNavigateCategoryName = pidNavigateCategoryName;
    }

    public Boolean getLevelType() {
        return levelType;
    }

    public void setLevelType(Boolean levelType) {
        this.levelType = levelType;
    }
}