
package com.okdeer.mall.activity.wxchat.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.wxchat.config.WechatConfig;
import com.okdeer.mall.activity.wxchat.entity.WechatUser;
import com.okdeer.mall.activity.wxchat.message.ImageWechatMsg;
import com.okdeer.mall.activity.wxchat.message.WechatEventMsg;
import com.okdeer.mall.activity.wxchat.message.WechatMsg;
import com.okdeer.mall.activity.wxchat.service.PosterActivityService;
import com.okdeer.mall.activity.wxchat.service.WechatMenuProcessService;
import com.okdeer.mall.activity.wxchat.service.WechatUserService;

@Service("posterActivityService")
public class PosterActivityServiceImpl implements WechatMenuProcessService, PosterActivityService {

	@Autowired
	private WechatUserService wechatUserService;
	
	@Autowired
	private WechatConfig wechatConfig;

	@Override
	public Object process(WechatEventMsg wechatEventMsg) {
		//查询发送信息的微信用户
		WechatUser wechatUser = wechatUserService.findByOpenid(wechatEventMsg.getFromUserName());
		ImageWechatMsg responseWechatMsg = new ImageWechatMsg();
		responseWechatMsg.setFromUserName(wechatConfig.getAccount());
		responseWechatMsg.setToUserName(wechatEventMsg.getFromUserName());
		responseWechatMsg.setMediaId("vnNkLA3NmeiM8Shwn0Nh0P1Z5zRhndx9bxY_i1RriB0");
		// 生成海报
		return responseWechatMsg;
	}


}
