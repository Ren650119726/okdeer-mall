package com.okdeer.mall.activity.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.bo.UserCouponsFilterContext;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;

@Service("bldCouponsFilterStrategy")
public class BldCouponsFilterStrategy extends GenericCouponsFilterStrategy {
	
	private static final Logger LOG = LoggerFactory.getLogger(BldCouponsFilterStrategy.class);
	
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoServiceApi;

	@Reference(version = "1.0.0", check = false)
	private GoodsNavigateCategoryServiceApi goodsNavigateCategoryServiceApi;

	@Override
	public boolean isOutOfLimitOrderType(FavourParamBO paramBo, ActivityCoupons couponsInfo) {
		// 便利店代金券隐藏约束：只能支持线上便利店使用，不能支持线下便利店订单如：扫码购、会员卡、微信小程序
		OrderResourceEnum orderChannel = paramBo.getChannel();
		return paramBo.getOrderType() != OrderTypeEnum.PHYSICAL_ORDER || orderChannel == OrderResourceEnum.MEMCARD
				|| orderChannel == OrderResourceEnum.SWEEP || orderChannel == OrderResourceEnum.WECHAT_MIN;
	}

	@Override
	public boolean isOutOfLimitAreaRange(FavourParamBO paramBo, ActivityCoupons couponsInfo,
			UserCouponsFilterContext filterContext) {
		// 指定地区范围
		StoreInfo storeInfo = filterContext.getStoreInfo();
		if(storeInfo == null){
			storeInfo = storeInfoServiceApi.findById(paramBo.getStoreId());
			filterContext.setStoreInfo(storeInfo);
		}
		return isOutOfLimitAreaRange(storeInfo.getProvinceId(), storeInfo.getCityId(), couponsInfo);
	}
	
	@Override
	public boolean checkLimitCategory(FavourParamBO paramBo,ActivityCoupons couponsInfo,UserCouponsFilterContext filterContext){
		// 查询便利店指定的商品分类
		try {
			List<String> limitCtgIds = goodsNavigateCategoryServiceApi.findNavigateCategoryByCouponId(couponsInfo.getId());
			// 不参与分类的商品总金额
			BigDecimal excludeAmount = BigDecimal.ZERO;
			// 类目设置类型0：正选，1：反选
			Integer chooseType = couponsInfo.getCategoryInvert();
			// 参与商品分类的商品数量
			int joinCategoryNum = 0;
			for (PlaceOrderItemDto goodsItem : paramBo.getGoodsList()) {
				if((Integer.valueOf(0).compareTo(chooseType) == 0 && limitCtgIds.contains(goodsItem.getSpuCategoryId())) 
						|| (Integer.valueOf(1).compareTo(chooseType)) == 0 && !limitCtgIds.contains(goodsItem.getSpuCategoryId())){
					// 正选，则要包含商品分类，反选则要不包含商品分类
					if (goodsItem.getSkuActType() != ActivityTypeEnum.LOW_PRICE.ordinal()
							|| (goodsItem.getSkuActType() == ActivityTypeEnum.LOW_PRICE.ordinal()
									&& goodsItem.getQuantity() > goodsItem.getSkuActQuantity())) {
						joinCategoryNum++;
					}
				}else{
					filterContext.getEnjoyFavourSkuIdList().remove(goodsItem.getStoreSkuId());
					excludeAmount = excludeAmount.add(goodsItem.getSkuPrice().multiply(BigDecimal.valueOf(goodsItem.getQuantity())));
				}
			}
			if(joinCategoryNum == 0){
				// 没有指定分类的商品，超出分类限制
				return true;
			}
			// 去除不在商品分类中的金额，刷新参与总金额。此处不去除在分类中且是特价的商品金额。特价商品留在后面的步骤进行处理。
			filterContext.setEnjoyFavourAmount(filterContext.getEnjoyFavourAmount().subtract(excludeAmount));
		} catch (ServiceException e) {
			LOG.error("根据代金券id{}查询代金券限制的导航类目发生异常{}",couponsInfo.getId(),e);
			return true;
		}

		return false;
	}
}
