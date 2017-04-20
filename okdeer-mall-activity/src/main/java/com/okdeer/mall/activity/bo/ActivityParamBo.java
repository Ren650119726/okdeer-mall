package com.okdeer.mall.activity.bo;

import java.util.Date;
import java.util.List;

import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;

/**
 * ClassName: ActivityParamBo 
 * @Description: 活动参数对象
 * @author maojj
 * @date 2017年4月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.3 		2017年4月18日				maojj
 */
public class ActivityParamBo {

	/**
	 * 活动状态
	 */
	private ActivityDiscountStatus status;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 更新用户Id
	 */
	private String updateUserId;

	/**
	 * 活动Id列表
	 */
	private List<String> activityIds;

	public ActivityDiscountStatus getStatus() {
		return status;
	}

	public void setStatus(ActivityDiscountStatus status) {
		this.status = status;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}

	public List<String> getActivityIds() {
		return activityIds;
	}

	public void setActivityIds(List<String> activityIds) {
		this.activityIds = activityIds;
	}

}
