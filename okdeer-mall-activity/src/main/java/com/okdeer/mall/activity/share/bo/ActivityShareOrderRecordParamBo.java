
package com.okdeer.mall.activity.share.bo;

import java.io.Serializable;
import java.util.List;

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

	/**
	 * 状态 0:创建订单 1:已经发货 2 已经完成
	 */
	private Integer status;

	/**
	 * 订单状态
	 */
	private List<Integer> statusList;

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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<Integer> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Integer> statusList) {
		this.statusList = statusList;
	}

}
