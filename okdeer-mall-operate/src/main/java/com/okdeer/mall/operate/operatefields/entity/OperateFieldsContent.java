/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * OperateFieldsContent.java
 * @Date 2017-04-13 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.operate.operatefields.entity;

import com.okdeer.mall.operate.enums.OperateFieldsBusinessType;
import com.okdeer.mall.operate.enums.OperateFieldsContentType;
import com.okdeer.mall.operate.enums.OperateFieldsSortType;

/**
 * 运营栏位内容表
 * 
 * @author null
 * @version 1.0 2017-04-13
 */
public class OperateFieldsContent {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 运营栏位id
	 */
	private String fieldId;

	/**
	 * 内容类型（0:单品选择1：店铺活动 2：指定店铺菜单 3：指定商品分类 4:h5链接5:原生专题页6：业务入口）
	 */
	private OperateFieldsContentType type;

	/**
	 * 图片地址
	 */
	private String imageUrl;

	/**
	 * 链接url (当类型为指定h5页面时)
	 */
	private String linkUrl;

	/**
	 * 业务类型( 0:特惠活动1：特价活动 2:商品详情3：店铺首页 4：店铺菜单)
	 */
	private OperateFieldsBusinessType businessType;

	/**
	 * 业务id(type=0为商品id type=2为菜单id type=3时为商品分类id type=5是为专题id type=6为商品id、菜单id)
	 */
	private String businessId;

	/**
	 * 排序类型(0:价格从大到小1：排序值从高到低 2：价格从小到大 3：排序值从低到高)
	 */
	private OperateFieldsSortType sortType;

	/**
	 * 排序值
	 */
	private Integer sort;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public OperateFieldsContentType getType() {
		return type;
	}

	public void setType(OperateFieldsContentType type) {
		this.type = type;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public OperateFieldsBusinessType getBusinessType() {
		return businessType;
	}

	public void setBusinessType(OperateFieldsBusinessType businessType) {
		this.businessType = businessType;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public OperateFieldsSortType getSortType() {
		return sortType;
	}

	public void setSortType(OperateFieldsSortType sortType) {
		this.sortType = sortType;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

}