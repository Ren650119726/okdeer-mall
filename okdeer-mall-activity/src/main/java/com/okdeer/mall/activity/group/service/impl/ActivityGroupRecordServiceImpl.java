package com.okdeer.mall.activity.group.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.group.entity.ActivityGroupRecord;
import com.okdeer.mall.activity.group.service.ActivityGroupRecordServiceApi;
import com.okdeer.mall.activity.group.mapper.ActivityGroupRecordMapper;
import com.okdeer.mall.activity.group.service.ActivityGroupRecordService;

/**
 * @DESC: 
 * @author yangqin
 * @date  2016-05-04 10:36:02
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.group.service.ActivityGroupRecordServiceApi")
class ActivityGroupRecordServiceImpl implements ActivityGroupRecordServiceApi, ActivityGroupRecordService {

	@Resource
	private ActivityGroupRecordMapper activityGroupRecordMapper;

	public static final Logger logger = LoggerFactory.getLogger(ActivityGroupRecordServiceImpl.class);

	@Override
	public int selectActivityGroupRecord(Map<String, Object> hasBuy) {
		return activityGroupRecordMapper.selectActivityGroupRecord(hasBuy);
	}

	@Override
	public void insertSelective(ActivityGroupRecord activityGroupRecord) {
		activityGroupRecordMapper.insertSelective(activityGroupRecord);

	}

	@Override
	public void updateDisabledByOrderId(String orderId) {
		activityGroupRecordMapper.updateDisabledByOrderId(orderId);

	}

}