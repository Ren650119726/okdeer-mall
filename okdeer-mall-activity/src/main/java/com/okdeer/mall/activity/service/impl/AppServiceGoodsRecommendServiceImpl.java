/** 
 *@Project: okdeer-mall-activity 
 *@Author: tangzj02
 *@Date: 2016年12月29日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.activity.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.dto.AppServiceGoodsRecommendParamDto;
import com.okdeer.mall.activity.entity.AppServiceGoodsRecommend;
import com.okdeer.mall.activity.mapper.AppServiceGoodsRecommendMapper;
import com.okdeer.mall.activity.service.AppServiceGoodsRecommendService;

/**
 * ClassName: AppServiceGoodsRecommendServiceImpl 
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
public class AppServiceGoodsRecommendServiceImpl extends BaseServiceImpl implements AppServiceGoodsRecommendService {

	@Autowired
	private AppServiceGoodsRecommendMapper appServiceGoodsRecommendMapper;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return appServiceGoodsRecommendMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.AppServiceGoodsRecommendService#deleteByIds(java.util.List)
	 */
	@Override
	public int deleteByIds(List<String> ids) throws Exception {
		return appServiceGoodsRecommendMapper.deleteByIds(ids);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.AppServiceGoodsRecommendService#findList(com.okdeer.mall.activity.dto.AppServiceGoodsRecommendParamDto)
	 */
	@Override
	public List<AppServiceGoodsRecommend> findList(AppServiceGoodsRecommendParamDto paramDto) throws Exception {
		return appServiceGoodsRecommendMapper.findList(paramDto);
	}

}
