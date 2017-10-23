/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityJoinRecordMapper.java
 * @Date 2017-10-10 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.discount.mapper;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.bo.ActivityJoinRecParamBo;
import com.okdeer.mall.order.entity.ActivityJoinRecord;

public interface ActivityJoinRecordMapper extends IBaseMapper {

	/**
	 * @Description: 统计活动参与总数
	 * @return   
	 * @author maojj
	 * @date 2017年10月11日
	 */
	int countActivityJoinNum(ActivityJoinRecParamBo paramBo);
	
	/**
	 * @Description: 根据订单id修改活动参与记录
	 * @param activityJoinRecord
	 * @return   
	 * @author maojj
	 * @date 2017年10月17日
	 */
	int updateByOrderId(ActivityJoinRecord activityJoinRecord);
}