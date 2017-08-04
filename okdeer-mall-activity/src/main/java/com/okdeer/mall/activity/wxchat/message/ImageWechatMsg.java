
package com.okdeer.mall.activity.wxchat.message;

import com.okdeer.mall.activity.wxchat.annotation.XStreamCDATA;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class ImageWechatMsg extends WechatMsg {

	@XStreamAlias("Image")
	@XStreamCDATA
	private WechatMedia wechatMedia;

	public ImageWechatMsg() {
		super("image");

	}

	public WechatMedia getWechatMedia() {
		return wechatMedia;
	}

	public void setWechatMedia(WechatMedia wechatMedia) {
		this.wechatMedia = wechatMedia;
	}

}
