package com.okdeer.mall.activity.discount.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.discount.entity.ActivityBusinessRel;
import com.okdeer.mall.activity.discount.mapper.ActivityBusinessRelMapper;
import com.okdeer.mall.activity.discount.service.ActivityBusinessRelService;

@Service
public class ActivityBusinessRelServiceImpl extends BaseServiceImpl implements ActivityBusinessRelService {
	
	@Resource
	private ActivityBusinessRelMapper activityBusinessRelMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return activityBusinessRelMapper;
	}

	@Override
	public List<ActivityBusinessRel> findByActivityId(String activityId) {
		return activityBusinessRelMapper.findByActivityId(activityId);
	}

	
}
