package com.okdeer.mall.order.handler.impl;

import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
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
	
	/**
	 * 代金券Mapper
	 */
	@Resource
	private ActivityCouponsMapper activityCouponsMapper;
	
	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		ServiceOrderReq reqData = req.getData();
		// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		ActivityTypeEnum activityType = reqData.getActivityType();
		if (activityType == null) {
			activityType = ActivityTypeEnum.NO_ACTIVITY;
		}
		// 代金券领取记录
		String recordId = reqData.getRecordId();
		// 活动id
		String activityId = reqData.getActivityId();
		StringBuilder couponsId = new StringBuilder();
		
		boolean isValid = true;
		switch (activityType) {
			case VONCHER:
				isValid = checkCoupons(recordId, couponsId);
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
		
		// Begin added by wushp 2016-10-13
		// 如果使用的是代金券，如果代金券指定分类，则需要校验选购的商品是否超出代金券指定分类
		if (activityType == ActivityTypeEnum.VONCHER) {
			// 查询代金券
			ActivityCoupons coupons = activityCouponsMapper.selectByPrimaryKey(couponsId.toString());
			// 0全部分类 1指定分类
			if (coupons.getIsCategory() == Constant.ONE) {
				Set<String> spuCategoryIds = (Set<String>) req.getContext().get("spuCategoryIds");
				// 如果是指定分类。校验商品的分类
				int count = activityCouponsRecordMapper.findServerBySpuCategoryIds(spuCategoryIds, coupons.getId());
				if (count == Constant.ZERO || count != spuCategoryIds.size()) {
					// 购买的商品分类超出代金券限购的分类。则订单提交失败
					resp.setResult(ResultCodeEnum.ACTIVITY_CATEGORY_LIMIT);
					req.setComplete(true);
					return;
				}
			}			
		}
		// end added by wushp 2016-10-13
		
	}
	
	/**
	 * @Description: 校验优惠券
	 * @param recordId 代金券领取记录ID
	 * @param couponsId 代金券id
	 * @return boolean  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private boolean checkCoupons(String recordId,StringBuilder couponsId) {
		boolean isValid = true;
		ActivityCouponsRecord conpons = activityCouponsRecordMapper.selectByPrimaryKey(recordId);
		couponsId.append(conpons.getCouponsId());
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
		ActivityDiscount discount = activityDiscountMapper.findById(activityId);
		if (discount.getStatus() != ActivityDiscountStatus.ing) {
			isValid = false;
		}
		return isValid;
	}

}
