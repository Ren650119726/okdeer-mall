package com.okdeer.mall.activity.coupons.service.receive.bo;

import java.io.Serializable;

import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;

/**
 * ClassName: CouponsReceiveBo 
 * @Description: 领取代金券实体类
 * @author tuzhd
 * @date 2017年11月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class CouponsReceiveBo implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 活动id
	 */
	private String collId;
	/**
	 * 用户id
	 */
	private String userId;
	/**
	 * 邀请人信息id或邀请码
	 */
	private String invitaUserId;
	
	/**
	 * 是否检查是否领取过新人代金券
	 */
	private boolean limitOne;
	
	/**
	 * 随机码或优惠码
	 */
	private String randCode;
	
	/**
	 * 手机号码
	 */
	private String phone;
	
	/**
	 * 活动id
	 */
	private String activityId;
	/**
	 * 代金券领取
	 */
	private ActivityCollectCoupons coll;

	public String getCollId() {
		return collId;
	}

	public void setCollId(String collId) {
		this.collId = collId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInvitaUserId() {
		return invitaUserId;
	}

	public void setInvitaUserId(String invitaUserId) {
		this.invitaUserId = invitaUserId;
	}

	public boolean isLimitOne() {
		return limitOne;
	}

	public void setLimitOne(boolean limitOne) {
		this.limitOne = limitOne;
	}

	public ActivityCollectCoupons getColl() {
		return coll;
	}

	public void setColl(ActivityCollectCoupons coll) {
		this.coll = coll;
	}

	public String getRandCode() {
		return randCode;
	}
	
	

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setRandCode(String randCode) {
		this.randCode = randCode;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	
}
