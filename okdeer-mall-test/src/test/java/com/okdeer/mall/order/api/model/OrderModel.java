package com.okdeer.mall.order.api.model;

import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;

public class OrderModel {

	private Request<PlaceOrderParamDto> confirmReq;

	private Request<PlaceOrderParamDto> submitReq;

	private int confirmExpiredCode;

	private int submitExpiredCode;

	public Request<PlaceOrderParamDto> getConfirmReq() {
		return confirmReq;
	}

	public void setConfirmReq(Request<PlaceOrderParamDto> confirmReq) {
		this.confirmReq = confirmReq;
	}

	public Request<PlaceOrderParamDto> getSubmitReq() {
		return submitReq;
	}

	public void setSubmitReq(Request<PlaceOrderParamDto> submitReq) {
		this.submitReq = submitReq;
	}

	public int getConfirmExpiredCode() {
		return confirmExpiredCode;
	}

	public void setConfirmExpiredCode(int confirmExpiredCode) {
		this.confirmExpiredCode = confirmExpiredCode;
	}

	public int getSubmitExpiredCode() {
		return submitExpiredCode;
	}

	public void setSubmitExpiredCode(int submitExpiredCode) {
		this.submitExpiredCode = submitExpiredCode;
	}

}
