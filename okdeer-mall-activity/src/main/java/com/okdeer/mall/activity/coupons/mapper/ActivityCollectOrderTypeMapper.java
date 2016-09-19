package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectOrderType;

/**
 * @DESC: 代金券活动关联订单类型
 * @author zhangkn
 * @date  2015-11-25 16:24:57
 * @version 1.0.0
 * @copyright ©2005-2020 okdeer.com Inc. All rights reserved
 * 
 */
public interface ActivityCollectOrderTypeMapper {
	
	/**
	 * @Description: 批量插入
	 * @param list
	 * @author zhangkn
	 * @date 2016年9月17日
	 */
	void saveOrderTypeBatch(List<ActivityCollectOrderType> list);
	
	/**
	 * @Description: 批量删除
	 * @param collectCouponsId 代金券活动id
	 * @author zhangkn
	 * @date 2016年9月17日
	 */
	void deleteOrderTypeByCollectCouponsId(String collectCouponsId);
	
	/**
	 * @Description: 根据活动id查询列表
	 * @param collectCouponsId 代金券活动id
	 * @author zhangkn
	 * @date 2016年9月17日
	 */
	List<ActivityCollectOrderType> findOrderTypeListByCollectCouponsId(String collectCouponsId);
}