package com.okdeer.mall.activity.group.mapper;

import java.util.Map;

import com.okdeer.mall.activity.group.entity.ActivityGroupRecord;

/**
 * @DESC: 
 * @author yangq
 * @date  2016-05-04 10:36:02
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface ActivityGroupRecordMapper{
	
	void insertSelective(ActivityGroupRecord groupRecord);
	
	/**
	 * 根据订单ID，逻辑删除特惠活动记录信息
	 */
	Integer updateDisabledByOrderId(String orderId);
	
	/**
	 * 查询团购活动用户购买数量 </p>
	 * 
	 * @author yangq
	 * @param hasMap
	 * @return
	 */
	int selectActivityGroupRecord(Map<String,Object> hasMap);
	
}