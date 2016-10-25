package com.okdeer.mall.activity.coupons.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.coupons.entity.ActivityPageView;
import com.okdeer.mall.activity.coupons.mapper.ActivityPageViewMapper;
import com.okdeer.mall.activity.coupons.service.ActivityPageViewServiceApi;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityPageViewServiceApi")
public class ActivityPageViewServiceImpl
		implements ActivityPageViewServiceApi {

	private static final Logger log = Logger.getLogger(ActivityPageViewServiceImpl.class);

	@Autowired
	private ActivityPageViewMapper activityPageViewMapper;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void save(ActivityPageView activityPageView) {
		activityPageViewMapper.save(activityPageView);
	}
}