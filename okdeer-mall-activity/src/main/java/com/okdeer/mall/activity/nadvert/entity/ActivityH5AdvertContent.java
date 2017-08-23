/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityH5AdvertContent.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.nadvert.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * h5_广告活动内容
 * 
 * @author mengsj
 * @version 1.0 2017-08-10
 */
public class ActivityH5AdvertContent implements Serializable {

    /**
	 * @Fields serialVersionUID : 序列号
	 */
	private static final long serialVersionUID = -3784483123755038321L;
	private String id;
    /**
     * 活动id
     */
    private String activityId;
    /**
     * 内容类型(1.广告图片 2.广告商品列表 3.添加代金券活4.标准商品列表)
     */
    private Integer contentType;
    /**
     * 排序
     */
    private Integer sortNo;
    /**
     * 颜色设置(json格式)
     */
    private String colorSetting;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private String createUserId;


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

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public String getColorSetting() {
        return colorSetting;
    }

    public void setColorSetting(String colorSetting) {
        this.colorSetting = colorSetting;
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
}