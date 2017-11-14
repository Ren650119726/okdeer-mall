package com.okdeer.mall.activity.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.bo.UserCouponsBo;
import com.okdeer.mall.activity.coupons.bo.UserCouponsFilterContext;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsArea;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRelationStore;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.enums.RecordCountRuleEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsAreaMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRelationStoreMapper;
import com.okdeer.mall.activity.service.CouponsFilterStrategy;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.common.enums.UseClientType;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.member.service.MemberConsigneeAddressService;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.system.service.SysBuyerFirstOrderRecordService;

/**
 * ClassName: GenericCouponsFilterStrategy 
 * @Description: 通用代金券过滤策略
 * @author maojj
 * @date 2017年11月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年11月7日				maojj
 */
public abstract class GenericCouponsFilterStrategy implements CouponsFilterStrategy {
	private static final Logger LOG = LoggerFactory.getLogger(GenericCouponsFilterStrategy.class);
	
	@Resource
	protected SysBuyerFirstOrderRecordService sysBuyerFirstOrderRecordService;
	
	@Resource
	protected ActivityCouponsRelationStoreMapper activityCouponsRelationStoreMapper;
	
	@Resource
	protected ActivityCouponsAreaMapper activityCouponsAreaMapper;
	
	@Resource
	protected MemberConsigneeAddressService memberConsigneeAddressService;
	
	@Override
	public boolean accept(UserCouponsBo userCouponsBo,FavourParamBO paramBo,UserCouponsFilterContext filterContext){
		Assert.notNull(filterContext, "代金券过滤上下文对象不能为空");
		filterContext.refresh(paramBo);
		ActivityCoupons couponsInfo = userCouponsBo.getCouponsInfo();
		// 代金券id
		String couponsId = couponsInfo.getId();
		// 可用的代金券Id列表
		List<String> enabledCouponsIdList = filterContext.getEnabledCouponsIdList();
		if(CollectionUtils.isNotEmpty(enabledCouponsIdList) && enabledCouponsIdList.contains(couponsId)){
			// 如果代金券已经被被检查过，且判定代金券为可用，则直接返回，不用重新检查。
			return true;
		}
		// 不可用的代金券id列表
		List<String> excludeCouponsIdList = filterContext.getExcludeCouponsIdList();
		if(CollectionUtils.isNotEmpty(excludeCouponsIdList) && excludeCouponsIdList.contains(couponsId)){
			// 如果代金券已经被检查过，且判定代金券为不可用，直接返回，不用重新检查
			return false;
		}
		// 不可用的代金券活动id列表
		List<String> excludeCouponsActIdList = filterContext.getExcludeCouponsActIdList();
		if(CollectionUtils.isNotEmpty(excludeCouponsActIdList) && excludeCouponsActIdList.contains(couponsInfo.getActivityId())){
			return false;
		}
		// 检查是否到达订单金额限制
		if(!isArriveOrderAmount(paramBo.getTotalAmount(), BigDecimal.valueOf(couponsInfo.getArriveLimit()))){
			LOG.debug("用户{}使用代金券{}订单金额为{}未达到金额上限",paramBo.getUserId(),couponsId,paramBo.getTotalAmount());
			return false;
		}
		// 检查是否超过订单类型限制
		if (isOutOfLimitOrderType(paramBo, couponsInfo)) {
			 LOG.debug("订单类型{}渠道{}使用代金券{}超出订单类型限制",paramBo.getOrderType(),paramBo.getChannel(),couponsId);
			 return false;
		}
		// 检查是否超出客户端限制
		if(isOutOfLimitClientType(paramBo.getClientType(),couponsInfo.getUseClientType())){
			LOG.debug("客户端{}使用代金券{}超出客户端限制",paramBo.getClientType(),couponsId);
			return false;
		}
		// 检查是否超出用户类型限制
		if(isOutOfLimitUserType(paramBo.getUserId(), filterContext, couponsInfo.getUseUserType())){
			LOG.debug("用户{}使用代金券{}超出用户类型限制",paramBo.getUserId(),couponsId);
			return false;
		}
		// 检查是否超出代金券使用范围
		if(isOutOfLimitRange(paramBo, couponsInfo,filterContext)){
			LOG.debug("用户{}在店铺{}使用代金券{}超出使用范围限制",paramBo.getUserId(),paramBo.getStoreId(),couponsId);
			return false;
		}
		// 是否超出类目限制
		if(isOutOfLimitCategory(paramBo, couponsInfo,filterContext)){
			LOG.debug("用户{}在店铺{}使用代金券{}超出类目限制",paramBo.getUserId(),paramBo.getStoreId(),couponsId);
			return false;
		}
		// 剔除特价商品
		excludeLowSku(paramBo.getOrderType(),paramBo.getGoodsList(),filterContext);
		// 重新校验享受优惠的订单金额是否达到优惠限制
		// 检查是否到达订单金额限制
		if(!isArriveOrderAmount(filterContext.getEnjoyFavourAmount(), BigDecimal.valueOf(couponsInfo.getArriveLimit()))){
			return false;
		}
		// 检查代金券用户日使用限制
		if(isOutOfLimitUserDayUseInCoupons(couponsInfo.getAccountDayLimit(), paramBo, couponsId)){
			LOG.debug("用户{}使用代金券{}超出代金券账户日使用限制",paramBo.getUserId(),couponsId);
			return false;
		}
		// 检查代金券设备日使用限制
		if(isOutOfLimitDeviceDayUseInCoupons(couponsInfo.getDeviceDayLimit(), paramBo, couponsId)){
			LOG.debug("用户{}在设备{}上使用代金券{}超出代金券设备日使用限制",paramBo.getUserId(),paramBo.getDeviceId(),couponsId);
			return false;
		}
		// 代金券活动信息
		ActivityCollectCoupons couponsActInfo = userCouponsBo.getCouponsActInfo();
		// 检查代金券活动日使用限制
		if(isOutOfLimitUserDayUseInAct(couponsActInfo.getAccountDayLimit(), paramBo, couponsActInfo.getId(), filterContext)){
			LOG.debug("用户{}使用代金券{}超出代金券活动账户日使用限制",paramBo.getUserId(),couponsId);
			return false;
		}
		// 检查代金券活动设备日使用限制
		if(isOutOfLimitDeviceDayUseInAct(couponsActInfo.getDeviceDayLimit(), paramBo, couponsActInfo.getId(), filterContext)){
			LOG.debug("用户{}在设备{}上使用代金券{}超出代金券活动设备日使用限制",paramBo.getUserId(),paramBo.getDeviceId(),couponsId);
			return false;
		}
		return true;
	}

	/**
	 * @Description: 是否超出订单类型限制
	 * @param orderType
	 * @param orderSource
	 * @param couponsInfo
	 * @return   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	public abstract boolean isOutOfLimitOrderType(FavourParamBO paramBo,ActivityCoupons couponsInfo);

	/**
	 * @Description: 是否超出客户端类型限制
	 * @param clientType
	 * @param limitClientType
	 * @return   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	public boolean isOutOfLimitClientType(UseClientType clientType,UseClientType limitClientType){
		return limitClientType != UseClientType.ALLOW_All && limitClientType != clientType;
	}
	
	/**
	 * @Description: 是否超出用户类型限制（是否限首单用户）
	 * @param userId
	 * @param filterContext
	 * @param limitUserType
	 * @return   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	public boolean isOutOfLimitUserType(String userId,UserCouponsFilterContext filterContext,UseUserType limitUserType){
		if(limitUserType == UseUserType.ALLOW_All){
			// 不限
			return false;
		}
		// 限制首单用户
		if(filterContext.isFirstOrderUser() == null){
			// 如果缓存中不存在，则判定首单用户
			boolean isFirstOrderUser = !sysBuyerFirstOrderRecordService.isExistsOrderRecord(userId);
			filterContext.setFirstOrderUser(isFirstOrderUser);
		}
		return !filterContext.isFirstOrderUser();
	}
	
	/**
	 * @Description: 是否达到用户订单金额限制
	 * @param orderAmount
	 * @param limitAmount
	 * @return   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	public boolean isArriveOrderAmount(BigDecimal orderAmount,BigDecimal limitAmount){
		// 如果订单金额为0，无需享受优惠
		return orderAmount.compareTo(BigDecimal.ZERO) > 0 && orderAmount.compareTo(limitAmount) >= 0;
	}
	
	/**
	 * @Description: 是否超出限制范围
	 * @param storeId
	 * @param userAddrId
	 * @param couponsInfo
	 * @return   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	public boolean isOutOfLimitRange(FavourParamBO paramBo,ActivityCoupons couponsInfo,UserCouponsFilterContext filterContext){
		AreaType limitAreaType = couponsInfo.getAreaType();
		if(limitAreaType == AreaType.national){
			return false;
		}
		if(limitAreaType == AreaType.store){
			// 范围是否包含.0:包含，1：不包含
			Integer areaTypeInvert = couponsInfo.getAreaTypeInvert();
			// 指定店铺
			ActivityCouponsRelationStore storeCouponsRel = activityCouponsRelationStoreMapper
					.findByStoreIdAndCouponsId(paramBo.getStoreId(), couponsInfo.getId());
			return (areaTypeInvert == 0 && storeCouponsRel == null)
					|| (areaTypeInvert == 1 && storeCouponsRel != null);
		}
		if(limitAreaType == AreaType.area){
			// 指定范围
			return isOutOfLimitAreaRange(paramBo, couponsInfo,filterContext);
		}
		return false;
	}
	
	/**
	 * @Description: 是否限制区域范围
	 * @param storeId
	 * @param userAddrId
	 * @param couponsId
	 * @return   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	public boolean isOutOfLimitAreaRange(FavourParamBO paramBo,ActivityCoupons couponsInfo,UserCouponsFilterContext filterContext){
		// 具体实现留给子类实现。需要实现该方法的子类代金券类型为：便利店专用代金券，服务店专用代金券，通用折扣券
		return false;
	}
	
	protected boolean isOutOfLimitAreaRange(String proviceId,String cityId,ActivityCoupons couponsInfo){
		// 构建查询条件
		ActivityCouponsArea areaParam = new ActivityCouponsArea();
		areaParam.setCouponsId(couponsInfo.getId());
		areaParam.setProvinceId(proviceId);
		areaParam.setCityId(cityId);
		// 查询所属城市、身份是否在限制区域中
		List<ActivityCouponsArea> limitAreaList = activityCouponsAreaMapper.findLimitAreaList(areaParam);
		// 活动限制范围选择类型：0：正选，1：反选
		Integer areaTypeInvert = couponsInfo.getAreaTypeInvert();
		// 如果活动指定为正选，则不包含当前地址则超出，如果为反选，则包含当前地址则超出。
		return (areaTypeInvert == 0 && CollectionUtils.isEmpty(limitAreaList)) 
				|| (areaTypeInvert == 1 && CollectionUtils.isNotEmpty(limitAreaList));
	}
	
	/**
	 * @Description: 是否超出限制类目
	 * @param paramBo
	 * @param isLimitCategory
	 * @param couponsId
	 * @return   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	public boolean isOutOfLimitCategory(FavourParamBO paramBo,ActivityCoupons couponsInfo,UserCouponsFilterContext filterContext){
		if(couponsInfo.getIsCategory() == Constant.ONE){
			// 指定分类
			return checkLimitCategory(paramBo,couponsInfo,filterContext);
		}
		return false;
	}
	
	/**
	 * @Description: 检查限制类目
	 * @param paramBo
	 * @param couponsId
	 * @return   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	public boolean checkLimitCategory(FavourParamBO paramBo,ActivityCoupons couponsInfo,UserCouponsFilterContext filterContext){
		// 具体实现留给子类实现。需要实现该方法的子类代金券类型为：便利店专用代金券，服务店专用代金券，通用折扣券
		return false;
	}
	
	/**
	 * @Description: 排除特价商品（特价商品不参与优惠券）
	 * @param orderType
	 * @param goodsList
	 * @param filterContext   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	public void excludeLowSku(OrderTypeEnum orderType,List<PlaceOrderItemDto> goodsList,UserCouponsFilterContext filterContext){
		// 只有线上便利店订单需要进行处理
		if(orderType != OrderTypeEnum.PHYSICAL_ORDER || CollectionUtils.isEmpty(goodsList)){
			return;
		}
		BigDecimal enjoyFavourAmount = filterContext.getEnjoyFavourAmount();
		// 从享受优惠的金额中扣除参与特价的商品总金额
		for(PlaceOrderItemDto goodsItem : goodsList){
			if(goodsItem.getSkuActType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
				enjoyFavourAmount = enjoyFavourAmount
						.subtract(goodsItem.getSkuPrice().multiply(BigDecimal.valueOf(goodsItem.getSkuActQuantity())));
			}
		}
		filterContext.setEnjoyFavourAmount(enjoyFavourAmount);
	}
	
	/**
	 * @Description: 是否超出代金券用户日使用限制
	 * @param limitUseNum
	 * @param paramBo
	 * @param couponsId
	 * @return   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	public boolean isOutOfLimitUserDayUseInCoupons(Integer limitUseNum,FavourParamBO paramBo,String couponsId){
		if(limitUseNum > 0){
			// 代金券每日限制账号使用次数
			if(limitUseNum.compareTo(paramBo.findCountNum(RecordCountRuleEnum.COUPONS_BY_USER, couponsId)) < 1){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @Description: 是否超出代金券设备日使用限制
	 * @param limitUseNum
	 * @param paramBo
	 * @param couponsId
	 * @return   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	public boolean isOutOfLimitDeviceDayUseInCoupons(Integer limitUseNum,FavourParamBO paramBo,String couponsId){
		if(limitUseNum > 0){
			// 代金券每日限制账号使用次数
			if(limitUseNum.compareTo(paramBo.findCountNum(RecordCountRuleEnum.COUPONS_BY_DEVICE, couponsId)) < 1){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @Description: 是否超出代金券活动日使用限制
	 * @param limitUseNum
	 * @param paramBo
	 * @param couponsActId
	 * @param filterContext
	 * @return   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	public boolean isOutOfLimitUserDayUseInAct(Integer limitUseNum, FavourParamBO paramBo, String couponsActId,
			UserCouponsFilterContext filterContext) {
		if(limitUseNum > 0){
			// 代金券每日限制账号使用次数
			if(limitUseNum.compareTo(paramBo.findCountNum(RecordCountRuleEnum.COUPONS_COLLECT_BY_USER, couponsActId)) < 1){
				// 活动不可用，将活动缓存到不可用列表中
				if(filterContext.getExcludeCouponsActIdList() == null){
					filterContext.setExcludeCouponsActIdList(Lists.newArrayList());
				}
				filterContext.getExcludeCouponsActIdList().add(couponsActId);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @Description: 是否超出代金券活动设备日使用限制
	 * @param limitUseNum
	 * @param paramBo
	 * @param couponsActId
	 * @param filterContext
	 * @return   
	 * @author maojj
	 * @date 2017年11月9日
	 */
	public boolean isOutOfLimitDeviceDayUseInAct(Integer limitUseNum, FavourParamBO paramBo, String couponsActId,
			UserCouponsFilterContext filterContext) {
		if(limitUseNum > 0){
			// 代金券每日限制账号使用次数
			if(limitUseNum.compareTo(paramBo.findCountNum(RecordCountRuleEnum.COUPONS_COLLECT_BY_DEVICE, couponsActId)) < 1){
				// 活动不可用，将活动缓存到不可用列表中
				if(filterContext.getExcludeCouponsActIdList() == null){
					filterContext.setExcludeCouponsActIdList(Lists.newArrayList());
				}
				filterContext.getExcludeCouponsActIdList().add(couponsActId);
				return true;
			}
		}
		return false;
	}
}
