package com.okdeer.mall.order.handler.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.order.constant.text.OrderTipMsgConstant;
import com.okdeer.mall.order.handler.FavourCheckService;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderRespDto;

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
 *		Bug:14093       2016-10-12			maojj   		优惠券校验使用品类
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
	
	// Begin Bug:14093 added by maojj 2016-10-12
	/**
	 * 代金券Mapper
	 */
	@Resource
	private ActivityCouponsMapper activityCouponsMapper;
	// End Bug:14093 added by maojj 2016-10-12
	
	/**
	 * 导航类目
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsNavigateCategoryServiceApi goodsNavigateCategoryServiceApi;
	
	/**
	 * 校验优惠券是否有效
	 */
	@Override
	public void process(TradeOrderReqDto reqDto, TradeOrderRespDto respDto) throws Exception {
		// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		ActivityTypeEnum activityType = reqDto.getData().getActivityType();
		String recordId = reqDto.getData().getRecordId();
		String activityId = reqDto.getData().getActivityId();
		StringBuilder couponsId = new StringBuilder();
		
		boolean isValid = true;
		switch (activityType) {
			case VONCHER:
				isValid = checkCoupons(recordId,couponsId);
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
		
		// Begin Bug:14093 added by maojj 2016-10-12
		// 如果使用的是代金券，如果代金券指定分类，则需要校验选购的商品是否超出代金券指定分类
		if(activityType == ActivityTypeEnum.VONCHER){
			// 查询代金券
			ActivityCoupons coupons = activityCouponsMapper.selectByPrimaryKey(couponsId.toString());
			// 0全部分类 1指定分类
			if(coupons.getIsCategory() == Constant.ONE){
				Set<String> spuCategoryIds = reqDto.getContext().getSpuCategoryIds();
				// 如果是指定分类。校验商品的分类
				// int count = activityCouponsRecordMapper.findIsContainBySpuCategoryIds(spuCategoryIds, coupons.getId());
				// 15719  商品关联导航类目需求变更兼容
				List<String> ids = goodsNavigateCategoryServiceApi.findNavigateCategoryByCouponId(coupons.getId());
				boolean bool = ids.containsAll(spuCategoryIds);
				if (!bool) {
					// 购买的商品分类超出代金券限购的分类。则订单提交失败
					respDto.setFlag(false);
					respDto.setMessage(OrderTipMsgConstant.KIND_LIMIT_OVER);
					respDto.getResp().setIsValid(0);
					return;
				}
			}			
		}
		// End Bug:14093 added by maojj 2016-10-12

	}

	/**
	 * @Description: 校验优惠券
	 * @param recordId 代金券领取记录ID
	 * @return boolean  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private boolean checkCoupons(String recordId,StringBuilder couponsId) {
		boolean isValid = true;
		ActivityCouponsRecord conpons = activityCouponsRecordMapper.selectByPrimaryKey(recordId);
		if (conpons.getStatus() != ActivityCouponsRecordStatusEnum.UNUSED) {
			isValid = false;
		}
		couponsId.append(conpons.getCouponsId());
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
