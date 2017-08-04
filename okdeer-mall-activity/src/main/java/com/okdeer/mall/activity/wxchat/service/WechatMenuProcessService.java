
package com.okdeer.mall.activity.wxchat.service;

import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wxchat.message.WechatEventMsg;

public interface WechatMenuProcessService {

	Object process(WechatEventMsg wechatEventMsg) throws MallApiException;
}
