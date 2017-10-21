
package com.okdeer.mall.activity.share.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.share.mapper.ActivityShareOrderRecordMapper;
import com.okdeer.mall.activity.share.service.ActivityShareOrderRecordService;

@Service
public class ActivityShareOrderRecordServiceImpl extends BaseServiceImpl implements ActivityShareOrderRecordService {

	@Autowired
	private ActivityShareOrderRecordMapper activityShareOrderRecordMapper;
	
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityShareOrderRecordMapper;
	}

}
