/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * WechatPassiveReply.java
 * @Date 2017-09-25 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.activity.wxchat.entity;

import java.util.Date;

import com.okdeer.base.common.enums.Disabled;
import com.okdeer.mall.activity.wechat.enums.WechatRespMsgTypeEnum;

/**
 * 微信被动回复
 * 
 * @author null
 * @version 1.0 2017-09-25
 */
public class WechatPassiveReply {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 关键值，多个关键字用,隔开
	 */
	private String inputKeys;

	/**
	 * 回复类型 1 回复文本消息 2 回复图片消息 3 回复语音消息 4 回复视频消息 5 回复音乐消息 6 回复图文消息
	 */
	private WechatRespMsgTypeEnum respMsgType;

	/**
	 * 回复内容
	 */
	private String respContent;

	/**
	 * 创建人
	 */
	private String createUserId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	private String updateUserId;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 删除标志 （1：已经删除 0:未删除）
	 */
	private Disabled disabled;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInputKeys() {
		return inputKeys;
	}

	public void setInputKeys(String inputKeys) {
		this.inputKeys = inputKeys;
	}

	public WechatRespMsgTypeEnum getRespMsgType() {
		return respMsgType;
	}

	public void setRespMsgType(WechatRespMsgTypeEnum respMsgType) {
		this.respMsgType = respMsgType;
	}

	public String getRespContent() {
		return respContent;
	}

	public void setRespContent(String respContent) {
		this.respContent = respContent;
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

	public Disabled getDisabled() {
		return disabled;
	}

	public void setDisabled(Disabled disabled) {
		this.disabled = disabled;
	}

}