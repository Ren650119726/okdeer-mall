package com.okdeer.mall.order.bo;

import java.math.BigDecimal;

/**
 * ClassName: PayTradeExt 
 * @Description: 支付交易拓展信息，存储提供给云钱包的订单扩展信息
 * @author maojj
 * @date 2017年6月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.3 		2017年6月28日				maojj
 */
public class PayTradeExt {

	/**
	 * 佣金
	 */
	private BigDecimal commission;

	/**
	 * 佣金比例
	 */
	private BigDecimal commissionRate;

	/**
	 * 运费
	 */
	private BigDecimal freight;

	/**
	 * 运费补贴
	 */
	private BigDecimal freightSubsidy;

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BigDecimal getCommissionRate() {
		return commissionRate;
	}

	public void setCommissionRate(BigDecimal commissionRate) {
		this.commissionRate = commissionRate;
	}

	public BigDecimal getFreight() {
		return freight;
	}

	public void setFreight(BigDecimal freight) {
		this.freight = freight;
	}

	public BigDecimal getFreightSubsidy() {
		return freightSubsidy;
	}

	public void setFreightSubsidy(BigDecimal freightSubsidy) {
		this.freightSubsidy = freightSubsidy;
	}

}
