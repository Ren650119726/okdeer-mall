package com.okdeer.mall.activity.discount.mapper;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseMapper;

/**
 * @DESC: 
 * @author yangq
 * @date  2016-03-25 20:00:26
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface ActivityDiscountRecordMapper extends IBaseMapper {

	/**
	 * @Description: 统计用户参与活动的总次数
	 * @param userId
	 * @param activityId
	 * @return   
	 * @author maojj
	 * @date 2017年4月21日
	 */
	int countTotalFreq(@Param("userId")String userId,@Param("activityId")String activityId);
	
	/**
	 * @Description: 根据订单删除用户使用活动记录
	 * @param record   
	 * @author maojj
	 * @date 2017年4月22日
	 */
	void deleteByOrderId(String orderId);
}