package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.base.common.constant.LoggerConstants;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.order.handler.FavourSearchService;
import com.okdeer.mall.order.vo.Coupons;
import com.okdeer.mall.order.vo.Discount;
import com.okdeer.mall.order.vo.FullSubtract;
import com.okdeer.mall.order.vo.TradeOrderReq;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderResp;
import com.okdeer.mall.order.vo.TradeOrderRespDto;

/**
 * ClassName: ValidFavourFindServiceImpl 
 * @Description: 查找用户有效的优惠记录
 * @author maojj
 * @date 2016年7月22日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-14			maojj			查找用户有效的优惠记录
 *		重构V4.1			2016-07-14			maojj			查找用户有效的优惠记录增加店铺类型的判断
 *		V1.1.0			2016-09-23			tangy			代金券判断指定分类使用
 *		V1.1.0			2016-09-23			tangy			添加日志
 */
@Service
public class FavourSearchServiceImpl implements FavourSearchService {
	/**
	 * log
	 */
	private static final Logger logger = LoggerFactory.getLogger(FavourSearchServiceImpl.class);
	
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
	 * 查找用户有效的优惠记录
	 * 注：平台发起的满减、代金券活动，只有云上店可以使用，其它类型的店铺均不可使用
	 */
	@Override
	public void process(TradeOrderReqDto reqDto, TradeOrderRespDto respDto) throws Exception {
		TradeOrderResp resp = respDto.getResp();
		//构建优惠查询请求条件
		Map<String, Object> queryCondition = buildFindFavourCondition(reqDto);
		// 获取用户有效的代金券
		List<Coupons> couponList = activityCouponsRecordMapper.findValidCoupons(queryCondition);
		// 获取用户有效的折扣
		List<Discount> discountList = activityDiscountMapper.findValidDiscount(queryCondition);
		// 获取用户有效的满减
		List<FullSubtract> fullSubtractList = activityDiscountMapper.findValidFullSubtract(queryCondition);
		//Begin added by tangy  2016-9-23
		//排除不符合的代金券
		if (CollectionUtils.isNotEmpty(couponList)) {
			//商品类目id集
			List<String> spuCategoryIds = duplicateRemoval(reqDto.getContext().getSpuCategoryIds());
			List<Coupons> delCouponList = new ArrayList<Coupons>();
			//判断筛选指定分类使用代金券
			for (Coupons coupons : couponList) {
				//是否指定分类使用
				if (Constant.ONE == coupons.getIsCategory().intValue() && CollectionUtils.isNotEmpty(spuCategoryIds)) {
					int count = activityCouponsRecordMapper.findIsContainBySpuCategoryIds(spuCategoryIds, coupons.getCouponId());
					logger.info(LoggerConstants.LOGGER_DEBUG_INCOMING_METHOD, count, spuCategoryIds.size());
					if (count == Constant.ZERO || count != spuCategoryIds.size()) {
						delCouponList.add(coupons);
					}
				}
			}
			//删除不符合指定分类使用的代金券
			if (CollectionUtils.isNotEmpty(delCouponList)) {
				couponList.removeAll(delCouponList);
			}
		}
		//End added by tangy
		resp.setCouponList(couponList);
		resp.setDiscountList(discountList);
		resp.setFullSubtractList(fullSubtractList);
	}

	private Map<String, Object> buildFindFavourCondition(TradeOrderReqDto reqDto) {
		TradeOrderReq req = reqDto.getData();
		// 订单总金额
		BigDecimal totalAmount = req.getTotalAmount();
		// 订单总金额存入上下文，后续流程需要使用
		reqDto.getContext().setTotalAmount(totalAmount);
		// Begin added by maojj 2016-08-19
		// 获取店铺类型
		StoreTypeEnum storeType = reqDto.getContext().getStoreInfo().getType();
		// End added by maojj 2016-08-19

		Map<String, Object> queryCondition = new HashMap<String, Object>();
		queryCondition.put("userId", req.getUserId());
		queryCondition.put("storeId", req.getStoreId());
		queryCondition.put("totalAmount", totalAmount);
		// Begin added by maojj 2016-08-19
		queryCondition.put("storeType", storeType.ordinal());
		// End added by maojj 2016-08-19
		//Begin added by tangy  2016-9-23
		//根据店铺类型查询代金券
		if (StoreTypeEnum.CLOUD_STORE.equals(storeType)) {
			queryCondition.put("type", Constant.ONE);
		} else if (StoreTypeEnum.SERVICE_STORE.equals(storeType)) {
			queryCondition.put("type", Constant.TWO);
			queryCondition.put("addressId", req.getAddressId());
		}
		//End added by tangy
		return queryCondition;
	}

	//Begin added by tangy  2016-10-05
	/**
	 * 
	 * @Description: 去除重复
	 * @param spuCategoryIds  商品类目id
	 * @return List<String>  
	 * @author tangy
	 * @date 2016年10月5日
	 */
	private static List<String> duplicateRemoval(List<String> spuCategoryIds){
		if (CollectionUtils.isNotEmpty(spuCategoryIds)) {
			HashSet<String> hsSpuCategoryIds = new HashSet<String>(spuCategoryIds);
			spuCategoryIds = new ArrayList<String>(hsSpuCategoryIds);
		}
		return spuCategoryIds;
	}
	//End added by tangy
	
}
