package com.okdeer.mall.activity.coupons.service.impl;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsTermType;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsReceiveStrategy;

/**
 * ClassName: ActivityCouponsTermStrategyImpl 
 * @Description: 代金券期限策略。后期根据情况按照策略模式（多态）拆分为多个实现类。目前暂时写在一起
 * @author maojj
 * @date 2017年9月9日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.3 		2017年9月9日				maojj        代金券期限策略
 */
@Service
public class ActivityCouponsReceiveStrategyImpl implements ActivityCouponsReceiveStrategy {

	@Override
	public Date getEffectTime(ActivityCoupons coupons) {
		// 生效时间
		Date effectTime = null;
		if(coupons.getTermType() == ActivityCouponsTermType.BY_DAY){
			// 获取当天凌晨的日历对象
			Calendar calendar = getCurrentCalendar();
			calendar.add(Calendar.DAY_OF_YEAR, coupons.getEffectDay());
			effectTime = calendar.getTime();
		}else{
			// 设置代金券的有效时间范围
			effectTime = coupons.getStartTime();
		}
		return effectTime;
	}

	@Override
	public Date getExpireTime(ActivityCoupons coupons) {
		// 生效时间
		Date expireTime = null;
		if(coupons.getTermType() == ActivityCouponsTermType.BY_DAY){
			// 获取当天凌晨的日历对象
			Calendar calendar = getCurrentCalendar();
			calendar.add(Calendar.DAY_OF_YEAR, coupons.getEffectDay() + coupons.getValidDay());
			expireTime = calendar.getTime();
		}else{
			// 设置代金券的有效时间范围
			expireTime = coupons.getEndTime();
		}
		return expireTime;
	}

	/**
	 * @Description: 获取当天0点0分0秒的日历对象
	 * @return   
	 * @author maojj
	 * @date 2017年9月9日
	 */
	private Calendar getCurrentCalendar(){
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	@Override
	public void process(ActivityCouponsRecord couponsRecord, ActivityCoupons coupons) {
		// 设置领取时间为当前时间
		couponsRecord.setCollectTime(new Date());
		// 设置生效时间
		couponsRecord.setEffectTime(getEffectTime(coupons));
		// 设置代金券到期时间
		couponsRecord.setValidTime(getExpireTime(coupons));
		// 如果生效时间在领取时间之后，则状态为未生效。否则为未使用
		if(couponsRecord.getEffectTime().after(couponsRecord.getCollectTime())){
			couponsRecord.setStatus(ActivityCouponsRecordStatusEnum.UNEFFECTIVE);
		}else if(couponsRecord.getCollectTime().after(couponsRecord.getValidTime())){
			couponsRecord.setStatus(ActivityCouponsRecordStatusEnum.EXPIRES);
		}else{
			couponsRecord.setStatus(ActivityCouponsRecordStatusEnum.UNUSED);
		}
	}
}
