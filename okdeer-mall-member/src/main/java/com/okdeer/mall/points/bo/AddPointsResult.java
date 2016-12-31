
package com.okdeer.mall.points.bo;

/**
 * ClassName: AddPointsResult 
 * @Description: 添加积分结果
 * @author zengjizu
 * @date 2016年12月30日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class AddPointsResult {

	/**
	 * 状态
	 */
	private int status;

	/**
	 * 状态描述
	 */
	private String msg;

	/**
	 * 领取积分
	 */
	private int pointVal;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getPointVal() {
		return pointVal;
	}

	public void setPointVal(int pointVal) {
		this.pointVal = pointVal;
	}

}
