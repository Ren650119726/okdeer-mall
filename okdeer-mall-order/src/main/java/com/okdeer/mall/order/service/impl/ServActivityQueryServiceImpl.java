package com.okdeer.mall.order.service.impl;

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

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.base.common.constant.LoggerConstants;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.service.MemberConsigneeAddressServiceApi;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.vo.Coupons;
import com.okdeer.mall.order.vo.Discount;
import com.okdeer.mall.order.vo.FullSubtract;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;

/**
 * ClassName: ServerCheckServiceImpl 
 * @Description: 活动查询
 * @author wushp
 * @date 2016年9月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.1.0			2016年9月28日				wushp		活动查询
 *	    V1.1.0		    2016-09-23			   tangy		到店消费按店铺地址查询代金券
 */
@Service("servActivityQueryService")
public class ServActivityQueryServiceImpl implements RequestHandler<ServiceOrderReq,ServiceOrderResp> {

	/**
	 * log
	 */
	private static final Logger logger = LoggerFactory.getLogger(ServActivityQueryServiceImpl.class);
			
	/**
	 * 满减满折活动Service
	 */
	@Resource
	private ActivityDiscountService activityDiscountService;
	
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
	
	//Begin added by tangy  2016-10-08
	/**
	 * 地址
	 */
	@Reference(version = "1.0.0", check = false)
	private MemberConsigneeAddressServiceApi memberConsigneeAddressService;
	//End added by tangy
	
	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		ServiceOrderReq reqData = req.getData();
		ServiceOrderResp respData = resp.getData();
		
		//构建优惠查询请求条件
		Map<String, Object> queryCondition = buildFindFavourCondition(reqData,respData);
		// 获取用户有效的代金券
		List<Coupons> couponList = activityCouponsRecordMapper.findValidCoupons(queryCondition);
		// 获取用户有效的折扣
		List<Discount> discountList = activityDiscountMapper.findValidDiscount(queryCondition);
		// 获取用户有效的满减
		List<FullSubtract> fullSubtractList = activityDiscountMapper.findValidFullSubtract(queryCondition);
		//排除不符合的代金券
		if (CollectionUtils.isNotEmpty(couponList)) {
			//商品类目id集
			List<String> spuCategoryIds = duplicateRemoval((List<String>)req.getContext().get("spuCategoryIds")); 
			List<Coupons> delCouponList = new ArrayList<Coupons>();
			//判断筛选指定分类使用代金券
			for (Coupons coupons : couponList) {
				//是否指定分类使用
				if (Constant.ONE == coupons.getIsCategory().intValue()) {
					int count = activityCouponsRecordMapper.findServerBySpuCategoryIds(spuCategoryIds, coupons.getCouponId());
					// Begin 2016-11-18 modified by maojj 
					if (count == Constant.ZERO || count != spuCategoryIds.size()) {
						delCouponList.add(coupons);
					}
					// End 2016-11-18 modified by maojj 
				}
			}
			//删除不符合指定分类使用的代金券
			if (CollectionUtils.isNotEmpty(delCouponList)) {
				couponList.removeAll(delCouponList);
			}
		}
		respData.setCouponList(couponList);
		respData.setDiscountList(discountList);
		respData.setFullSubtractList(fullSubtractList);
	}

	private Map<String, Object> buildFindFavourCondition(ServiceOrderReq reqDto, ServiceOrderResp respData) {
		// 获取店铺类型
		Integer storeType = respData.getStoreInfo().getStoreType();

		Map<String, Object> queryCondition = new HashMap<String, Object>();
		queryCondition.put("userId", reqDto.getUserId());
		queryCondition.put("storeId", reqDto.getStoreId());
		queryCondition.put("totalAmount", reqDto.getTotalAmount());
		queryCondition.put("storeType", storeType);
		//根据店铺类型查询代金券
		if (StoreTypeEnum.CLOUD_STORE.ordinal() == storeType) {
			queryCondition.put("type", Constant.ONE);
		} else if (StoreTypeEnum.SERVICE_STORE.ordinal() == storeType) {
			queryCondition.put("type", Constant.TWO);
			//Begin added by tangy  2016-10-08
			//到店消费根据店铺地址查询代金券
			if (OrderTypeEnum.STORE_CONSUME_ORDER.equals(reqDto.getOrderType())) {
				try {
					MemberConsigneeAddress mAddress = memberConsigneeAddressService.findByStoreId(reqDto.getStoreId());
				    if (mAddress != null) {
				    	queryCondition.put("addressId", mAddress.getId());
					}
				} catch (Exception e) {
					logger.error(LoggerConstants.LOGGER_ERROR_EXCEPTION, e);
				}
			}else {
				if(respData.getDefaultAddress() != null){
					queryCondition.put("addressId", respData.getDefaultAddress().getAddressId());
				}else{
					queryCondition.put("addressId", "");
				}
			}
			//End added by tangy
		}
		return queryCondition;
	}

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

}
