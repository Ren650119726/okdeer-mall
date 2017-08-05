
package com.okdeer.mall.activity.wxchat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterShareInfo;
import com.okdeer.mall.activity.wxchat.mapper.ActivityPosterShareInfoMapper;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterShareInfoService;

@Service
public class ActivityPosterShareInfoServiceImpl extends BaseServiceImpl implements ActivityPosterShareInfoService {

	@Autowired
	private ActivityPosterShareInfoMapper activityPosterShareInfoMapper;
	
	@Override
	public ActivityPosterShareInfo findByOpenid(String openid) {
		
		return activityPosterShareInfoMapper.findByOpenid(openid);
	}

	@Override
	public IBaseMapper getBaseMapper() {

		return activityPosterShareInfoMapper;
	}

	@Override
	public int queryCountByShareOpenId(String shareOpenid) {
		return activityPosterShareInfoMapper.queryCountByShareOpenId(shareOpenid);
	}

}
