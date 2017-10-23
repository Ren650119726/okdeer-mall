/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityShareOrderRecord.java
 * @Date 2017-10-20 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.activity.share.entity;

import java.util.Date;

/**
 * 活动分享下单纪录表
 * 
 * @author null
 * @version 1.0 2017-10-20
 */
public class ActivityShareOrderRecord {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 订单id
	 */
	private String orderId;

	/**
	 * 分享id
	 */
	private String shareId;

	/**
	 * 0:订单 1:退款单
	 */
	private Integer type;

	/**
	 * 状态 0:创建订单 1:已经发货 2 已经完成
	 */
	private Integer status;

	/**
	 * 创建时间
	 */
	private Date createTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}