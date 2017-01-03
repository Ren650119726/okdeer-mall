
package com.okdeer.mall.member.bo;

/**
 * ClassName: SignResult 
 * @Description: 签到结果
 * @author zengjizu
 * @date 2016年12月31日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class SignResult {

	/**
	 * 0:签到成功 非0代表签到失败
	 */
	private int status;

	/**
	 * 状态描述
	 */
	private String msg;

	/**
	 * 签到获得积分值,如果是null的话，说明积分获取已经超过规则限制了
	 */
	private Integer pointVal;

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

	public Integer getPointVal() {
		return pointVal;
	}

	public void setPointVal(Integer pointVal) {
		this.pointVal = pointVal;
	}

}
