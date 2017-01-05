package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.base.common.constant.LoggerConstants;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.entity.PreferentialVo;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.service.IGetPreferentialServiceApi;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.enums.UseClientType;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.service.MemberConsigneeAddressService;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.service.GetPreferentialService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.vo.Coupons;
import com.okdeer.mall.order.vo.Discount;
import com.okdeer.mall.order.vo.FullSubtract;

/**
 * 
 * ClassName: GetPreferentialServiceImpl 
 * @Description: 获取优惠列表
 * @author tangy
 * @date 2016年9月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.1.0          2016年9月29日                               tangy             新增
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.discount.service.IGetPreferentialServiceApi")
public class GetPreferentialServiceImpl implements GetPreferentialService, IGetPreferentialServiceApi {
	/**
	 * log
	 */
	private static final Logger logger = LoggerFactory.getLogger(GetPreferentialServiceImpl.class);

	/**
	 * 店铺商品Api
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;
	
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
	 * 收货地址服务接口
	 */
	@Resource
	private MemberConsigneeAddressService memberConsigneeAddressService;
	
	/**
	 * 导航类目
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsNavigateCategoryServiceApi goodsNavigateCategoryServiceApi;

	@Resource
	private TradeOrderService tradeOrderService;
	
	@Override
	public PreferentialVo findPreferentialByUser(String userId, StoreInfo storeInfo, BigDecimal totalAmount,
			List<String> skuIdList, String addressId,OrderResourceEnum orderResourceEnum) throws Exception {
		PreferentialVo preferentialVo = new PreferentialVo();
		StoreTypeEnum storeType = storeInfo.getType();
		Map<String, Object> queryCondition = new HashMap<String, Object>();
		queryCondition.put("userId", userId);
		queryCondition.put("storeId", storeInfo.getId());
		queryCondition.put("totalAmount", totalAmount);
		queryCondition.put("storeType", storeType.ordinal());
		//根据店铺类型查询代金券
		if (StoreTypeEnum.CLOUD_STORE.equals(storeType)) {
			queryCondition.put("type", Constant.ONE);
		} else if (StoreTypeEnum.SERVICE_STORE.equals(storeType)) {
			queryCondition.put("type", Constant.TWO);
			queryCondition.put("addressId", addressId);
			//到店服务商品根据店铺地址查询
			if (CollectionUtils.isNotEmpty(skuIdList) && skuIdList.size() == 1) {
				GoodsStoreSku sku = goodsStoreSkuServiceApi.getById(skuIdList.get(0));
				if (SpuTypeEnum.fwdDdxfSpu.equals(sku.getSpuTypeEnum())) {
					MemberConsigneeAddress address = memberConsigneeAddressService.findByStoreId(storeInfo.getId());
					if (address != null) {
						queryCondition.put("addressId", address.getId());
					}
				}
			}

		}
		if(orderResourceEnum != null){
			if(orderResourceEnum == OrderResourceEnum.WECHAT){
				queryCondition.put("useClientType", UseClientType.ONlY_WECHAT_USE);
			}else if(orderResourceEnum == OrderResourceEnum.YSCAPP){
				queryCondition.put("useClientType", UseClientType.ONlY_APP_USE);
			}
		}
		
		// 获取用户有效的代金券
		List<Coupons> couponList = activityCouponsRecordMapper.findValidCoupons(queryCondition);
		// 获取用户有效的折扣
		List<Discount> discountList = activityDiscountMapper.findValidDiscount(queryCondition);
		// 获取用户有效的满减
		List<FullSubtract> fullSubtractList = activityDiscountMapper.findValidFullSubtract(queryCondition);
		//排除不符合的代金券
		if (CollectionUtils.isNotEmpty(couponList) && CollectionUtils.isNotEmpty(skuIdList)) {
			List<Coupons> delCouponList = new ArrayList<Coupons>();
			List<String> couponIds = new ArrayList<String>();
			List<GoodsStoreSku> goodsStoreSkus = goodsStoreSkuServiceApi.findStoreSkuForOrder(skuIdList);
			Set<String> spuCategoryIds = new HashSet<String>();
			for (GoodsStoreSku goodsStoreSku : goodsStoreSkus) {
				spuCategoryIds.add(goodsStoreSku.getSpuCategoryId());
			}
			//先确定新用户专享代金券是否能使用 tuzhd
			boolean isNewUser = tradeOrderService.checkUserUseCoupons((String)queryCondition.get("userId"));
			//判断筛选指定分类使用代金券
			for (Coupons coupons : couponList) {
				//如果此券为新用户专享。且不能使用 该券不显示 tuzhd
				if(!isNewUser && UseUserType.ONlY_NEW_USER == coupons.getUseUserType()){
					delCouponList.add(coupons);
					continue;
				}
				
				//是否指定分类使用
				if (Constant.ONE == coupons.getIsCategory().intValue()) {
					int count = 0;
					if (StoreTypeEnum.CLOUD_STORE.equals(storeType)) {
						//count = activityCouponsRecordMapper.findIsContainBySpuCategoryIds(spuCategoryIds, coupons.getCouponId());
						List<String> ids = goodsNavigateCategoryServiceApi.findNavigateCategoryByCouponId(coupons.getCouponId());
						boolean bool = ids.containsAll(spuCategoryIds);
						if (bool) {
							count = spuCategoryIds.size();
						}
					} else if (StoreTypeEnum.SERVICE_STORE.equals(storeType)) {
						count = activityCouponsRecordMapper.findServerBySpuCategoryIds(spuCategoryIds, coupons.getCouponId());
					}
					logger.info(LoggerConstants.LOGGER_DEBUG_INCOMING_METHOD, count, spuCategoryIds.size());
					if (count == Constant.ZERO || count != spuCategoryIds.size()) {
						delCouponList.add(coupons);
					} else {
						couponIds.add(coupons.getCouponId());
					}
				}
			}
			//删除不符合指定分类使用的代金券
			if (CollectionUtils.isNotEmpty(delCouponList)) {
				couponList.removeAll(delCouponList);
			}
			
			//指定类目返回类目名称集
			if (CollectionUtils.isNotEmpty(couponIds)) {
				List<Map<String, Object>> retMap = activityCouponsRecordMapper.findByCategoryNames(couponIds);
				if (retMap != null) {
					for (Map<String, Object> map : retMap) {
						String id = (String) map.get("couponId");
						String categoryNames = (String) map.get("categoryNames");
						for (Coupons coupons : couponList) {
							if (coupons.getCouponId().equals(id)) {
								coupons.setCategoryNames(categoryNames);
								break;
							}
						}
					}
				}
			}
			
		}

		preferentialVo.setCouponList(couponList);
		preferentialVo.setDiscountList(discountList);
		preferentialVo.setFullSubtractList(fullSubtractList);
		return preferentialVo;
	}
}
