package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;

import com.okdeer.mall.activity.coupons.entity.ActivityCollectArea;

/**
 * @DESC: 代金券地区表
 * @author zhangkn
 * @date  2015-11-25 16:24:57
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface ActivityCollectAreaMapper {
	
	/**
	 * @desc 根据collectCouponsId删除
	 * @param collectCouponsId 代金券活动id
	 */
	void deleteByCollectCouponsId(String collectCouponsId);
	
	/**
	 * @desc 批量添加
	 * @param list 代金券活动list
	 */
	void saveBatch(List<ActivityCollectArea> list);
	
	/**
	 * @desc 通过collectCouponsId取列表
	 * @param collectCouponsId 代金券活动id
	 * @return List<ActivityCollectArea>
	 */
	List<ActivityCollectArea> listByCollectCouponsId(String collectCouponsId);
}