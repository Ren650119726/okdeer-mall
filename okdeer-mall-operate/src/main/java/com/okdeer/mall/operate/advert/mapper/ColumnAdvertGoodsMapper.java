/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ColumnAdvertGoodsMapper.java
 * @Date 2016-12-08 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.operate.advert.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.archive.goods.store.dto.GoodsStoreActivitySkuDto;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.advert.entity.ColumnAdvertGoods;

/**
 * ClassName: ColumnAdvertGoodsMapper 
 * @Description: 活动商品中间表mapper
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动商品中间表mapper
 */
 
public interface ColumnAdvertGoodsMapper extends IBaseMapper {

	/**
	 * 
	 * @Description: 通过活动id查询商品id
	 * @param activityId
	 * @return   
	 * @author xuzq01
	 * @date 2016年12月8日
	 */
	public List<ColumnAdvertGoods> findByAdvertId(String advertId);
	
	/**
	 * @Description: 根据广告id获取广告商品列表
	 * @param modelId  广告模块id
	 * @return list
	 * @author xuzq01
	 * @param storeId 
	 * @date 2017年02月08日
	 */
	List<GoodsStoreActivitySkuDto> findAdvertGoodsByAdvertId(@Param("modelId")String modelId, @Param("storeId")String storeId);
	
	
	/**
	 * @Description: 根据店铺活动类型 活动商品列表
	 * @return list
	 * @author tuzhd
	 * @param storeId 
	 * @date 2017年03月13日
	 */
	List<GoodsStoreActivitySkuDto> findGoodsByActivityType(@Param("storeId")String storeId,@Param("saleType")Integer saleType);
	
	/**
	 * @Description: 获取广告商品列表
	 * @param map  查询参数
	 * @return list
	 * @author zhangkn
	 * @date 2016年10月18日
	 */
	List<Map<String,Object>> listGoodsForAdvert(Map<String, Object> map);
}