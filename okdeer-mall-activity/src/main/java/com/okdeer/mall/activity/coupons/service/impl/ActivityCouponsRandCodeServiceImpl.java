package com.okdeer.mall.activity.coupons.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRandCode;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRandCodeMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRandCodeService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRandCodeServiceApi;

/**
 * ClassName: ActivityCouponsRandCodeServiceImpl 
 * @Description: 代金券随机码
 * @author zhulq
 * @date 2017年4月6日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2017年4月6日 			zhulq
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivityCouponsRandCodeServiceApi")
public class ActivityCouponsRandCodeServiceImpl 
			implements ActivityCouponsRandCodeServiceApi,ActivityCouponsRandCodeService {

	private static final Logger log = Logger.getLogger(ActivityCollectCouponsServiceImpl.class);

	/**
	 * 代金卷随机码Mapper
	 */
	@Autowired
	private ActivityCouponsRandCodeMapper activityCouponsRandCodeMapper;
	
	@Override
	public List<ActivityCouponsRandCode> findByCouponsId(String couponsId) throws ServiceException {
		return activityCouponsRandCodeMapper.findByCouponsId(couponsId);
	}

}
