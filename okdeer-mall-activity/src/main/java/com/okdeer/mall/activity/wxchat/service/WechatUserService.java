
package com.okdeer.mall.activity.wxchat.service;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.wxchat.entity.WechatUser;

public interface WechatUserService extends IBaseService {

	/**
	 * @Description: 根据微信openId获取微信用户信息
	 * @param openid
	 * @return
	 * @author zengjizu
	 * @date 2017年8月3日
	 */
	WechatUser findByOpenid(String openid);
	
	void updateUserInfo(String openid);
}
