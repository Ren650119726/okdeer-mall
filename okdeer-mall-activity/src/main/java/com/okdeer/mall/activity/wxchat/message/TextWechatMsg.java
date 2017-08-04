
package com.okdeer.mall.activity.wxchat.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class TextWechatMsg extends WechatMsg {

	@XStreamAlias("Content")
	private String content;

	public TextWechatMsg() {
		super("text");
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
