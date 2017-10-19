package com.okdeer.mall.activity.discount.service;

import java.util.List;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.discount.entity.ActivityBusinessRel;


public interface ActivityBusinessRelService extends IBaseService {

	/**
	 * @Description: 查询活动业务关联关系
	 * @param activityId
	 * @return   
	 * @author maojj
	 * @date 2017年4月19日
	 */
	List<ActivityBusinessRel> findByActivityId(String activityId);
}
