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

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.google.common.base.Charsets;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.exception.StockException;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
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
import net.sf.json.JSONObject;

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

	/** * 店铺商品Service */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

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
	 * @author zengj
	 * @throws Exception 异常处理
	 * @date 2016年9月6日
	 */
	public void orderCompleteSyncToJxc(String orderId) throws Exception {
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
		JSONObject orderInfo = buildOrderInfo(tradeOrder);
		// 订单项json数组
		JSONArray orderItemList = buildOrderItemList(tradeOrder, tradeOrder.getTradeOrderItem());
		// 订单支付信息
		JSONArray orderPayInfo = buildOrderPayList(tradeOrder, tradeOrderPay);

	}

	/**
	 * 
	 * @Description: 构建订单支付信息JSON
	 * @param order 订单信息
	 * @param orderPay 订单支付信息
	 * @return JSONArray 返回的订单支付信息JSON  
	 * @author zengj
	 * @date 2016年9月7日
	 */
	private JSONArray buildOrderPayList(TradeOrder order, TradeOrderPay orderPay) {
		JSONObject orderPayJson = new JSONObject();
		orderPayJson.put("id", orderPay.getId());
		orderPayJson.put("orderId", orderPay.getOrderId());
		orderPayJson.put("rowNo", 1);
		orderPayJson.put("payTypeId", orderPay.getPayType().ordinal());
		orderPayJson.put("payAmount", orderPay.getPayAmount());
		// 代金券ID
		String payItemId = null;
		if (order.getActivityType() == ActivityTypeEnum.VONCHER) {
			payItemId = order.getActivityId();
		}
		orderPayJson.put("payItemId", payItemId);
		orderPayJson.put("payAmount", orderPay.getPayAmount());
		orderPayJson.put("payTradeNo", orderPay.getReturns());
		orderPayJson.put("remark", order.getRemark());
		orderPayJson.put("payTime", orderPay.getPayTime());
		orderPayJson.put("createTime", orderPay.getCreateTime());
		JSONArray orderPayArr = new JSONArray();
		orderPayArr.add(orderPayJson);
		return orderPayArr;
	}

	/**
	 * 
	 * @Description: 构建订单项JSON信息
	 * @param order 订单信息
	 * @param orderItemList 订单项集合
	 * @return JSONArray 订单项JSON信息  
	 * @throws Exception   异常信息
	 * @author zengj
	 * @date 2016年9月7日
	 */
	private JSONArray buildOrderItemList(TradeOrder order, List<TradeOrderItem> orderItemList) throws Exception {
		JSONArray orderItemArr = new JSONArray();
		JSONObject item = null;
		int orderItemSize = orderItemList.size();
		for (int i = 0; i < orderItemSize; i++) {
			TradeOrderItem orderItem = orderItemList.get(i);
			GoodsStoreSku goods = goodsStoreSkuServiceApi.getById(orderItem.getStoreSkuId());
			item = new JSONObject();
			item.put("id", orderItem.getId());
			item.put("orderId", orderItem.getOrderId());
			item.put("rowNo", i + 1);
			item.put("skuId", goods.getSkuId());
			// item.put("skuCode", goods.geta);
			item.put("saleType", ORDER_TYPE_A);
			item.put("saleNum", orderItem.getQuantity());
			// item.put("originalPrice", orderItem.getUnitPrice());
			// item.put("salePrice", orderItem.getUnitPrice());
			// item.put("totalAmount", orderItem.getUnitPrice());
			// item.put("saleAmount", orderItem.getUnitPrice());
			// item.put("discountAmount", orderItem.getUnitPrice());
			item.put("activityType", order.getActivityType().ordinal());
			item.put("activityId", order.getActivityId());
			item.put("activityItemId", order.getActivityItemId());
			item.put("remark", order.getRemark());
			item.put("createTime", orderItem.getCreateTime());
			orderItemArr.add(item);
		}
		return orderItemArr;
	}

	/**
	 * 
	 * @Description: 构建订单信息JSON
	 * @param order 订单信息
	 * @return JSONObject  订单信息JSON
	 * @author zengj
	 * @date 2016年9月7日
	 */
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
		orderInfo.put("discountAmount", order.getPreferentialPrice());
		orderInfo.put("freightAmount", order.getFare());
		orderInfo.put("userId", order.getUserId());
		orderInfo.put("referenceNo", null);
		orderInfo.put("operatorId", order.getSellerId());
		orderInfo.put("pickUpCode", order.getPickUpCode());
		orderInfo.put("remark", order.getRemark());
		orderInfo.put("createrId", order.getCreateUserId());
		orderInfo.put("createTime", order.getCreateTime());
		return orderInfo;
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
