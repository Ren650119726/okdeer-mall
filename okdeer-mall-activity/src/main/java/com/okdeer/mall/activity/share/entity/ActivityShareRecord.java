/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityShareRecord.java
 * @Date 2017-10-20 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.activity.share.entity;

import java.util.Date;

import com.okdeer.base.common.enums.Disabled;

/**
 * 分享纪录表
 * 
 * @author null
 * @version 1.0 2017-10-20
 */
public class ActivityShareRecord {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 用户id
	 */
	private String sysUserId;

	/**
	 * 用户手机号码
	 */
	private String sysUserPhone;

	/**
	 * 商品id
	 */
	private String storeSkuId;

	/**
	 * 店铺id
	 */
	private String storeId;

	/**
	 * 活动id
	 */
	private String activityId;

	/**
	 * 活动类型
	 */
	private Integer activityType;

	/**
	 * 已经发货数量
	 */
	private Integer deliveryNum;

	/**
	 * 完成数量
	 */
	private Integer completeNum;

	/**
	 * 退款数量
	 */
	private Integer refundNum;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 创建人
	 */
	private String createUserId;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 更新人
	 */
	private String updateUserId;

	/**
	 * 0:未删除 1:删除
	 */
	private Disabled disabled;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSysUserId() {
		return sysUserId;
	}

	public void setSysUserId(String sysUserId) {
		this.sysUserId = sysUserId;
	}

	public String getSysUserPhone() {
		return sysUserPhone;
	}

	public void setSysUserPhone(String sysUserPhone) {
		this.sysUserPhone = sysUserPhone;
	}

	public String getStoreSkuId() {
		return storeSkuId;
	}

	public void setStoreSkuId(String storeSkuId) {
		this.storeSkuId = storeSkuId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
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

	public Integer getDeliveryNum() {
		return deliveryNum;
	}

	public void setDeliveryNum(Integer deliveryNum) {
		this.deliveryNum = deliveryNum;
	}

	public Integer getCompleteNum() {
		return completeNum;
	}

	public void setCompleteNum(Integer completeNum) {
		this.completeNum = completeNum;
	}

	public Integer getRefundNum() {
		return refundNum;
	}

	public void setRefundNum(Integer refundNum) {
		this.refundNum = refundNum;
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

	public Disabled getDisabled() {
		return disabled;
	}

	public void setDisabled(Disabled disabled) {
		this.disabled = disabled;
	}

}