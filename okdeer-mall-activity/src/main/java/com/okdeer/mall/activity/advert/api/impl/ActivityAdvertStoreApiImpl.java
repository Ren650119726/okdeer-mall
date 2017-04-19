/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.api.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.advert.bo.ActivityAdvertStoreBo;
import com.okdeer.mall.activity.advert.dto.ActivityAdvertStoreDto;
import com.okdeer.mall.activity.advert.service.ActivityAdvertStoreApi;
import com.okdeer.mall.activity.advert.service.ActivityAdvertStoreService;

/**
 * ClassName: ActivityAdvertStoreMapper 
 * @Description: 关联店铺信息对外实现类
 * @author tuzhd
 * @date 2017年4月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V2.2.2			2017-4-18			tuzhd			关联店铺信息对外实现类
 */
@Service(version="1.0.0")
public class ActivityAdvertStoreApiImpl implements ActivityAdvertStoreApi {
	@Autowired
	public ActivityAdvertStoreService activityAdvertStoreService;
	/**
	 * @Description:查询店铺信息根据活动id
	 * @param activityAdverId
	 * @return List<ActivityAdvertStoreDto>  
	 * @author tuzhd
	 * @date 2017年4月19日
	 */
	public List<ActivityAdvertStoreDto> findShopByAdvertId(String activityAdverId){
		List<ActivityAdvertStoreBo> bo = activityAdvertStoreService.findShopByAdvertId(activityAdverId);
		return BeanMapper.mapList(bo, ActivityAdvertStoreDto.class);
	}
}
