package com.okdeer.mall.order.refundstrace;

import com.okdeer.mall.order.entity.TradeOrderRefunds;

/**
 * ClassName: RefundsTrace 
 * @Description: 退款轨迹接口。
 * @author maojj
 * @date 2016年9月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构1.1			2016年9月28日				maojj         退款轨迹接口
 */
public interface RefundsTrace {

	void saveRefundsTrace(TradeOrderRefunds refundsOrder);
}
