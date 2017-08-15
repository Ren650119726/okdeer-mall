
package com.okdeer.mall.activity.wxchat.bo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateQrCodeResult extends WechatBaseResult {

	/**
	 * 获取的二维码ticket，凭借此ticket可以在有效时间内换取二维码。
	 */
	private String ticket;

	/**
	 * 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天）
	 */
	@JsonProperty("expire_seconds")
	private Integer expireSeconds;

	/**
	 * 二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片
	 */
	private String url;

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public Integer getExpireSeconds() {
		return expireSeconds;
	}

	public void setExpireSeconds(Integer expireSeconds) {
		this.expireSeconds = expireSeconds;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
