
package com.okdeer.mall.activity.coupons.bo;

import java.io.Serializable;
import java.util.List;

public class ActivityCollectStoreParamBo implements Serializable {

	private String collectCouponsId;

	private List<String> collectCouponsIdList;

	/**
	 * 店铺id
	 */
	private String storeId;

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

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

}
