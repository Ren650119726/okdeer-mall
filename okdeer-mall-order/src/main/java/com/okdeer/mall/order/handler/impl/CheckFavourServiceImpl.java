package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Maps;
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
import com.okdeer.mall.activity.discount.entity.ActivityDiscountCondition;
import com.okdeer.mall.activity.discount.enums.ActivityBusinessType;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.enums.LimitSkuType;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountConditionMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountRecordService;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.dto.ActivityInfoDto;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.enums.UseClientType;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.system.service.SysBuyerFirstOrderRecordService;


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
	private ActivityDiscountService activityDiscountService;

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
	
	@Resource
	private SysBuyerFirstOrderRecordService sysBuyerFirstOrderRecordService;
	
	@Resource
	private ActivityDiscountRecordService activityDiscountRecordService;
	
	@Resource
	private ActivityDiscountConditionMapper activityDiscountConditionMapper;

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		ActivityTypeEnum activityType = paramDto.getActivityType();
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		if(parserBo.isLowFavour() && activityType != ActivityTypeEnum.NO_ACTIVITY){
			// 参与低价之后，不能参与任何其他优惠活动
			resp.setResult(ResultCodeEnum.FAVOUR_NOT_SUPPORT);
			return;
		}
		boolean isValid = true;
		switch (activityType) {
			case VONCHER:
				isValid = checkCoupons(paramDto,parserBo,resp);
				break;
			case FULL_REDUCTION_ACTIVITIES:
			case FULL_DISCOUNT_ACTIVITIES:
				isValid = checkDiscount(paramDto,parserBo);
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
	private boolean checkCoupons(PlaceOrderParamDto paramDto,StoreSkuParserBo parserBo,Response<PlaceOrderDto> resp) throws Exception{
		String recordId = paramDto.getRecordId();
		ActivityCouponsRecord couponsRecord = activityCouponsRecordMapper.selectByPrimaryKey(recordId);
		if (couponsRecord.getStatus() != ActivityCouponsRecordStatusEnum.UNUSED) {
			return false;
		}
		// 查询代金券
		ActivityCoupons coupons = activityCouponsMapper.selectByPrimaryKey(couponsRecord.getCouponsId());
		if(coupons.getUseClientType() == UseClientType.ONlY_APP_USE){
			// 仅限首单用户，检查当前用户是否为首单用户。
			if(!isFirstOrderUser(paramDto.getUserId())){
				resp.setResult(ResultCodeEnum.ACTIVITY_LIMIT_FIRST_ORDER);
				return false;
			}
		}
		if(coupons.getIsCategory() == Constant.ONE){
			if(coupons.getType() == CouponsType.bld.ordinal()){
				// 便利店限制品类
				List<String> categoryIdLimitList = goodsNavigateCategoryServiceApi.findNavigateCategoryByCouponId(coupons.getId());
				Map<String, CurrentStoreSkuBo> haveFavourGoodsMap = Maps.newHashMap();
				BigDecimal totalAmount = BigDecimal.valueOf(0.00);
				for(CurrentStoreSkuBo storeSkuBo : parserBo.getCurrentSkuMap().values()){
					if(categoryIdLimitList.contains(storeSkuBo.getSpuCategoryId())){
						haveFavourGoodsMap.put(storeSkuBo.getId(), storeSkuBo);
						totalAmount = totalAmount.add(storeSkuBo.getTotalAmount());
					}
				}
				if(totalAmount.compareTo(BigDecimal.valueOf(0.0)) == 0 || totalAmount.compareTo(BigDecimal.valueOf(coupons.getArriveLimit())) == -1){
					// 如果享受优惠的商品总金额为0，标识没有指定分类的商品。如果享受优惠的商品总金额小于代金券的使用条件，也不能使用该代金券
					resp.setResult(ResultCodeEnum.ACTIVITY_CATEGORY_LIMIT);
					return false;
				}
				parserBo.setHaveFavourGoodsMap(haveFavourGoodsMap);
				parserBo.setTotalAmountHaveFavour(totalAmount);
			} else if(coupons.getType() == CouponsType.fwd.ordinal()){
				Set<String> spuCategoryIds = parserBo == null ? null : parserBo.getCategoryIdSet();
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
	 * @throws Exception 
	 * @date 2016年7月14日
	 */
	private boolean checkDiscount(PlaceOrderParamDto paramDto,StoreSkuParserBo parserBo) throws Exception {
		String activityId = paramDto.getActivityId();
		String userId = paramDto.getUserId();
		String activityItemId = paramDto.getActivityItemId();
		boolean isValid = true;
		ActivityInfoDto actInfoDto = activityDiscountService.findInfoById(activityId, false);
		ActivityDiscountCondition condition = activityDiscountConditionMapper.findById(activityItemId);
		ActivityDiscount actInfo = actInfoDto.getActivityInfo();
		if (actInfo.getStatus() != ActivityDiscountStatus.ing) {
			isValid = false;
		}
		if (actInfo.getLimitUser() == UseUserType.ONlY_NEW_USER) {
			// 如果限制首单用户
			if (!isFirstOrderUser(userId)) {
				return false;
			}
		}
		// 参与活动次数限制
		int limitTotalFreq = actInfo.getLimitTotalFreq().intValue();
		if (limitTotalFreq > 0) {
			// 用户参与活动次数。0：不限，大于0有限制
			int userTotalFreq = activityDiscountRecordService.countTotalFreq(userId,activityId);
			if (userTotalFreq >= limitTotalFreq) {
				return false;
			}
		}
		// 商品限制
		LimitSkuType limitSkuType = actInfo.getLimitSku();
		// 参与活动的商品总金额
		BigDecimal totalAmount = BigDecimal.valueOf(0.00);
		Map<String, CurrentStoreSkuBo> haveFavourGoodsMap = Maps.newHashMap();
		if (limitSkuType == LimitSkuType.LIMIT_CATEGORY) {
			// 活动限制的分类Id列表
			List<String> limitCtgIds = actInfoDto.getBusinessIds(ActivityBusinessType.SKU_CATEGORY);
			// 指定分类
			// 遍历购买的商品
			for (CurrentStoreSkuBo storeSkuBo : parserBo.getCurrentSkuMap().values()) {
				if (limitCtgIds.contains(storeSkuBo.getSpuCategoryId())) {
					haveFavourGoodsMap.put(storeSkuBo.getId(), storeSkuBo);
					totalAmount = totalAmount.add(storeSkuBo.getTotalAmount());
				}
			}
			if (totalAmount.compareTo(BigDecimal.valueOf(0.00)) == 0 || totalAmount.compareTo(condition.getArrive()) == -1) {
				return false;
			}
			parserBo.setHaveFavourGoodsMap(haveFavourGoodsMap);
			parserBo.setTotalAmountHaveFavour(totalAmount);
		} else if (limitSkuType == LimitSkuType.LIMIT_SKU) {
			// 限制商品Id列表
			List<String> limitSkuIds = actInfoDto.getBusinessIds(ActivityBusinessType.SKU);
			// 遍历购买的商品
			for (CurrentStoreSkuBo storeSkuBo : parserBo.getCurrentSkuMap().values()) {
				if (limitSkuIds.contains(storeSkuBo.getId())) {
					haveFavourGoodsMap.put(storeSkuBo.getId(), storeSkuBo);
					totalAmount = totalAmount.add(storeSkuBo.getTotalAmount());
				}
			}
			if (totalAmount.compareTo(BigDecimal.valueOf(0.00)) == 0 || totalAmount.compareTo(condition.getArrive()) == -1) {
				return false;
			}
			parserBo.setHaveFavourGoodsMap(haveFavourGoodsMap);
			parserBo.setTotalAmountHaveFavour(totalAmount);
		}
		return isValid;
	}

	private boolean isFirstOrderUser(String userId){
		return sysBuyerFirstOrderRecordService.isExistsOrderRecord(userId) ? false
				: true;
	}
}
