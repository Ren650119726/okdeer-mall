package com.okdeer.mall.activity.coupons.service;

import java.util.Date;

import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;

/**
 * ClassName: ActivityCouponsTermStrategy 
 * @Description: 代金券期限策略
 * @author maojj
 * @date 2017年9月9日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.3 		2017年9月9日				maojj
 */
public interface ActivityCouponsReceiveStrategy {

	/**
	 * @Description: 获取生效时间
	 * @return   
	 * @author maojj
	 * @date 2017年9月9日
	 */
	Date getEffectTime(ActivityCoupons coupons);
	
	/**
	 * @Description: 获取过期时间
	 * @return   
	 * @author maojj
	 * @date 2017年9月9日
	 */
	Date getExpireTime(ActivityCoupons coupons);
	
	/**
	 * @Description: 处理代金券领取记录
	 * @param couponsRecord
	 * @param coupons   
	 * @author maojj
	 * @date 2017年9月9日
	 */
	void process(ActivityCouponsRecord couponsRecord,ActivityCoupons coupons);
	
	
}
