package com.okdeer.mall.activity.wxchat.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wechat.dto.WechatPassiveReplyParamDto;
import com.okdeer.mall.activity.wxchat.entity.WechatPassiveReply;
import com.okdeer.mall.activity.wxchat.enums.MsgTypeEnum;
import com.okdeer.mall.activity.wxchat.message.TextWechatMsg;
import com.okdeer.mall.activity.wxchat.message.WechatMsg;
import com.okdeer.mall.activity.wxchat.service.CustomerService;
import com.okdeer.mall.activity.wxchat.service.WechatPassiveReplyService;

@Service
public class WechatTextMsgHandler extends WechatMsgHandler {

	@Autowired
	private WechatPassiveReplyService wechatPassiveReplyService;

	@Autowired
	private CustomerService customerService;

	@Override
	String getMsgTypeEvent() {
		return MsgTypeEnum.TEXT.getName();
	}

	@Override
	Object process(Object obj) throws MallApiException {
		TextWechatMsg textWechatMsg = (TextWechatMsg) obj;
		System.out.println("接受到的内容是：" + textWechatMsg.getContent());

		WechatPassiveReplyParamDto wechatPassiveReplyParamDto = new WechatPassiveReplyParamDto();
		wechatPassiveReplyParamDto.setInputKeys(textWechatMsg.getContent());
		List<WechatPassiveReply> list = wechatPassiveReplyService.findList(wechatPassiveReplyParamDto);
		if (CollectionUtils.isNotEmpty(list)) {
			for (WechatPassiveReply wechatPassiveReply : list) {
				sendMsg(textWechatMsg, wechatPassiveReply);
			}
		}
		return null;
	}

	public void sendMsg(WechatMsg requestWechatMsg, WechatPassiveReply wechatPassiveReply) {
		WechatMsg mesage = null;
		switch (wechatPassiveReply.getRespMsgType()) {
		case TEXT:
			mesage = createTextMsg(wechatPassiveReply);
			break;

		default:
			break;
		}

		if (mesage != null) {
			mesage.setToUserName(requestWechatMsg.getFromUserName());
			customerService.sendMsg(mesage);
		}
	}


	private WechatMsg createTextMsg(WechatPassiveReply wechatPassiveReply) {
		TextWechatMsg textWechatMsg = new TextWechatMsg();
		textWechatMsg.setContent(wechatPassiveReply.getRespContent());
		return textWechatMsg;
	}

	@Override
	Class<?> getRequestClass() {
		return TextWechatMsg.class;
	}

}
