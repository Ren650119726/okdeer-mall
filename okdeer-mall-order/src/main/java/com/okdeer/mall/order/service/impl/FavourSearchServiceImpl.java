package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.mall.order.vo.Coupons;
import com.okdeer.mall.order.vo.Discount;
import com.okdeer.mall.order.vo.FullSubtract;
import com.okdeer.mall.order.vo.TradeOrderReq;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderResp;
import com.okdeer.mall.order.vo.TradeOrderRespDto;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.order.service.FavourSearchService;

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
 */
@Service
public class FavourSearchServiceImpl implements FavourSearchService {

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
		Map<String,Object> queryCondition = buildFindFavourCondition(reqDto);
		// 获取用户有效的代金券
		List<Coupons> couponList = activityCouponsRecordMapper.findValidCoupons(queryCondition);
		// 获取用户有效的折扣
		List<Discount> discountList = activityDiscountMapper.findValidDiscount(queryCondition);
		// 获取用户有效的满减
		List<FullSubtract> fullSubtractList = activityDiscountMapper.findValidFullSubtract(queryCondition);

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
		return queryCondition;
	}
	
	
}
