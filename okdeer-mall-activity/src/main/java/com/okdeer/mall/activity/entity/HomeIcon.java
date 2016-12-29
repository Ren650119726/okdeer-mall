/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * HomeIcon.java
 * @Date 2016-12-29 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.entity;

import java.util.Date;

/**
 * 
 * 
 * @author tangzj02
 * @version 1.0 2016-12-29
 */
public class HomeIcon {

    /**
     * 主键
     */
    private String id;
    /**
     * ICON名称
     */
    private String name;
    /**
     * ICON位置 1:首页ICON一 2:首页ICON二 3:首页ICON三 ......
     */
    private Boolean place;
    /**
     * ICON图片
     */
    private String iconUrl;
    /**
     * 二级页面顶部图片
     */
    private String bannerUrl;
    /**
     * 冗余字段
     */
    private String taskContent;
    /**
     * 任务内容  0:指定指定商品推荐  1: 自助买单 2:分类 
     */
    private Boolean taskType;
    /**
     * 任务范围 0:全国  1:按城市选择任务范围
     */
    private Boolean taskScope;
    /**
     * 创建人
     */
    private String createUserId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private String updateUserId;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除(0：否 1：是)
     */
    private Boolean disabled;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPlace() {
        return place;
    }

    public void setPlace(Boolean place) {
        this.place = place;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public Boolean getTaskType() {
        return taskType;
    }

    public void setTaskType(Boolean taskType) {
        this.taskType = taskType;
    }

    public Boolean getTaskScope() {
        return taskScope;
    }

    public void setTaskScope(Boolean taskScope) {
        this.taskScope = taskScope;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}