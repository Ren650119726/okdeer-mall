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
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.archive.goods.store.dto.GoodsStoreActivitySkuDto;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.advert.entity.ColumnAdvertGoods;
import com.okdeer.mall.operate.advert.bo.ActivityAdvertStoreSkuBo;
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
	
	/**
	 * @Description: 根据运营活动id获取广告商品列表
	 * @param modelId  广告模块id
	 * @param storeId 店铺id
	 * @return list
	 * @author tuzhd
	 * @date 2017年4月12日
	 */
	public PageUtils<GoodsStoreActivitySkuDto> findAdvertGoodsByAdvertId(String modelId, String storeId, 
			Integer pageNumber, Integer pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<GoodsStoreActivitySkuDto> list = columnAdvertGoodsMapper.findAdvertGoodsByAdvertId(modelId, storeId);
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
	
	/**
	 * @Description: 批量添加 活动商品列表
	 * @param list   要插入的商品集合
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月17日
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveBatch(List<ColumnAdvertGoods> list){
		columnAdvertGoodsMapper.saveBatch(list);
	}

	@Override
	public List<ActivityAdvertStoreSkuBo> findServiceSkuByModelId(String modelId, String activityAdvertId) {
		return columnAdvertGoodsMapper.findServiceSkuByModelId(modelId, activityAdvertId);
	}

	@Override
	public List<ActivityAdvertStoreSkuBo> findCloudSkuByModelId(String modelId, String activityAdvertId) {
		return columnAdvertGoodsMapper.findCloudSkuByModelId(modelId, activityAdvertId);
	}

	/**
	 * @Description: 删除关联商品信息by活动id
	 * @param activityAdvertId 活动id
	 * @return int  
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月19日
	 */
	public int deleteByActivityAdvertId(String activityAdvertId){
		return columnAdvertGoodsMapper.deleteByActivityAdvertId(activityAdvertId);
	}
}
