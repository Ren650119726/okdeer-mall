package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.mall.activity.coupons.bo.ActivityRecordParamBo;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
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
import com.okdeer.mall.common.enums.UseClientType;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.order.constant.text.OrderTipMsgConstant;
import com.okdeer.mall.order.handler.FavourCheckService;
import com.okdeer.mall.order.vo.TradeOrderGoodsItem;
import com.okdeer.mall.order.vo.TradeOrderReq;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderRespDto;
import com.okdeer.mall.system.service.SysBuyerFirstOrderRecordService;

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
	private ActivityDiscountService activityDiscountService;
	
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
	
	@Resource
	private SysBuyerFirstOrderRecordService sysBuyerFirstOrderRecordService;
	
	@Resource
	private ActivityDiscountRecordService activityDiscountRecordService;
	
	@Resource
	private ActivityDiscountConditionMapper activityDiscountConditionMapper;
	
	/**
	 * 校验优惠券是否有效
	 */
	@Override
	public void process(TradeOrderReqDto reqDto, TradeOrderRespDto respDto) throws Exception {
		// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		ActivityTypeEnum activityType = reqDto.getData().getActivityType();		
		boolean isValid = true;
		switch (activityType) {
			case VONCHER:
				isValid = checkCoupons(reqDto,respDto);
				break;
			case FULL_REDUCTION_ACTIVITIES:
			case FULL_DISCOUNT_ACTIVITIES:
				isValid = checkDiscount(reqDto);
				if (!isValid) {
					respDto.setFlag(false);
					respDto.setMessage(OrderTipMsgConstant.PRIVILEGE_INVALID);
					respDto.getResp().setIsValid(0);
				}
				break;
			default:
				break;
		}
		

	}

	/**
	 * @Description: 校验优惠券
	 * @param recordId 代金券领取记录ID
	 * @return boolean  
	 * @author maojj
	 * @throws ServiceException 
	 * @date 2016年7月14日
	 */
	private boolean checkCoupons(TradeOrderReqDto reqDto, TradeOrderRespDto respDto) throws ServiceException {
		TradeOrderReq reqParam = reqDto.getData();
		String recordId = reqParam.getRecordId();
		ActivityCouponsRecord couponsRecord = activityCouponsRecordMapper.selectByPrimaryKey(recordId);
		// Begin V2.4 added by maojj 2017-05-31
		// 增加领取记录的校验。校验请求提供的领取记录是否是当前用户
		if(couponsRecord == null || !couponsRecord.getCollectUserId().equals(reqParam.getUserId())){
			return false;
		}
		// End V2.4 added by maojj 2017-05-31
		if (couponsRecord.getStatus() != ActivityCouponsRecordStatusEnum.UNUSED) {
			return false;
		}
		// 查询代金券
		ActivityCoupons coupons = activityCouponsMapper.selectByPrimaryKey(couponsRecord.getCouponsId());
		if (coupons.getUseClientType() == UseClientType.ONlY_APP_USE) {
			// 仅限首单用户，检查当前用户是否为首单用户。
			if (!isFirstOrderUser(reqParam.getUserId())) {
				respDto.setFlag(false);
				respDto.setMessage(OrderTipMsgConstant.PRIVILEGE_INVALID);
				respDto.getResp().setIsValid(0);
				return false;
			}
		}
		if (coupons.getIsCategory() == Constant.ONE) {
			List<String> categoryIdLimitList = goodsNavigateCategoryServiceApi
					.findNavigateCategoryByCouponId(coupons.getId());
			// 便利店限制品类
			List<String> haveFavourGoodsIdList = Lists.newArrayList();
			BigDecimal totalAmount = BigDecimal.valueOf(0.00);
			for (TradeOrderGoodsItem item : reqParam.getList()) {
				if (categoryIdLimitList.contains(item.getSpuCategoryId())) {
					haveFavourGoodsIdList.add(item.getSkuId());
					totalAmount = totalAmount.add(item.getTotalAmount());
				}
			}
			if (CollectionUtils.isEmpty(haveFavourGoodsIdList)
					|| totalAmount.compareTo(BigDecimal.valueOf(coupons.getArriveLimit())) == -1) {
				// 没有指定分类的商品或者如果享受优惠的商品总金额小于代金券的使用条件，不能使用该代金券
				respDto.setFlag(false);
				respDto.setMessage(OrderTipMsgConstant.KIND_LIMIT_OVER);
				respDto.getResp().setIsValid(0);
				return false;
			}
			reqDto.getContext().setHaveFavourGoodsIds(haveFavourGoodsIdList);
			reqDto.getContext().setTotalAmount(totalAmount);
		}
		ActivityRecordParamBo recParamBo = null;
		if(coupons.getDeviceDayLimit() != null && coupons.getDeviceDayLimit() > 0 && StringUtils.isNotEmpty(reqParam.getDeviceId())){
			// 同一设备id每天最多使用张数 0：不限，大于0有限制
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setPkId(coupons.getId());
			recParamBo.setDeviceId(reqParam.getDeviceId());
			recParamBo.setRecDate(DateUtils.getDate());
			int deviceTotalNum = activityCouponsRecordMapper.countDayFreq(recParamBo);
			if (coupons.getDeviceDayLimit().intValue() <= deviceTotalNum) {
				return false;
			}
		}
		if(coupons.getAccountDayLimit() != null && coupons.getAccountDayLimit() > 0){
			// 同一设备id每天最多使用张数 0：不限，大于0有限制
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setPkId(coupons.getId());
			recParamBo.setUserId(reqParam.getUserId());
			recParamBo.setRecDate(DateUtils.getDate());
			int userTotalNum = activityCouponsRecordMapper.countDayFreq(recParamBo);
			if (coupons.getAccountDayLimit().intValue() <= userTotalNum) {
				return false;
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
	private boolean checkDiscount(TradeOrderReqDto reqDto) throws Exception {
		TradeOrderReq reqParam = reqDto.getData();
		String activityId = reqParam.getActivityId();
		String userId = reqParam.getUserId();
		String activityItemId = reqParam.getActivityItemId();
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
		ActivityRecordParamBo recParamBo = null;
		if (limitTotalFreq > 0) {
			// 用户参与活动次数。0：不限，大于0有限制
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setUserId(userId);
			recParamBo.setPkId(activityId);
			int userTotalFreq = activityDiscountRecordService.countTotalFreq(recParamBo);
			if (userTotalFreq >= limitTotalFreq) {
				return false;
			}
		}
		if(actInfo.getDeviceDayLimit() != null && actInfo.getDeviceDayLimit() > 0 && StringUtils.isNotEmpty(reqParam.getDeviceId())){
			// 同一设备id每天最多使用张数 0：不限，大于0有限制
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setPkId(activityId);
			recParamBo.setDeviceId(reqParam.getDeviceId());
			recParamBo.setRecDate(DateUtils.getDate());
			int deviceTotalNum = activityDiscountRecordService.countTotalFreq(recParamBo);
			if (actInfo.getDeviceDayLimit().intValue() <= deviceTotalNum) {
				return false;
			}
		}
		if(actInfo.getAccountDayLimit() != null && actInfo.getAccountDayLimit() > 0){
			// 同一设备id每天最多使用张数 0：不限，大于0有限制
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setPkId(activityId);
			recParamBo.setUserId(userId);
			recParamBo.setRecDate(DateUtils.getDate());
			int userTotalNum = activityDiscountRecordService.countTotalFreq(recParamBo);
			if (actInfo.getAccountDayLimit().intValue() <= userTotalNum) {
				return false;
			}
		}
		// 商品限制
		LimitSkuType limitSkuType = actInfo.getLimitSku();
		// 参与活动的商品总金额
		BigDecimal totalAmount = BigDecimal.valueOf(0.00);
		List<String> haveFavourGoodsIdList = Lists.newArrayList();
		if (limitSkuType == LimitSkuType.LIMIT_CATEGORY) {
			// 活动限制的分类Id列表
			List<String> limitCtgIds = actInfoDto.getBusinessIds(ActivityBusinessType.SKU_CATEGORY);
			// 指定分类
			// 遍历购买的商品
			for (TradeOrderGoodsItem item : reqParam.getList()) {
				if (limitCtgIds.contains(item.getSpuCategoryId())) {
					haveFavourGoodsIdList.add(item.getSkuId());
					totalAmount = totalAmount.add(item.getTotalAmount());
				}
			}
			if (CollectionUtils.isEmpty(haveFavourGoodsIdList) || totalAmount.compareTo(condition.getArrive()) == -1) {
				return false;
			}
			reqDto.getContext().setHaveFavourGoodsIds(haveFavourGoodsIdList);
			reqDto.getContext().setTotalAmount(totalAmount);
		} else if (limitSkuType == LimitSkuType.LIMIT_SKU) {
			// 限制商品Id列表
			List<String> limitSkuIds = actInfoDto.getBusinessIds(ActivityBusinessType.SKU);
			// 遍历购买的商品
			for (TradeOrderGoodsItem item : reqParam.getList()) {
				if (limitSkuIds.contains(item.getSkuId())) {
					haveFavourGoodsIdList.add(item.getSkuId());
					totalAmount = totalAmount.add(item.getTotalAmount());
				}
			}
			if (CollectionUtils.isEmpty(haveFavourGoodsIdList)  || totalAmount.compareTo(condition.getArrive()) == -1) {
				return false;
			}
			reqDto.getContext().setHaveFavourGoodsIds(haveFavourGoodsIdList);
			reqDto.getContext().setTotalAmount(totalAmount);
		}
		return isValid;
	}
	
	private boolean isFirstOrderUser(String userId){
		return sysBuyerFirstOrderRecordService.isExistsOrderRecord(userId) ? false
				: true;
	}
}
