
package com.okdeer.mall.activity.coupons.bo;

import com.okdeer.mall.activity.coupons.enums.ActivityCouponsType;

public class ActivityCouponsOrderRecordParamBo {

	/**
	* 代金卷活动类型：0代金券领取活动，1注册活动，2开门成功送代金券活动3 邀请注册送代金券活动4 消费返券活动
	*/
	private ActivityCouponsType collectType;

	/**
	 * 代金券活动ID
	 */
	private String couponsCollectId;

	/**
	 * 领取代金券开始时间
	 */
	private String collectStartTime;

	/**
	 * 领取代金券结束时间
	 */
	private String collectEndTime;

	/**
	 * 领取人ID
	 */
	private String collectUserId;

	/**
	 *  订单id
	 */
	private String orderId;

	public ActivityCouponsType getCollectType() {
		return collectType;
	}

	public void setCollectType(ActivityCouponsType collectType) {
		this.collectType = collectType;
	}

	public String getCouponsCollectId() {
		return couponsCollectId;
	}

	public void setCouponsCollectId(String couponsCollectId) {
		this.couponsCollectId = couponsCollectId;
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

	public String getCollectUserId() {
		return collectUserId;
	}

	public void setCollectUserId(String collectUserId) {
		this.collectUserId = collectUserId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}
