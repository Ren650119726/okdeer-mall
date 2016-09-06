package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;

/**
 * @DESC: 特惠活动商品dao
 * @author zhangkn
 * @date  2015-11-25 16:24:57
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-15			maojj			添加批量查询活动商品信息的方法
 * 
 */
public interface ActivitySaleGoodsMapper {
	
	/**
	 * 根据主键删除
	 * @param id 主键id
	 */
	void deleteById(String id);
	
	/**
	 * 根据saleId删除
	 * @param saleId 特惠活动主键id
	 */
	void deleteBySaleId(String saleId);
	
	/**
	 * @desc 批量保存
	 * @param list 特惠活动商品list
	 */
	void saveBatch(List<ActivitySaleGoods> list);
	
	/**
	 * @desc 通过saleId取列表
	 * @param saleId 特惠活动主键id
	 * @return List<ActivitySaleGoods>
	 */
	List<ActivitySaleGoods> listBySaleId(String saleId);
	
	/**
	 * 
	 * 查询特惠活动商品  现在查询单个 
	 *
	 * @param activitySaleGoods
	 * @return
	 */
	ActivitySaleGoods selectByObject(ActivitySaleGoods activitySaleGoods);
	
	
	/**
	 * 查询特惠活动商品价格 </p>
	 * @author yangqin
	 * @param map
	 * @return
	 */
	ActivitySaleGoods  selectActivitySaleByParams(Map<String,Object> map);
	
	/**
	 * @desc 通过主键获取对象
	 * @param id
	 * @return
	 */
	ActivitySaleGoods get(String id);
	
	// Begin added by maojj 2016-07-14
	/**
	 * @Description: 根据活动ID和店铺商品id查询活动商品信息
	 * @param saleId
	 * @param storeSkuIds
	 * @return   
	 * @return List<ActivitySaleGoods>  
	 * @throws
	 * @author maojj
	 * @date 2016年7月15日
	 */
	List<ActivitySaleGoods> findActivityGoodsList(@Param("saleId")String saleId,@Param("storeSkuIds")List<String> storeSkuIds);
	// End added by maojj 2016-07-14
}