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
import com.okdeer.mall.activity.entity.HomeIconArea;
import com.okdeer.mall.activity.mapper.HomeIconAreaMapper;
import com.okdeer.mall.activity.service.HomeIconAreaService;

/**
 * ClassName: HomeIconAreaServiceImpl 
 * @Description: 首页ICON与城市关联服务
 * @author tangzj02
 * @date 2016年12月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-28        tangzj02                     添加
 */

@Service
public class HomeIconAreaServiceImpl extends BaseServiceImpl implements HomeIconAreaService {

	@Autowired
	private HomeIconAreaMapper homeIconAreaMapper;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return homeIconAreaMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.HomeIconAreaService#findListByHomeIcon(java.lang.String)
	 */
	@Override
	public List<HomeIconArea> findListByHomeIcon(String iconId) throws Exception {
		return homeIconAreaMapper.findListByHomeIcon(iconId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.HomeIconAreaService#deleteByHomeIconId(java.lang.String)
	 */
	@Override
	public int deleteByHomeIconId(String iconId) throws Exception {
		return homeIconAreaMapper.deleteByHomeIconId(iconId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.HomeIconAreaService#insertMore(java.util.List)
	 */
	@Override
	public int insertMore(List<HomeIconArea> scopeList) throws Exception {
		return homeIconAreaMapper.insertMore(scopeList);
	}

}
