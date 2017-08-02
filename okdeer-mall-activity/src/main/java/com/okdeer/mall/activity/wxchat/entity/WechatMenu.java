/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * WechatMenu.java
 * @Date 2017-08-01 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.activity.wxchat.entity;

import java.util.Date;

import com.okdeer.mall.activity.wechat.enums.WechatMenuTypeEnum;

/**
 * 
 * 
 * @author null
 * @version 1.0 2017-08-01
 */
public class WechatMenu {

	private String id;

	/**
	 * 1:一级菜单 2：二级菜单
	 */
	private Integer levelType;

	private String parentId;

	/**
	 * 按钮名称
	 */
	private String buttonName;

	/**
	 * 按钮key值
	 */
	private String buttonKey;

	/**
	 * 按钮类型(1、click 2、view 3、scancode_push 4、scancode_waitmsg5、pic_sysphoto 6、pic_photo_or_album 7、pic_weixin  8、location_select 9、media_id  10、view_limited)
	 */
	private WechatMenuTypeEnum type;

	/**
	 * 网页地址
	 */
	private String url;

	/**
	 * 素材id
	 */
	private String mediaId;

	/**
	 * 排序值
	 */
	private Integer sort;

	private Date createTime;

	private String createUser;

	private Date updateTime;

	private String updateUser;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getLevelType() {
		return levelType;
	}

	public void setLevelType(Integer levelType) {
		this.levelType = levelType;
	}

	public String getButtonName() {
		return buttonName;
	}

	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

	public String getButtonKey() {
		return buttonKey;
	}

	public void setButtonKey(String buttonKey) {
		this.buttonKey = buttonKey;
	}

	public WechatMenuTypeEnum getType() {
		return type;
	}

	public void setType(WechatMenuTypeEnum type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}