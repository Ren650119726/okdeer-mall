
package com.okdeer.mall.activity.wxchat.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class SubscribeEventWechatEventMsg extends WechatEventMsg {

	@XStreamAlias("Ticket")
	private String ticket;

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

}
