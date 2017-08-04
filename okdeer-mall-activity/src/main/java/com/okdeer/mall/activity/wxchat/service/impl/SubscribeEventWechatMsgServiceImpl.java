
package com.okdeer.mall.activity.wxchat.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.activity.wxchat.bo.WechatUserInfo;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterShareInfo;
import com.okdeer.mall.activity.wxchat.message.SubscribeEventWechatEventMsg;
import com.okdeer.mall.activity.wxchat.message.TextWechatMsg;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterShareInfoService;
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
	private WechatUserService wechatUserService;

	@Autowired
	private WechatService wechatService;

	@Autowired
	private ActivityPosterShareInfoService activityPosterShareInfoService;

	private static final String QRSCENE_STR = "qrscene_";

	@Override
	Object process(Object object) throws MallApiException {
		SubscribeEventWechatEventMsg wechatEventMsg = (SubscribeEventWechatEventMsg) object;
		logger.info("{}用户关注了我们的公众号", wechatEventMsg.getFromUserName());
		WechatUserInfo subscribeUser = wechatUserService.updateUserInfo(wechatEventMsg.getFromUserName());
		if (StringUtils.isNotEmpty(wechatEventMsg.getEventKey())
				&& wechatEventMsg.getEventKey().startsWith(QRSCENE_STR)) {
			// 用户未关注我们的公众号，并且通过好友分享的二维码来关注我们的公众号
			String shareOpenid = wechatEventMsg.getEventKey()
					.substring(wechatEventMsg.getEventKey().indexOf("qrscene_") + QRSCENE_STR.length());
			ActivityPosterShareInfo activityPosterShareInfo = activityPosterShareInfoService
					.findByOpenid(wechatEventMsg.getFromUserName());
			if (activityPosterShareInfo == null) {
				try {
					WechatUserInfo wechatUserInfo = wechatService.getUserInfo(shareOpenid);
					return createResponse(subscribeUser, wechatUserInfo);
				} catch (Exception e) {
					logger.error("获取分享人信息出错", e);
				}

			}
		}
		return null;
	}

	private TextWechatMsg createResponse(WechatUserInfo subscribeUser, WechatUserInfo wechatUserInfo) {
		TextWechatMsg textWechatMsg = new TextWechatMsg();
		textWechatMsg.setFromUserName(wechatConfig.getAccount());
		textWechatMsg.setToUserName(subscribeUser.getOpenid());
		String content = subscribeUser.getNickName() + "，您现在是" + wechatUserInfo.getNickName()
				+ "的推荐好友啦，您也可点击服务号栏目“七夕情报” 生成您的个人专属情报，每三位好友扫码关注，您就可以领取iPhone 7/鲜花/100元优惠券，每天可领15次！点击查看活动细则及奖品记录（链接）”。"
				+ "点击<a href=\"http://www.baidu.com\">【查看活动细则及奖品记录】</a>链接，进入七夕情报拆奖页面。";
		textWechatMsg.setContent(content);
		return textWechatMsg;
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
