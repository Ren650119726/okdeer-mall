
package com.okdeer.mall.order.bo;

import java.io.Serializable;

/**
 * ClassName: TradeOrderLogisticsParamBo 
 * @Description: 物流信息查询参数
 * @author zengjizu
 * @date 2017年10月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class TradeOrderLogisticsParamBo implements Serializable {

	/**
	 * 订单id
	 */
	private String orderId;

	/**
	 * 物流单号
	 */
	private String logisticsNo;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getLogisticsNo() {
		return logisticsNo;
	}

	public void setLogisticsNo(String logisticsNo) {
		this.logisticsNo = logisticsNo;
	}

}
