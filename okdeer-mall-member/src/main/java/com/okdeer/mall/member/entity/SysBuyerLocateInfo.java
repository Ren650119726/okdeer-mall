/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * SysBuyerLocateInfo.java
 * @Date 2017-02-17 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.member.entity;

import java.util.Date;

/**
 * 买家用户定位信息表
 * 
 * @author chenzc
 * @version 1.0 2017-02-17
 */
public class SysBuyerLocateInfo {

    private String id;
    /**
     * 买家用户ID。关联sys_buyer_user表
     */
    private String userId;
    /**
     * 用户机器码
     */
    private String machineCode;
    /**
     * 省id
     */
    private String provinceId;
    /**
     * 用户定位的省份名称
     */
    private String provinceName;
    /**
     * 市id
     */
    private String cityId;
    /**
     * 用户定位的城市名称
     */
    private String cityName;
    /**
     * 区id
     */
    private String areaId;
    /**
     * 用户定位的经度
     */
    private Double longitude;
    /**
     * 用户定位的纬度
     */
    private Double latitude;
    /**
     * 注册来源
     */
    private String registerSource;
    /**
     * 用户首次打开APP记录时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * pos机注册时所在店铺城市id
     */
    private String storeCityId;
    /**
     * 具体地址
     */
    private String address;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
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

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getRegisterSource() {
        return registerSource;
    }

    public void setRegisterSource(String registerSource) {
        this.registerSource = registerSource;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public String getStoreCityId() {
		return storeCityId;
	}

	public void setStoreCityId(String storeCityId) {
		this.storeCityId = storeCityId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}