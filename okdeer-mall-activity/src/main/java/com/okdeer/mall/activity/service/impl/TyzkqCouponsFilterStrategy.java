package com.okdeer.mall.activity.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.bo.UserCouponsFilterContext;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsCategoryMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;

@Service
public class TyzkqCouponsFilterStrategy extends GenericCouponsFilterStrategy {

	@Resource
	private ActivityCouponsCategoryMapper activityCouponsCategoryMapper;
	
	@Reference(version = "1.0.0", check = false)
	protected StoreInfoServiceApi storeInfoServiceApi;
	
	@Reference(version = "1.0.0", check = false)
	protected GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	@Override
	public boolean isOutOfLimitOrderType(FavourParamBO paramBo, ActivityCoupons couponsInfo) {
		// 订单类型
		OrderTypeEnum orderType = paramBo.getOrderType();
		// 订单来源
		OrderResourceEnum orderSource = paramBo.getChannel();
		// app版本
		String version = paramBo.getClientVersion();
		if (StringUtils.isNotEmpty(version)
				&& "V2.6.4".compareTo(version.substring(0, version.lastIndexOf(".") + 2)) > 0) {
			// 2.6.4以前的版本不能享受折扣优惠
			return true;
		}
		// 可用订单类型，多个之间逗号分隔：0便利店订单 1服务店订单 2会员卡订单 3扫码购订单
		String limitType = couponsInfo.getOrderTypes();
		// 将订单类型转换为限制类型字符串
		String orderTypeMapLimitType = null;
		switch (orderType) {
			case PHYSICAL_ORDER:
				if (orderSource == OrderResourceEnum.MEMCARD) {
					orderTypeMapLimitType = "2";
				} else if (orderSource == OrderResourceEnum.SWEEP) {
					orderTypeMapLimitType = "3";
				} else {
					orderTypeMapLimitType = "0";
				}
				break;
			case SERVICE_ORDER:
			case SERVICE_STORE_ORDER:
			case STORE_CONSUME_ORDER:
				orderTypeMapLimitType = "1";
				break;
			default:
				break;
		}
		return StringUtils.isEmpty(orderTypeMapLimitType) || !limitType.contains(orderTypeMapLimitType);
	}

	@Override
	public boolean isOutOfLimitAreaRange(FavourParamBO paramBo, ActivityCoupons couponsInfo,
			UserCouponsFilterContext filterContext) {
		// 省id
		String provinceId = null;
		// 市id
		String cityId = null;
		switch (paramBo.getOrderType()) {
			case PHYSICAL_ORDER:
				// 便利店，检查店铺地址信息
				StoreInfo storeInfo = filterContext.getStoreInfo();
				if(storeInfo == null){
					storeInfo = storeInfoServiceApi.findById(paramBo.getStoreId());
					filterContext.setStoreInfo(storeInfo);
				}
				if (storeInfo != null) {
					provinceId = storeInfo.getProvinceId();
					cityId = storeInfo.getCityId();
				}
				break;
			case SERVICE_ORDER:
			case SERVICE_STORE_ORDER:
			case STORE_CONSUME_ORDER:
				// 服务店，检查服务地址信息。其中如果是到店消费的订单，addressId对应为店铺默认地址id
				MemberConsigneeAddress addrInfo = filterContext.getAddrInfo();
				if(addrInfo == null){
					addrInfo =  memberConsigneeAddressService.findById(paramBo.getAddressId());
					filterContext.setAddrInfo(addrInfo);
				}
				if (addrInfo != null) {
					provinceId = addrInfo.getProvinceId();
					cityId = addrInfo.getCityId();
				}
				break;
			default:
				break;
		}
		return isOutOfLimitAreaRange(provinceId, cityId, couponsInfo);
	}

	@Override
	public boolean checkLimitCategory(FavourParamBO paramBo, ActivityCoupons couponsInfo,
			UserCouponsFilterContext filterContext) {
		// 查询存在于类目限制的商品类目
		List<String> limitCategoryIds = activityCouponsCategoryMapper.findLimitCategoryList(couponsInfo.getId(),
				paramBo.getSpuCategoryIds());
		// 类目设置类型0：正选，1：反选
		Integer chooseType = couponsInfo.getCategoryInvert();
		// 订单类型
		OrderTypeEnum orderType = paramBo.getOrderType();
		if (orderType == OrderTypeEnum.PHYSICAL_ORDER && (paramBo.getChannel() == OrderResourceEnum.MEMCARD
				|| paramBo.getChannel() == OrderResourceEnum.SWEEP)) {
			return false;
		}
		if (orderType == OrderTypeEnum.SERVICE_ORDER || orderType == OrderTypeEnum.SERVICE_STORE_ORDER
				|| orderType == OrderTypeEnum.STORE_CONSUME_ORDER) {
			// 如果是服务店订单，不能超过分类限制
			if(CollectionUtils.isEmpty(paramBo.getSpuCategoryIds())){
				List<GoodsStoreSku> currentStoreSkuList = goodsStoreSkuServiceApi
						.findStoreSkuForOrder(paramBo.getSkuIdList());
				if (CollectionUtils.isNotEmpty(currentStoreSkuList)) {
					Set<String> spuCategoryIds = currentStoreSkuList.stream().map(GoodsStoreSku::getSpuCategoryId)
							.collect(Collectors.toSet());
					paramBo.setSpuCategoryIds(spuCategoryIds);
				}
			}
			return (chooseType == 0 && limitCategoryIds.size() != paramBo.getSpuCategoryIds().size())
					|| (chooseType == 1 && CollectionUtils.isNotEmpty(limitCategoryIds));
		}
		if (orderType == OrderTypeEnum.PHYSICAL_ORDER) {
			// 不参与分类的商品总金额
			BigDecimal excludeAmount = BigDecimal.ZERO;
			// 参与商品分类的商品数量
			int joinCategoryNum = 0;
			for (PlaceOrderItemDto goodsItem : paramBo.getGoodsList()) {
				if ((Integer.valueOf(0).compareTo(chooseType) == 0
						&& limitCategoryIds.contains(goodsItem.getSpuCategoryId()))
						|| (Integer.valueOf(1).compareTo(chooseType)) == 0
								&& !limitCategoryIds.contains(goodsItem.getSpuCategoryId())) {
					// 正选，则要包含商品分类，反选则要不包含商品分类
					if (goodsItem.getSkuActType() != ActivityTypeEnum.LOW_PRICE.ordinal()
							|| (goodsItem.getSkuActType() == ActivityTypeEnum.LOW_PRICE.ordinal()
									&& goodsItem.getQuantity() > goodsItem.getSkuActQuantity())) {
						joinCategoryNum++;
					}
				} else {
					filterContext.getEnjoyFavourSkuIdList().remove(goodsItem.getStoreSkuId());
					excludeAmount = excludeAmount
							.add(goodsItem.getSkuPrice().multiply(BigDecimal.valueOf(goodsItem.getQuantity())));
				}
			}
			if (joinCategoryNum == 0) {
				// 没有指定分类的商品，超出分类限制
				return true;
			}
			// 去除不在商品分类中的金额，刷新参与总金额。此处不去除在分类中且是特价的商品金额。特价商品留在后面的步骤进行处理。
			filterContext.setEnjoyFavourAmount(filterContext.getEnjoyFavourAmount().subtract(excludeAmount));
			return false;
		}
		return true;
	}
}
