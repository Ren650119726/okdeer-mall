package com.okdeer.mall.activity.discount.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.discount.mapper.ActivityJoinRecordMapper;
import com.okdeer.mall.activity.discount.service.ActivityJoinRecordService;
import com.okdeer.mall.order.entity.ActivityJoinRecord;

@Service
public class ActivityJoinRecordServiceImpl extends BaseServiceImpl implements ActivityJoinRecordService {
	
	@Resource
	private ActivityJoinRecordMapper activityJoinRecordMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return activityJoinRecordMapper;
	}

	@Override
	public int updateByOrderId(ActivityJoinRecord activityJoinRecord) {
		return activityJoinRecordMapper.updateByOrderId(activityJoinRecord);
	}

}
