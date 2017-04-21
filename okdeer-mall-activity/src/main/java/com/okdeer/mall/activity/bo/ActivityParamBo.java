package com.okdeer.mall.activity.bo;

import java.util.Date;
import java.util.List;

import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountType;

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
	 * 活动类型
	 */
	private ActivityDiscountType type;

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

	/**
	 * 排除的活动ID
	 */
	private String excludedId;

	/**
	 * 限制范围ID列表
	 */
	private List<String> limitRangeIds;

	/**
	 * 活动开始时间
	 */
	private Date startTime;

	/**
	 * 活动结束时间
	 */
	private Date endTime;

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

	public String getExcludedId() {
		return excludedId;
	}

	public void setExcludedId(String excludedId) {
		this.excludedId = excludedId;
	}

	public List<String> getLimitRangeIds() {
		return limitRangeIds;
	}

	public void setLimitRangeIds(List<String> limitRangeIds) {
		this.limitRangeIds = limitRangeIds;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public ActivityDiscountType getType() {
		return type;
	}

	public void setType(ActivityDiscountType type) {
		this.type = type;
	}

}
