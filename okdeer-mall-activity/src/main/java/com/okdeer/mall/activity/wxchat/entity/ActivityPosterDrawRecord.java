/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityPosterDrawRecord.java
 * @Date 2017-08-07 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.activity.wxchat.entity;

import java.util.Date;

/**
 * 海报活动抽奖记录
 * 
 * @author null
 * @version 1.0 2017-08-07
 */
public class ActivityPosterDrawRecord {

	/**
	 * 主键活动id
	 */
	private String id;

	private String activityId;

	/**
	 * 用户id
	 */
	private String openid;

	/**
	 * 昵称
	 */
	private String nickName;

	/**
	 * 奖品id
	 */
	private String prizeId;

	/**
	 * 奖品名称
	 */
	private String prizeName;

	/**
	 * 代金卷活动id
	 */
	private String activityCollectId;

	/**
	 * 是否领取(0:未领取 1:已经领取)
	 */
	private Integer isTake;

	/**
	 * 抽奖时间
	 */
	private Date drawTime;

	/**
	 * 领取时间
	 */
	private Date takeTime;

	/**
	 * 领取手机号码
	 */
	private String takeMobile;

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

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getPrizeId() {
		return prizeId;
	}

	public void setPrizeId(String prizeId) {
		this.prizeId = prizeId;
	}

	public String getPrizeName() {
		return prizeName;
	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

	public String getActivityCollectId() {
		return activityCollectId;
	}

	public void setActivityCollectId(String activityCollectId) {
		this.activityCollectId = activityCollectId;
	}

	public Integer getIsTake() {
		return isTake;
	}

	public void setIsTake(Integer isTake) {
		this.isTake = isTake;
	}

	public Date getDrawTime() {
		return drawTime;
	}

	public void setDrawTime(Date drawTime) {
		this.drawTime = drawTime;
	}

	public Date getTakeTime() {
		return takeTime;
	}

	public void setTakeTime(Date takeTime) {
		this.takeTime = takeTime;
	}

	public String getTakeMobile() {
		return takeMobile;
	}

	public void setTakeMobile(String takeMobile) {
		this.takeMobile = takeMobile;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

}