/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityH5AdvertRole.java
 * @Date 2017-08-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.nadvert.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * h5_活动规则内容
 * 
 * @author mengsj
 * @version 1.0 2017-08-10
 */
public class ActivityH5AdvertRole implements Serializable {

    /**
	 * @Fields serialVersionUID : 序列号
	 */
	private static final long serialVersionUID = -1273320554736723358L;
	private String id;
    /**
     *  活动ID
     */
    private String activityId;
    /**
     * 规则引用城市id(0表示全国)
     */
    private String cityId;
    /**
     * 规则内容
     */
    private String content;
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

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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