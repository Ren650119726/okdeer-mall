package com.okdeer.mall.order.bo;

import java.util.List;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;

/**
 * ClassName: TradeOrderBo 
 * @Description: 交易订单Bo对象
 * @author maojj
 * @date 2017年2月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.1 			2017年2月18日				maojj
 */
public class UserOrderBo {

	private TradeOrder tradeOrder;
	
	private List<TradeOrderItem> orderItems;

	
	public TradeOrder getTradeOrder() {
		return tradeOrder;
	}

	
	public void setTradeOrder(TradeOrder tradeOrder) {
		this.tradeOrder = tradeOrder;
	}

	
	public List<TradeOrderItem> getOrderItems() {
		return orderItems;
	}

	
	public void setOrderItems(List<TradeOrderItem> orderItems) {
		this.orderItems = orderItems;
	}
}
