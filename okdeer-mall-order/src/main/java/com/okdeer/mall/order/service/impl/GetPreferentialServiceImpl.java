package com.okdeer.mall.order.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.enums.CouponsType;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.discount.entity.PreferentialVo;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.service.FavourFilterStrategy;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.order.service.GetPreferentialService;
import com.okdeer.mall.order.vo.Coupons;
import com.okdeer.mall.order.vo.Discount;
import com.okdeer.mall.order.vo.Favour;
import com.okdeer.mall.order.vo.FullSubtract;
import com.okdeer.mall.system.service.SysBuyerFirstOrderRecordService;

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
 *     V2.1			  2017年2月17日		  maojj				优化处理方式	  
 */
@Service
public class GetPreferentialServiceImpl implements GetPreferentialService {

	/**
	 * log
	 */
	// private static final Logger logger = LoggerFactory.getLogger(GetPreferentialServiceImpl.class);

	/**
	 * 代金券记录Service
	 */
	@Resource
	private ActivityCouponsRecordService activityCouponsRecordService;

	/**
	 * 折扣、满减活动Service
	 */
	@Resource
	private ActivityDiscountService activityDiscountService;

	@Resource
	private SysBuyerFirstOrderRecordService sysBuyerFirstOrderRecordService;
	
	/**
	 * 导航类目
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsNavigateCategoryServiceApi goodsNavigateCategoryServiceApi;

	// Begin V2.1 modified by maojj 2017-02-17
	@Override
	public PreferentialVo findPreferentialByUser(FavourParamBO paramBo) throws Exception {
		PreferentialVo preferentialVo = new PreferentialVo();
		// 确定首单用户专享代金券是否能使用
		boolean isFirstOrderUser = sysBuyerFirstOrderRecordService.isExistsOrderRecord(paramBo.getUserId())?false:true;
		// 获取用户有效的代金券
		List<Coupons> couponList = getCouponsList(paramBo, isFirstOrderUser);
		// 获取用户有效的折扣
		List<Discount> discountList = getDiscountList(paramBo, isFirstOrderUser);
		// 获取用户有效的满减
		List<FullSubtract> fullSubtractList = getFullSubtractList(paramBo, isFirstOrderUser);
		// 获取线上支付最大优惠
		Favour maxFavourOnline = getMaxFavour(couponList,discountList,fullSubtractList,true);
		// 获取线下支付最大优惠
		Favour maxFavourOffline = getMaxFavour(couponList,discountList,fullSubtractList,false);
		
		preferentialVo.setCouponList(couponList);
		preferentialVo.setDiscountList(discountList);
		preferentialVo.setFullSubtractList(fullSubtractList);
		preferentialVo.setMaxFavourOnline(maxFavourOnline);
		preferentialVo.setMaxFavourOffline(maxFavourOffline);
		return preferentialVo;
	}

	/**
	 * @Description: 获取用户有效的代金券
	 * @param paramBo
	 * @param isNewUser
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年2月17日
	 */
	private List<Coupons> getCouponsList(FavourParamBO paramBo, boolean isNewUser) throws Exception {
		// 获取用户有效的代金券
		List<Coupons> couponList = activityCouponsRecordService.findValidCoupons(paramBo, new FavourFilterStrategy() {

			@Override
			public boolean accept(Favour favour) throws Exception {
				Coupons coupons = (Coupons) favour;
				coupons.setActivityType(ActivityTypeEnum.VONCHER.ordinal());
				if (coupons.getUseUserType() == UseUserType.ONlY_NEW_USER && !isNewUser) {
					// 如果代金券限制首单用户。则非首单用户则不能使用
					return false;
				}
				if (Constant.ONE == coupons.getIsCategory().intValue()) {
					// 如果代金券指定分类，检查分类是否超出指定分类
					List<String> categoryIdLimitList = null;
					if(coupons.getType() == CouponsType.bld.ordinal()){
						categoryIdLimitList = goodsNavigateCategoryServiceApi.findNavigateCategoryByCouponId(coupons.getCouponId());
					}else if(coupons.getType() == CouponsType.fwd.ordinal()){
						categoryIdLimitList = goodsNavigateCategoryServiceApi
								.findNavigateCategoryBySkuIds(paramBo.getSpuCategoryIds());
					}
					if (CollectionUtils.isEmpty(paramBo.getSpuCategoryIds()) || CollectionUtils.isEmpty(categoryIdLimitList) || !categoryIdLimitList.containsAll(paramBo.getSpuCategoryIds())) {
						return false;
					}
				}
				return true;
			}
		});
		return couponList;
	}

	/**
	 * @Description: 获取用户有效的折扣
	 * @param paramBo
	 * @param isNewUser
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年2月17日
	 */
	private List<Discount> getDiscountList(FavourParamBO paramBo, boolean isNewUser) throws Exception {
		return activityDiscountService.findValidDiscount(paramBo, new FavourFilterStrategy() {

			@Override
			public boolean accept(Favour favour) throws Exception {
				favour.setActivityType(ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES.ordinal());
				return true;
			}
		});
	}

	/**
	 * @Description: 获取用户有效的满减
	 * @param paramBo
	 * @param isNewUser
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年2月17日
	 */
	private List<FullSubtract> getFullSubtractList(FavourParamBO paramBo, boolean isNewUser) throws Exception {
		return activityDiscountService.findValidFullSubtract(paramBo, new FavourFilterStrategy() {

			@Override
			public boolean accept(Favour favour) throws Exception {
				favour.setActivityType(ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES.ordinal());
				return true;
			}
		});
	}
	
	/**
	 * @Description: 获取最大优惠
	 * @param couponList
	 * @param discountList
	 * @param fullSubtractList
	 * @param isOnLine
	 * @return   
	 * @author maojj
	 * @date 2017年2月17日
	 */
	private Favour getMaxFavour(List<Coupons> couponList,List<Discount> discountList,List<FullSubtract> fullSubtractList,boolean isOnLine){
		List<Favour> favourList = new ArrayList<Favour>();
		addFavourList(favourList,couponList,isOnLine);
		addFavourList(favourList,discountList,isOnLine);
		addFavourList(favourList,fullSubtractList,isOnLine);
		Collections.sort(favourList, new Comparator<Favour>() {
			@Override
			public int compare(Favour o1, Favour o2) {
				return o2.getMaxFavourStrategy().compareTo(o1.getMaxFavourStrategy());
			}
		});
		if(CollectionUtils.isEmpty(favourList)){
			return null;
		}else{
			return favourList.get(0);
		}
	}
	
	/**
	 * @Description: 增加优惠列表
	 * @param favourList
	 * @param subFavourList
	 * @param isOnLine   
	 * @author maojj
	 * @date 2017年2月17日
	 */
	private void addFavourList(List<Favour> favourList,List<? extends Favour> subFavourList,boolean isOnLine){
		if(CollectionUtils.isEmpty(subFavourList)){
			return;
		}
		for(Favour favour : subFavourList){
			if(isOnLine && ("0".equals(favour.getUsableRange()) || "2".equals(favour.getUsableRange()))){
				// 如果是在线支付，则优惠可用范围为0：支持在线，或者为2：支持所有
				favourList.add(favour);
			}else if(!isOnLine && ("1".equals(favour.getUsableRange()) || "2".equals(favour.getUsableRange()))){
				// 如果是线下支付，则优惠可用范围为1：支持到付，或者为2：支持所有
				favourList.add(favour);
			}
		}
	}
	// End V2.1 modified by maojj 2017-02-17
}
