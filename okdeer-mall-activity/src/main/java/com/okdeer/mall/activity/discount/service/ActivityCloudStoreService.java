package com.okdeer.mall.activity.discount.service;

import com.okdeer.mall.activity.discount.dto.ActivityCloudStoreParamDto;
import com.okdeer.mall.activity.discount.dto.ActivityCloudStoreResultDto;

/**
 * ClassName: ActivityCloudStoreService 
 * @Description: 便利店活动服务接口
 * @author tuzhd
 * @date 2017年12月7日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.7				2017-12-07         tuzhd			 便利店活动服务接口
 */
public interface ActivityCloudStoreService {
	
	/**
	 * @Description: 根据店铺及商品查询店铺活动信息，满赠、加价购、N件X元
	 * @param paramDto
	 * @return ActivityCloudStoreResultDto  
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年12月7日
	 */
	ActivityCloudStoreResultDto getCloudStoreActivity(ActivityCloudStoreParamDto paramDto) throws Exception;

}
