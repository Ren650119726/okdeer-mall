/** 
 *@Project: yschome-mall-order 
 *@Author: zengj
 *@Date: 2016年9月6日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.order.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.google.common.base.Charsets;
import com.okdeer.archive.stock.exception.StockException;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.mapper.TradeOrderPayMapper;
import com.okdeer.mall.order.utils.OrderNoUtils;
import com.yschome.base.common.exception.ServiceException;
import com.yschome.base.framework.mq.RocketMQProducer;

import net.sf.json.JSONArray;

/**
 * ClassName: TradeOrderCompleteProcessServiceImpl 
 * @Description: 订单或退款单完成后同步处理Service
 * @author zengj
 * @date 2016年9月6日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     1.0.Z          2016年9月6日                               zengj				订单或退款单完成后同步处理Service
 */
@Service
public class TradeOrderCompleteProcessServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderCompleteProcessServiceImpl.class);

	/** * 消息生产者 */
	@Resource
	private RocketMQProducer rocketMqProducer;

	/** * 订单Mapper */
	@Resource
	private TradeOrderMapper tradeOrderMapper;

	/** * 订单项Mapper */
	@Resource
	private TradeOrderItemMapper tradeOrderItemMapper;

	/** * 订单项Mapper */
	@Resource
	private TradeOrderPayMapper tradeOrderPayMapper;

	/** * A：销售单、B：退货单 */
	private static final String ORDER_TYPE_A = "A";

	/** * A：销售单、B：退货单 */
	private static final String ORDER_TYPE_B = "B";

	/** * 订单ID为空 */
	public static final String ORDER_ID_IS_NULL = "订单ID为空";

	/** * 订单信息为空 */
	public static final String ORDER_NOT_EXISTS = "订单不存在";

	/** * 订单状态不匹配 */
	public static final String ORDER_STATUS_NO_MATCHING = "订单状态不匹配,ID{},,应该为{}实际为{}";

	/**
	 * 
	 * @Description: 订单完成时发送MQ消息同步到商业管理系统
	 * @param orderId 订单ID
	 * @throws ServiceException 异常信息  
	 * @author zengj
	 * @date 2016年9月6日
	 */
	public void orderCompleteSyncToJxc(String orderId) throws ServiceException {
		if (StringUtils.isBlank(orderId)) {
			throw new ServiceException(ORDER_ID_IS_NULL);
		}
		// 查询订单信息
		TradeOrder tradeOrder = tradeOrderMapper.selectByPrimaryKey(orderId);
		// 查询订单项信息
		List<TradeOrderItem> tradeOrderItemList = tradeOrderItemMapper.selectOrderItemDetailById(orderId);
		if (tradeOrder == null || CollectionUtils.isEmpty(tradeOrderItemList)) {
			throw new ServiceException(ORDER_NOT_EXISTS);
		}

		if (tradeOrder.getStatus() != OrderStatusEnum.HAS_BEEN_SIGNED) {
			logger.error(ORDER_STATUS_NO_MATCHING, orderId, OrderStatusEnum.HAS_BEEN_SIGNED.getName(),
					tradeOrder.getStatus().getName());
			throw new ServiceException(ORDER_STATUS_NO_MATCHING);
		}

		// 查询订单支付信息
		TradeOrderPay tradeOrderPay = null;
		// 线上支付才有支付信息
		if (tradeOrder.getPayWay() == PayWayEnum.PAY_ONLINE) {
			tradeOrderPay = tradeOrderPayMapper.selectByOrderId(orderId);
		} else {
			// 否则new一个实例
			tradeOrderPay = new TradeOrderPay();
		}

		// 订单信息
		JSONObject orderInfo = new JSONObject();
		// 订单项json数组
		JSONArray orderItemList = new JSONArray();
		// 订单支付信息
		JSONObject orderPayInfo = new JSONObject();

	}

	private JSONObject buildOrderInfo(TradeOrder order) {
		// 订单信息
		JSONObject orderInfo = new JSONObject();
		orderInfo.put("id", order.getId());
		orderInfo.put("orderNo", order.getOrderNo());
		orderInfo.put("storeId", order.getStoreId());
		if (order.getOrderResource() == OrderResourceEnum.POS) {
			orderInfo.put("posId", OrderNoUtils.OFFLINE_POS_ID);
		} else {
			orderInfo.put("posId", OrderNoUtils.ONLINE_POS_ID);
		}
		orderInfo.put("saleType", ORDER_TYPE_A);
		orderInfo.put("orderResource", order.getOrderResource().ordinal());
		orderInfo.put("totalAmount", order.getTotalAmount());
		orderInfo.put("amount", order.getIncome());
		orderInfo.put("discountAmount", order.getIncome());
		return null;
	}

	/**
	 * 
	 * @Description: 发送MQ消息
	 * @param topic MQ TOPIC
	 * @param tag MQ TAG
	 * @param msg 发送的消息
	 * @throws Exception  抛出的异常  
	 * @author zengj
	 * @date 2016年9月6日
	 */
	private void send(String topic, String tag, String msg) throws Exception {
		Message message = new Message(topic, tag, msg.getBytes(Charsets.UTF_8));
		SendResult sendResult = rocketMqProducer.send(message);
		if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
			throw new StockException("写mq数据失败");
		}
	}
}
