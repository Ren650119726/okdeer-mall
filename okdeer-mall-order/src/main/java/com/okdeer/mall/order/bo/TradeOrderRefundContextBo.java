
package com.okdeer.mall.order.bo;

import java.io.Serializable;
import java.util.List;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsCertificate;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;

@SuppressWarnings("serial")
public class TradeOrderRefundContextBo implements Serializable {

	/**
	 * 订单信息
	 */
	private TradeOrder tradeOrder;

	/**
	 * 订单项
	 */
	private List<TradeOrderItem> tradeOrderItemList;

	/**
	 * 订单项明细
	 */
	private List<TradeOrderItemDetail> tradeOrderItemDetail;

	/**
	 * 退款单信息
	 */
	private TradeOrderRefunds tradeOrderRefunds;

	/**
	 * 退款单明细
	 */
	private List<TradeOrderRefundsItem> tradeOrderRefundsItemList;

	/**
	 * 支付信息
	 */
	private TradeOrderPay tradeOrderPay;

	/**
	 * 店铺店老板用户id
	 */
	private String sotreUserId;

	/**
	 * 退款凭证信息
	 */
	private TradeOrderRefundsCertificate tradeOrderRefundsCertificate;

	public TradeOrder getTradeOrder() {
		return tradeOrder;
	}

	public void setTradeOrder(TradeOrder tradeOrder) {
		this.tradeOrder = tradeOrder;
	}

	public List<TradeOrderItem> getTradeOrderItemList() {
		return tradeOrderItemList;
	}

	public void setTradeOrderItemList(List<TradeOrderItem> tradeOrderItemList) {
		this.tradeOrderItemList = tradeOrderItemList;
	}

	public List<TradeOrderItemDetail> getTradeOrderItemDetail() {
		return tradeOrderItemDetail;
	}

	public void setTradeOrderItemDetail(List<TradeOrderItemDetail> tradeOrderItemDetail) {
		this.tradeOrderItemDetail = tradeOrderItemDetail;
	}

	public TradeOrderRefunds getTradeOrderRefunds() {
		return tradeOrderRefunds;
	}

	public void setTradeOrderRefunds(TradeOrderRefunds tradeOrderRefunds) {
		this.tradeOrderRefunds = tradeOrderRefunds;
	}

	public List<TradeOrderRefundsItem> getTradeOrderRefundsItemList() {
		return tradeOrderRefundsItemList;
	}

	public void setTradeOrderRefundsItemList(List<TradeOrderRefundsItem> tradeOrderRefundsItemList) {
		this.tradeOrderRefundsItemList = tradeOrderRefundsItemList;
	}

	public TradeOrderPay getTradeOrderPay() {
		return tradeOrderPay;
	}

	public void setTradeOrderPay(TradeOrderPay tradeOrderPay) {
		this.tradeOrderPay = tradeOrderPay;
	}

	public String getSotreUserId() {
		return sotreUserId;
	}

	public void setSotreUserId(String sotreUserId) {
		this.sotreUserId = sotreUserId;
	}

	public TradeOrderRefundsCertificate getTradeOrderRefundsCertificate() {
		return tradeOrderRefundsCertificate;
	}

	public void setTradeOrderRefundsCertificate(TradeOrderRefundsCertificate tradeOrderRefundsCertificate) {
		this.tradeOrderRefundsCertificate = tradeOrderRefundsCertificate;
	}

}
