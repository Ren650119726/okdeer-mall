
package com.okdeer.mall.activity.wxchat.message;

import com.okdeer.mall.activity.wxchat.annotation.XStreamCDATA;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class WechatEventMsg extends WechatMsg {

	/**
	 * 事件类型，CLICK、VIEW Event
	 */
	@XStreamAlias("Event")
	@XStreamCDATA
	private String event;

	/**
	 * 事件KEY值，与自定义菜单接口中KEY值对应 EventKey
	 */
	@XStreamAlias("EventKey")
	@XStreamCDATA
	private String eventKey;

	@XStreamAlias("Ticket")
	private String ticket;

	@XStreamAlias("Latitude")
	private String latitude;

	@XStreamAlias("Longitude")
	private String longitude;

	@XStreamAlias("Precision")
	private String precision;

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

}
