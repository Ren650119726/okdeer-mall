package com.okdeer.mall.activity.api;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountCondition;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountType;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.discount.service.ActivityDiscountServiceApi;
import com.okdeer.mall.activity.dto.ActivityDiscountQueryDto;
import com.okdeer.mall.activity.dto.ActivityParamDto;
import com.okdeer.mall.activity.dto.ActivityPinMoneyDto;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.discount.service.ActivityDiscountServiceApi")
public class ActivityDiscountServiceApiImpl implements ActivityDiscountServiceApi {

	private static Logger LOGGER = LoggerFactory.getLogger(ActivityDiscountServiceApiImpl.class);
	
	@Resource
	private ActivityDiscountService activityDiscountService;
	
	/**
	 * 店铺信息api
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoApi;
	
	@Override
	public ActivityDiscount selectByPrimaryKey(String id) {
		ActivityDiscount activityDiscount = null;
		try {
			activityDiscount = activityDiscountService.findById(id);
		} catch (Exception e) {
			LOGGER.error("查询平台活动发生异常：{}",e);
		}
		return activityDiscount;
	}

	@Override
	public List<ActivityPinMoneyDto> findDiscountList(ActivityDiscountQueryDto discountQueryDto) {
		//订单来源  店铺id 活动类型 状态 城市名称
		ActivityParamDto paramDto = new ActivityParamDto();
		paramDto.setType(ActivityDiscountType.PIN_MONEY);
		paramDto.setStatus(ActivityDiscountStatus.ing);
		paramDto.setLimitChannel(discountQueryDto.getLimitChannel());
		List<ActivityDiscount> discountList = activityDiscountService.findListByParam(paramDto);
		return BeanMapper.mapList(discountList, ActivityPinMoneyDto.class);
	}

	@Override
	public boolean isJoinPinMoney(ActivityPinMoneyDto activityPinMoneyDto, String storeId) {
		// 根据店铺id获取店铺的地址信息
		StoreInfo storeInfo = storeInfoApi.findById(storeId);
		return activityDiscountService.isJoinPinMoney(activityPinMoneyDto, storeId,storeInfo.getCityId());
	}

	@Override
	public List<ActivityDiscountCondition> getActivityDiscountConditions(String discountId) {
		// TODO Auto-generated method stub
		return null;
	}

}
