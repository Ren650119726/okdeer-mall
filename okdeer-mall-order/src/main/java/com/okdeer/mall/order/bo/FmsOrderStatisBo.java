
package com.okdeer.mall.order.bo;

import java.math.BigDecimal;

/**
 * ClassName: FmsOrderStatisBo 
 * @Description: 财务系统订单统计
 * @author zengjizu
 * @date 2017年8月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class FmsOrderStatisBo {

	/**
	 * 总数量
	 */
	private int totalCount;

	/**
	 * 状态 订单状态(0:等待买家付款,1:待发货,2:已取消,3:已发货,4:已拒收,5:已签收(交易完成),6:交易关闭)、7：待消费、8：部分消费、9：已消费、10：已过期、11:等待买家支付中、12：取消中、13:拒收中、14:待接单
	 */
	private int status;

	/**
	 * 总金额
	 */
	private BigDecimal totalAmount;

	/**
	 * 实际支付金额
	 */
	private BigDecimal actualAmount;

	/**
	 * 优惠金额
	 */
	private BigDecimal preferentialPrice;

	/**
	 * 店铺优惠金额
	 */
	private BigDecimal storePreferential;

	/**
	 * 平台优惠金额
	 */
	private BigDecimal platformPreferential;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	public BigDecimal getPreferentialPrice() {
		return preferentialPrice;
	}

	public void setPreferentialPrice(BigDecimal preferentialPrice) {
		this.preferentialPrice = preferentialPrice;
	}

	public BigDecimal getStorePreferential() {
		return storePreferential;
	}

	public void setStorePreferential(BigDecimal storePreferential) {
		this.storePreferential = storePreferential;
	}

	public BigDecimal getPlatformPreferential() {
		return platformPreferential;
	}

	public void setPlatformPreferential(BigDecimal platformPreferential) {
		this.platformPreferential = platformPreferential;
	}

}
