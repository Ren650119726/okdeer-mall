
package com.okdeer.mall.activity.coupons.bo;

import java.io.Serializable;
import java.util.List;

public class ActivityCollectAreaParamBo implements Serializable {

	/**
	 * 区域类型：0市，1省
	 */
	private Integer type;

	private String collectCouponsId;

	private List<String> collectCouponsIdList;

	private String areaId;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCollectCouponsId() {
		return collectCouponsId;
	}

	public void setCollectCouponsId(String collectCouponsId) {
		this.collectCouponsId = collectCouponsId;
	}

	public List<String> getCollectCouponsIdList() {
		return collectCouponsIdList;
	}

	public void setCollectCouponsIdList(List<String> collectCouponsIdList) {
		this.collectCouponsIdList = collectCouponsIdList;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

}
