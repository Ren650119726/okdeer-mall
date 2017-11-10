package com.okdeer.mall.activity.coupons.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.coupons.bo.ActivityCollectAreaParamBo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectArea;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectAreaMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCollectAreaService;

@Service
public class ActivityCollectAreaServiceImpl implements ActivityCollectAreaService {
	
	@Autowired
	private ActivityCollectAreaMapper activityCollectAreaMapper;

	@Override
	public void deleteByCollectCouponsId(String collectCouponsId) {
		
		activityCollectAreaMapper.deleteByCollectCouponsId(collectCouponsId);
	}

	@Override
	public void saveBatch(List<ActivityCollectArea> list) {
		activityCollectAreaMapper.saveBatch(list);
	}

	@Override
	public List<ActivityCollectArea> listByCollectCouponsId(String collectCouponsId) {
		return activityCollectAreaMapper.listByCollectCouponsId(collectCouponsId);
	}

	@Override
	public List<ActivityCollectArea> findList(ActivityCollectAreaParamBo activityCollectAreaParamBo) {
		return activityCollectAreaMapper.findList(activityCollectAreaParamBo);
	}
	
	
	
}
