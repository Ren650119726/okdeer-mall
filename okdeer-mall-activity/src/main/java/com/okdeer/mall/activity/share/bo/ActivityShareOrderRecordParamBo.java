
package com.okdeer.mall.activity.share.bo;

import java.io.Serializable;

public class ActivityShareOrderRecordParamBo implements Serializable {

	/**
	 * 订单id
	 */
	private String orderId;

	/**
	 * 分享id
	 */
	private String shareId;

	/**
	 * 0:订单 1:退款单
	 */
	private Integer type;

	/**
	 * 是否排序,默认排序,排序字段为 create_time
	 */
	private boolean isSort = true;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	
	public boolean isSort() {
		return isSort;
	}

	
	public void setSort(boolean isSort) {
		this.isSort = isSort;
	}

}
