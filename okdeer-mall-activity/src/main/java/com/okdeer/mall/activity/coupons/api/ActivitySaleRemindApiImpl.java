package com.okdeer.mall.activity.coupons.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRemindApi;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRemindService;

/**
 * 
 * ClassName: ActivitySaleRemindApiImpl 
 * @Description: 活动安全库存预警提醒
 * @author tangy
 * @date 2017年2月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2017年2月21日                               tangy             新增
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivitySaleRemindApi")
public class ActivitySaleRemindApiImpl implements ActivitySaleRemindApi {

	/**
	 * logger
	 */
	private static final Logger log = LoggerFactory.getLogger(ActivitySaleRemindApiImpl.class);
 
	/**
	 * 安全库存联系关联人
	 */
	@Autowired
	private ActivitySaleRemindService activitySaleRemindService;
 

}
