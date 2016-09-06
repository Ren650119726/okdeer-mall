package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderRespDto;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.order.constant.OrderTipMsgConstant;
import com.okdeer.mall.order.service.FavourCheckService;

/**
 * ClassName: FavourCheckServiceImpl 
 * @Description: 优惠校验
 * @author maojj
 * @date 2016年7月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-14			maojj			优惠校验
 *		重构V4.1			2016-07-14			maojj			查询用户有效优惠
 */
@Service
public class FavourCheckServiceImpl implements FavourCheckService {
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

	/**
	 * 校验优惠券是否有效
	 */
	@Override
	public void process(TradeOrderReqDto reqDto, TradeOrderRespDto respDto) throws Exception {
		// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		ActivityTypeEnum activityType = reqDto.getData().getActivityType();
		String recordId = reqDto.getData().getRecordId();
		String activityId = reqDto.getData().getActivityId();

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
			respDto.setFlag(false);
			respDto.setMessage(OrderTipMsgConstant.PRIVILEGE_INVALID);
			respDto.getResp().setIsValid(0);
			return;
		}

	}

	/**
	 * @Description: 校验优惠券
	 * @param recordId 代金券领取记录ID
	 * @return boolean  
	 * @author maojj
	 * @date 2016年7月14日
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
	 * @author maojj
	 * @date 2016年7月14日
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
