package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.base.common.constant.LoggerConstants;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.vo.UserAddressVo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.vo.Coupons;
import com.okdeer.mall.order.vo.Discount;
import com.okdeer.mall.order.vo.FullSubtract;

/**
 * ClassName: FindFavourServiceImpl 
 * @Description: TODO
 * @author maojj
 * @date 2017年1月5日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.0 			2017年1月5日				maojj
 */
@Service("findFavourService")
public class FindFavourServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto>{

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
	 * 导航类目
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsNavigateCategoryServiceApi goodsNavigateCategoryServiceApi;
	
	/**
	 * 查找用户有效的优惠记录
	 * 注：平台发起的满减、代金券活动，只有云上店可以使用，其它类型的店铺均不可使用
	 */
	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		if(parserBo.isLowFavour()){
			// 参与低价之后，不能参与任何其他优惠活动
			return;
		}
		
		//构建优惠查询请求条件
		Map<String, Object> queryCondition = buildFindFavourCondition(paramDto,resp.getData());
		// 获取用户有效的代金券
		List<Coupons> couponList = activityCouponsRecordMapper.findValidCoupons(queryCondition);
		// 获取用户有效的折扣
		List<Discount> discountList = activityDiscountMapper.findValidDiscount(queryCondition);
		// 获取用户有效的满减
		List<FullSubtract> fullSubtractList = activityDiscountMapper.findValidFullSubtract(queryCondition);
		//排除不符合的代金券
		if (CollectionUtils.isNotEmpty(couponList)) {
			//商品类目id集
			Set<String> spuCategoryIds = ((StoreSkuParserBo)paramDto.get("parserBo")).getCategoryIdSet();
			List<Coupons> delCouponList = new ArrayList<Coupons>();
			//判断筛选指定分类使用代金券
			for (Coupons coupons : couponList) {
				//是否指定分类使用
				if (Constant.ONE == coupons.getIsCategory().intValue() && CollectionUtils.isNotEmpty(spuCategoryIds)) {
					List<String> ids = goodsNavigateCategoryServiceApi.findNavigateCategoryByCouponId(coupons.getCouponId());
					boolean bool = ids.containsAll(spuCategoryIds);
					if (!bool) {
						delCouponList.add(coupons);
					}
				}
			}
			//删除不符合指定分类使用的代金券
			if (CollectionUtils.isNotEmpty(delCouponList)) {
				couponList.removeAll(delCouponList);
			}
		}
		resp.getData().setCouponList(couponList);
		resp.getData().setDiscountList(discountList);
		resp.getData().setFullSubtractList(fullSubtractList);
	}

	private Map<String, Object> buildFindFavourCondition(PlaceOrderParamDto paramDto,PlaceOrderDto orderDto) {
		// 订单总金额
		BigDecimal totalAmount = paramDto.getTotalAmount();
		// 订单总金额存入上下文，后续流程需要使用
		paramDto.put("totalAmount", totalAmount);
		// 获取店铺类型
		StoreTypeEnum storeType = ((StoreInfo)paramDto.get("storeInfo")).getType();

		Map<String, Object> queryCondition = new HashMap<String, Object>();
		queryCondition.put("userId", paramDto.getUserId());
		queryCondition.put("storeId", paramDto.getStoreId());
		queryCondition.put("totalAmount", totalAmount);
		queryCondition.put("storeType", storeType.ordinal());
		//根据店铺类型查询代金券
		if (StoreTypeEnum.CLOUD_STORE.equals(storeType)) {
			queryCondition.put("type", Constant.ONE);
		} else if (StoreTypeEnum.SERVICE_STORE.equals(storeType)) {
			queryCondition.put("type", Constant.TWO);
			UserAddressVo addr = orderDto.getUserAddrInfo();
		    if (addr != null) {
		    	queryCondition.put("addressId", addr.getAddressId());
			}else {
				queryCondition.put("addressId", "");
			}
		}
		return queryCondition;
	}

	
}
