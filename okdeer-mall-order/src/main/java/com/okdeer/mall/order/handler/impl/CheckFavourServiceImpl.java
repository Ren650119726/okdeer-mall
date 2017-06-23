package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Maps;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.common.utils.EnumAdapter;
import com.okdeer.mall.activity.coupons.bo.ActivityRecordParamBo;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.enums.CouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsStoreMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountCondition;
import com.okdeer.mall.activity.discount.enums.ActivityBusinessType;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountType;
import com.okdeer.mall.activity.discount.enums.LimitSkuType;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountConditionMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountRecordService;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.dto.ActivityInfoDto;
import com.okdeer.mall.activity.dto.ActivityParamDto;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.common.enums.UseClientType;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.system.service.SysBuyerFirstOrderRecordService;


/**
 * ClassName: CheckFavourServiceImpl 
 * @Description: 检查优惠信息的服务
 * @author maojj
 * @date 2017年1月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.0 			2017年1月7日				maojj
 */
@Service("checkFavourService")
public class CheckFavourServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {

	/**
	 * 代金券记录Mapper
	 */
	@Resource
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;

	/**
	 * 折扣、满减活动Mapper
	 */
	@Resource
	private ActivityDiscountService activityDiscountService;

	@Resource
	private ActivityDiscountMapper activityDiscountMapper;
	
	/**
	 * 代金券Mapper
	 */
	@Resource
	private ActivityCouponsMapper activityCouponsMapper;
	
	@Resource
	private ActivityCouponsStoreMapper activityCouponsStoreMapper;

	/**
	 * 导航类目
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsNavigateCategoryServiceApi goodsNavigateCategoryServiceApi;
	
	@Resource
	private SysBuyerFirstOrderRecordService sysBuyerFirstOrderRecordService;
	
	@Resource
	private ActivityDiscountRecordService activityDiscountRecordService;
	
	@Resource
	private ActivityDiscountConditionMapper activityDiscountConditionMapper;

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		ActivityTypeEnum activityType = paramDto.getActivityType();
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		boolean isValid = true;
		switch (activityType) {
			case VONCHER:
				isValid = checkCoupons(paramDto,parserBo,resp);
				break;
			case FULL_REDUCTION_ACTIVITIES:
			case FULL_DISCOUNT_ACTIVITIES:
				isValid = checkDiscount(paramDto,parserBo);
				break;
			default:
				break;
		}
		// 检查运费优惠
		if(!StringUtils.isEmpty(paramDto.getFareActivityId())){
			// 如果请求中存在运费领取记录Id，则检查运费
			checkFareCoupons(paramDto, parserBo, resp);
		}
		if (!isValid && resp.isSuccess()) {
			resp.setResult(ResultCodeEnum.FAVOUR_NOT_SUPPORT);
			return;
		}
	}

	/**
	 * @Description: 校验优惠券
	 * @param recordId 代金券领取记录ID
	 * @return boolean  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private boolean checkCoupons(PlaceOrderParamDto paramDto,StoreSkuParserBo parserBo,Response<PlaceOrderDto> resp) throws Exception{
		String recordId = paramDto.getRecordId();
		ActivityCouponsRecord couponsRecord = activityCouponsRecordMapper.selectByPrimaryKey(recordId);
		// Begin V2.4 added by maojj 2017-05-31
		// 增加领取记录的校验。校验请求提供的领取记录是否是当前用户
		if(couponsRecord == null || !couponsRecord.getCollectUserId().equals(paramDto.getUserId())){
			return false;
		}
		// End V2.4 added by maojj 2017-05-31
		if (couponsRecord.getStatus() != ActivityCouponsRecordStatusEnum.UNUSED) {
			return false;
		}
		// 查询代金券
		ActivityCoupons coupons = activityCouponsMapper.selectByPrimaryKey(couponsRecord.getCouponsId());
		// 检查金额是否达到使用下限
		if(paramDto.getEnjoyFavourTotalAmount().compareTo(new BigDecimal(coupons.getArriveLimit())) == -1){
			return false;
		}
		// 检查当前店铺是否可使用该代金券
		if(coupons.getAreaType() != AreaType.national && activityCouponsStoreMapper.findByStoreIdAndCouponsId(paramDto.getStoreId(), coupons.getId()) == null){
			return false;
		}
		
		if(coupons.getUseUserType() == UseUserType.ONlY_NEW_USER){
			// 仅限首单用户，检查当前用户是否为首单用户。
			if(!isFirstOrderUser(paramDto.getUserId())){
				resp.setResult(ResultCodeEnum.ACTIVITY_LIMIT_FIRST_ORDER);
				return false;
			}
		}
		if(coupons.getUseClientType() != UseClientType.ALLOW_All && coupons.getUseClientType() != EnumAdapter.convert(paramDto.getChannel())){
			return false;
		}
		if(coupons.getIsCategory() == Constant.ONE){
			if(coupons.getType() == CouponsType.bld.ordinal()){
				// 便利店限制品类
				List<String> categoryIdLimitList = goodsNavigateCategoryServiceApi.findNavigateCategoryByCouponId(coupons.getId());
				Map<String, CurrentStoreSkuBo> haveFavourGoodsMap = Maps.newHashMap();
				BigDecimal totalAmount = BigDecimal.valueOf(0.00);
				for(CurrentStoreSkuBo storeSkuBo : parserBo.getCurrentSkuMap().values()){
					if(categoryIdLimitList.contains(storeSkuBo.getSpuCategoryId())){
						haveFavourGoodsMap.put(storeSkuBo.getId(), storeSkuBo);
						if(storeSkuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
							totalAmount = totalAmount.add(storeSkuBo.getOnlinePrice().multiply(BigDecimal.valueOf(storeSkuBo.getQuantity()-storeSkuBo.getSkuActQuantity())));
						}else{
							totalAmount = totalAmount.add(storeSkuBo.getTotalAmount());
						}
					}
				}
				if(totalAmount.compareTo(BigDecimal.valueOf(0.0)) == 0 || totalAmount.compareTo(BigDecimal.valueOf(coupons.getArriveLimit())) == -1){
					// 如果享受优惠的商品总金额为0，标识没有指定分类的商品。如果享受优惠的商品总金额小于代金券的使用条件，也不能使用该代金券
					resp.setResult(ResultCodeEnum.ACTIVITY_CATEGORY_LIMIT);
					return false;
				}
				parserBo.setHaveFavourGoodsMap(haveFavourGoodsMap);
				parserBo.setTotalAmountHaveFavour(totalAmount);
			} else if(coupons.getType() == CouponsType.fwd.ordinal()){
				Set<String> spuCategoryIds = parserBo == null ? null : parserBo.getCategoryIdSet();
				int count = activityCouponsRecordMapper.findServerBySpuCategoryIds(spuCategoryIds, coupons.getId());
				if (count != spuCategoryIds.size()) {
					// 超出指定分类
					resp.setResult(ResultCodeEnum.ACTIVITY_CATEGORY_LIMIT);
					return false;
				}
			}
		}	
		ActivityRecordParamBo recParamBo = null;
		if(coupons.getDeviceDayLimit() != null && coupons.getDeviceDayLimit() > 0 && StringUtils.isNotEmpty(paramDto.getDeviceId())){
			// 同一设备id每天最多使用张数 0：不限，大于0有限制
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setPkId(coupons.getId());
			recParamBo.setDeviceId(paramDto.getDeviceId());
			recParamBo.setRecDate(DateUtils.getDate());
			int deviceTotalNum = activityCouponsRecordMapper.countDayFreq(recParamBo);
			if (coupons.getDeviceDayLimit().intValue() <= deviceTotalNum) {
				return false;
			}
		}
		if(coupons.getAccountDayLimit() != null && coupons.getAccountDayLimit() > 0){
			// 同一设备id每天最多使用张数 0：不限，大于0有限制
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setPkId(coupons.getId());
			recParamBo.setUserId(paramDto.getUserId());
			recParamBo.setRecDate(DateUtils.getDate());
			int userTotalNum = activityCouponsRecordMapper.countDayFreq(recParamBo);
			if (coupons.getAccountDayLimit().intValue() <= userTotalNum) {
				return false;
			}
		}
		parserBo.setPlatformPreferential(new BigDecimal(coupons.getFaceValue()));
		return true;
	}

	/**
	 * @Description: 校验满减满折
	 * @param activityId 活动ID
	 * @return boolean  
	 * @author maojj
	 * @throws Exception 
	 * @date 2016年7月14日
	 */
	private boolean checkDiscount(PlaceOrderParamDto paramDto,StoreSkuParserBo parserBo) throws Exception {
		String userId = paramDto.getUserId();
		String activityItemId = paramDto.getActivityItemId();
		boolean isValid = true;
		ActivityDiscountCondition condition = activityDiscountConditionMapper.findById(activityItemId);
		String activityId = condition.getDiscountId();
		ActivityInfoDto actInfoDto = activityDiscountService.findInfoById(activityId, false);
		ActivityDiscount actInfo = actInfoDto.getActivityInfo();
		if (actInfo.getStatus() != ActivityDiscountStatus.ing) {
			isValid = false;
		}
		// Begin V2.5 added by maojj 2017-06-23
		// 检查当前店铺是否可使用该满减活动
		ActivityParamDto actParamDto = new ActivityParamDto();
		actParamDto.setStoreId(paramDto.getStoreId());
		actParamDto.setLimitChannel(String.valueOf(paramDto.getChannel().ordinal()));
		actParamDto.setType(actInfo.getType());
		List<String> activityIds = activityDiscountMapper.findByStore(actParamDto);
		if(!activityIds.contains(activityIds)){
			return false;
		}
		// End V2.5 added by maojj 2017-06-23
		if (actInfo.getLimitUser() == UseUserType.ONlY_NEW_USER) {
			// 如果限制首单用户
			if (!isFirstOrderUser(userId)) {
				return false;
			}
		}
		// 参与活动次数限制
		int limitTotalFreq = actInfo.getLimitTotalFreq().intValue();
		ActivityRecordParamBo recParamBo = null;
		if (limitTotalFreq > 0) {
			// 用户参与活动次数。0：不限，大于0有限制
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setUserId(userId);
			recParamBo.setPkId(activityId);
			int userTotalFreq = activityDiscountRecordService.countTotalFreq(recParamBo);
			if (userTotalFreq >= limitTotalFreq) {
				return false;
			}
		}
		if(actInfo.getDeviceDayLimit() != null && actInfo.getDeviceDayLimit() > 0 && StringUtils.isNotEmpty(paramDto.getDeviceId())){
			// 同一设备id每天最多使用张数 0：不限，大于0有限制
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setPkId(activityId);
			recParamBo.setDeviceId(paramDto.getDeviceId());
			recParamBo.setRecDate(DateUtils.getDate());
			int deviceTotalNum = activityDiscountRecordService.countTotalFreq(recParamBo);
			if (actInfo.getDeviceDayLimit().intValue() <= deviceTotalNum) {
				return false;
			}
		}
		if(actInfo.getAccountDayLimit() != null && actInfo.getAccountDayLimit() > 0){
			// 同一设备id每天最多使用张数 0：不限，大于0有限制
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setPkId(activityId);
			recParamBo.setUserId(userId);
			recParamBo.setRecDate(DateUtils.getDate());
			int userTotalNum = activityDiscountRecordService.countTotalFreq(recParamBo);
			if (actInfo.getAccountDayLimit().intValue() <= userTotalNum) {
				return false;
			}
		}
		// 商品限制
		LimitSkuType limitSkuType = actInfo.getLimitSku();
		// 参与活动的商品总金额
		BigDecimal totalAmount = BigDecimal.valueOf(0.00);
		Map<String, CurrentStoreSkuBo> haveFavourGoodsMap = Maps.newHashMap();
		if (limitSkuType == LimitSkuType.LIMIT_CATEGORY) {
			// 活动限制的分类Id列表
			List<String> limitCtgIds = actInfoDto.getBusinessIds(ActivityBusinessType.SKU_CATEGORY);
			// 指定分类
			// 遍历购买的商品
			for (CurrentStoreSkuBo storeSkuBo : parserBo.getCurrentSkuMap().values()) {
				if (limitCtgIds.contains(storeSkuBo.getSpuCategoryId())) {
					haveFavourGoodsMap.put(storeSkuBo.getId(), storeSkuBo);
					if(storeSkuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
						totalAmount = totalAmount.add(storeSkuBo.getOnlinePrice().multiply(BigDecimal.valueOf(storeSkuBo.getQuantity()-storeSkuBo.getSkuActQuantity())));
					}else{
						totalAmount = totalAmount.add(storeSkuBo.getTotalAmount());
					}
				}
			}
			if (totalAmount.compareTo(BigDecimal.valueOf(0.00)) == 0 || totalAmount.compareTo(condition.getArrive()) == -1) {
				return false;
			}
			parserBo.setHaveFavourGoodsMap(haveFavourGoodsMap);
			parserBo.setTotalAmountHaveFavour(totalAmount);
		} else if (limitSkuType == LimitSkuType.LIMIT_SKU) {
			// 限制商品Id列表
			List<String> limitSkuIds = actInfoDto.getBusinessIds(ActivityBusinessType.SKU);
			// 遍历购买的商品
			for (CurrentStoreSkuBo storeSkuBo : parserBo.getCurrentSkuMap().values()) {
				if (limitSkuIds.contains(storeSkuBo.getId())) {
					haveFavourGoodsMap.put(storeSkuBo.getId(), storeSkuBo);
					if(storeSkuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
						totalAmount = totalAmount.add(storeSkuBo.getOnlinePrice().multiply(BigDecimal.valueOf(storeSkuBo.getQuantity()-storeSkuBo.getSkuActQuantity())));
					}else{
						totalAmount = totalAmount.add(storeSkuBo.getTotalAmount());
					}
				}
			}
			if (totalAmount.compareTo(BigDecimal.valueOf(0.00)) == 0 || totalAmount.compareTo(condition.getArrive()) == -1) {
				return false;
			}
			parserBo.setHaveFavourGoodsMap(haveFavourGoodsMap);
			parserBo.setTotalAmountHaveFavour(totalAmount);
		}
		
		// 满立减只能由平台发起，属于平台优惠。且目前暂时只有满减，没有满折。后续出现满折再在此处做修改
		parserBo.setPlatformPreferential(condition.getDiscount());
		return isValid;
	}

	private boolean isFirstOrderUser(String userId){
		return sysBuyerFirstOrderRecordService.isExistsOrderRecord(userId) ? false
				: true;
	}
	
	private boolean checkFareCoupons(PlaceOrderParamDto paramDto,StoreSkuParserBo parserBo,Response<PlaceOrderDto> resp) throws Exception{
		ActivityCouponsRecord couponsRecord = activityCouponsRecordMapper.selectByPrimaryKey(paramDto.getFareActivityId());
		// 增加领取记录的校验。校验请求提供的领取记录是否是当前用户
		if(couponsRecord == null || !couponsRecord.getCollectUserId().equals(paramDto.getUserId())){
			return false;
		}
		if (couponsRecord.getStatus() != ActivityCouponsRecordStatusEnum.UNUSED) {
			return false;
		}
		// 查询代金券
		ActivityCoupons coupons = activityCouponsMapper.selectByPrimaryKey(couponsRecord.getCouponsId());
		// 检查金额是否达到使用下限
		if(paramDto.getEnjoyFavourTotalAmount().compareTo(new BigDecimal(coupons.getArriveLimit())) == -1){
			return false;
		}
		if(coupons.getType() != CouponsType.bldyf.ordinal()){
			return false;
		}
		if(coupons.getUseUserType() == UseUserType.ONlY_NEW_USER){
			// 仅限首单用户，检查当前用户是否为首单用户。
			if(!isFirstOrderUser(paramDto.getUserId())){
				resp.setResult(ResultCodeEnum.ACTIVITY_LIMIT_FIRST_ORDER);
				return false;
			}
		}
		if(coupons.getUseClientType() != UseClientType.ALLOW_All && coupons.getUseClientType() != EnumAdapter.convert(paramDto.getChannel())){
			return false;
		}
		ActivityRecordParamBo recParamBo = null;
		if(coupons.getDeviceDayLimit() != null && coupons.getDeviceDayLimit() > 0 && StringUtils.isNotEmpty(paramDto.getDeviceId())){
			// 同一设备id每天最多使用张数 0：不限，大于0有限制
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setPkId(coupons.getId());
			recParamBo.setDeviceId(paramDto.getDeviceId());
			recParamBo.setRecDate(DateUtils.getDate());
			int deviceTotalNum = activityCouponsRecordMapper.countDayFreq(recParamBo);
			if (coupons.getDeviceDayLimit().intValue() <= deviceTotalNum) {
				return false;
			}
		}
		if(coupons.getAccountDayLimit() != null && coupons.getAccountDayLimit() > 0){
			// 同一设备id每天最多使用张数 0：不限，大于0有限制
			recParamBo = new ActivityRecordParamBo();
			recParamBo.setPkId(coupons.getId());
			recParamBo.setUserId(paramDto.getUserId());
			recParamBo.setRecDate(DateUtils.getDate());
			int userTotalNum = activityCouponsRecordMapper.countDayFreq(recParamBo);
			if (coupons.getAccountDayLimit().intValue() <= userTotalNum) {
				return false;
			}
		}
		BigDecimal couponsValue = new BigDecimal(coupons.getFaceValue());
		parserBo.setFarePreferential(couponsValue);
		if(couponsValue.compareTo(parserBo.getFare()) >= 0){
			parserBo.setRealFarePreferential(parserBo.getFare());
		}else{
			parserBo.setRealFarePreferential(couponsValue);
		}
		
		return true;
	} 
}
