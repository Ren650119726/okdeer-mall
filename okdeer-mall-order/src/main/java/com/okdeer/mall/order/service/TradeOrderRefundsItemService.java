package com.okdeer.mall.order.service;

import java.util.List;

import com.okdeer.mall.order.entity.TradeOrderRefundsItem;

/**
 * 
 * 
 * @pr mall
 * @desc 售后订单项service
 * @author chenwj
 * @date 2016年4月1日 下午4:01:09
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
public interface TradeOrderRefundsItemService {

	List<TradeOrderRefundsItem> getTradeOrderRefundsItemByRefundsId(String refundsId);

	/**
	 * 批量插入订单项 
	 */
	int insert(List<TradeOrderRefundsItem> refundsItem);
}