
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
	private List<String> mediaIdList;

	public ImageWechatMsg() {
		setMsgType("image");
		setCreateTime(Long.toString((new Date().getTime()) / 1000));

	}

	public List<String> getMediaIdList() {
		return mediaIdList;
	}

	public void setMediaIdList(List<String> mediaIdList) {
		this.mediaIdList = mediaIdList;
	}


}
