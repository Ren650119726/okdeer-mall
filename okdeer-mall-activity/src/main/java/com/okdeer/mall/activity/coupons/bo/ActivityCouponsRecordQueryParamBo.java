
package com.okdeer.mall.activity.coupons.bo;

import java.io.Serializable;
import java.util.Date;

public class ActivityCouponsRecordQueryParamBo implements Serializable {

	/**
	 * 代价劵活动类型 0代金券领取活动，1注册活动，2开门成功送代金券活动3 邀请注册送代金券活动4 消费返券活动
	 */
	private Integer collectType;

	/**
	 * 代金劵id
	 */
	private String couponsId;

	/**
	 * 代金券活动ID
	 */
	private String couponsCollectId;

	/**
	 * 代金卷领取时间
	 */
	private Date collectTime;

	/**
	 * 领取人用户id
	 */
	private String collectUserId;

	/**
	 * 0未使用，1已使用，2已过期
	 */
	private Integer status;

	/**
	 * 订单id
	 */
	private String orderId;

	/**
	 * 设备id
	 */
	private String deviceId;
	/**
	 * 领取开始时间(yyyy-MM-dd HH:mm:ss)
	 */
	private String collectStartTime;
	/**
	 * 领取结束时间(yyyy-MM-dd HH:mm:ss)
	 */
	private String collectEndTime;
	

	public Integer getCollectType() {
		return collectType;
	}

	public void setCollectType(Integer collectType) {
		this.collectType = collectType;
	}

	public String getCouponsId() {
		return couponsId;
	}

	public void setCouponsId(String couponsId) {
		this.couponsId = couponsId;
	}

	public String getCouponsCollectId() {
		return couponsCollectId;
	}

	public void setCouponsCollectId(String couponsCollectId) {
		this.couponsCollectId = couponsCollectId;
	}

	public Date getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
	}

	public String getCollectUserId() {
		return collectUserId;
	}

	public void setCollectUserId(String collectUserId) {
		this.collectUserId = collectUserId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	
	public String getCollectStartTime() {
		return collectStartTime;
	}

	
	public void setCollectStartTime(String collectStartTime) {
		this.collectStartTime = collectStartTime;
	}

	
	public String getCollectEndTime() {
		return collectEndTime;
	}

	
	public void setCollectEndTime(String collectEndTime) {
		this.collectEndTime = collectEndTime;
	}
	
}
