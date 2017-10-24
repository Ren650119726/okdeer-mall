
package com.okdeer.mall.activity.share.bo;

import java.io.Serializable;

public class ActivityShareRecordNumParamBo implements Serializable {

	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 已经发货数量
	 */
	private Integer deliveryNum;

	/**
	 * 完成数量
	 */
	private Integer completeNum;

	/**
	 * 退款数量
	 */
	private Integer refundNum;

	/**
	 * 主键id
	 */
	private String id;

	public Integer getDeliveryNum() {
		return deliveryNum;
	}

	public void setDeliveryNum(Integer deliveryNum) {
		this.deliveryNum = deliveryNum;
	}

	public Integer getCompleteNum() {
		return completeNum;
	}

	public void setCompleteNum(Integer completeNum) {
		this.completeNum = completeNum;
	}

	public Integer getRefundNum() {
		return refundNum;
	}

	public void setRefundNum(Integer refundNum) {
		this.refundNum = refundNum;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
