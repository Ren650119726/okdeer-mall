package com.okdeer.mall.activity.group.service;

import java.util.Map;

import com.okdeer.mall.activity.group.entity.ActivityGroupRecord;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-05-04 10:36:02
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface ActivityGroupRecordService {

	int selectActivityGroupRecord(Map<String, Object> hasBuy);

	void insertSelective(ActivityGroupRecord activityGroupRecord);
	
	void updateDisabledByOrderId(String orderId);

}