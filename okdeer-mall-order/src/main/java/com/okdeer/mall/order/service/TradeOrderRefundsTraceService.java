package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.enums.RefundsLogisticsEnum;

/**
 * ClassName: TradeOrderRefundsTraceService 
 * @Description: 交易订单退款轨迹服务
 * @author maojj
 * @date 2016年9月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构1.1			2016年9月28日				maojj
 */
public interface TradeOrderRefundsTraceService {
	
	/**
	 * @Description: 保存用户退款轨迹
	 * @param refundsOrder   
	 * @author maojj
	 * @date 2016年9月28日
	 */
	void saveRefundTrace(TradeOrderRefunds refundsOrder);
	
	/**
	 * @Description: 保存用户退款轨迹（重载方法）
	 * @param refundsOrder   
	 * @author maojj
	 * @date 2016年9月28日
	 */
	void saveRefundTrace(TradeOrderRefunds refundsOrder,RefundsLogisticsEnum logisticsType);
}
