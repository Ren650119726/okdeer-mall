
package com.okdeer.mall.activity.wxchat.service;

import com.okdeer.mall.activity.wxchat.message.WechatEventMsg;

public interface WechatMenuProcessService {

	Object process(WechatEventMsg wechatEventMsg);
}
