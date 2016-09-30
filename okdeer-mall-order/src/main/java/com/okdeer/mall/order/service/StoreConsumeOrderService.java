
package com.okdeer.mall.order.service;

import java.util.List;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.vo.ExpireStoreConsumerOrderVo;

/**
 * ClassName: StoreConsumeOrderService 
 * @Description: 到店消费订单service
 * @author zengjizu
 * @date 2016年9月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface StoreConsumeOrderService {

	/**
	 * @Description: 查询过期订单列表
	 * @return
	 * @author zengjizu
	 * @date 2016年9月29日
	 */
	List<ExpireStoreConsumerOrderVo> findExpireOrder();

	void handleExpireOrder(TradeOrder order, List<TradeOrderItemDetail> detailList) throws Exception;
}
