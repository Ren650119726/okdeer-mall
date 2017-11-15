
package com.okdeer.mall.activity.coupons.bo;

import java.io.Serializable;
import java.util.List;

public class CouponsRelationStoreParamBo implements Serializable {

	/**
	 * 关联的店铺id
	 */
	private String storeId;

	/**
	 * 代金卷id
	 */
	private String couponsId;

	/**
	 * 优惠卷列表
	 */
	private List<String> couponsIdList;

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getCouponsId() {
		return couponsId;
	}

	public void setCouponsId(String couponsId) {
		this.couponsId = couponsId;
	}

	public List<String> getCouponsIdList() {
		return couponsIdList;
	}

	public void setCouponsIdList(List<String> couponsIdList) {
		this.couponsIdList = couponsIdList;
	}

}
