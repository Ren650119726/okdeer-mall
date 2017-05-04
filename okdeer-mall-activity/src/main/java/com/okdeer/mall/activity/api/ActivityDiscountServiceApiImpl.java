package com.okdeer.mall.activity.api;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.discount.service.ActivityDiscountServiceApi;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.discount.service.ActivityDiscountServiceApi")
public class ActivityDiscountServiceApiImpl implements ActivityDiscountServiceApi {

	private static Logger logger = LoggerFactory.getLogger(ActivityDiscountServiceApiImpl.class);
	
	@Resource
	private ActivityDiscountService activityDiscountService;
	
	@Override
	public ActivityDiscount selectByPrimaryKey(String id) {
		ActivityDiscount activityDiscount = null;
		try {
			activityDiscount = activityDiscountService.findById(id);
		} catch (Exception e) {
			logger.error("查询平台活动发生异常：{}",e);
		}
		return activityDiscount;
	}

}
