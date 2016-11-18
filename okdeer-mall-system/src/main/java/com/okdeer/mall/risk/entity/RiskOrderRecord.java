/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * RiskOrderRecord.java
 * @Date 2016-11-17 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.risk.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.okdeer.mall.risk.enums.IsPreferential;

/**
 * 风控记录
 * 
 * @author guocp
 * @version 1.0 2016-11-17
 */
public class RiskOrderRecord {

    /**
     * 主键
     */
    private String id;
    /**
     * 登入用户名
     */
    private String loginName;
    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 支付帐号
     */
    private String payAccount;
    /**
     * 支付帐号类型：0支付宝，1微信
     */
    private Byte payAccountType;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 充值号码
     */
    private String tel;
    /**
     * 充值面值
     */
    private BigDecimal facePrice;
    /**
     * 是否使用优惠
     */
    private IsPreferential isPreferential;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    public Byte getPayAccountType() {
        return payAccountType;
    }

    public void setPayAccountType(Byte payAccountType) {
        this.payAccountType = payAccountType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public BigDecimal getFacePrice() {
        return facePrice;
    }

    public void setFacePrice(BigDecimal facePrice) {
        this.facePrice = facePrice;
    }

    public IsPreferential getIsPreferential() {
        return isPreferential;
    }

    public void setIsPreferential(IsPreferential isPreferential) {
        this.isPreferential = isPreferential;
    }
}