package com.okdeer.mall.order.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.vo.TradeOrderItemDetailVo;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderItemMapper{
	
	/**
	 * @Description: 批量添加订单项
	 * @param tradeOrderItemList 订单项list
	 * @author zhangkn
	 * @date 2016年8月16日
	 */
	void insertBatch(List<TradeOrderItem> tradeOrderItemList);
	
	void insertSelective(TradeOrderItem tradeOrderItem);
	
	List<TradeOrderItem> selectTradeOrderItem(String orderId);

	/**
	 * 根据订单id查询订单项和关联的退款单
	 * @param orderId 请求参数
	 * @return 返回结果集
	 */
	List<TradeOrderItem> selectTradeOrderItemOrRefund(String orderId);
	
	/**
	 * 根据订单id查询服务订单项
	 * @param orderId 请求参数
	 * @return 返回结果
	 */
	List<TradeOrderItem> selectTradeOrderItemOrDetail(String orderId);
	
	/**
	 * zhongy
	 * 根据订单id判断该该订单是否评价
	 * @param orderId 请求参数
	 * @return 返回结果
	 */
	Integer selectTradeOrderItemIsAppraise(String orderId);
	
	
	
	/**
	 * 
	 * 根据订单id，查询订单项和订单项消费信息 
	 * 
	 * @author wusw
	 * @param orderId 订单id
	 * @return
	 */
	List<TradeOrderItem> selectOrderItemDetailById(String orderId);
	
	/**
	 * 
	 * 根据订单id，查询订单项和订单项消费信息 （主要用于商城后台的服务订单详情） 
	 * 
	 * @author wusw
	 * @param orderId
	 * @return
	 */
	List<TradeOrderItemDetailVo> selectItemDetailByOrderId(String orderId);
	
	/**
	 * 
	 * 根据订单id和订单项详情消费状态，查询记录数量 
	 * 
	 * @author wusw
	 * @param params
	 * @return
	 */
	int selectCountByOrderIdDetailStatus(Map<String,Object> params);
	
	
	
	/**
	 * 根据订单项ID查询该订单项的信息
	 * @param orderItemId
	 * @return
	 */
	TradeOrderItem selectOrderItemById(String id);
	
	//void updateByBatch(@Param("tradeOrderCommentList") List<TradeOrderComment> tradeOrderCommentList);
	
	/**
	 * 
	 * 订单评价后 将订单项都改为评价状态
	 *
	 * @param orderId
	 */
	void updateByOrderId(@Param("orderId") String orderId); 
	
	/**
	 * 查询订单项列表
	 * 
	 * @author yangq
	 * @param orderId
	 * @return
	 */
	List<TradeOrderItem> selectOrderItemListById(String orderId);
	
	/**
	 * 
	 * @Description: 更新订单项为已完成
	 * @param ids   订单项ID
	 * @author zengj
	 * @date 2016年8月6日
	 */
	void updateCompleteById(@Param("ids") List<String> ids);
}