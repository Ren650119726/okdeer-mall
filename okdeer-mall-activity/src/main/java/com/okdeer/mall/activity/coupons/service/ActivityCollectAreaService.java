
package com.okdeer.mall.activity.coupons.service;

import java.util.List;

import com.okdeer.mall.activity.coupons.bo.ActivityCollectAreaParamBo;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectArea;

public interface ActivityCollectAreaService {

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
	/**
	 * @Description: 根据查询参数查询列表
	 * @param activityCollectAreaParamBo
	 * @return
	 * @author zengjizu
	 * @date 2017年11月10日
	 */
	List<ActivityCollectArea> findList(ActivityCollectAreaParamBo activityCollectAreaParamBo);
	
	
}
