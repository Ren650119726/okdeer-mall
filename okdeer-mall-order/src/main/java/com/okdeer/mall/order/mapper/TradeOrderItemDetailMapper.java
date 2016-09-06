package com.okdeer.mall.order.mapper;

import java.util.List;

import com.okdeer.mall.order.entity.TradeOrderItemDetail;

/**
 * @DESC:
 * @author YSCGD
 * @date 2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderItemDetailMapper{

	/**
	 * 更新消费码状态为退款(sql中更新的状态是2.为已退款，更新注释)
	 */
	int updateStatusWithRefund(String orderItemId);
	
	/**
	 * 更新消费码状态为已消费
	 * @author zengj
	 * @param orderItemId
	 * @return
	 */
	int updateStatusWithConsumed(String orderItemId);
	
	/**
	 * 更新消费码状态为过期
	 * @param orderItemId
	 * @return
	 */
	int updateStatusWithExpire(String orderItemId);
	
	/**
	 * 根据orderId查询交易订单项消费详细表(仅服务型商品有)
	 *
	 * @param orderItemId 订单orderItemId
	 * @return 返回查询结果集
	 */
	TradeOrderItemDetail selectByOrderId(String orderItemId);
	
	/**
	 * 根据orderItemId查询交易订单项消费详细表(仅服务型商品有)
	 *
	 * @param orderItemId 订单orderItemId
	 * @return 返回查询结果集
	 */
	List<TradeOrderItemDetail> selectByOrderItemId(String orderItemId);
	
	/**
	 * 消费码生成 </p>
	 * 
	 * @author yangq
	 * @param itemDetail
	 */
	void insertSelective(TradeOrderItemDetail itemDetail);
	
	void insertBatch(List<TradeOrderItemDetail> itemDetailList);
	
	/**
	 * 查询是否生成消息码 </p>
	 * 
	 * @author yangq
	 * @param orderItemId
	 * @return
	 */
	List<TradeOrderItemDetail> selectByOrderItemById(String orderItemId);
	
	/**
	 * 查询未消费数量
	 */
	Integer selectUnConsumerCount(String orderItemId);

}