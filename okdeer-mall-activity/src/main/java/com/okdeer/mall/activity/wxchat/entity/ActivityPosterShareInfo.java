/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityPosterShareInfo.java
 * @Date 2017-08-04 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.wxchat.entity;

import java.util.Date;

/**
 * 海报活动用户分享信息
 * 
 * @author null
 * @version 1.0 2017-08-04
 */
public class ActivityPosterShareInfo {

    /**
     * 主键id
     */
    private String id;
    /**
     * 关注用id
     */
    private String openid;
    /**
     * 分享用户id
     */
    private String shareOpenid;
    /**
     * 关注时间
     */
    private Date createTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getShareOpenid() {
        return shareOpenid;
    }

    public void setShareOpenid(String shareOpenid) {
        this.shareOpenid = shareOpenid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}