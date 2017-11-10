package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.base.service.GoodsNavigateCategoryServiceApi;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.common.utils.EnumAdapter;
import com.okdeer.mall.activity.bo.FavourParamBO;
import com.okdeer.mall.activity.bo.FavourParamBuilder;
import com.okdeer.mall.activity.coupons.bo.ActivityRecordParamBo;
import com.okdeer.mall.activity.coupons.bo.UserCouponsBo;
import com.okdeer.mall.activity.coupons.bo.UserCouponsFilterContext;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityCouponsRecordStatusEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.enums.CouponsType;
import com.okdeer.mall.activity.coupons.mapper.ActivityCollectCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRelationStoreMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountCondition;
import com.okdeer.mall.activity.discount.enums.ActivityBusinessType;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.enums.LimitSkuType;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountConditionMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountRecordService;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.dto.ActivityInfoDto;
import com.okdeer.mall.activity.dto.ActivityParamDto;
import com.okdeer.mall.activity.service.CouponsFilterStrategy;
import com.okdeer.mall.activity.service.impl.CouponsFilterStrategyFactory;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.enums.UseClientType;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
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
	
	private static final Logger logger = LoggerFactory.getLogger(CheckFavourServiceImpl.class);
	
	@Resource
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;

	/**
	 * 代金券记录Mapper
	 */
	@Resource
	private ActivityCouponsRecordService activityCouponsRecordService;
	
	@Resource
	private ActivityCollectCouponsMapper activityCollectCouponsMapper;

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
	private ActivityCouponsRelationStoreMapper activityCouponsRelationStoreMapper;

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
	
	@Resource
	private CouponsFilterStrategyFactory couponsFilterStrategyFactory;
	
	@Resource
	private FavourParamBuilder favourParamBuilder;

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		ActivityTypeEnum activityType = paramDto.getActivityType();
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		// 刷新请求商品列表
		if(paramDto.getChannel() != OrderResourceEnum.MEMCARD && 
				paramDto.getChannel() != OrderResourceEnum.SWEEP && 
				paramDto.getChannel() != OrderResourceEnum.WECHAT_MIN){
			parserBo.refreshReqSkuList(paramDto);
		}
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
		if(isValid && !StringUtils.isEmpty(paramDto.getFareRecId())){
			// 如果请求中存在运费领取记录Id，则检查运费
			isValid = checkFareCoupons(paramDto, parserBo, resp);
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
	private boolean checkCoupons(PlaceOrderParamDto paramDto, StoreSkuParserBo parserBo, Response<PlaceOrderDto> resp)
			throws Exception {
		String recordId = paramDto.getRecordId();
		ActivityCouponsRecord couponsRecord = activityCouponsRecordService.selectByPrimaryKey(recordId);
		// 增加领取记录的校验。校验请求提供的领取记录是否是当前用户
		if(couponsRecord == null || !couponsRecord.getCollectUserId().equals(paramDto.getUserId())){
			logger.info("代金券使用检查不通过1：{}",JsonMapper.nonDefaultMapper().toJson(couponsRecord));
			return false;
		}
		if (couponsRecord.getStatus() != ActivityCouponsRecordStatusEnum.UNUSED) {
			logger.info("代金券使用检查不通过2：{}",JsonMapper.nonDefaultMapper().toJson(couponsRecord));
			return false;
		}
		// 查询代金券
		ActivityCoupons coupons = activityCouponsMapper.selectByPrimaryKey(couponsRecord.getCouponsId());
		// 查询代金券活动信息
		ActivityCollectCoupons couponsActInfo = activityCollectCouponsMapper.get(coupons.getActivityId());
		CouponsFilterStrategy filterStrategy = couponsFilterStrategyFactory.get(coupons.getType());
		// 构建过滤的条件参数
		// 1、构建用户代金券信息
		UserCouponsBo userCouponsBo = new UserCouponsBo();
		userCouponsBo.setCollectRecord(couponsRecord);
		userCouponsBo.setCouponsInfo(coupons);
		userCouponsBo.setCouponsActInfo(couponsActInfo);
		// 2、构建用户优惠请求信息
		parserBo.refreshReqSkuList(paramDto);
		FavourParamBO favourParamBo = favourParamBuilder.build(paramDto, resp.getData(), parserBo.getCategoryIdSet(),
				paramDto.getSkuList());
		// 统计代金券使用记录
		activityCouponsRecordService.countUseRecord(favourParamBo,Arrays.asList(new UserCouponsBo[]{userCouponsBo}));
		// 3、构建过滤上下文对象
		UserCouponsFilterContext filterContext = new UserCouponsFilterContext();
		filterContext.setEnjoyFavourAmount(favourParamBo.getTotalAmount());
		// 检查代金券
		if(!filterStrategy.accept(userCouponsBo, favourParamBo, filterContext)){
			return false;
		}
		// 设置参与优惠的商品id列表
		parserBo.setEnjoyFavourSkuIdList(filterContext.getEnjoyFavourSkuIdList());
		// 设置参与优惠的总金额
		parserBo.setTotalAmountHaveFavour(filterContext.getEnjoyFavourAmount());
		// 设置平台优惠金额
		parserBo.setPlatformPreferential(calucateDiscountAmount(coupons, filterContext.getEnjoyFavourAmount()));
		parserBo.addCoupons(couponsRecord);
		return true;
	}
	
	private BigDecimal calucateDiscountAmount(ActivityCoupons couponsInfo,BigDecimal enjoyFavoutAmount){
		BigDecimal discountAmount = null;
		if (couponsInfo.getType() == CouponsType.tyzkq.getValue()) {
			// 如果是折扣券，优惠金额等于享受优惠的总金额*(100-折扣比例)/100.且不得超过折扣上限
			BigDecimal orderDiscountMax = BigDecimal.valueOf(couponsInfo.getOrderDiscountMax());
			discountAmount = enjoyFavoutAmount.multiply(BigDecimal.valueOf(100 - couponsInfo.getFaceValue()))
					.divide(BigDecimal.valueOf(100), BigDecimal.ROUND_DOWN);
			if(orderDiscountMax.compareTo(BigDecimal.ZERO) > 0){
				// 如果设置了上线，需要取最小值
				if(discountAmount.compareTo(orderDiscountMax) >= 0){
					discountAmount = orderDiscountMax;
				}
			}
		}else{
			discountAmount = BigDecimal.valueOf(couponsInfo.getFaceValue());
		}
		return discountAmount;
	}
	
	/**
	 * @Description: 校验满减满折
	 * @param activityId 活动ID
	 * @return boolean  
	 * @author maojj
	 * @throws Exception 
	 * @date 2016年7月14日
	 */
	private boolean checkDiscount(PlaceOrderParamDto paramDto, StoreSkuParserBo parserBo) throws Exception {
		String userId = paramDto.getUserId();
		String activityItemId = paramDto.getActivityItemId();
		boolean isValid = true;
		ActivityDiscountCondition condition = activityDiscountConditionMapper.findById(activityItemId);
		String activityId = condition.getDiscountId();
		if(!activityId.equals(paramDto.getActivityId())){
			// 如果请求的满减活动Id与满减条件Id不对应，则返回失败
			return false;
		}
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
		if(!activityIds.contains(activityId)){
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
		List<String> enjoyFavourSkuIdList = Lists.newArrayList();
		if (limitSkuType == LimitSkuType.LIMIT_CATEGORY) {
			// 活动限制的分类Id列表
			List<String> limitCtgIds = actInfoDto.getBusinessIds(ActivityBusinessType.SKU_CATEGORY);
			// 指定分类
			// 遍历购买的商品
			for (CurrentStoreSkuBo storeSkuBo : parserBo.getCurrentSkuMap().values()) {
				if (limitCtgIds.contains(storeSkuBo.getSpuCategoryId())) {
					if (storeSkuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()
							&& storeSkuBo.getQuantity() == storeSkuBo.getSkuActQuantity()) {
						// 如果商品是低价商品，且商品购买数量=商品低价购买数量，则说明该商品不享受优惠活动
						continue;
					}
					enjoyFavourSkuIdList.add(storeSkuBo.getId());
					if(storeSkuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
						totalAmount = totalAmount.add(storeSkuBo.getOnlinePrice().multiply(
								BigDecimal.valueOf(storeSkuBo.getQuantity() - storeSkuBo.getSkuActQuantity())));
					}else{
						totalAmount = totalAmount.add(storeSkuBo.getTotalAmount());
					}
				}
			}
			if (totalAmount.compareTo(BigDecimal.valueOf(0.00)) == 0 || totalAmount.compareTo(condition.getArrive()) < 0) {
				return false;
			}
			parserBo.setEnjoyFavourSkuIdList(enjoyFavourSkuIdList);
			parserBo.setTotalAmountHaveFavour(totalAmount);
		} else if (limitSkuType == LimitSkuType.LIMIT_SKU) {
			// 限制商品Id列表
			List<String> limitSkuIds = actInfoDto.getBusinessIds(ActivityBusinessType.SKU);
			// 遍历购买的商品
			for (CurrentStoreSkuBo storeSkuBo : parserBo.getCurrentSkuMap().values()) {
				if (limitSkuIds.contains(storeSkuBo.getId())) {
					if (storeSkuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()
							&& storeSkuBo.getQuantity() == storeSkuBo.getSkuActQuantity()) {
						// 如果商品是低价商品，且商品购买数量=商品低价购买数量，则说明该商品不享受优惠活动
						continue;
					}
					enjoyFavourSkuIdList.add(storeSkuBo.getId());
					if(storeSkuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
						totalAmount = totalAmount.add(storeSkuBo.getOnlinePrice().multiply(
								BigDecimal.valueOf(storeSkuBo.getQuantity() - storeSkuBo.getSkuActQuantity())));
					}else{
						totalAmount = totalAmount.add(storeSkuBo.getTotalAmount());
					}
				}
			}
			if (totalAmount.compareTo(BigDecimal.valueOf(0.00)) == 0 || totalAmount.compareTo(condition.getArrive()) < 0) {
				return false;
			}
			parserBo.setEnjoyFavourSkuIdList(enjoyFavourSkuIdList);
			parserBo.setTotalAmountHaveFavour(totalAmount);
		} else {
			// 商品没有任何限制，判定能够享受满减总金额是否达到满减条件的限制金额
			if(parserBo.getTotalAmountHaveFavour().compareTo(condition.getArrive()) < 0){
				return false;
			}
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
		// Begin V2.5.1 added by maojj 2017-07-29
		// 为了解决IOS到店自提请求中提交了运费券的bug。免运费时提交了云费券的bug
		if(paramDto.getPickType() == PickUpTypeEnum.TO_STORE_PICKUP || parserBo.getFare().compareTo(BigDecimal.ZERO) == 0){
			// 如果是到店自提，则将运费券记录id置为空
			paramDto.setFareRecId("");
			return true;
		}
		// End V2.5.1 added by maojj 2017-07-29
		ActivityCouponsRecord couponsRecord = activityCouponsRecordMapper.selectByPrimaryKey(paramDto.getFareRecId());
		// 增加领取记录的校验。校验请求提供的领取记录是否是当前用户
		if(couponsRecord == null || !couponsRecord.getCollectUserId().equals(paramDto.getUserId())){
			return false;
		}
		if (couponsRecord.getStatus() != ActivityCouponsRecordStatusEnum.UNUSED) {
			return false;
		}
		// 查询代金券
		ActivityCoupons coupons = activityCouponsMapper.selectByPrimaryKey(couponsRecord.getCouponsId());
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
		if (coupons.getUseClientType() != UseClientType.ALLOW_All
				&& coupons.getUseClientType() != EnumAdapter.convert(paramDto.getChannel())) {
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
		parserBo.addCoupons(couponsRecord);
		parserBo.setFareActivityId(coupons.getActivityId());
		return true;
	} 
}
