
package com.okdeer.mall.activity.coupons.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.coupons.bo.ActivityCollectCouponsRecordParamBo;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsRecordService;

@Service
public class ActivityCollectCouponsRecordServiceImpl extends BaseServiceImpl
		implements ActivityCollectCouponsRecordService {

	@Autowired
	private ActivityCollectCouponsRecordMapper activityCollectCouponsRecordMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return activityCollectCouponsRecordMapper;
	}

	@Override
	public int findCountByParams(ActivityCollectCouponsRecordParamBo activityCollectCouponsRecordParamBo) {
		return activityCollectCouponsRecordMapper.findCountByParams(activityCollectCouponsRecordParamBo);
	}

}
