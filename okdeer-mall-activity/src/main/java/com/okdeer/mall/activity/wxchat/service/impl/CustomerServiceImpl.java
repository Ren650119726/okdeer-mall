
package com.okdeer.mall.activity.wxchat.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.mall.activity.wxchat.message.ImageWechatMsg;
import com.okdeer.mall.activity.wxchat.message.TextWechatMsg;
import com.okdeer.mall.activity.wxchat.service.CustomerService;
import com.okdeer.mall.activity.wxchat.service.WechatService;
import com.okdeer.mall.activity.wxchat.util.WxchatUtils;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private WechatService wechatService;

	@Override
	public void sendMsg(Object obj) {
		Map<String, Object> entity = Maps.newHashMap();
		if (obj instanceof TextWechatMsg) {
			TextWechatMsg textWechatMsg = (TextWechatMsg) obj;
			// 文本消息
			Map<String, Object> textMap = Maps.newHashMap();
			textMap.put("content", textWechatMsg.getContent());
			putContent(entity, textWechatMsg.getToUserName(), WxchatUtils.RESP_MESSAGE_TYPE_TEXT, textMap);
		} else if (obj instanceof ImageWechatMsg) {
			ImageWechatMsg imageWechatMsg = (ImageWechatMsg) obj;
			Map<String, Object> imageMap = Maps.newHashMap();
			imageMap.put("media_id", imageWechatMsg.getWechatMedia().getMediaIdList().get(0));
			putContent(entity, imageWechatMsg.getToUserName(), WxchatUtils.RESP_MESSAGE_TYPE_IMAGE, imageMap);
		}

		String msginfo = JsonMapper.nonEmptyMapper().toJson(entity);
		boolean sendResult = false;
		for (int i = 0; i < 3; i++) {
			sendResult = wechatService.send(msginfo);
			if (sendResult) {
				break;
			}
		}
	}

	private void putContent(Map<String, Object> entity, String toUser, String msgtype, Map<String, Object> contentMap) {
		entity.put("touser", toUser);
		entity.put("msgtype", msgtype);
		entity.put(msgtype, contentMap);
	}

}
