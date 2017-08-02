
package com.okdeer.mall.activity.wxchat.api.impl;

import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.wechat.dto.CheckWxchatServerParamDto;
import com.okdeer.mall.activity.wechat.service.WechatApi;
import com.okdeer.mall.activity.wxchat.config.WechatConfig;
import com.okdeer.mall.activity.wxchat.service.WechatService;

@Service(version = "1.0.0")
public class WechatApiImpl implements WechatApi {

	@Autowired
	private WechatConfig wechatConfig;
	
	@Autowired
	private WechatService wechatService;




	@Override
	public boolean checkWxchatServer(CheckWxchatServerParamDto checkWxchatServerParamDto) {
		String[] array = new String[] { wechatConfig.getToken(), checkWxchatServerParamDto.getTimestamp(), checkWxchatServerParamDto.getNonce()};
		StringBuilder sb = new StringBuilder();
		// 字符串排序
		Arrays.sort(array);
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
		}
		String str = sb.toString();
		String msgSignature = DigestUtils.sha1Hex(str);
		return msgSignature.equals(checkWxchatServerParamDto.getSignature());
	}

}
