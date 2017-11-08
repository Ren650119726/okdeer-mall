
package com.okdeer.mall.operate.advert.bo;

public class ColumnAdvertShowRecordParamBo {

	/**
	* 广告id
	*/
	private String advertId;

	/**
	 * 设备号
	 */
	private String deviceNo;

	/**
	 * 记录开始时间
	 */
	private String startCreateTime;

	/**
	 * 记录结束时间
	 */
	private String endCreateTime;

	public String getAdvertId() {
		return advertId;
	}

	public void setAdvertId(String advertId) {
		this.advertId = advertId;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public String getStartCreateTime() {
		return startCreateTime;
	}

	public void setStartCreateTime(String startCreateTime) {
		this.startCreateTime = startCreateTime;
	}

	public String getEndCreateTime() {
		return endCreateTime;
	}

	public void setEndCreateTime(String endCreateTime) {
		this.endCreateTime = endCreateTime;
	}

}
