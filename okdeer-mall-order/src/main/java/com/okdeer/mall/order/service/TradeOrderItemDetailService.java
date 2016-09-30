
package com.okdeer.mall.order.service;

import java.util.List;

import com.okdeer.mall.order.entity.TradeOrderItemDetail;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderItemDetailService {

	/**
	 * 更新订单项明细为退货 
	 */
	int updateStatusWithRefund(String orderItemId);

	/**
	 * 查询订单明细
	 */
	List<TradeOrderItemDetail> selectByOrderItemId(String orderItemId);

	/**
	 * 消费码生成 </p>
	 * 
	 * @author yangq
	 * @param itemDetail
	 */
	void insertSelective(TradeOrderItemDetail itemDetail) throws Exception;

	/**
	 * 查询是否生成消息码 </p>
	 * 
	 * @author yangq
	 * @param orderItemId
	 * @return
	 */
	List<TradeOrderItemDetail> selectByOrderItemById(String orderItemId) throws Exception;

	/**
	 * 批量新增消费码 </p>
	 * 
	 * @author yangq
	 * @param itemDetailList
	 * @throws Exception
	 */
	void insertBatch(List<TradeOrderItemDetail> itemDetailList) throws Exception;

	/**
	 * 查询未消费数量
	 */
	int selectUnConsumerCount(String orderItemId);

	/**
	 * 更改消费码过期
	 * @param orderItemId
	 * @return
	 */
	int updateStatusWithExpire(String orderItemId);

	// start added by luosm 20160929 V1.1.0
    /**
     * @Description: 根据订单id查询明细列表
     * @param orderId 订单id
     * @return 订单明细列表
     * @author luosm
     * @date 2016年9月29日
     */
    List<TradeOrderItemDetail> selectByOrderItemDetailByOrderId(String orderId);
    // end added by luosm 20160929 V1.1.0
    
	/**
	 * 验证消费码在店铺中是否已经存在
	 * @param storeId 店铺Id
	 * @param consumeCode 消费码
	 * @return
	 */
	TradeOrderItemDetail checkConsumeHasExsit(String storeId, String consumeCode);
}