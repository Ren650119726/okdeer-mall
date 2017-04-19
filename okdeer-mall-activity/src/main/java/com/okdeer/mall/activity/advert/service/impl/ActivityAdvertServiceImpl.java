/** 
 *@Project: okdeer-mall-api 
 *@Author: xuzq01
 *@Date: 2017年4月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.activity.advert.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.advert.entity.ActivityAdvert;
import com.okdeer.mall.activity.advert.mapper.ActivityAdvertMapper;
import com.okdeer.mall.activity.advert.service.ActivityAdvertService;

/**
 * ClassName: ActivityAdvertServiceImpl 
 * @Description: 
 * @author xuzq01
 * @date 2017年4月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class ActivityAdvertServiceImpl extends BaseServiceImpl implements ActivityAdvertService {
	
	/**
	 * H5活动Service
	 */
	@Autowired
	ActivityAdvertMapper activityAdvertMapper;
	
	
	@Override
	public IBaseMapper getBaseMapper() {
		return activityAdvertMapper;
	}

	@Override
	public int findCountByName(String advertName) {
		return activityAdvertMapper.findCountByName(advertName);
	}

	@Override
	public PageUtils<ActivityAdvert> findActivityAdvertList(ActivityAdvert activityAdvert, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<ActivityAdvert> result = activityAdvertMapper.findActivityAdvertList(activityAdvert);
		return new PageUtils<ActivityAdvert>(result);
	}
	
	/**
	 * @Description: 保存新增活动信息
	 * @param ActivityAdvert 活动对象
	 * @param userId    用户id
	 * @return void  
	 * @author tuzhd
	 * @throws Exception 
	 * @date 2017年4月18日
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addActivityAdvert(ActivityAdvert activityAdvert) throws Exception{
		activityAdvertMapper.add(activityAdvert);
	}


	@Override
	public List<ActivityAdvert> findActivityListByStatus(List<String> statusList) {
		return activityAdvertMapper.findActivityListByStatus(statusList);
	}

}
