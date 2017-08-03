
package com.okdeer.mall.activity.wxchat.message;

public class WechatEventMsg extends WechatMsg{
	
	/**
	 * 事件类型，CLICK、VIEW  Event
	 */
	private String event;

	/**
	 * 事件KEY值，与自定义菜单接口中KEY值对应 EventKey
	 */
	private String eventKey;

	

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

}
