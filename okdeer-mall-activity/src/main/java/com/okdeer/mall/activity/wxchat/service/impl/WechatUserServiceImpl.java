package com.okdeer.mall.activity.wxchat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.wxchat.entity.WechatUser;
import com.okdeer.mall.activity.wxchat.mapper.WechatUserMapper;
import com.okdeer.mall.activity.wxchat.service.WechatUserService;

@Service
public class WechatUserServiceImpl extends BaseServiceImpl implements WechatUserService {

	@Autowired
	private WechatUserMapper wechatUserMapper;
	
	@Override
	public WechatUser findByOpenid(String openid) {
		
		return wechatUserMapper.findByOpenid(openid);
	}

	@Override
	public IBaseMapper getBaseMapper() {
		
		return wechatUserMapper;
	}

}
