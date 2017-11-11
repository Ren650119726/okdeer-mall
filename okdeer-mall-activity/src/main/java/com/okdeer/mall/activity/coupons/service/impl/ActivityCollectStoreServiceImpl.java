package com.okdeer.mall.activity.coupons.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.coupons.bo.ActivityCollectStoreParamBo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectStore;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectStoreMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCollectStoreService;


@Service
public class ActivityCollectStoreServiceImpl implements ActivityCollectStoreService {

	@Autowired
	private ActivityCollectStoreMapper activityCollectStoreMapper;

	@Override
	public void deleteByCollectCouponsId(String collectCouponsId) {
		activityCollectStoreMapper.deleteByCollectCouponsId(collectCouponsId);
	}

	@Override
	public void saveBatch(List<ActivityCollectStore> list) {
		activityCollectStoreMapper.saveBatch(list);
	}

	@Override
	public List<ActivityCollectStore> listByCollectCouponsId(String collectCouponsId) {
		
		return activityCollectStoreMapper.listByCollectCouponsId(collectCouponsId);
	}

	@Override
	public List<ActivityCollectStore> findList(ActivityCollectStoreParamBo activityCollectStoreParamBo) {
		return activityCollectStoreMapper.findList(activityCollectStoreParamBo);
	}
	
}
