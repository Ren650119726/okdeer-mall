package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoodsBo;
import com.okdeer.mall.activity.dto.ActivitySaleGoodsParamDto;

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
	 * 通过页面查询参数查找低价抢购商品
	 * @Description: 
	 * @param param
	 * @return List<ActivitySaleGoods>
	 * @throws
	 * @author mengsj
	 * @date 2016年12月31日
	 */
	List<ActivitySaleGoodsBo> findSaleGoodsByParams(@Param("param")ActivitySaleGoodsParamDto param);
	
	/**
	 * 通过页面查询参数查找低价抢购商品-分页
	 * @Description: 
	 * @param param
	 * @param pageSize
	 * @param pageNum
	 * @return PageUtils<ActivitySaleGoodsBo>
	 * @throws
	 * @author mengsj
	 * @date 2017年1月4日
	 */
	PageUtils<ActivitySaleGoodsBo> findSaleGoodsPageByParams(@Param("param")ActivitySaleGoodsParamDto param, @Param("pageSize")Integer pageSize, @Param("pageNum")Integer pageNum);
	
	/**
	 * 批量设置低价抢购商品-暂时没用到,未实现
	 * @Description:
	 * @param saleGoods
	 * @throws Exception void
	 * @author mengsj
	 * @date 2016年12月31日
	 */
	void updateBatch(@Param("list")List<ActivitySaleGoods> saleGoods) throws Exception;
	
	/**
	 * 关闭低价抢购商品
	 * @Description:
	 * @param saleGoods void
	 * @throws
	 * @author mengsj
	 * @date 2017年1月3日
	 */
	void updateById(ActivitySaleGoods saleGoods) throws Exception;
	
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
	
	// Begin V2.0 added by maojj 2016-12-31
	/**
	 * @Description: 根据活动ID列表和店铺商品id查询活动商品信息
	 * @param saleId
	 * @param storeSkuIds
	 * @return   
	 * @author maojj
	 * @date 2016年12月31日
	 */
	List<ActivitySaleGoods> findBySaleIdsAndSkuIds(@Param("saleIds")Set<String> saleIds,@Param("storeSkuIds")List<String> storeSkuIds);
	// End added by maojj 2016-12-31
}