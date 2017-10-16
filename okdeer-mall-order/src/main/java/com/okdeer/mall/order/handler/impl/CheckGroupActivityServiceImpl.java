package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.activity.bo.ActivityJoinRecParamBo;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountGroup;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountGroupMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityJoinRecordMapper;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.common.enums.UseUserType;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.entity.TradeOrderGroup;
import com.okdeer.mall.order.enums.GroupJoinTypeEnum;
import com.okdeer.mall.order.enums.GroupOrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.mapper.TradeOrderGroupMapper;
import com.okdeer.mall.order.mapper.TradeOrderGroupRelationMapper;
import com.okdeer.mall.system.service.SysBuyerFirstOrderRecordService;

/**
 * ClassName: CheckGroupActivityServiceImpl 
 * @Description: 检查团购活动
 * @author maojj
 * @date 2017年10月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.6.3 		2017年10月10日				maojj
 */
@Service("checkGroupActivityService")
public class CheckGroupActivityServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {

	@Resource
	private ActivityJoinRecordMapper activityJoinRecordMapper;
	
	@Resource
	private ActivityDiscountMapper activityDiscountMapper;
	
	@Resource
	private ActivityDiscountGroupMapper activityDiscountGroupMapper;
	
	@Resource
	private SysBuyerFirstOrderRecordService sysBuyerFirstOrderRecordService;
	
	@Resource
	private TradeOrderGroupMapper tradeOrderGroupMapper;
	
	@Resource
	private TradeOrderGroupRelationMapper tradeOrderGroupRelationMapper;

	/**
	 * 活动商品用户日限购
	 */
	private static final int LIMIT_SKU_USER_DAY = 0;

	/**
	 * 活动商品用户总限购
	 */
	private static final int LIMIT_SKU_USER_TOTAL = 1;

	/**
	 * 活动用户总限购
	 */
	private static final int LIMIT_ACTIVITY_USER_TOTAL = 2;

	/**
	 * 活动设备总限购
	 */
	private static final int LIMIT_ACTIVITY_DEVICE_TOTAL = 3;

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		// 获取团购的商品信息
		Map<String, CurrentStoreSkuBo> currentSkuMap = parserBo.getCurrentSkuMap();
		// 团购一次只能团购一件商品
		CurrentStoreSkuBo storeSkuBo = currentSkuMap.values().stream().findFirst().get();
		storeSkuBo.setActivityType(ActivityTypeEnum.GROUP_ACTIVITY.ordinal());
		// 团购活动id
		String groupActId = storeSkuBo.getActivityId();
		// 检查团购活动是否结束
		if(StringUtils.isEmpty(groupActId) || storeSkuBo.getActivityType() != ActivityTypeEnum.GROUP_ACTIVITY.ordinal()){
			// 如果商品关联的活动id为空，或者关联的活动类型不为团购活动，标识团购活动已经结束或者关闭
			resp.setResult(ResultCodeEnum.ACTIVITY_IS_END);
			return;
		}
		// 查询团购活动信息
		ActivityDiscount activityGroup = activityDiscountMapper.findById(groupActId);
		// 如果参团类型为加入则需要检查团购用户限制
		if(isOutOfLimitUserType(paramDto,activityGroup)){
			resp.setResult(ResultCodeEnum.GROUP_OPEN_NOT_SUPPORT);
			return;
		}
		// 检查是否可以入团
		if(!isJoinGroupEnabled(paramDto,resp.getData())){
			resp.setResult(ResultCodeEnum.GROUP_IS_ENOUGH);
			return;
		}
		// 查询团购商品信息
		ActivityDiscountGroup groupSku = activityDiscountGroupMapper.findByActivityIdAndSkuId(groupActId, storeSkuBo.getId());
		// 检查用户是否超出日限购
		if(isOutOfLimit(paramDto,storeSkuBo,groupSku.getUserDayCountLimit(),LIMIT_SKU_USER_DAY)){
			resp.setResult(ResultCodeEnum.GROUP_SKU_USER_DAY_LIMIT_OUT);
			return;
		}
		// 检查是否超出商品总限购
		if(isOutOfLimit(paramDto,storeSkuBo,groupSku.getUserCountLimit(),LIMIT_SKU_USER_TOTAL)){
			resp.setResult(ResultCodeEnum.GROUP_TOTAL_USER_LIMIT_OUT);
			return;
		}
		// 检查是否超出活动用户总限购
		if(isOutOfLimit(paramDto,storeSkuBo,activityGroup.getAccountAllLimit(),LIMIT_ACTIVITY_USER_TOTAL)){
			resp.setResult(ResultCodeEnum.GROUP_TOTAL_USER_LIMIT_OUT);
			return;
		}
		// 检查是否超出活动设备总限制
		if(isOutOfLimit(paramDto,storeSkuBo,activityGroup.getDeviceAllLimit(),LIMIT_ACTIVITY_DEVICE_TOTAL)){
			resp.setResult(ResultCodeEnum.GROUP_DEVICE_LIMIT_OUT);
			return;
		}
		// 计算平台优惠
		BigDecimal platformFavour = storeSkuBo.getOnlinePrice().subtract(groupSku.getGroupPrice());
		parserBo.setPlatformPreferential(platformFavour);
		paramDto.setActivityId(groupActId);
		paramDto.setActivityType(String.valueOf(ActivityTypeEnum.GROUP_ACTIVITY.ordinal()));
		paramDto.setActivityItemId(storeSkuBo.getId());
		paramDto.setSkuType(String.valueOf(OrderTypeEnum.GROUP_ORDER.ordinal()));
		// 缓存团购活动信息
		paramDto.put("activityGroup", activityGroup);
	}
	
	/**
	 * @Description: 是否超出活动用户限制类型
	 * @param paramDto
	 * @param activityGroup
	 * @return   
	 * @author maojj
	 * @date 2017年10月11日
	 */
	private boolean isOutOfLimitUserType(PlaceOrderParamDto paramDto,ActivityDiscount activityGroup){
		if(paramDto.getGroupJoinType() == GroupJoinTypeEnum.GROUP_OPEN){
			// 如果是开团，没有用户限制
			return false;
		}
		if(activityGroup.getLimitUser() == UseUserType.ALLOW_All){
			// 不限制
			return false;
		}
		// 限制新用户，判断当前用户是否为新用户.如果存在首单记录，则超出限制，否则不超出
		return sysBuyerFirstOrderRecordService.isExistsOrderRecord(paramDto.getUserId());
	}
	
	
	private boolean isJoinGroupEnabled(PlaceOrderParamDto paramDto,PlaceOrderDto respDto){
		if(paramDto.getGroupJoinType() == GroupJoinTypeEnum.GROUP_OPEN){
			return true;
		}
		// 如果是参团，查看参团订单的信息
		TradeOrderGroup orderGroup = tradeOrderGroupMapper.findById(paramDto.getGroupOrderId());
		if (orderGroup.getStatus() != GroupOrderStatusEnum.UN_GROUP) {
			return false;
		}
		respDto.setGroupExpireTime(orderGroup.getExpireTime().getTime() - System.currentTimeMillis());
		respDto.setAbsendNum(orderGroup.getGroupCount());
		return true;
	}

	/**
	 * @Description: 是否超过活动商品用户日限购
	 * @param userId 用户Id
	 * @param activityId 团购活动id
	 * @param storeSkuId 团购商品id
	 * @param limitDayUser 用户每日限购数量
	 * @Param buyNum 用户当前购买数量
	 * @return   
	 * @author maojj
	 * @date 2017年10月11日
	 */
	private boolean isOutOfLimit(PlaceOrderParamDto paramDto, CurrentStoreSkuBo storeSkuBo, int limitNum, int limitType) {
		if (limitNum == Constant.ZERO) {
			// 限购数为0，标识不限购
			return false;
		}
		if(limitType == 3 && StringUtils.isEmpty(paramDto.getDeviceId())){
			// 如果限设备，但是设备id为空，则不做校验
			return false;
		}
		// 查询用户当日参与活动商品总数量
		ActivityJoinRecParamBo paramBo = null;
		switch (limitType) {
			case 0:
				// 活动商品用户日限购
				paramBo = new ActivityJoinRecParamBo(paramDto.getUserId(), storeSkuBo.getActivityId(), storeSkuBo.getId(),new Date());
				break;
			case 1:
				// 活动商品用户总限购
				paramBo = new ActivityJoinRecParamBo(paramDto.getUserId(), storeSkuBo.getActivityId(), storeSkuBo.getId());
				break;
			case 2:
				// 活动用户总限购
				paramBo = new ActivityJoinRecParamBo(paramDto.getUserId(),storeSkuBo.getActivityId());
				break;
			case 3:
				// 活动设备总限购
				paramBo = new ActivityJoinRecParamBo(null,storeSkuBo.getActivityId() ,null, paramDto.getDeviceId(), null);
				break;
			default:
				break;
		}
		int totalJoinNum = activityJoinRecordMapper.countActivityJoinNum(paramBo);
		// 用户当日参与活动总数量+当前购买数量不能超过限制数量
		if (totalJoinNum + storeSkuBo.getQuantity() > limitNum) {
			return true;
		}
		return false;
	}
}
