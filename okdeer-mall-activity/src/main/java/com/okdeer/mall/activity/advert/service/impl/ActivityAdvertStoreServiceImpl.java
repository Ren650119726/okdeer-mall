/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.advert.entity.ActivityAdvertStore;
import com.okdeer.mall.activity.advert.mapper.ActivityAdvertStoreMapper;
import com.okdeer.mall.activity.advert.service.ActivityAdvertStoreApi;
import com.okdeer.mall.activity.advert.service.ActivityAdvertStoreService;

/**
 * ClassName: ActivityAdvertStoreMapper 
 * @Description: 关联店铺信息实现类
 * @author tuzhd
 * @date 2017年4月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V2.2.2			2017-4-18			tuzhd				关联店铺信息实现类
 */
@Service
public class ActivityAdvertStoreServiceImpl extends BaseServiceImpl implements ActivityAdvertStoreService {

	@Autowired
	public ActivityAdvertStoreMapper activityAdvertStoreMapper;
	@Override
	public IBaseMapper getBaseMapper() {
		return activityAdvertStoreMapper;
	}
	
	/**
	 * @Description: 批量添加关联店铺信息
	 * @param list   要插入的关联店铺集合
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月17日
	 */
	public void saveBatch(List<ActivityAdvertStore> list){
		activityAdvertStoreMapper.saveBatch(list);
	}

}
