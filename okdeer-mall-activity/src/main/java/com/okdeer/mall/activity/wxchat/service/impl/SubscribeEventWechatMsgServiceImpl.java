
package com.okdeer.mall.activity.wxchat.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wxchat.bo.PosterAddWechatUserRequest;
import com.okdeer.mall.activity.wxchat.bo.WechatUserInfo;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterShareInfo;
import com.okdeer.mall.activity.wxchat.message.SubscribeEventWechatEventMsg;
import com.okdeer.mall.activity.wxchat.message.TextWechatMsg;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterShareInfoService;
import com.okdeer.mall.activity.wxchat.service.PosterActivityService;
import com.okdeer.mall.activity.wxchat.service.WechatService;
import com.okdeer.mall.activity.wxchat.service.WechatUserService;
import com.okdeer.mall.activity.wxchat.util.WxchatUtils;

/**
 * ClassName: SubscribeEventWechatMsgServiceImpl 
 * @Description: 用户关注公众号事件
 * @author zengjizu
 * @date 2017年8月4日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class SubscribeEventWechatMsgServiceImpl extends AbstractEventWechatMsgService {

	private static final Logger logger = LoggerFactory.getLogger(SubscribeEventWechatMsgServiceImpl.class);

	@Autowired
	private PosterActivityService posterActivityService;

	@Override
	Object process(Object object) throws MallApiException {
		SubscribeEventWechatEventMsg wechatEventMsg = (SubscribeEventWechatEventMsg) object;
		logger.info("{}用户关注了我们的公众号", wechatEventMsg.getFromUserName());
		PosterAddWechatUserRequest posterAddWechatUserRequest = new PosterAddWechatUserRequest(wechatEventMsg);
		posterActivityService.putPosterAddWechatUserRequest(posterAddWechatUserRequest);
		return null;
	}

	@Override
	String getEvent() {
		return WxchatUtils.EVENT_TYPE_SUBSCRIBE;
	}

	@Override
	Class<?> getRequestClass() {
		return SubscribeEventWechatEventMsg.class;
	}

}
