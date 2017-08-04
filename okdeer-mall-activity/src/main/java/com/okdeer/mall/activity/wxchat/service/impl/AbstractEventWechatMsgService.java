
package com.okdeer.mall.activity.wxchat.service.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wxchat.config.WechatConfig;
import com.okdeer.mall.activity.wxchat.service.EventWechatAddService;

public abstract class AbstractEventWechatMsgService implements InitializingBean {

	@Autowired
	private EventWechatAddService eventWechatAddService;
	
	@Autowired
	protected WechatConfig wechatConfig;

	@Override
	public void afterPropertiesSet() throws Exception {
		eventWechatAddService.addEventWechatService(this.getEvent(), this);
	}

	abstract Object process(Object object) throws MallApiException;

	abstract String getEvent();
	
}
