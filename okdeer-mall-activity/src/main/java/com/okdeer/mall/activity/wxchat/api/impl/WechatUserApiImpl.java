
package com.okdeer.mall.activity.wxchat.api.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.wechat.dto.WechatUserDto;
import com.okdeer.mall.activity.wechat.service.WechatUserApi;
import com.okdeer.mall.activity.wxchat.bo.WechatUserInfo;
import com.okdeer.mall.activity.wxchat.entity.WechatUser;
import com.okdeer.mall.activity.wxchat.service.WechatUserService;

@Service(version = "1.0.0")
public class WechatUserApiImpl implements WechatUserApi {

	@Autowired
	private WechatUserService wechatUserService;

	@Override
	public WechatUserDto findByOpenid(String openid) {
		WechatUser wechatUser = wechatUserService.findByOpenid(openid);
		WechatUserDto wechatUserDto = new WechatUserDto();
		if (wechatUser == null) {
			WechatUserInfo wechatUserInfo = wechatUserService.updateUserInfo(openid);
			BeanMapper.copy(wechatUserInfo, wechatUserDto);
		} else {
			BeanMapper.copy(wechatUser, wechatUserDto);
		}
		return wechatUserDto;
	}

}
