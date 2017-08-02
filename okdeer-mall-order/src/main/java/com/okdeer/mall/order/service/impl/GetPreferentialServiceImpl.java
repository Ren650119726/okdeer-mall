package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.coupons.bo.ActivityRecordParamBo;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.enums.CouponsType;
import com.okdeer.mall.activity.coupons.enums.RecordCountRuleEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.PreferentialVo;
import com.okdeer.mall.activity.discount.enums.ActivityBusinessType;
import com.okdeer.mall.activity.discount.enums.LimitSkuType;
import com.okdeer.mall.activity.discount.service.ActivityDiscountRecordService;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.dto.ActivityInfoDto;
import com.okdeer.mall.activity.service.CouponsFilterStrategy;
import com.okdeer.mall.activity.service.FavourFilterStrategy;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
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
	// private static final Logger logger =
	// LoggerFactory.getLogger(GetPreferentialServiceImpl.class);

	/**
	 * 代金券记录Service
	 */
	@Resource
	private ActivityCouponsRecordService activityCouponsRecordService;

	@Resource
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;

	/**
	 * 折扣、满减活动Service
	 */
	@Resource
	private ActivityDiscountService activityDiscountService;

	@Resource
	private ActivityDiscountRecordService activityDiscountRecordService;

	@Resource
	private SysBuyerFirstOrderRecordService sysBuyerFirstOrderRecordService;

	/**
	 * 导航类目
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsNavigateCategoryServiceApi goodsNavigateCategoryServiceApi;

	// Begin V2.1 modified by maojj 2017-02-17
	// V2.3版本开始，只存在满减活动，去除了满折活动。只有便利店有满减活动，服务店没有满减
	@Override
	public PreferentialVo findPreferentialByUser(FavourParamBO paramBo) throws Exception {
		PreferentialVo preferentialVo = new PreferentialVo();
		// 获取用户有效的代金券
		List<Coupons> couponList = getCouponsList(paramBo);
		// 抽取运费代金券
		List<Coupons> fareCouponList = extractFareCouponsList(couponList);
		List<Discount> discountList = Lists.newArrayList();
		// 获取用户有效的满减
		List<FullSubtract> fullSubtractList = getFullSubtractList(paramBo);
		// 获取线上支付最大优惠
		Favour maxFavourOnline = getMaxFavour(couponList, discountList, fullSubtractList, true);
		// 获取线下支付最大优惠
		Favour maxFavourOffline = getMaxFavour(couponList, discountList, fullSubtractList, false);
		// 获取最大运费代金券
		Favour maxFareFavour = getMaxFavour(fareCouponList);
		preferentialVo.setCouponList(couponList);
		preferentialVo.setDiscountList(discountList);
		preferentialVo.setFullSubtractList(fullSubtractList);
		preferentialVo.setFareCouponsList(fareCouponList);
		preferentialVo.setMaxFavourOnline(maxFavourOnline);
		preferentialVo.setMaxFavourOffline(maxFavourOffline);
		preferentialVo.setMaxFareFavour(maxFareFavour);
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
	private List<Coupons> getCouponsList(FavourParamBO paramBo) throws Exception {
		// 获取用户有效的代金券
		List<Coupons> couponList = activityCouponsRecordService.findValidCoupons(paramBo, new CouponsFilterStrategy() {

			@Override
			public boolean accept(Favour favour) throws Exception {
				Coupons coupons = (Coupons) favour;
				coupons.setActivityType(ActivityTypeEnum.VONCHER.ordinal());
				// 判断代金券是否限制首单用户
				if (coupons.getUseUserType() == UseUserType.ONlY_NEW_USER) {
					// 如果代金券限制首单用户。判断当前用户是否为首单
					if(!isFirstOrderUser(paramBo)){
						return false;
					}
				}
				if (Constant.ONE == coupons.getIsCategory().intValue()) {
					// 如果代金券指定分类，检查分类是否超出指定分类
					// 便利店代金券，超出分类依然可以购买，V2.3修改此逻辑，服务店保持不变
					if (coupons.getType() == CouponsType.bld.ordinal()) {
						List<String> limitCtgIds = goodsNavigateCategoryServiceApi.findNavigateCategoryByCouponId(coupons.getCouponId());
						if(CollectionUtils.isEmpty(paramBo.getGoodsList())){
							return false;
						}
						BigDecimal totalAmount = BigDecimal.valueOf(0.00);
						for (PlaceOrderItemDto goods : paramBo.getGoodsList()) {
							if (limitCtgIds.contains(goods.getSpuCategoryId())) {
								// 如果包含此分类，则意味着该商品在活动范围之类。计算总金额
								if(goods.getSkuActType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
									// 低价商品只有原价购买的才可享受优惠
									totalAmount = totalAmount.add(goods.getSkuPrice().multiply(BigDecimal.valueOf(goods.getQuantity()-goods.getSkuActQuantity())));
								}else{
									totalAmount = totalAmount.add(goods.getTotalAmount());
								}
							}
						}
						if (totalAmount.compareTo(BigDecimal.valueOf(0.0)) == 0 || totalAmount.compareTo(new BigDecimal(coupons.getArrive())) == -1) {
							return false;
						}
					} else if (coupons.getType() == CouponsType.fwd.ordinal()) {
						int count = activityCouponsRecordMapper.findServerBySpuCategoryIds(paramBo.getSpuCategoryIds(),
								coupons.getCouponId());
						if (count != paramBo.getSpuCategoryIds().size()) {
							return false;
						}
					}

				}else{
					// 如果不指定分类，低价商品不可享受优惠
					BigDecimal totalAmount = BigDecimal.valueOf(0.00);
					for (PlaceOrderItemDto goods : paramBo.getGoodsList()) {
						if(goods.getSkuActType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
							if(coupons.getType() == CouponsType.bldyf.ordinal()){
								//运费券不排除低价部分
								totalAmount = totalAmount.add(goods.getSkuPrice().multiply(BigDecimal.valueOf(goods.getQuantity())));
							}else{
								// 低价商品只有原价购买的才可享受优惠
								totalAmount = totalAmount.add(goods.getSkuPrice().multiply(BigDecimal.valueOf(goods.getQuantity()-goods.getSkuActQuantity())));
							}
						}else{
							totalAmount = totalAmount.add(goods.getTotalAmount());
						}
					}
					if (totalAmount.compareTo(BigDecimal.valueOf(0.0)) == 0 || totalAmount.compareTo(new BigDecimal(coupons.getArrive())) == -1) {
						return false;
					}
				}
				// 如果代金券限制设备
				if(coupons.getDeviceDayLimit() != null && coupons.getDeviceDayLimit() > 0){
					if(coupons.getDeviceDayLimit().compareTo(paramBo.findCountNum(RecordCountRuleEnum.COUPONS_BY_DEVICE, coupons.getCouponId()))< 1){
						return false;
					}
				}
				// 如果代金券限制账号
				if(coupons.getAccountDayLimit() != null && coupons.getAccountDayLimit() > 0){
					if(coupons.getAccountDayLimit().compareTo(paramBo.findCountNum(RecordCountRuleEnum.COUPONS_BY_USER, coupons.getCouponId()))< 1){
						return false;
					}
				}
				return true;
			}
		});
		return couponList;
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
	private List<FullSubtract> getFullSubtractList(FavourParamBO paramBo) throws Exception {
		if (paramBo.getStoreType() == StoreTypeEnum.SERVICE_STORE) {
			// 2.3版本，服务店铺无满减活动
			return Lists.newArrayList();
		}

		return activityDiscountService.findValidFullSubtract(paramBo, new FavourFilterStrategy() {

			@Override
			public boolean accept(ActivityInfoDto actInfoDto) throws Exception {
				ActivityDiscount actInfo = actInfoDto.getActivityInfo();
				if (actInfo.getLimitUser() == UseUserType.ONlY_NEW_USER) {
					// 如果限制首单用户
					if (!isFirstOrderUser(paramBo)) {
						return false;
					}
				}
				// 参与活动次数限制
				int limitTotalFreq = actInfo.getLimitTotalFreq().intValue();
				ActivityRecordParamBo recParamBo = null;
				if (limitTotalFreq > 0) {
					// 用户参与活动次数。0：不限，大于0有限制
					recParamBo = new ActivityRecordParamBo();
					recParamBo.setUserId(paramBo.getUserId());
					recParamBo.setPkId(actInfo.getId());
					int userTotalFreq = activityDiscountRecordService.countTotalFreq(recParamBo);
					if (userTotalFreq >= limitTotalFreq) {
						return false;
					}
				}
				if(actInfo.getDeviceDayLimit() != null && actInfo.getDeviceDayLimit() > 0 && StringUtils.isNotEmpty(paramBo.getDeviceId())){
					// 同一设备id每天最多使用张数 0：不限，大于0有限制
					recParamBo = new ActivityRecordParamBo();
					recParamBo.setPkId(actInfo.getId());
					recParamBo.setDeviceId(paramBo.getDeviceId());
					recParamBo.setRecDate(DateUtils.getDate());
					int deviceTotalNum = activityDiscountRecordService.countTotalFreq(recParamBo);
					if (actInfo.getDeviceDayLimit().intValue() <= deviceTotalNum) {
						return false;
					}
				}
				if(actInfo.getAccountDayLimit() != null && actInfo.getAccountDayLimit() > 0){
					// 同一设备id每天最多使用张数 0：不限，大于0有限制
					recParamBo = new ActivityRecordParamBo();
					recParamBo.setPkId(actInfo.getId());
					recParamBo.setUserId(paramBo.getUserId());
					recParamBo.setRecDate(DateUtils.getDate());
					int userTotalNum = activityDiscountRecordService.countTotalFreq(recParamBo);
					if (actInfo.getAccountDayLimit().intValue() <= userTotalNum) {
						return false;
					}
				}
				
				// 商品限制
				LimitSkuType limitSkuType = actInfo.getLimitSku();
				// 请求的商品列表
				List<PlaceOrderItemDto> goodsList = paramBo.getGoodsList();
				// 参与活动的商品总金额
				BigDecimal totalAmount = BigDecimal.valueOf(0.00);
				if (limitSkuType == LimitSkuType.LIMIT_CATEGORY) {
					// 活动限制的分类Id列表
					List<String> limitCtgIds = actInfoDto.getBusinessIds(ActivityBusinessType.SKU_CATEGORY);
					// 指定分类
					// 遍历购买的商品
					for (PlaceOrderItemDto goods : goodsList) {
						if (limitCtgIds.contains(goods.getSpuCategoryId())) {
							if(goods.getSkuActType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
								// 低价商品只有原价购买的才可享受优惠
								totalAmount = totalAmount.add(goods.getSkuPrice().multiply(BigDecimal.valueOf(goods.getQuantity()-goods.getSkuActQuantity())));
							}else{
								totalAmount = totalAmount.add(goods.getTotalAmount());
							}
						}
					}
					if (totalAmount.compareTo(BigDecimal.valueOf(0.00)) == 0) {
						return false;
					}
					paramBo.setTotalAmount(totalAmount);
				} else if (limitSkuType == LimitSkuType.LIMIT_SKU) {
					// 限制商品Id列表
					List<String> limitSkuIds = actInfoDto.getBusinessIds(ActivityBusinessType.SKU);
					// 遍历购买的商品
					for (PlaceOrderItemDto goods : goodsList) {
						if (limitSkuIds.contains(goods.getStoreSkuId())) {
							// 如果包含此商品，则意味着该商品在活动范围之类。计算总金额
							if(goods.getSkuActType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
								// 低价商品只有原价购买的才可享受优惠
								totalAmount = totalAmount.add(goods.getSkuPrice().multiply(BigDecimal.valueOf(goods.getQuantity()-goods.getSkuActQuantity())));
							}else{
								totalAmount = totalAmount.add(goods.getTotalAmount());
							}
						}
					}
					if (totalAmount.compareTo(BigDecimal.valueOf(0.00)) == 0) {
						return false;
					}
					paramBo.setTotalAmount(totalAmount);
				}
				return true;
			}
		});

	}

	/**
	 * @Description: 
	 * @return   
	 * @author maojj
	 * @date 2017年4月21日
	 */
	private boolean isFirstOrderUser(FavourParamBO paramBo) {
		if (paramBo.getIsFirstOrderUser() == null) {
			boolean isFirstOrderUser = sysBuyerFirstOrderRecordService.isExistsOrderRecord(paramBo.getUserId()) ? false
					: true;
			paramBo.setIsFirstOrderUser(isFirstOrderUser);
		}
		if (Boolean.FALSE.equals(paramBo.getIsFirstOrderUser())) {
			return false;
		}
		return true;
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
	private Favour getMaxFavour(List<Coupons> couponList, List<Discount> discountList,
			List<FullSubtract> fullSubtractList, boolean isOnLine) {
		List<Favour> favourList = new ArrayList<Favour>();
		addFavourList(favourList, couponList, isOnLine);
		addFavourList(favourList, discountList, isOnLine);
		addFavourList(favourList, fullSubtractList, isOnLine);
		Collections.sort(favourList, new Comparator<Favour>() {

			@Override
			public int compare(Favour o1, Favour o2) {
				return o2.getMaxFavourStrategy().compareTo(o1.getMaxFavourStrategy());
			}
		});
		if (CollectionUtils.isEmpty(favourList)) {
			return null;
		} else {
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
	private void addFavourList(List<Favour> favourList, List<? extends Favour> subFavourList, boolean isOnLine) {
		if (CollectionUtils.isEmpty(subFavourList)) {
			return;
		}
		for (Favour favour : subFavourList) {
			if (isOnLine && ("0".equals(favour.getUsableRange()) || "2".equals(favour.getUsableRange()))) {
				// 如果是在线支付，则优惠可用范围为0：支持在线，或者为2：支持所有
				favourList.add(favour);
			} else if (!isOnLine && ("1".equals(favour.getUsableRange()) || "2".equals(favour.getUsableRange()))) {
				// 如果是线下支付，则优惠可用范围为1：支持到付，或者为2：支持所有
				favourList.add(favour);
			}
		}
	}
	// End V2.1 modified by maojj 2017-02-17
	
	// Begin V2.5 added by maojj 2017-06-22
	/**
	 * @Description: 抽取运费代金券
	 * @param couponList
	 * @return   
	 * @author maojj
	 * @date 2017年6月22日
	 */
	private List<Coupons> extractFareCouponsList(List<Coupons> couponsList){
		List<Coupons> fareCouponsList = Lists.newArrayList();
		if(CollectionUtils.isEmpty(couponsList)){
			return fareCouponsList;
		}
		Coupons coupons = null;
		for(Iterator<Coupons> it = couponsList.iterator();it.hasNext();){
			coupons = it.next();
			if(CouponsType.bldyf.getValue().equals(coupons.getType())){
				// 代金券类型为运费代金券
				fareCouponsList.add(coupons);
				it.remove();
			}
		}
		return fareCouponsList;
	}
	
	
	private Favour getMaxFavour(List<Coupons> couponList) {
		Collections.sort(couponList, new Comparator<Coupons>() {

			@Override
			public int compare(Coupons o1, Coupons o2) {
				return o2.getMaxFavourStrategy().compareTo(o1.getMaxFavourStrategy());
			}
		});
		if (CollectionUtils.isEmpty(couponList)) {
			return null;
		} else {
			return couponList.get(0);
		}
	}
	// End V2.5 added by maojj 2017-06-22
}
