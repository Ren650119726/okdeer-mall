package com.okdeer.mall.activity.wxchat.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.wxchat.service.ClickEventWechatMsgService;
import com.okdeer.mall.activity.wxchat.service.WechatMenuProcessService;

@Service
public class WechatActivityInitConfig implements InitializingBean{

	private static final String POSTER_ACTIVITY_MENU_EVENT_KEY = "generatePosterImage"; 
	
	@Autowired
	private ClickEventWechatMsgService clickEventWechatMsgService;
	
	@Resource(name = "posterActivityService")
	private WechatMenuProcessService posterActivityService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		clickEventWechatMsgService.addMenuProcessService(POSTER_ACTIVITY_MENU_EVENT_KEY, posterActivityService);
	}

}
