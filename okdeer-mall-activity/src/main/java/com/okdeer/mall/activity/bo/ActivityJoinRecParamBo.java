package com.okdeer.mall.activity.bo;

import java.util.Date;

public class ActivityJoinRecParamBo {

	/**
	 * 用户Id
	 */
	private String userId;

	/**
	 * 活动Id
	 */
	private String activityId;

	/**
	 * 活动项Id
	 */
	private String activityItemId;

	/**
	 * 设备Id
	 */
	private String deviceId;

	/**
	 * 活动参与日期
	 */
	private Date joinDate;
	
	public ActivityJoinRecParamBo(){}
	
	public ActivityJoinRecParamBo(String userId, String activityId){
		this(userId,activityId,null);
	}
	
	public ActivityJoinRecParamBo(String userId,String activityId,String activityItemId){
		this(userId,activityId,activityItemId,null);
	}
	
	public ActivityJoinRecParamBo(String userId,String activityId,String activityItemId,Date joinDate){
		this(userId,activityId,activityItemId,null,joinDate);
	}

	public ActivityJoinRecParamBo(String userId, String activityId, String activityItemId, String deviceId,
			Date joinDate) {
		super();
		this.userId = userId;
		this.activityId = activityId;
		this.activityItemId = activityItemId;
		this.deviceId = deviceId;
		this.joinDate = joinDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityItemId() {
		return activityItemId;
	}

	public void setActivityItemId(String activityItemId) {
		this.activityItemId = activityItemId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

}
