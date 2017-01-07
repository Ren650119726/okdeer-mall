
package com.okdeer.mall.points.bo;

public class PointQueryResult {

	/**
	 * 用户剩余积分
	 */
	private Integer userPointVal;

	/**
	 * 根据业务id查询到当次所加（扣）积分值 ，正数为加的积分，负数为扣的积分
	 */
	private Integer pointVal;

	public Integer getUserPointVal() {
		return userPointVal;
	}

	public void setUserPointVal(Integer userPointVal) {
		this.userPointVal = userPointVal;
	}

	public Integer getPointVal() {
		return pointVal;
	}

	public void setPointVal(Integer pointVal) {
		this.pointVal = pointVal;
	}

}
