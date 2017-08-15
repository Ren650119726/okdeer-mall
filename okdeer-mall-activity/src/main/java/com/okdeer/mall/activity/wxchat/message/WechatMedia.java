package com.okdeer.mall.activity.wxchat.message;

import java.util.List;

import com.okdeer.mall.activity.wxchat.annotation.XStreamCDATA;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class WechatMedia {

	@XStreamImplicit(itemFieldName="MediaId")
	@XStreamCDATA
	private List<String> mediaIdList;

	public List<String> getMediaIdList() {
		return mediaIdList;
	}

	public void setMediaIdList(List<String> mediaIdList) {
		this.mediaIdList = mediaIdList;
	}

}
