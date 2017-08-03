
package com.okdeer.mall.activity.wxchat.message;

import java.util.Date;
import java.util.List;

import com.okdeer.mall.activity.wxchat.annotation.XStreamCDATA;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("xml")
public class ImageWechatMsg extends WechatMsg {

	@XStreamImplicit(keyFieldName="Image",itemFieldName="MediaId")
	@XStreamCDATA
	private List<String> mediaId;

	public ImageWechatMsg() {
		setMsgType("image");
		setCreateTime(Long.toString((new Date().getTime()) / 1000));

	}

	public List<String> getMediaId() {
		return mediaId;
	}

	public void setMediaId(List<String> mediaId) {
		this.mediaId = mediaId;
	}

}
