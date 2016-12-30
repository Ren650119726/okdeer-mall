/** 
 *@Project: okdeer-mall-operate 
 *@Author: tangzj02
 *@Date: 2016年12月28日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.entity.ActivitySelectArea;
import com.okdeer.mall.activity.mapper.ActivitySelectAreaMapper;
import com.okdeer.mall.activity.service.ActivitySelectAreaService;

/**
 * ClassName: ActivitySelectAreaServiceImpl 
 * @Description: 活动与城市关联服务
 * @author tangzj02
 * @date 2016年12月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-28        tangzj02                     添加
 */

@Service
public class ActivitySelectAreaServiceImpl extends BaseServiceImpl implements ActivitySelectAreaService {

	@Autowired
	private ActivitySelectAreaMapper activitySelectAreaMapper;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return activitySelectAreaMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ActivitySelectAreaService#findListByActivityId(java.lang.String)
	 */
	@Override
	public List<ActivitySelectArea> findListByActivityId(String iconId) throws Exception {
		return activitySelectAreaMapper.findListByActivityId(iconId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ActivitySelectAreaService#deleteByActivityId(java.lang.String)
	 */
	@Override
	public int deleteByActivityId(String iconId) throws Exception {
		return activitySelectAreaMapper.deleteByActivityId(iconId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ActivitySelectAreaService#insertMore(java.util.List)
	 */
	@Override
	public int insertMore(List<ActivitySelectArea> scopeList) throws Exception {
		return activitySelectAreaMapper.insertMore(scopeList);
	}

}
