/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityH5AdvertContentCoupons.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.nadvert.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * H5广告活动-代金劵活动
 * 
 * @author mengsj
 * @version 1.0 2017-08-10
 */
public class ActivityH5AdvertContentCoupons implements Serializable {

    /**
	 * @Fields serialVersionUID : 序列号
	 */
	private static final long serialVersionUID = -7264496827497316536L;
	/**
     * ID
     */
    private String id;
    /**
     * 广告活动id
     */
    private String activityId;
    /**
     * 广告活动内容id
     */
    private String contentId;
    /**
     * 广告代金券活动ID
     */
    private String collectCouponsId;
    /**
     * 城市id(0表示全国)
     */
    private String cityId = "0";
    /**
     *  图片设置
     */
    private String picUrl;
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

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getCollectCouponsId() {
        return collectCouponsId;
    }

    public void setCollectCouponsId(String collectCouponsId) {
        this.collectCouponsId = collectCouponsId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
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