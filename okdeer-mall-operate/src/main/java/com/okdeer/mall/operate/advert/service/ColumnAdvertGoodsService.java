/** 
 *@Project: okdeer-mall-activity 
 *@Author: xuzq01
 *@Date: 2016年12月8日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.operate.advert.service;

import java.util.List;
import java.util.Map;

import com.okdeer.archive.goods.store.dto.GoodsStoreActivitySkuDto;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.advert.entity.ColumnAdvertGoods;

/**
 * ClassName: ColumnAdvertGoodsApiImpl 
 * @Description: 活动商品中间表
 * @author xuzq01
 * @date 2016年12月8日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		v1.2.3			2016年12月8日		xuzq01				活动商品中间表Service
 */

public interface ColumnAdvertGoodsService extends IBaseService {

	/**
	 * @Description: 通过广告id查询广告商品id
	 * @param advertId
	 * @return   
	 * @author xuzq01
	 * @date 2016年12月10日
	 */
	List<ColumnAdvertGoods> findByAdvertId(String advertId);
	/**
	 * @Description: 根据运营活动id获取广告商品列表
	 * @param modelId  广告模块id
	 * @return list
	 * @author tuzhd
	 * @param storeId 
	 * @date 2017年4月12日
	 */
	PageUtils<GoodsStoreActivitySkuDto> findAdvertGoodsByAdvertId(String modelId, String storeId, Integer pageNumber, Integer pageSize);
	
	/**
	 * @Description:根据店铺活动类型 活动商品列表
	 * @param storeId
	 * @param saleType
	 * @author tuzhd
	 * @date 2017年4月12日
	 */
	public List<GoodsStoreActivitySkuDto> findGoodsByActivityType(String storeId,Integer saleType);
	
	/**
	 * @Description: 获取广告服务商品列表
	 * @param map  查询参数
	 * @return list
	 * @author zhangkn
	 * @date 2016年10月18日
	 */
	List<Map<String,Object>> listGoodsForAdvert(Map<String, Object> map);
	
	/**
	 * @Description: 批量添加 活动商品列表
	 * @param list   要插入的商品集合
	 * @throws
	 * @author tuzhd
	 * @date 2017年4月17日
	 */
	void saveBatch(List<ColumnAdvertGoods> list);
}
