package com.okdeer.mall.order.bo;

import java.util.Date;

import com.okdeer.mall.order.enums.GroupOrderStatusEnum;

/**
 * ClassName: TradeOrderGroupParamBo 
 * @Description: 团购订单查询参数
 * @author maojj
 * @date 2017年10月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年10月12日				maojj
 */
public class TradeOrderGroupParamBo {

	/**
	 * 团购活动id
	 */
	private String activityId;

	/**
	 * 团购商品id
	 */
	private String storeSkuId;

	/**
	 * 成团时间开始区间
	 */
	private Date startTime;

	/**
	 * 成团时间结束区间
	 */
	private Date endTime;

	/**
	 * 成团状态
	 */
	private GroupOrderStatusEnum status;

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getStoreSkuId() {
		return storeSkuId;
	}

	public void setStoreSkuId(String storeSkuId) {
		this.storeSkuId = storeSkuId;
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

	public GroupOrderStatusEnum getStatus() {
		return status;
	}

	public void setStatus(GroupOrderStatusEnum status) {
		this.status = status;
	}

}
