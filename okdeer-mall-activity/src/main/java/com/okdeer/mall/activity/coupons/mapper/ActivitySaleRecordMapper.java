
package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleRecord;

/**
 * @DESC: 特惠活动记录mapper
 * @author wusw
 * @date  2016-04-18 11:37:48
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-14			maojj			增加统计用户购买记录和批量插入购买记录的方法
 *		重构V4.1			2016-07-14			wangf01			selectOrderGoodsActivity
 *
 * 
 */
public interface ActivitySaleRecordMapper extends IBaseCrudMapper {

	/**
	 * 
	 * 根据订单ID，逻辑删除特惠活动记录信息
	 *
	 * @param params
	 */
	Integer updateDisabledByOrderId(Map<String, Object> map);

	/**
	 * 查询用户购买的特惠商品数量 </p>
	 * 
	 * @author yangq
	 * @param map
	 * @return
	 */
	int selectActivitySaleRecord(Map<String, Object> map);

	/**
	 * 查询订单商品是否参与特惠活动
	 */
	int selectOrderGoodsCount(Map<String, Object> map);

	// begin add by wangf01 2016.08.08
	/**
	 * 查询订单商品是否参与特惠活动，返回特惠活动id
	 */
	String selectOrderGoodsActivity(Map<String, Object> map);
	// end add by wangf01 2016.08.08

	/**
	 * 查询用户所购买特惠活动商品的款数 </p>
	 * 
	 * @author yangq
	 * @param map
	 * @return
	 */
	List<String> selectActivitySaleRecordOfFund(Map<String, Object> map);

	List<String> selectActivitySaleRecordList(Map<String, Object> map);

	// Begin added by maojj 2016-07-14
	/**
	 * @Description: 统计用户购买特惠活动商品记录
	 * @param map
	 * @return   
	 * @return List<ActivitySaleRecord>  
	 * @throws
	 * @author maojj
	 * @date 2016年7月15日
	 */
	List<ActivitySaleRecord> countSaleRecord(Map<String, Object> map);

	/**
	 * @Description: 批量插入购买特惠活动商品记录
	 * @param recordList
	 * @return   
	 * @return int  
	 * @throws
	 * @author maojj
	 * @date 2016年7月15日
	 */
	int batchInsert(List<ActivitySaleRecord> recordList);
	// End added by maojj 2016-07-14
	
	// Begin V2.0 added by maojj 2017-01-03
	/**
	 * @Description: 查询用户参与指定活动列表的购买记录
	 * @param userId
	 * @param saleIdList
	 * @return   
	 * @author maojj
	 * @date 2017年1月3日
	 */
	List<ActivitySaleRecord> findSaleRecord(@Param("userId")String userId,@Param("saleIdList")Set<String> saleIdList);
	// End added by maojj 2017-01-03
}