package com.okdeer.mall.activity.discount.mapper;

import com.okdeer.mall.activity.discount.entity.ActivityDiscountRecord;

/**
 * @DESC: 
 * @author yangq
 * @date  2016-03-25 20:00:26
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface ActivityDiscountRecordMapper {
	
	/**
	 * 添加活动使用记录 </p>
	 * 
	 * @author yangq
	 * @param record
	 */
	void insertRecord(ActivityDiscountRecord record);
	
}