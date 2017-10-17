package com.okdeer.mall.activity.discount.service;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.order.entity.ActivityJoinRecord;


public interface ActivityJoinRecordService extends IBaseService {

	/**
	 * @Description: 根据订单id修改活动参与记录
	 * @param activityJoinRecord
	 * @return   
	 * @author maojj
	 * @date 2017年10月17日
	 */
	int updateByOrderId(ActivityJoinRecord activityJoinRecord);
}
