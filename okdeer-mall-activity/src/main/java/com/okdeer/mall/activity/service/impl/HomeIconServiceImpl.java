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
import com.okdeer.mall.activity.dto.HomeIconParamDto;
import com.okdeer.mall.activity.entity.HomeIcon;
import com.okdeer.mall.activity.mapper.HomeIconMapper;
import com.okdeer.mall.activity.service.HomeIconService;

/**
 * ClassName: HomeIconServiceImpl 
 * @Description: 首页ICON的服务实现
 * @author tangzj02
 * @date 2016年12月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * * 	        友门鹿2.0        2016-12-28        tangzj02                     添加
 */

@Service
public class HomeIconServiceImpl extends BaseServiceImpl implements HomeIconService {

	@Autowired
	private HomeIconMapper homeIconMapper;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return homeIconMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.HomeIconService#findList(com.okdeer.mall.activity.dto.HomeIconParamDto)
	 */
	@Override
	public List<HomeIcon> findList(HomeIconParamDto paramDto) {
		return homeIconMapper.findList(paramDto);
	}

}
