package com.okdeer.mall.order.service;

import java.util.List;
import java.util.Map;

import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.vo.TradeOrderItemDetailVo;
import com.okdeer.base.common.exception.ServiceException;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderItemService {

	void insertSelective(TradeOrderItem tradeOrderItem) throws ServiceException;

	List<TradeOrderItem> selectOrderItemByOrderId(String orderId) throws ServiceException;

	/**
	 *  zhongy
	 * 根据订单id查询订单是否已评价
	 * @param orderId
	 * @return
	 * @throws ServiceException
	 */
	Integer findTradeOrderItemIsAppraise(String orderId) throws ServiceException;

	/**
	 * 
	 * 根据订单id，查询订单项和订单项消费信息  （主要用于商城后台的服务订单详情） 
	 * 
	 * @author wusw
	 * @param orderId 订单id
	 * @return
	 * @throws ServiceException
	 */
	List<TradeOrderItemDetailVo> getItemDetailByOrderId(String orderId) throws ServiceException;

	/**
	 * 
	 * 根据订单id和订单项详情消费状态，查询记录数量 
	 * 
	 * @author wusw
	 * @param params
	 * @return
	 * @throws ServiceException
	 */
	int selectCountByOrderIdDetailStatus(Map<String, Object> params) throws ServiceException;

	TradeOrderItem selectByPrimaryKey(String id);
	
	/**
	 * @Description: 更新订单项为订单完成状态
	 * @param id 订单项id
	 * @return
	 * @author zengjizu
	 * @date 2016年10月5日
	 */
	void updateWithComplete(List<String> ids);

	// Begin V2.1 added by maojj 2017-02-18
	/**
	 * @Description: 根据订单id查询订单明细列表
	 * @param ids
	 * @return   
	 * @author maojj
	 * @date 2017年2月18日
	 */
	List<TradeOrderItem> findOrderItems(List<String> orderIds);
	// End V2.1 added by maojj 2017-02-18
}