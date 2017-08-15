/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * wechatUser.java
 * @Date 2017-08-01 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.wxchat.entity;

import java.util.Date;

/**
 * 
 * 
 * @author null
 * @version 1.0 2017-08-01
 */
public class WechatUser {

    private String id;
    /**
     * 微信用户唯一标识
     */
    private String openid;
    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     */
    private Integer sex;
    /**
     * 城市
     */
    private String city;
    /**
     * 国家
     */
    private String country;
    private String province;
    private String language;
    /**
     * 用户头像地址
     */
    private String headImgUrl;
    /**
     * 用户最后关注时间
     */
    private Date subscribeTime;
    /**
     * 关联id
     */
    private String unionid;
    /**
     * 备注
     */
    private String remark;
    /**
     * 分组
     */
    private String groupId;
    /**
     * 标签ID列表
     */
    private String tagids;


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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public Date getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(Date subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTagids() {
        return tagids;
    }

    public void setTagids(String tagids) {
        this.tagids = tagids;
    }
}