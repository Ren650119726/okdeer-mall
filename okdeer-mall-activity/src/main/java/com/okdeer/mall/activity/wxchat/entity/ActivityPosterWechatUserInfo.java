/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityPosterWechatUserInfo.java
 * @Date 2017-08-04 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.wxchat.entity;

import java.util.Date;

/**
 * 海报活动微信用户信息
 * 
 * @author null
 * @version 1.0 2017-08-04
 */
public class ActivityPosterWechatUserInfo {

    /**
     * 用户id
     */
    private String openid;
    /**
     * 活动抽奖资格次数
     */
    private Integer qualificaCount;
    /**
     * 已经使用资格次数
     */
    private Integer usedQualificaCount;
    /**
     * 最后更新时间
     */
    private Date updateTime;
    /**
     * 海报素材id
     */
    private String posterMediaId;
    /**
     * 海报过期时间
     */
    private Date posterExpireTime;
    /**
     * 绑定手机号码
     */
    private String phoneNo;


    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Integer getQualificaCount() {
        return qualificaCount;
    }

    public void setQualificaCount(Integer qualificaCount) {
        this.qualificaCount = qualificaCount;
    }

    public Integer getUsedQualificaCount() {
        return usedQualificaCount;
    }

    public void setUsedQualificaCount(Integer usedQualificaCount) {
        this.usedQualificaCount = usedQualificaCount;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getPosterMediaId() {
        return posterMediaId;
    }

    public void setPosterMediaId(String posterMediaId) {
        this.posterMediaId = posterMediaId;
    }

    public Date getPosterExpireTime() {
        return posterExpireTime;
    }

    public void setPosterExpireTime(Date posterExpireTime) {
        this.posterExpireTime = posterExpireTime;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}