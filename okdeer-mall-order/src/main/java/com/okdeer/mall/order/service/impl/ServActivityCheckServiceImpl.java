package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;

/**
 * ClassName: ServerCheckServiceImpl 
 * @Description: 活动校验
 * @author wushp
 * @date 2016年9月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.1.0			2016年9月28日				wushp		活动校验
 */
@Service("servActivityCheckService")
public class ServActivityCheckServiceImpl implements RequestHandler<ServiceOrderReq,ServiceOrderResp> {

	/**
	 * 满减满折活动Service
	 */
	@Resource
	private ActivityDiscountService activityDiscountService;
	
	/**
	 * 代金券记录Mapper
	 */
	@Resource
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;

	/**
	 * 折扣、满减活动Mapper
	 */
	@Resource
	private ActivityDiscountMapper activityDiscountMapper;
	
	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		ServiceOrderReq reqData = req.getData();
		ServiceOrderResp respData = resp.getData();
		// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		ActivityTypeEnum activityType = reqData.getActivityType();
		if (activityType == null) {
			activityType = ActivityTypeEnum.NO_ACTIVITY;
		}
		// 代金券领取记录
		String recordId = reqData.getRecordId();
		// 活动id
		String activityId = reqData.getActivityId();
		
		boolean isValid = true;
		switch (activityType) {
			case VONCHER:
				isValid = checkCoupons(recordId);
				break;
			case FULL_REDUCTION_ACTIVITIES:
			case FULL_DISCOUNT_ACTIVITIES:
				isValid = checkDiscount(activityId);
				break;
			default:
				break;
		}
		
		if (!isValid) {
			resp.setResult(ResultCodeEnum.ACTIVITY_IS_EXPIRES);
			req.setComplete(true);
			return;
		}
		
	}

	/**
	 * @Description: 校验优惠券
	 * @param recordId 代金券领取记录ID
	 * @return boolean  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private boolean checkCoupons(String recordId) {
		boolean isValid = true;
		ActivityCouponsRecord conpons = activityCouponsRecordMapper.selectByPrimaryKey(recordId);
		if (conpons.getStatus() == ActivityCouponsRecordStatusEnum.EXPIRES) {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * @Description: 校验满减满折
	 * @param activityId 活动ID
	 * @return boolean  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private boolean checkDiscount(String activityId) {
		boolean isValid = true;
		ActivityDiscount discount = activityDiscountMapper.selectByPrimaryKey(activityId);
		if (discount.getStatus() != ActivityDiscountStatus.ing) {
			isValid = false;
		}
		return isValid;
	}

}
