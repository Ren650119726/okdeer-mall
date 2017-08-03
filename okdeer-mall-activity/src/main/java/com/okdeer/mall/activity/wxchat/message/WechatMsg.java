
package com.okdeer.mall.activity.wxchat.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class WechatMsg {

	/**
	 * 开发者微信号 ToUserName
	 */
	@XStreamAlias("ToUserName")
	private String toUserName;

	/**
	 * 发送方帐号（一个OpenID） 
	 */
	@XStreamAlias("FromUserName")
	private String fromUserName;

	/**
	 * 消息创建时间 （整型） CreateTime
	 */
	@XStreamAlias("createTime")
	private String createTime;

	/**
	 * 消息类型，event  MsgType
	 */
	@XStreamAlias("msgType")
	private String msgType;

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

}
