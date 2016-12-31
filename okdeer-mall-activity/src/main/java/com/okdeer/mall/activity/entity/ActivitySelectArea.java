/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivitySelectArea.java
 * @Date 2016-12-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.entity;

/**
 * 活动与地区关联表(按城市选择任务范围才有关联)
 * 
 * @author tangzj02
 * @version 1.0 2016-12-30
 */
public class ActivitySelectArea {

    /**
     * 主键
     */
    private String id;
    /**
     * 相关活动ID
     */
    private String activityId;
    /**
     * 活动类型 1:首页ICON 2:APP端服务商品推荐
     */
    private Integer activityType;
    /**
     * 省ID
     */
    private String provinceId;
    /**
     * 省名称
     */
    private String provinceName;
    /**
     * 城市ID(如果没有城市，这表示选择的是全省)
     */
    private String cityId;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 1:城市  2:省
     */
    private Integer areaType;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Integer getActivityType() {
        return activityType;
    }

    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
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

    public Integer getAreaType() {
        return areaType;
    }

	public void setAreaType(Integer areaType) {
        this.areaType = areaType;
    }
}