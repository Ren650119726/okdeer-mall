/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.advert.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.goods.store.dto.GoodsStoreActivitySkuDto;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.advert.entity.ColumnAdvertGoods;
import com.okdeer.mall.activity.advert.service.ColumnAdvertGoodsApi;
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
@Service(version="1.0.0")
public class ColumnAdvertGoodsApiImpl implements ColumnAdvertGoodsApi {

	/**
	 * 活动商品中间表Service
	 */
	@Autowired
	ColumnAdvertGoodsService columnAdvertGoodsService;
	
	@Override
	public List<ColumnAdvertGoods> findByAdvertId(String advertId) {
		return columnAdvertGoodsService.findByAdvertId(advertId);
	}

	/**
	 * @Description: 根据运营活动id获取广告商品列表
	 * @param modelId  广告模块id
	 * @return list
	 * @author tuzhd
	 * @param storeId 
	 * @date 2017年4月12日
	 */
	public PageUtils<GoodsStoreActivitySkuDto> findAdvertGoodsByAdvertId(String modelId, String storeId, Integer pageNumber, Integer pageSize){
		return columnAdvertGoodsService.findAdvertGoodsByAdvertId(modelId, storeId, pageNumber, pageSize);
	}
	
	/**
	 * @Description:根据店铺活动类型 活动商品列表
	 * @param storeId
	 * @param saleType
	 * @author tuzhd
	 * @date 2017年4月12日
	 */
	public List<GoodsStoreActivitySkuDto> findGoodsByActivityType(String storeId,Integer saleType){
		return columnAdvertGoodsService.findGoodsByActivityType(storeId, saleType);
	}
	
	/**
	 * @Description: 获取广告服务商品列表
	 * @param map  查询参数
	 * @return list
	 * @author zhangkn
	 * @date 2016年10月18日
	 */
	public List<Map<String,Object>> listGoodsForAdvert(Map<String, Object> map){
		return columnAdvertGoodsService.listGoodsForAdvert(map);
	}

}
