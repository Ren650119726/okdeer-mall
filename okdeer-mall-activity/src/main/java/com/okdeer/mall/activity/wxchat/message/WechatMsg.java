
package com.okdeer.mall.activity.wxchat.message;

import java.util.Date;

import com.okdeer.mall.activity.wxchat.annotation.XStreamCDATA;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class WechatMsg {

	/**
	 * 开发者微信号 ToUserName
	 */
	@XStreamAlias("ToUserName")
	@XStreamCDATA
	private String toUserName;

	/**
	 * 发送方帐号（一个OpenID） 
	 */
	@XStreamAlias("FromUserName")
	@XStreamCDATA
	private String fromUserName;

	/**
	 * 消息创建时间 （整型） CreateTime
	 */
	@XStreamAlias("CreateTime")
	@XStreamCDATA
	private String createTime;

	/**
	 * 消息类型，event  MsgType
	 */
	@XStreamAlias("MsgType")
	@XStreamCDATA
	private String msgType;

	public WechatMsg(String msgType) {
		this.msgType = msgType;
		this.createTime = Long.toString((new Date().getTime()) / 1000);
	}

	public WechatMsg() {

	}

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
