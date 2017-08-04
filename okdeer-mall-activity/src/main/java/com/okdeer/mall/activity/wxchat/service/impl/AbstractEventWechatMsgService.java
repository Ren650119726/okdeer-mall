
package com.okdeer.mall.activity.wxchat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.activity.wxchat.config.WechatConfig;
import com.okdeer.mall.activity.wxchat.enums.MsgTypeEnum;

public abstract class AbstractEventWechatMsgService extends WechatMsgHandler {

	@Autowired
	protected WechatConfig wechatConfig;


	abstract String getEvent();

	@Override
	String getMsgTypeEvent() {

		return MsgTypeEnum.EVENT + getEvent();
	}

}
