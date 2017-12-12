package com.okdeer.mall.activity.api;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.discount.dto.ActivityCloudStoreParamDto;
import com.okdeer.mall.activity.discount.dto.ActivityCloudStoreResultDto;
import com.okdeer.mall.activity.discount.service.ActivityCloudStoreService;
import com.okdeer.mall.activity.discount.service.ActivityCloudStoreServiceApi;

/**
 * ClassName: ActivityCloudStoreServiceApiImpl 
 * @Description: 店铺活动服务实现
 * @author tuzhd
 * @date 2017年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.7				2017-12-07         tuzhd			 便利店活动服务实现
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.discount.service.ActivityCloudStoreServiceApi")
public class ActivityCloudStoreServiceApiImpl implements ActivityCloudStoreServiceApi {
	@Resource
	private ActivityCloudStoreService activityCloudStoreService;
	
	/**
	 * 获取购物车商品的活动
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.discount.service.ActivityCloudStoreServiceApi#getCloudStoreActivity(com.okdeer.mall.activity.discount.dto.ActivityCloudStoreParamDto)
	 */
	@Override
	public ActivityCloudStoreResultDto getCloudStoreActivity(ActivityCloudStoreParamDto paramDto) throws Exception {
		return activityCloudStoreService.getCloudStoreActivity(paramDto);
	}

}
