
package com.okdeer.mall.order.bo;

import java.math.BigDecimal;

public class FmsStatisOrderCannelRefundBo {

	/**
	 * 退款数量
	 */
	private int refundCount;

	/**
	 * 退款金额
	 */
	private BigDecimal refundAmount;

	public int getRefundCount() {
		return refundCount;
	}

	public void setRefundCount(int refundCount) {
		this.refundCount = refundCount;
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

}
