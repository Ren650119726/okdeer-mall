
package com.okdeer.mall.activity.coupons.service;

import java.util.List;

import com.okdeer.mall.activity.coupons.bo.ActivityCollectStoreParamBo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectStore;

public interface ActivityCollectStoreService {

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
	/**
	 * @Description: 根据参数查询列表
	 * @param activityCollectStoreParamBo
	 * @return
	 * @author zengjizu
	 * @date 2017年11月10日
	 */
	List<ActivityCollectStore> findList(ActivityCollectStoreParamBo activityCollectStoreParamBo);
	
	
}
