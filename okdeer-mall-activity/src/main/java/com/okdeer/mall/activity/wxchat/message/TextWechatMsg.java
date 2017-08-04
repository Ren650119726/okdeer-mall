
package com.okdeer.mall.activity.wxchat.message;

import com.okdeer.mall.activity.wxchat.annotation.XStreamCDATA;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class TextWechatMsg extends WechatMsg {

	@XStreamAlias("Content")
	@XStreamCDATA
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
