/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityPosterConfig.java
 * @Date 2017-08-05 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */

package com.okdeer.mall.activity.wxchat.entity;

/**
 * 海报活动配置
 * 
 * @author null
 * @version 1.0 2017-08-05
 */
public class ActivityPosterConfig {

	private String id;

	/**
	 * 生成海报提示
	 */
	private String createPosterTip;

	/**
	 * 抽奖次数上限
	 */
	private Integer drawCountLimit;

	/**
	 * 好友达到数量赠送抽奖次数周期
	 */
	private Integer friendReachCountPer;

	/**
	 * 好友关注公众号提示
	 */
	private String friendSubscribeTip;

	/**
	 * 关注公众号提示
	 */
	private String subscribeWechatTip;

	/**
	 * 获得资格提示
	 */
	private String getQualificaTip;

	/**
	 * 海报图片，以,分隔
	 */
	private String posterImg;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatePosterTip() {
		return createPosterTip;
	}

	public void setCreatePosterTip(String createPosterTip) {
		this.createPosterTip = createPosterTip;
	}

	public Integer getDrawCountLimit() {
		return drawCountLimit;
	}

	public void setDrawCountLimit(Integer drawCountLimit) {
		this.drawCountLimit = drawCountLimit;
	}

	public Integer getFriendReachCountPer() {
		return friendReachCountPer;
	}

	public void setFriendReachCountPer(Integer friendReachCountPer) {
		this.friendReachCountPer = friendReachCountPer;
	}

	public String getFriendSubscribeTip() {
		return friendSubscribeTip;
	}

	public void setFriendSubscribeTip(String friendSubscribeTip) {
		this.friendSubscribeTip = friendSubscribeTip;
	}

	public String getSubscribeWechatTip() {
		return subscribeWechatTip;
	}

	public void setSubscribeWechatTip(String subscribeWechatTip) {
		this.subscribeWechatTip = subscribeWechatTip;
	}

	public String getGetQualificaTip() {
		return getQualificaTip;
	}

	public void setGetQualificaTip(String getQualificaTip) {
		this.getQualificaTip = getQualificaTip;
	}

	public String getPosterImg() {
		return posterImg;
	}

	public void setPosterImg(String posterImg) {
		this.posterImg = posterImg;
	}

}