package com.okdeer.mall.activity.coupons.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRelationStore;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRelationStoreMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRelationStoreService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRelationStoreServiceApi;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityCouponsRelationStoreServiceApi")
public class ActivityCouponsRelationStoreServiceImpl implements ActivityCouponsRelationStoreService,ActivityCouponsRelationStoreServiceApi{

	@Autowired
	private ActivityCouponsRelationStoreMapper activityCouponsRelationStoreMapper;
	
	@Override
	public void addCouponsRelationStore(List<ActivityCouponsRelationStore> list) throws ServiceException {
		activityCouponsRelationStoreMapper.insertCouponsRelationStore(list);		
	}

	
}
