
package com.okdeer.mall.order.bo;

import java.util.List;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefunds;

/**
 * ClassName: TradeOrderContext 
 * @Description: 和erp左文明对接加的bo类
 * @author zhangkn
 * @date 2017年6月5日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.3			 2017年6月5日 			zhagnkn
 */
public class TradeOrderContext {

	private TradeOrder tradeOrder;//订单对象

	private TradeOrderPay tradeOrderPay;//订单支付对象

	private List<TradeOrderItem> itemList;//订单项列表

	private TradeOrderLogistics tradeOrderLogistics;//订单物流对象
	
	private TradeOrderRefunds tradeOrderRefunds;//退款对象

	public TradeOrder getTradeOrder() {
		return tradeOrder;
	}

	public void setTradeOrder(TradeOrder tradeOrder) {
		this.tradeOrder = tradeOrder;
	}

	public TradeOrderPay getTradeOrderPay() {
		return tradeOrderPay;
	}

	public void setTradeOrderPay(TradeOrderPay tradeOrderPay) {
		this.tradeOrderPay = tradeOrderPay;
	}

	public TradeOrderLogistics getTradeOrderLogistics() {
		return tradeOrderLogistics;
	}

	
	public void setTradeOrderLogistics(TradeOrderLogistics tradeOrderLogistics) {
		this.tradeOrderLogistics = tradeOrderLogistics;
	}

	
	public List<TradeOrderItem> getItemList() {
		return itemList;
	}

	
	public void setItemList(List<TradeOrderItem> itemList) {
		this.itemList = itemList;
	}

	
	public TradeOrderRefunds getTradeOrderRefunds() {
		return tradeOrderRefunds;
	}

	
	public void setTradeOrderRefunds(TradeOrderRefunds tradeOrderRefunds) {
		this.tradeOrderRefunds = tradeOrderRefunds;
	}
}
