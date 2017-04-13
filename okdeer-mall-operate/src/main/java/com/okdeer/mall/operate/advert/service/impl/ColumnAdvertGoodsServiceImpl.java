/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.advert.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.store.dto.GoodsStoreActivitySkuDto;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.advert.entity.ColumnAdvertGoods;
import com.okdeer.mall.operate.advert.mapper.ColumnAdvertGoodsMapper;
import com.okdeer.mall.operate.advert.service.ColumnAdvertGoodsService;

/**
 * ClassName: ColumnAdvertGoodsApiImpl 
 * @Description: 活动商品中间表Service实现类
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动商品中间表Service实现类
 */
@Service
public class ColumnAdvertGoodsServiceImpl extends BaseServiceImpl implements ColumnAdvertGoodsService {

	/**
	 * 活动商品中间表mapper
	 */
	@Autowired
	ColumnAdvertGoodsMapper columnAdvertGoodsMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return columnAdvertGoodsMapper;
	}

	@Override
	public List<ColumnAdvertGoods> findByAdvertId(String advertId) {
		return columnAdvertGoodsMapper.findByAdvertId(advertId);
	}
	
	@Override
	public PageUtils<GoodsStoreActivitySkuDto> findAdvertGoodsByAdvertId(String advertId, String storeId, 
			Integer pageNumber, Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<GoodsStoreActivitySkuDto> list = columnAdvertGoodsMapper.findAdvertGoodsByAdvertId(advertId, storeId);
		return new PageUtils<GoodsStoreActivitySkuDto>(list);
	}
	
	/**
	 * @Description:根据店铺活动类型 活动商品列表
	 * @param storeId
	 * @param saleType
	 * @author tuzhd
	 * @date 2017年3月13日
	 */
	@Override
	public List<GoodsStoreActivitySkuDto> findGoodsByActivityType(String storeId,Integer saleType) {
		return columnAdvertGoodsMapper.findGoodsByActivityType(storeId,saleType);
	}
	
	/**
	 * @Description:根据服务店商品id查询商品信息
	 * @param storeId
	 * @author tuzhd
	 * @date 2017年3月13日
	 */
	@Override
	public List<Map<String, Object>> listGoodsForAdvert(Map<String, Object> map) {
		return columnAdvertGoodsMapper.listGoodsForAdvert(map);
	}

}
