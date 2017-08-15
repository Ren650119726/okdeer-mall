
package com.okdeer.mall.activity.wxchat.bo;

public class DrawResult {

	/**
	 * 返回码
	 */
	private int code;

	/**
	 * 信息提示
	 */
	private String msg;

	/**
	 * 奖品名称
	 */
	private String prizeName;

	/**
	 * 奖品id
	 */
	private String prizeId;

	/**
	 * 领取记录id
	 */
	private String recordId;

	/**
	 * 是否已经领取 1:已经领取 0:未领取
	 */
	private Integer isTake;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getPrizeName() {
		return prizeName;
	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

	public String getPrizeId() {
		return prizeId;
	}

	public void setPrizeId(String prizeId) {
		this.prizeId = prizeId;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public Integer getIsTake() {
		return isTake;
	}

	public void setIsTake(Integer isTake) {
		this.isTake = isTake;
	}

}
