package com.okdeer.mall.activity.serviceLimit.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.serviceLimit.entity.ActivityServiceLimitGoods;

/**
 * @DESC: 限购活动商品dao
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
public interface ActivityServiceLimitGoodsMapper extends IBaseMapper{
	
	/**
	 * 根据活动id删除
	 */
	void deleteByActivityId(String activityId);
	
	/**
	 * @desc 批量保存
	 * @param list 限购活动商品list
	 */
	void addBatch(List<ActivityServiceLimitGoods> list);
	
	/**
	 * @desc 通过LimitId取列表
	 * @param LimitId 限购活动主键id
	 * @return List<ActivityLimitGoods>
	 */
	List<ActivityServiceLimitGoods> listByActivityId(String activityId);
}