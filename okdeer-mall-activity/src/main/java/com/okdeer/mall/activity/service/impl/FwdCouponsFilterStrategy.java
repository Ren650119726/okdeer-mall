package com.okdeer.mall.activity.service.impl;

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
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.bo.UserCouponsFilterContext;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.order.enums.OrderTypeEnum;

@Service("fwdCouponsFilterStrategy")
public class FwdCouponsFilterStrategy extends GenericCouponsFilterStrategy {
	
	@Resource
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;
	
	@Reference(version = "1.0.0", check = false)
	protected GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	@Override
	public boolean isOutOfLimitOrderType(FavourParamBO paramBo, ActivityCoupons couponsInfo) {
		return paramBo.getOrderType() != OrderTypeEnum.SERVICE_ORDER
				&& paramBo.getOrderType() != OrderTypeEnum.SERVICE_STORE_ORDER
				&& paramBo.getOrderType() != OrderTypeEnum.STORE_CONSUME_ORDER;
	}

	@Override
	public boolean isOutOfLimitAreaRange(FavourParamBO paramBo, ActivityCoupons couponsInfo,
			UserCouponsFilterContext filterContext) {
		// 服务店，检查用户服务地址
		if(StringUtils.isEmpty(paramBo.getAddressId())){
			return true;
		}
		// 根据用户地址id查询用户地址信息
		MemberConsigneeAddress userAddrInfo = filterContext.getAddrInfo(); 
		if(userAddrInfo == null){
			userAddrInfo = memberConsigneeAddressService.findById(paramBo.getAddressId());
			filterContext.setAddrInfo(userAddrInfo);
		}
		return isOutOfLimitAreaRange(userAddrInfo.getProvinceId(), userAddrInfo.getCityId(), couponsInfo);
	}
	
	@Override
	public boolean checkLimitCategory(FavourParamBO paramBo,ActivityCoupons couponsInfo,UserCouponsFilterContext filterContext){
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
		// 1、服务店代金券指定分类为商品的三级分类。2、订单商品必须不得超出指定的分类。（便利店可以。原因是服务店无法做到兼容。原有接口只提供了商品的总金额，未提供各个商品的购买数量）
		int count = activityCouponsRecordMapper.findServerBySpuCategoryIds(paramBo.getSpuCategoryIds(),
				couponsInfo.getId());
		return count != paramBo.getSpuCategoryIds().size();
	}
}
