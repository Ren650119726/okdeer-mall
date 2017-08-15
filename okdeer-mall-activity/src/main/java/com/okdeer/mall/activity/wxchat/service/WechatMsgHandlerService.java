
package com.okdeer.mall.activity.wxchat.service;

import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wxchat.service.impl.WechatMsgHandler;

public interface WechatMsgHandlerService {

	void addHandler(WechatMsgHandler wechatMsgHandler) throws MallApiException;
}
