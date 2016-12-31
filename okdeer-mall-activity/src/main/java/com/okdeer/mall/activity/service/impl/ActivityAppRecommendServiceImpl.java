/** 
 *@Project: okdeer-mall-activity 
 *@Author: tangzj02
 *@Date: 2016年12月29日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.dto.ActivityAppRecommendDto;
import com.okdeer.mall.activity.dto.AppRecommendParamDto;
import com.okdeer.mall.activity.entity.ActivityAppRecommend;
import com.okdeer.mall.activity.mapper.ActivityAppRecommendMapper;
import com.okdeer.mall.activity.service.ActivityAppRecommendService;
import com.okdeer.mall.operate.dto.SkinManagerDto;

/**
 * ClassName: ActivityAppRecommendServiceImpl 
 * @Description: APP端服务商品推荐服务实现
 * @author tangzj02
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-29        tangzj02                     添加
 */
@Service
public class ActivityAppRecommendServiceImpl extends BaseServiceImpl implements ActivityAppRecommendService {

	@Autowired
	private ActivityAppRecommendMapper appRecommendMapper;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return appRecommendMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendService#deleteByIds(java.util.List)
	 */
	@Override
	public int deleteByIds(List<String> ids) throws Exception {
		return appRecommendMapper.deleteByIds(ids);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ActivityAppRecommendService#findList(com.okdeer.mall.activity.dto.AppRecommendParamDto)
	 */
	@Override
	public List<ActivityAppRecommend> findList(AppRecommendParamDto paramDto) throws Exception {
		return appRecommendMapper.findList(paramDto);
	}

	@Override
	public PageUtils<ActivityAppRecommendDto> findPageList(AppRecommendParamDto paramDto) {
		PageHelper.startPage(paramDto.getPageNumber(), paramDto.getPageSize(), true);
		List<ActivityAppRecommend> result = appRecommendMapper.findList(paramDto);
		if (result == null) {
			result = new ArrayList<ActivityAppRecommend>();
		}
		List<ActivityAppRecommendDto> list = BeanMapper.mapList(result, ActivityAppRecommendDto.class);
		return new PageUtils<ActivityAppRecommendDto>(list);
	}

}
