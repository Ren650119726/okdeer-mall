package com.okdeer.mall.activity.coupons.service;

import java.util.List;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRandCode;

/**
 * ClassName: 代金券随机码
 * @Description: 根据代金券id获取代金卷随机码
 * @author zhulq
 * @date 2017年4月6日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构4.1			 2017年4月6日 			zhulq
 */
public interface ActivityCouponsRandCodeService {
	
	/**
	 * @Description: 根据代金券id获取代金券随机码
	 * @param couponsId 代金券id
	 * @return 集合
	 * @author zhulq
	 * @date 2017年4月6日
	 */
	List<ActivityCouponsRandCode> findByCouponsId(String couponsId) throws ServiceException;

}
