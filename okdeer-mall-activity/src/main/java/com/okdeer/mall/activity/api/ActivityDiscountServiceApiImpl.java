package com.okdeer.mall.activity.api;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.bdp.address.entity.Address;
import com.okdeer.bdp.address.service.IAddressService;
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
	 * 基础数据平台地址Service
	 */
	@Reference(version = "1.0.0", check = false)
	IAddressService addressService;
	
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
	public boolean isJoinPinMoney(ActivityPinMoneyDto activityPinMoneyDto, String storeId, String cityName) {
		//根据城市名称获取城市地址信息
		Address address = addressService.getByName(cityName);
		return activityDiscountService.isJoinPinMoney(activityPinMoneyDto, storeId,address.getId().toString());
	}

	@Override
	public List<ActivityDiscountCondition> getActivityDiscountConditions(String discountId) {
		// TODO Auto-generated method stub
		return null;
	}

}
