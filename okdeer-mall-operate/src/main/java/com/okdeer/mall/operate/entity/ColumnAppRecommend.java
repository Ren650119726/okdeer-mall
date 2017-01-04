/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityAppRecommend.java
 * @Date 2016-12-30 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.entity;

import java.util.Date;

import com.okdeer.mall.operate.enums.AppRecommendPlace;
import com.okdeer.mall.operate.enums.AppRecommendStatus;
import com.okdeer.mall.operate.enums.SelectAreaType;

/**
 * APP端服务商品推荐表
 * 
 * @author tangzj02
 * @version 1.0 2016-12-30
 */
public class ColumnAppRecommend {

	private String id;

	/**
	 * 推荐标题
	 */
	private String title;

	/**
	 * 推荐位置 0:首页 1:发现
	 */
	private AppRecommendPlace place;

	/**
	 * 封面展示图片地址
	 */
	private String coverPicUrl;

	/**
	 * 封面展示图片跳转地址
	 */
	private String coverSkipUrl;

	/**
	 * 商品总数
	 */
	private Integer goodsCount;

	/**
	 * 展示商品数
	 */
	private Integer showGoodsCount;

	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 0:正在展示 1:已关闭
	 */
	private AppRecommendStatus status;

	/**
	 * 地区范围  0:全国  1:按城市选择范围
	 */
	private SelectAreaType areaType;

	/**
	 * 创建者
	 */
	private String createUserId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 修改者
	 */
	private String updateUserId;

	/**
	 * 修改时间
	 */
	private Date updateTime;

	/**
	 * 删除标示(0: 有效 1:无效)
	 */
	private Integer disabled;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public AppRecommendPlace getPlace() {
		return place;
	}

	public void setPlace(AppRecommendPlace place) {
		this.place = place;
	}

	public String getCoverPicUrl() {
		return coverPicUrl;
	}

	public void setCoverPicUrl(String coverPicUrl) {
		this.coverPicUrl = coverPicUrl;
	}

	public String getCoverSkipUrl() {
		return coverSkipUrl;
	}

	public void setCoverSkipUrl(String coverSkipUrl) {
		this.coverSkipUrl = coverSkipUrl;
	}

	public Integer getGoodsCount() {
		return goodsCount;
	}

	public void setGoodsCount(Integer goodsCount) {
		this.goodsCount = goodsCount;
	}

	public Integer getShowGoodsCount() {
		return showGoodsCount;
	}

	public void setShowGoodsCount(Integer showGoodsCount) {
		this.showGoodsCount = showGoodsCount;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public AppRecommendStatus getStatus() {
		return status;
	}

	public void setStatus(AppRecommendStatus status) {
		this.status = status;
	}

	public SelectAreaType getAreaType() {
		return areaType;
	}

	public void setAreaType(SelectAreaType areaType) {
		this.areaType = areaType;
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