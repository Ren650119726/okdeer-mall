
package com.okdeer.mall.activity.wxchat.message;

import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class ImageWechatMsg extends WechatMsg {

	@XStreamAlias("MediaId")
	private String mediaId;

	public ImageWechatMsg() {
		setMsgType("image");
		setCreateTime(Long.toString((new Date().getTime())/1000));
		
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

}
