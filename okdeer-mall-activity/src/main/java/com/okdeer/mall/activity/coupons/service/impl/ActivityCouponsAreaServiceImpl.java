package com.okdeer.mall.activity.coupons.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsArea;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsAreaMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsAreaService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsAreaServiceApi;

/**
 * ClassName: ActivityCouponsAreaServiceImpl 
 * @Description: 代金券区域关联关系实现类
 * @author zhulq
 * @date 2017年4月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2017年4月11日 			zhulq
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityCouponsAreaServiceApi")
public class ActivityCouponsAreaServiceImpl implements ActivityCouponsAreaServiceApi, ActivityCouponsAreaService{

	/**
	 * 注入代金券区域管理mapper
	 */
	@Autowired
	private ActivityCouponsAreaMapper activityCouponsAreaMapper;
	
	@Override
	public List<ActivityCouponsArea> findListByType(ActivityCouponsArea activityCouponsArea) throws ServiceException {
		return activityCouponsAreaMapper.findListByType(activityCouponsArea);
	}

}
