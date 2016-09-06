package com.okdeer.mall.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.order.entity.TradeOrderPay;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderPayMapper {
	
	void insertSelective(TradeOrderPay tradeOrderPay);
	
	TradeOrderPay selectByOrderId (String id);
	
	/**
	 * 订单支付 
	 * @author yangq
	 * @param tradeOrderPay
	 */
	void insertTradeOrderPay(TradeOrderPay tradeOrderPay);
	
	
	/**
 	 * 查询订单是否产生交易记录
 	 * 
 	 * @author yangq
 	 * @param tradeNum
 	 * @return
 	 */
 	int selectTradeOrderPayByOrderId(String orderId);
	
	// Begin sql优化，将复杂sql拆分开来 add by zengj
	/**
	 * 
	 * @Description: 根据订单ID集合查询订单支付信息
	 * @param orderIds 订单ID集合
	 * @return  订单支付信息
	 * @author zengj
	 * @date 2016年8月17日
	 */
	List<TradeOrderPay> selectByOrderIds(@Param("orderIds") List<String> orderIds);
	// End sql优化，将复杂sql拆分开来 add by zengj
}