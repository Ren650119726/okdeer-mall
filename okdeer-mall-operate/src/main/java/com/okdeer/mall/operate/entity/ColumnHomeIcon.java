/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityHomeIcon.java
 * @Date 2016-12-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.entity;

import java.util.Date;

import com.okdeer.mall.operate.enums.HomeIconPlace;
import com.okdeer.mall.operate.enums.HomeIconTaskType;
import com.okdeer.mall.operate.enums.SelectAreaType;

/**
 * 
 * 
 * @author tangzj02
 * @version 1.0 2016-12-30
 */
public class ColumnHomeIcon {

	/**
	 * 主键
	 */
	private String id;

	/**
	 * ICON名称
	 */
	private String name;

	/**
	 * ICON位置 0:首页ICON一 1:首页ICON二  2:首页ICON三 ......（必填）
	 */
	private HomeIconPlace place;

	/**
	 * ICON图片
	 */
	private String iconUrl;

	/**
	 * 二级页面顶部图片
	 */
	private String bannerUrl;

	/**
	 * 用于列表中区域字段内容显示
	 */
	private String taskContent;

	/**
	 * 任务内容  0:指定指定商品推荐  1: 自助买单 2:分类 
	 */
	private HomeIconTaskType taskType;

	/**
	 * 任务范围 0:全国  1:按城市选择任务范围
	 */
	private SelectAreaType taskScope;

	/**
	 * 创建人
	 */
	private String createUserId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新人
	 */
	private String updateUserId;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 是否删除(0：否 1：是)
	 */
	private Integer disabled;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HomeIconPlace getPlace() {
		return place;
	}

	public void setPlace(HomeIconPlace place) {
		this.place = place;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getBannerUrl() {
		return bannerUrl;
	}

	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}

	public String getTaskContent() {
		return taskContent;
	}

	public void setTaskContent(String taskContent) {
		this.taskContent = taskContent;
	}

	public HomeIconTaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(HomeIconTaskType taskType) {
		this.taskType = taskType;
	}

	public SelectAreaType getTaskScope() {
		return taskScope;
	}

	public void setTaskScope(SelectAreaType taskScope) {
		this.taskScope = taskScope;
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

	public Integer getDisabled() {
		return disabled;
	}

	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}
}