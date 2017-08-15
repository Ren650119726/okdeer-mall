
package com.okdeer.mall.activity.wxchat.bo;

import java.util.concurrent.CountDownLatch;

public class WechatMsgRequest {

	/**
	 * 请求xml
	 */
	private String requestXml;

	/**
	 * 返回结果
	 */
	private Object result;

	/**
	 * 是否处理超时
	 */
	private boolean isTimeOut = false;

	private CountDownLatch countDownLatch = new CountDownLatch(1);

	public WechatMsgRequest() {

	}

	public WechatMsgRequest(String requestXml) {
		this.requestXml = requestXml;
	}

	public String getRequestXml() {
		return requestXml;
	}

	public void setRequestXml(String requestXml) {
		this.requestXml = requestXml;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public boolean isTimeOut() {
		return isTimeOut;
	}

	public void setTimeOut(boolean isTimeOut) {
		this.isTimeOut = isTimeOut;
	}

	public CountDownLatch getCountDownLatch() {
		return countDownLatch;
	}

	public void setCountDownLatch(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}

}
