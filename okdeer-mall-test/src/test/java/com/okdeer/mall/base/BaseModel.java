package com.okdeer.mall.base;

public class BaseModel<T> {

	private T req;

	private int code;

	public T getReq() {
		return req;
	}

	public void setReq(T req) {
		this.req = req;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
