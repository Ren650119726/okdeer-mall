
package com.okdeer.mall.activity.wxchat.bo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JsApiTicketResult extends WechatBaseResult {

	private String ticket;

	@JsonProperty("expires_in")
	private Integer expiresIn;

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public Integer getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
	}

}
