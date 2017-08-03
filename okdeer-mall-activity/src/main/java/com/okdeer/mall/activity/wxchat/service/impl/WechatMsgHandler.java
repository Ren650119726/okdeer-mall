
package com.okdeer.mall.activity.wxchat.service.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wxchat.service.WechatMsgHandlerService;

public abstract class WechatMsgHandler implements InitializingBean {

	@Autowired
	private WechatMsgHandlerService msgHandlerService;

	abstract String getMsgType();

	abstract Object process(Object obj) throws MallApiException;

	@Override
	public void afterPropertiesSet() throws Exception {
		msgHandlerService.addHandler(this);
	}

	abstract Class<?> getRequestClass();
}
