
package com.okdeer.mall.activity.wxchat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.wxchat.mapper.ActivityPosterConfigMapper;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterConfigService;

@Service
public class ActivityPosterConfigServiceImpl extends BaseServiceImpl implements ActivityPosterConfigService {

	@Autowired
	private ActivityPosterConfigMapper activityPosterConfigMapper;

	@Override
	public IBaseMapper getBaseMapper() {

		return activityPosterConfigMapper;
	}

}
