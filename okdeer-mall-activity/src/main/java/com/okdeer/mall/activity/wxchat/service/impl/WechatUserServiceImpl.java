
package com.okdeer.mall.activity.wxchat.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.wxchat.bo.WechatUserInfo;
import com.okdeer.mall.activity.wxchat.entity.WechatUser;
import com.okdeer.mall.activity.wxchat.mapper.WechatUserMapper;
import com.okdeer.mall.activity.wxchat.service.WechatService;
import com.okdeer.mall.activity.wxchat.service.WechatUserService;
import com.okdeer.mall.activity.wxchat.util.EmojiFilter;

@Service
public class WechatUserServiceImpl extends BaseServiceImpl implements WechatUserService {

	private static final Logger logger = LoggerFactory.getLogger(WechatUserServiceImpl.class);

	@Autowired
	private WechatUserMapper wechatUserMapper;

	@Autowired
	private WechatService wechatService;

	@Override
	public WechatUser findByOpenid(String openid) {

		return wechatUserMapper.findByOpenid(openid);
	}

	@Override
	public IBaseMapper getBaseMapper() {

		return wechatUserMapper;
	}

	@Override
	public void updateUserInfo(String openid) {
		WechatUser wechatUser = wechatUserMapper.findByOpenid(openid);
		try {
			WechatUserInfo wechatUserInfo = wechatService.getUserInfo(openid);
			if (wechatUser == null) {
				// 用户不存在
				wechatUser = createWechatUser(wechatUserInfo);
				wechatUser.setId(UuidUtils.getUuid());
				wechatUserMapper.add(wechatUser);
			} else {
				// 已经存在,更新用户信息
				WechatUser updateInfo = createWechatUser(wechatUserInfo);
				updateInfo.setId(wechatUser.getId());
				wechatUserMapper.update(updateInfo);
			}
		} catch (Exception e) {
			logger.error("查询用户信息出错", e);
		}
	}

	private WechatUser createWechatUser(WechatUserInfo wechatUserInfo) {
		WechatUser wechatUser = new WechatUser();
		BeanMapper.copy(wechatUserInfo, wechatUser);
		wechatUser.setSubscribeTime(new Date(wechatUserInfo.getSubscribeTime() * 1000));
		List<String> tagList = wechatUserInfo.getTagids();
		if (CollectionUtils.isNotEmpty(tagList)) {
			StringBuilder tagids = new StringBuilder();
			for (String tagid : tagList) {
				tagids.append(tagid + ",");
			}
			wechatUser.setTagids(tagids.toString());
		}
		//过滤表情包
		wechatUser.setNickName(EmojiFilter.filterEmoji(wechatUser.getNickName()));
		return wechatUser;
	}

}
