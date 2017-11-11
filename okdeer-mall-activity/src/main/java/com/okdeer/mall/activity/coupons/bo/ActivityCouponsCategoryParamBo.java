
package com.okdeer.mall.activity.coupons.bo;

import java.io.Serializable;
import java.util.List;

public class ActivityCouponsCategoryParamBo implements Serializable {

	private String couponId;

	private List<String> couponIdList;

	private String categoryId;

	private Integer type;

	public String getCouponId() {
		return couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}

	public List<String> getCouponIdList() {
		return couponIdList;
	}

	public void setCouponIdList(List<String> couponIdList) {
		this.couponIdList = couponIdList;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
