/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivitySelectArea.java
 * @Date 2016-12-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.entity;

import com.okdeer.mall.operate.enums.SelectAreaType;

/**
 * 活动与地区关联表(按城市选择任务范围才有关联)
 * 
 * @author tangzj02
 * @version 1.0 2016-12-30
 */
public class ColumnSelectArea {

	/**
	 * 主键
	 */
	private String id;

	/**
	 * 相关栏位ID
	 */
	private String columnId;

	/**
	 * 栏位类型 1:首页ICON 2:APP端服务商品推荐
	 */
	private Integer columnType;

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
	private SelectAreaType areaType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public Integer getColumnType() {
		return columnType;
	}

	public void setColumnType(Integer columnType) {
		this.columnType = columnType;
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

	public SelectAreaType getAreaType() {
		return areaType;
	}

	public void setAreaType(SelectAreaType areaType) {
		this.areaType = areaType;
	}

}