/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysAppAccessRecord.java
 * @Date 2017-01-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.member.entity;

import java.util.Date;

/**
 * APP设备访问记录表
 * 
 * @author tangzj02
 * @version 1.0 2017-01-10
 */
public class SysAppAccessRecord {

	/**
	 * 主键ID
	 */
	private String id;

	/**
	 * 城市ID
	 */
	private String cityId;

	/**
	 * 城市名称
	 */
	private String cityName;

	/**
	 * app类型 0:管家 3:便利店
	 */
	private Integer appType;

	/**
	 * APP版本
	 */
	private String appVersion;

	/**
	 * 手机品牌
	 */
	private String brand;

	/**
	 * 手机分辨率
	 */
	private String screen;

	/**
	 * 手机网络
	 */
	private String network;

	/**
	 * 手机设备号
	 */
	private String machineCode;

	/**
	 * 添加时间
	 */
	private Date createTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Integer getAppType() {
		return appType;
	}

	public void setAppType(Integer appType) {
		this.appType = appType;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getScreen() {
		return screen;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}