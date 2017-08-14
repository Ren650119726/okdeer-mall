
package com.okdeer.mall.activity.wxchat.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.wxchat.entity.WechatMenu;
import com.okdeer.mall.activity.wxchat.mapper.WechatMenuMapper;
import com.okdeer.mall.activity.wxchat.service.WechatMenuService;

@Service
public class WechatMenuServiceImpl extends BaseServiceImpl implements WechatMenuService {

	@Autowired
	private WechatMenuMapper wechatMenuMapper;

	@Override
	public List<WechatMenu> findByList() {
		return wechatMenuMapper.findByList();
	}

	@Override
	public IBaseMapper getBaseMapper() {
		return wechatMenuMapper;
	}

}
