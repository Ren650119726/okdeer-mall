
package com.okdeer.mall.activity.wxchat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterWechatUserInfo;
import com.okdeer.mall.activity.wxchat.mapper.ActivityPosterWechatUserInfoMapper;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterWechatUserService;

@Service
public class ActivityPosterWechatUserServiceImpl extends BaseServiceImpl implements ActivityPosterWechatUserService {

	@Autowired
	private ActivityPosterWechatUserInfoMapper activityPosterWechatUserInfoMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return activityPosterWechatUserInfoMapper;
	}

	@Override
	public ActivityPosterWechatUserInfo findByOpenid(String openid) {
		ActivityPosterWechatUserInfo activityPosterWechatUserInfo = activityPosterWechatUserInfoMapper.findById(openid);
		if (activityPosterWechatUserInfo == null) {
			activityPosterWechatUserInfo = new ActivityPosterWechatUserInfo();
			activityPosterWechatUserInfo.setOpenid(openid);
			activityPosterWechatUserInfo.setQualificaCount(0);
			activityPosterWechatUserInfo.setUsedQualificaCount(0);
			activityPosterWechatUserInfoMapper.add(activityPosterWechatUserInfo);
		}
		return activityPosterWechatUserInfo;
	}

	@Override
	public int updateUsedQualificaCount(String openid,int usedQualificaCount, int conditionUsedQualificaCount) {
		
		return activityPosterWechatUserInfoMapper.updateUsedQualificaCount(openid,usedQualificaCount,conditionUsedQualificaCount);
	}

}
