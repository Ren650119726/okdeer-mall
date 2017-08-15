
package com.okdeer.mall.activity.wxchat.service;

import com.okdeer.mall.activity.wxchat.service.impl.AbstractEventWechatMsgService;

public interface EventWechatAddService {
	
	void addEventWechatService(String event, AbstractEventWechatMsgService eventWechatMsgService);

}
