/** 
 *@Project: okdeer-mall-activity 
 *@Author: tangzj02
 *@Date: 2016年12月30日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.operate.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.dto.AppRecommendGoodsParamDto;
import com.okdeer.mall.operate.dto.ServerGoodsChoolseDto;
import com.okdeer.mall.operate.entity.ColumnAppRecommendGoods;
import com.okdeer.mall.operate.mapper.ColumnAppRecommendGoodsMapper;
import com.okdeer.mall.operate.service.ColumnAppRecommendGoodsService;

/**
 * ClassName: ColumnAppRecommendGoodsServiceImpl 
 * @Description: APP端服务商品推荐与商品关联服务
 * @author tangzj02
 * @date 2016年12月30日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 * 	        友门鹿2.0       2016-12-30       tangzj02                           添加
 */

@Service
public class ColumnAppRecommendGoodsServiceImpl extends BaseServiceImpl implements ColumnAppRecommendGoodsService {

	@Autowired
	private ColumnAppRecommendGoodsMapper appRecommendGoodsMapper;

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.base.service.BaseServiceImpl#getBaseMapper()
	 */
	@Override
	public IBaseMapper getBaseMapper() {
		return appRecommendGoodsMapper;
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ColumnAppRecommendGoodsService#findListByRecommendId(java.lang.String)
	 */
	@Override
	public List<ColumnAppRecommendGoods> findListByRecommendId(String recommendId) {
		return appRecommendGoodsMapper.findListByRecommendId(recommendId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ColumnAppRecommendGoodsService#findList(com.okdeer.mall.operate.dto.AppRecommendGoodsParamDto)
	 */
	@Override
	public List<ColumnAppRecommendGoods> findList(AppRecommendGoodsParamDto paramDto) throws Exception {
		return appRecommendGoodsMapper.findList(paramDto);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ColumnAppRecommendGoodsService#deleteByRecommendId(java.lang.String)
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int deleteByRecommendId(String recommendId) throws Exception {
		if (StringUtils.isBlank(recommendId)) {
			return 0;
		}
		return appRecommendGoodsMapper.deleteByRecommendId(recommendId);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.activity.service.ColumnAppRecommendGoodsService#insertMore(java.util.List)
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int insertMore(List<ColumnAppRecommendGoods> goodsList) throws Exception {
		return appRecommendGoodsMapper.insertMore(goodsList);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendGoodsService#findShowListByStoreSkuIds(java.util.List)
	 */
	@Override
	public List<ColumnAppRecommendGoods> findShowListByStoreSkuIds(List<String> storeSkuIds) throws Exception {
		return appRecommendGoodsMapper.findShowListByStoreSkuIds(storeSkuIds);
	}

	@Override
	public PageUtils<ServerGoodsChoolseDto> findServerGoodsChoolseList(ServerGoodsChoolseDto serverGoodsChoolseDto)
			throws Exception {
		PageHelper.startPage(serverGoodsChoolseDto.getPageNumber(), serverGoodsChoolseDto.getPageSize(), true);
		List<ServerGoodsChoolseDto> result = appRecommendGoodsMapper.findServerGoodsList(serverGoodsChoolseDto);
		if (result == null) {
			result = new ArrayList<ServerGoodsChoolseDto>();
		}
		return new PageUtils<ServerGoodsChoolseDto>(result);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.operate.service.ColumnAppRecommendGoodsService#findListByStoreSkuIds(java.util.List)
	 */
	@Override
	public List<ColumnAppRecommendGoods> findListByStoreSkuIds(List<String> storeSkuIds) {
		return appRecommendGoodsMapper.findListByStoreSkuIds(storeSkuIds);
	}

}
