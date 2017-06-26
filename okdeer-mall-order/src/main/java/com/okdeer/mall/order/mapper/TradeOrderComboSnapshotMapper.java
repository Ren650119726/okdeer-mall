/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * TradeOrderComboSnapshotMapper.java
 * @Date 2017-06-23 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.order.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.order.entity.TradeOrderComboSnapshot;

public interface TradeOrderComboSnapshotMapper extends IBaseMapper {

	/**
	 * @Description: 批量保存组合商品详情信息
	 * @param comboDetailList   
	 * @author maojj
	 * @date 2017年6月24日
	 */
	void batchAdd(List<TradeOrderComboSnapshot> comboDetailList);
	
	/**
	 * @Description: 根据订单Id查询组合商品明细信息
	 * @param comboSkuIds
	 * @param orderId
	 * @return   
	 * @author maojj
	 * @date 2017年6月24日
	 */
	List<TradeOrderComboSnapshot> findByOrderId(String orderId);
}