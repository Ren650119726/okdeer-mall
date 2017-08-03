
package com.okdeer.mall.activity.wxchat.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.okdeer.mall.activity.wxchat.bo.WechatUserInfo;
import com.okdeer.mall.activity.wxchat.config.WechatConfig;
import com.okdeer.mall.activity.wxchat.entity.WechatUser;
import com.okdeer.mall.activity.wxchat.message.ImageWechatMsg;
import com.okdeer.mall.activity.wxchat.message.WechatEventMsg;
import com.okdeer.mall.activity.wxchat.message.WechatMedia;
import com.okdeer.mall.activity.wxchat.message.WechatMsg;
import com.okdeer.mall.activity.wxchat.service.PosterActivityService;
import com.okdeer.mall.activity.wxchat.service.WechatMenuProcessService;
import com.okdeer.mall.activity.wxchat.service.WechatService;
import com.okdeer.mall.activity.wxchat.service.WechatUserService;

@Service("posterActivityService")
public class PosterActivityServiceImpl implements WechatMenuProcessService, PosterActivityService {

	private static final Logger logger = LoggerFactory.getLogger(PosterActivityServiceImpl.class);

	@Autowired
	private WechatUserService wechatUserService;

	@Autowired
	private WechatConfig wechatConfig;

	@Autowired
	private WechatService wechatService;

	@Override
	public Object process(WechatEventMsg wechatEventMsg) {
		// 获取微信用户最新信息
		try {
			WechatUserInfo wechatUserInfo = wechatService.getUserInfo(wechatEventMsg.getFromUserName());
			System.out.println(wechatUserInfo.getNickName());
			String mediaId = "vnNkLA3NmeiM8Shwn0Nh0P1Z5zRhndx9bxY_i1RriB0";
			// 生成海报
			return createImageWechatMsg(wechatEventMsg.getFromUserName(),mediaId);
		} catch (Exception e) {
			logger.error("获取微信用户信息出错", e);
		}
		return null;
	}

	private ImageWechatMsg createImageWechatMsg(String openid,String mediaId) {
		ImageWechatMsg responseWechatMsg = new ImageWechatMsg();
		responseWechatMsg.setFromUserName(wechatConfig.getAccount());
		responseWechatMsg.setToUserName(openid);
		WechatMedia wechatMedia = new WechatMedia();
		List<String> mediaIdList = Lists.newArrayList();
		mediaIdList.add(mediaId);
		wechatMedia.setMediaIdList(mediaIdList);
		responseWechatMsg.setWechatMedia(wechatMedia);
		// 生成海报
		return responseWechatMsg;
	}

}
