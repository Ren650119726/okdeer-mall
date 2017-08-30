
package com.okdeer.mall.activity.coupons.bo;

public class ActivityCollectCouponsRecordParamBo {

	/**
	 * 代金劵活动id
	 */
	private String couponsCollectId;

	/**
	 * 手机号码
	 */
	private String phoneNo;

	/**
	 * 领取结束时间
	 */
	private String collectStartTime;

	/**
	 * 领取结束时间
	 */
	private String collectEndTime;

	public String getCouponsCollectId() {
		return couponsCollectId;
	}

	public void setCouponsCollectId(String couponsCollectId) {
		this.couponsCollectId = couponsCollectId;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
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
