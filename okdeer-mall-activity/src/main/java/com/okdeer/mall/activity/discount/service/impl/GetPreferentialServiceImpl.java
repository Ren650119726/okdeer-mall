package com.okdeer.mall.activity.discount.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.entity.PreferentialVo;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.service.GetPreferentialService;
import com.okdeer.mall.activity.discount.service.IGetPreferentialServiceApi;
import com.okdeer.mall.common.consts.Constant;
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
	 * 代金券记录Mapper
	 */
	@Resource
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;

	/**
	 * 折扣、满减活动Mapper
	 */
	@Resource
	private ActivityDiscountMapper activityDiscountMapper;
	
	@Override
	public PreferentialVo findPreferentialByUser(String userId, StoreInfo storeInfo, BigDecimal totalAmount,
			List<String> skuIdList) {
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
			//判断筛选指定分类使用代金券
			for (Coupons coupons : couponList) {
				//是否指定分类使用
				if (Constant.ONE == coupons.getIsCategory().intValue()) {
					int count = activityCouponsRecordMapper.findIsContainBySpuCategoryIds(skuIdList,
							coupons.getId());
					if (count > Constant.ZERO) {
						delCouponList.add(coupons);
					} else {
						couponIds.add(coupons.getId());
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
							if (coupons.getId().equals(id)) {
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
