
package com.okdeer.mall.activity.wxchat.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wxchat.enums.MsgTypeEnum;
import com.okdeer.mall.activity.wxchat.message.WechatEventMsg;
import com.okdeer.mall.activity.wxchat.service.EventWechatAddService;

@Service
public class EventWechatMsgHandler extends WechatMsgHandler implements EventWechatAddService {

	private Map<String, AbstractEventWechatMsgService> eventServiceMap = Maps.newHashMap();

	@Override
	String getMsgType() {
		return MsgTypeEnum.EVENT.getName();
	}

	@Override
	Object process(Object obj) throws MallApiException {
		WechatEventMsg wechatEventMsg = (WechatEventMsg) obj;
		if (eventServiceMap.get(wechatEventMsg.getEvent()) == null) {
			return null;
		}
		return eventServiceMap.get(wechatEventMsg.getEvent()).process(obj);
	}

	@Override
	public void addEventWechatService(String event, AbstractEventWechatMsgService eventWechatMsgService) {
		if (eventServiceMap.get(event) != null) {
			throw new RuntimeException(event + "serice已经存在," + eventServiceMap.get(event).getClass().getName());
		}
		eventServiceMap.put(event, eventWechatMsgService);
	}
	
	@Override
	Class<?> getRequestClass() {
		return WechatEventMsg.class;
	}

}
