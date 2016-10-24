package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;
import java.util.Map;

import com.okdeer.mall.activity.coupons.entity.ActivitySale;

/**
 * @DESC:特惠活动dao
 * @author zhangkn
 * @date  2015-11-25 16:24:57
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface ActivitySaleMapper {
	
	/**
	 * @desc 保存 
	 * @param activitySale 特惠活动对象
	 */
	void save(ActivitySale activitySale);
	
	/**
	 * @desc 修改
	 * @param activitySale 特惠活动对象
	 */
	void update(ActivitySale activitySale);
	
	/**
	 * @desc 通过主键获取对象
	 * @param id 主键
	 * @return 特惠活动对象
	 */
	ActivitySale get(String id);
	
	/**
	 * @desc 列表搜索
	 * @param map 封装了搜索条件
	 * @return 特惠活动列表
	 */
	List<ActivitySale> list(Map<String,Object> map);
	
	/**
	 * @desc 批量更新
	 * @param map 
	 */
	void updateBatchStatus(Map<String,Object> map);
	
//	List<GoodsStoreSku> listGoodsStoreSku(Map<String,Object> map);
	List<Map<String,Object>> listGoodsStoreSku(Map<String,Object> map);
	
	int validateExist(Map<String,Object> map);
	
	List<ActivitySale> listByTask();
	
	/**
	 * 查询特惠限款数
	 * 
	 * @author yangq
	 * @param id
	 * @return
	 */
	int selectActivitySale(String id);
	
	List<String> selectActivitySaleList(String id);
	
	/**
	 * @desc 通过主键获取对象  已经开始的活动
	 * @param id 主键
	 * @return 特惠活动对象
	 */
	ActivitySale selectById(String id);
	
	/**
	 * 获取特惠活动对象 </p>
	 * 
	 * @param id
	 * @return
	 */
	ActivitySale getAcSaleStatus(String id);
	
	/**
	 * @Description: TODO
	 * @param map
	 * @return
	 * @author zhangkn
	 * @date 2016年10月21日
	 */
	List<ActivitySale> listByStoreId(Map<String,Object> map);
	
}