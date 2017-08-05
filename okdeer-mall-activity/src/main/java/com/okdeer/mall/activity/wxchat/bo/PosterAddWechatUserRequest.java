
package com.okdeer.mall.activity.wxchat.bo;

import com.okdeer.mall.activity.wxchat.message.SubscribeEventWechatEventMsg;

public class PosterAddWechatUserRequest {

	private SubscribeEventWechatEventMsg subscribeEventWechatEventMsg;

	public PosterAddWechatUserRequest() {
	}

	public PosterAddWechatUserRequest(SubscribeEventWechatEventMsg subscribeEventWechatEventMsg) {
		this.subscribeEventWechatEventMsg = subscribeEventWechatEventMsg;
	}

	public SubscribeEventWechatEventMsg getSubscribeEventWechatEventMsg() {
		return subscribeEventWechatEventMsg;
	}

	public void setSubscribeEventWechatEventMsg(SubscribeEventWechatEventMsg subscribeEventWechatEventMsg) {
		this.subscribeEventWechatEventMsg = subscribeEventWechatEventMsg;
	}

}
