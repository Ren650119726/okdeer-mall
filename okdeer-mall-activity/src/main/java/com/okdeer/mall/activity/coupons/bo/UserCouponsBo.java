package com.okdeer.mall.activity.coupons.bo;

import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;

/**
 * ClassName: UserCouponsBo 
 * @Description: 用户代金券信息
 * @author maojj
 * @date 2017年11月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年11月7日				maojj
 */
public class UserCouponsBo {

	/**
	 * 用户代金券领取记录
	 */
	private ActivityCouponsRecord collectRecord;

	/**
	 * 代金券活动信息
	 */
	private ActivityCollectCoupons couponsActInfo;

	/**
	 * 代金券信息
	 */
	private ActivityCoupons couponsInfo;

	public ActivityCouponsRecord getCollectRecord() {
		return collectRecord;
	}

	public void setCollectRecord(ActivityCouponsRecord collectRecord) {
		this.collectRecord = collectRecord;
	}

	public ActivityCoupons getCouponsInfo() {
		return couponsInfo;
	}

	public void setCouponsInfo(ActivityCoupons couponsInfo) {
		this.couponsInfo = couponsInfo;
	}

	public ActivityCollectCoupons getCouponsActInfo() {
		return couponsActInfo;
	}

	public void setCouponsActInfo(ActivityCollectCoupons couponsActInfo) {
		this.couponsActInfo = couponsActInfo;
	}

}
