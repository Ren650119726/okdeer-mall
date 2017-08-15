
package com.okdeer.mall.activity.wxchat.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wxchat.message.WechatEventMsg;
import com.okdeer.mall.activity.wxchat.service.ClickEventWechatMsgService;
import com.okdeer.mall.activity.wxchat.service.WechatMenuProcessService;
import com.okdeer.mall.activity.wxchat.util.WxchatUtils;

@Service
public class ClickEventWechatMsgServiceImpl extends AbstractEventWechatMsgService
		implements ClickEventWechatMsgService {

	private Map<String, WechatMenuProcessService> wechatMenuProcessServiceMap = Maps.newHashMap();

	@Override
	Object process(Object obj) throws MallApiException {
		WechatEventMsg wechatEventMsg = (WechatEventMsg) obj;
		if (wechatMenuProcessServiceMap.get(wechatEventMsg.getEventKey()) != null) {
			Object wechatMsg = wechatMenuProcessServiceMap.get(wechatEventMsg.getEventKey()).process(wechatEventMsg);
			return wechatMsg;
		}
		return null;
	}

	@Override
	String getEvent() {

		return WxchatUtils.EVENT_TYPE_CLICK;
	}

	@Override
	public void addMenuProcessService(String eventKey, WechatMenuProcessService wechatMenuProcessService) {
		if (wechatMenuProcessServiceMap.get(eventKey) != null) {
			throw new RuntimeException(eventKey + "处理类已经存在," + wechatMenuProcessService.getClass().getName());
		}
		wechatMenuProcessServiceMap.put(eventKey, wechatMenuProcessService);
	}

	@Override
	Class<?> getRequestClass() {
		return WechatEventMsg.class;
	}

}
