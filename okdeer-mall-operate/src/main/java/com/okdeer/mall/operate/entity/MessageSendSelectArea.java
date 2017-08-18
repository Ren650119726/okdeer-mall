/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * MessageSendSelectArea.java
 * @Date 2017-08-15 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.entity;

/**
 * app消息推送设置与发送地区关联表(V2.6)
 * 
 * @author xuzq01
 * @version 1.0 2017-08-15
 */
public class MessageSendSelectArea {

    /**
     * 主键
     */
    private String id;
    /**
     * 推送消息ID
     */
    private String messageId;
    /**
     * 消息类型 0:app消息推送
     */
    private Integer messageType;
    /**
     * 省ID
     */
    private String provinceId;
    /**
     * 省名称
     */
    private String provinceName;
    /**
     * 城市ID(如果没有城市，这表示选择的是全省) 值为0
     */
    private String cityId;
    /**
     * 城市名称
     */
    private String cityName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

}