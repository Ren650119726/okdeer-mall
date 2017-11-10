package com.okdeer.mall.activity.coupons.bo;

import java.util.List;

import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;

/**
 * ClassName: ActivityCouponsRecordParamBo 
 * @Description: 代金券记录查询参数对象
 * @author maojj
 * @date 2017年11月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年11月7日				maojj
 */
public class ActivityCouponsRecordParamBo {

	/**
	 * 领取用户id
	 */
	private String collectUserId;

	private List<ActivityCouponsRecordStatusEnum> includeStatusList;

	public String getCollectUserId() {
		return collectUserId;
	}

	public void setCollectUserId(String collectUserId) {
		this.collectUserId = collectUserId;
	}

	public List<ActivityCouponsRecordStatusEnum> getIncludeStatusList() {
		return includeStatusList;
	}

	public void setIncludeStatusList(List<ActivityCouponsRecordStatusEnum> includeStatusList) {
		this.includeStatusList = includeStatusList;
	}

}
