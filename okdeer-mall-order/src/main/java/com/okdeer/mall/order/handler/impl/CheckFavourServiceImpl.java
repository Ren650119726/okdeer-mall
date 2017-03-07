package com.okdeer.mall.order.handler.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.enums.CouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.constant.text.OrderTipMsgConstant;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.RequestHandler;

/**
 * ClassName: CheckFavourServiceImpl 
 * @Description: 检查优惠信息的服务
 * @author maojj
 * @date 2017年1月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.0 			2017年1月7日				maojj
 */
@Service("checkFavourService")
public class CheckFavourServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {

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

	/**
	 * 导航类目
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsNavigateCategoryServiceApi goodsNavigateCategoryServiceApi;

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		ActivityTypeEnum activityType = paramDto.getActivityType();
		String recordId = paramDto.getRecordId();
		String activityId = paramDto.getActivityId();
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		Set<String> spuCategoryIds = parserBo == null ? null : parserBo.getCategoryIdSet();
		
		boolean isValid = true;
		switch (activityType) {
			case VONCHER:
				isValid = checkCoupons(recordId,spuCategoryIds,resp);
				break;
			case FULL_REDUCTION_ACTIVITIES:
			case FULL_DISCOUNT_ACTIVITIES:
				isValid = checkDiscount(activityId);
				break;
			default:
				break;
		}
		if (!isValid && resp.isSuccess()) {
			resp.setResult(ResultCodeEnum.FAVOUR_NOT_SUPPORT);
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
	private boolean checkCoupons(String recordId,Set<String> spuCategoryIds,Response<PlaceOrderDto> resp) throws Exception{
		ActivityCouponsRecord couponsRecord = activityCouponsRecordMapper.selectByPrimaryKey(recordId);
		if (couponsRecord.getStatus() != ActivityCouponsRecordStatusEnum.UNUSED) {
			return false;
		}
		
		// 查询代金券
		ActivityCoupons coupons = activityCouponsMapper.selectByPrimaryKey(couponsRecord.getCouponsId());
		if(coupons.getIsCategory() == Constant.ONE){
			if(coupons.getType() == CouponsType.bld.ordinal()){
				List<String> categoryIdLimitList = goodsNavigateCategoryServiceApi.findNavigateCategoryByCouponId(coupons.getId());
				if (!categoryIdLimitList.containsAll(spuCategoryIds)) {
					// 超出指定分类
					resp.setResult(ResultCodeEnum.ACTIVITY_CATEGORY_LIMIT);
					return false;
				}
			}else if(coupons.getType() == CouponsType.fwd.ordinal()){
				int count = activityCouponsRecordMapper.findServerBySpuCategoryIds(spuCategoryIds, coupons.getId());
				if (count != spuCategoryIds.size()) {
					// 超出指定分类
					resp.setResult(ResultCodeEnum.ACTIVITY_CATEGORY_LIMIT);
					return false;
				}
			}
		}			
		return true;
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
