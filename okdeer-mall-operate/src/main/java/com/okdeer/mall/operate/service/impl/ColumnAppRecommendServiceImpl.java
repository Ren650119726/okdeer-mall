/** 
 *@Project: okdeer-mall-activity 
 *@Author: tangzj02
 *@Date: 2016年12月29日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.dto.AppRecommendDto;
import com.okdeer.mall.operate.dto.AppRecommendParamDto;
import com.okdeer.mall.operate.entity.ColumnAppRecommend;
import com.okdeer.mall.operate.mapper.ColumnAppRecommendMapper;
import com.okdeer.mall.operate.service.ColumnAppRecommendService;

/**
 * ClassName: ColumnAppRecommendServiceImpl 
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
public class ColumnAppRecommendServiceImpl extends BaseServiceImpl implements ColumnAppRecommendService {

	@Autowired
	private ColumnAppRecommendMapper appRecommendMapper;

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
	 * @see com.okdeer.mall.activity.service.ColumnAppRecommendService#deleteByIds(java.util.List)
	 */
	@Override
	public int deleteByIds(List<String> ids) throws Exception {
		return appRecommendMapper.deleteByIds(ids);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ColumnAppRecommendService#findList(com.okdeer.mall.operate.dto.AppRecommendParamDto)
	 */
	@Override
	public List<ColumnAppRecommend> findList(AppRecommendParamDto paramDto) throws Exception {
		return appRecommendMapper.findList(paramDto);
	}

	@Override
	public PageUtils<AppRecommendDto> findListPage(AppRecommendParamDto paramDto) throws Exception {
		PageHelper.startPage(paramDto.getPageNumber(), paramDto.getPageSize(), true);
		List<ColumnAppRecommend> result = appRecommendMapper.findList(paramDto);
		if (result == null) {
			result = new ArrayList<ColumnAppRecommend>();
		}
		List<AppRecommendDto> list = BeanMapper.mapList(result, AppRecommendDto.class);
		return new PageUtils<AppRecommendDto>(list);
	}

}
