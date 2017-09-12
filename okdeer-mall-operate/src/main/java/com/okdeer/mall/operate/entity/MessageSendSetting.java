/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * MessageSendSetting.java
 * @Date 2017-08-15 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.entity;

import java.util.Date;

/**
 * app消息推送设置表(V2.6)
 * 
 * @author xuzq01
 * @version 1.0 2017-08-15
 */
public class MessageSendSetting {

    /**
     * 主键
     */
    private String id;
    /**
     * 消息名称
     */
    private String messageName;
    /**
     * 消息内容 可输入url地址
     */
    private String context;
    /**
     * 发送范围类型 0：全部 1：区域城市
     */
    private Integer rangeType;
    /**
     * 发送类型 0 立即发送 1自定义发送
     */
    private Integer sendType;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 发送类型 0 全部用户 1新用户 2 老用户 3 自定义用户
     */
    private Byte type;
    /**
     * 自定义发送多少天没有下单用户（type为3时有效）
     */
    private Integer sendObject;
    /**
     * 消息推送状态 0未推送 1已推送 2已关闭
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人id
     */
    private String createUserId;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 更新人id
     */
    private String updateUserId;
    /**
     * 删除标志 0否 1是
     */
    private Boolean disabled;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Integer getRangeType() {
		return rangeType;
	}

	public void setRangeType(Integer rangeType) {
		this.rangeType = rangeType;
	}

	public Integer getSendType() {
		return sendType;
	}

	public void setSendType(Integer sendType) {
		this.sendType = sendType;
	}

	public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Integer getSendObject() {
        return sendObject;
    }

    public void setSendObject(Integer sendObject) {
        this.sendObject = sendObject;
    }

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}