package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.activity.coupons.entity.ActivitySale;

/**
 * ClassName: ActivitySaleMapper 
 * @Description: 特惠活动dao
 * @author zhangkn
 * @date  2015-11-25 16:24:57
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿1.2.5		2016年12月31日				maojj		 增加根据活动id列表查询的方法
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
	
	/**
	 * 根据店铺id查找低价抢购活动
	 * @Description: 
	 * @param storeId
	 * @param actType
	 * @return ActivitySale
	 * @author mengsj
	 * @date 2016年12月31日
	 */
	ActivitySale findByActivitySaleByStoreId(@Param("storeId")String storeId,@Param("actType")Integer actType);
	
	// Begin V2.0 added by maojj 2016-12-31 
	/**
	 * @Description: 根据活动Id列表查询正在进行中的活动列表
	 * @param saleIds
	 * @return   
	 * @author maojj
	 * @date 2016年12月31日
	 */
	List<ActivitySale> findBySaleIds(Set<String> idList);
	// End V2.0 added by maojj 2016-12-31 
}