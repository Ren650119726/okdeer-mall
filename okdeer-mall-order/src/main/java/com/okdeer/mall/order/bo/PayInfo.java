
package com.okdeer.mall.order.bo;

public class PayInfo {

	/**
	 * 支付数据字符串
	 */
	private String data;

	/**
	 * 订单id
	 */
	private String orderId;

	/**
	 * 支付方式
	 */
	private int paymentType;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public int getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}
}
