package com.okdeer.mall.member.bo;

import java.util.List;
import java.util.Map;

import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.enums.ActivityBusinessType;

/**
 * ClassName: AddressFilterCondition 
 * @Description: 地址过滤条件
 * @author maojj
 * @date 2017年10月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年10月11日				maojj
 */
public class UserAddressFilterCondition {

	/**
	 * 店铺Id。店铺有服务范围，通过店铺id查找店铺的服务范围过滤地址信息
	 */
	private String storeId;

	/**
	 * 店铺信息
	 */
	private StoreInfo storeInfo;

	/**
	 * 活动Id。活动有范围限制。根据活动id查找活动的范围限制过滤地址信息
	 */
	private String activityId;

	/**
	 * 活动信息
	 */
	private ActivityDiscount activityInfo;

	private Map<ActivityBusinessType, List<String>> areaLimitCondition;

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

	public StoreInfo getStoreInfo() {
		return storeInfo;
	}

	public void setStoreInfo(StoreInfo storeInfo) {
		this.storeInfo = storeInfo;
	}

	public ActivityDiscount getActivityInfo() {
		return activityInfo;
	}

	public void setActivityInfo(ActivityDiscount activityInfo) {
		this.activityInfo = activityInfo;
	}

	public Map<ActivityBusinessType, List<String>> getAreaLimitCondition() {
		return areaLimitCondition;
	}

	public void setAreaLimitCondition(Map<ActivityBusinessType, List<String>> areaLimitCondition) {
		this.areaLimitCondition = areaLimitCondition;
	}

}
