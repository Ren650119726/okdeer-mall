package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectStore;

/**
 * @DESC: 代金券小区表
 * @author zhangkn
 * @date  2015-11-25 16:24:57
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface ActivityCollectStoreMapper {
	
	/**
	 * 根据collectCouponsId删除
	 * @param collectCouponsId 代金券活动主键id
	 */
	void deleteByCollectCouponsId(String collectCouponsId);
	
	/**
	 * @desc 批量保存
	 * @param list 代金券店铺list
	 */
	void saveBatch(List<ActivityCollectStore> list);
	
	/**
	 * @desc 通过collectCouponsId取列表
	 * @param collectCouponsId 代金券活动id
	 * @return List<ActivityCollectArea>
	 */
	List<ActivityCollectStore> listByCollectCouponsId(String collectCouponsId);
}