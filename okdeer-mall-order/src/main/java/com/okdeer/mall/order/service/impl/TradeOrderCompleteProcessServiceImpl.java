/** 
 *@Project: yschome-mall-order 
 *@Author: zengj
 *@Date: 2016年9月6日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.google.common.base.Charsets;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.exception.StockException;
import com.okdeer.common.consts.LogConstants;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.order.constant.OrderMessageConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.mapper.TradeOrderPayMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsMapper;
import com.okdeer.mall.order.service.TradeOrderCompleteProcessService;
import com.okdeer.mall.order.service.TradeOrderCompleteProcessServiceApi;
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
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderCompleteProcessServiceApi")
public class TradeOrderCompleteProcessServiceImpl
		implements TradeOrderCompleteProcessServiceApi, TradeOrderCompleteProcessService {

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

	/** * 退款单Mapper */
	@Resource
	private TradeOrderRefundsMapper tradeOrderRefundsMapper;

	/** * 退款单项Mapper */
	@Resource
	private TradeOrderRefundsItemMapper tradeOrderRefundsItemMapper;

	/** * 店铺商品Service */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	/** * A：销售单、B：退货单 */
	private static final String ORDER_TYPE_A = "A";

	/** * A：销售单、B：退货单 */
	private static final String ORDER_TYPE_B = "B";

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
			throw new ServiceException(LogConstants.ORDER_ID_IS_NULL);
		}
		// 查询订单信息
		TradeOrder tradeOrder = tradeOrderMapper.selectByPrimaryKey(orderId);
		// 查询订单项信息
		List<TradeOrderItem> tradeOrderItemList = tradeOrderItemMapper.selectOrderItemDetailById(orderId);
		if (tradeOrder == null || CollectionUtils.isEmpty(tradeOrderItemList)) {
			throw new ServiceException(LogConstants.ORDER_NOT_EXISTS);
		}

		// 判断订单状态是否为已完成
		if (tradeOrder.getStatus() != OrderStatusEnum.HAS_BEEN_SIGNED) {
			logger.error(LogConstants.ORDER_STATUS_NO_MATCHING, orderId, OrderStatusEnum.HAS_BEEN_SIGNED.getName(),
					tradeOrder.getStatus().getName());
			throw new ServiceException(LogConstants.ORDER_STATUS_NO_MATCHING);
		}

		// 查询订单支付信息
		TradeOrderPay tradeOrderPay = null;
		// 线上支付才有支付信息
		if (tradeOrder.getPayWay() == PayWayEnum.PAY_ONLINE) {
			tradeOrderPay = tradeOrderPayMapper.selectByOrderId(orderId);
		}

		// 订单信息
		JSONObject orderInfo = buildOrderInfo(tradeOrder);
		// 订单项json数组
		JSONArray orderItemList = buildOrderItemList(tradeOrder, tradeOrderItemList);
		// 订单支付信息
		JSONObject orderPayInfo = buildOrderPayInfo(tradeOrder, tradeOrderPay);

		orderInfo.put("orderItemList", orderItemList);
		orderInfo.put("orderPayInfo", orderPayInfo);

		// 发送消息
		this.send(OrderMessageConstant.TOPIC_ORDER_COMPLETE, OrderMessageConstant.TAG_ORDER_COMPLETE,
				orderInfo.toString());

	}

	/**
	 * 
	 * @Description: 退款单完成时发送MQ消息同步到商业管理系统
	 * @param refundsId 退款单ID
	 * @author zengj
	 * @throws Exception 异常处理
	 * @date 2016年9月6日
	 */
	public void orderRefundsCompleteSyncToJxc(String refundsId) throws Exception {
		if (StringUtils.isBlank(refundsId)) {
			throw new ServiceException(LogConstants.ORDER_REFUNDS_ID_IS_NULL);
		}
		TradeOrderRefunds orderRefunds = tradeOrderRefundsMapper.selectByPrimaryKey(refundsId);
		// 查询退款单项信息
		List<TradeOrderRefundsItem> tradeOrderRefundsItemList = tradeOrderRefundsItemMapper
				.getTradeOrderRefundsItemByRefundsId(refundsId);
		if (orderRefunds == null || CollectionUtils.isEmpty(tradeOrderRefundsItemList)) {
			throw new ServiceException(LogConstants.ORDER_REFUNDS_NOT_EXISTS);
		}

		// 判断退款单状态是否为已完成
		if (orderRefunds.getRefundsStatus() != RefundsStatusEnum.REFUND_SUCCESS
				&& orderRefunds.getRefundsStatus() != RefundsStatusEnum.SELLER_REFUNDING
				&& orderRefunds.getRefundsStatus() != RefundsStatusEnum.FORCE_SELLER_REFUND_SUCCESS
				&& orderRefunds.getRefundsStatus() != RefundsStatusEnum.YSC_REFUND_SUCCESS) {
			logger.error(LogConstants.ORDER_STATUS_NO_MATCHING, refundsId, RefundsStatusEnum.REFUND_SUCCESS.getName(),
					orderRefunds.getRefundsStatus().getName());
			throw new ServiceException(LogConstants.ORDER_STATUS_NO_MATCHING);
		}
		// 订单信息
		JSONObject orderRefundsInfo = buildOrderRefundsInfo(orderRefunds);
		// 订单项json数组
		JSONArray orderRefundsItemList = buildOrderRefundsItemList(orderRefunds, tradeOrderRefundsItemList);

		orderRefundsInfo.put("orderRefundsItemList", orderRefundsItemList);
		// orderInfo.put("orderPayInfo", orderPayInfo);
		// 发送消息
		this.send(OrderMessageConstant.TOPIC_REFUND_ORDER_COMPLETE, OrderMessageConstant.TAG_REFUND_ORDER_COMPLETE,
				orderRefundsInfo.toString());
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
		// 订单ID
		orderInfo.put("id", order.getId());
		// 订单编号
		orderInfo.put("orderNo", order.getOrderNo());
		// 店铺ID
		orderInfo.put("storeId", order.getStoreId());
		// 判断是POS单还是线上单，给posID
		if (order.getOrderResource() == OrderResourceEnum.POS) {
			orderInfo.put("posId", OrderNoUtils.OFFLINE_POS_ID);
		} else {
			orderInfo.put("posId", OrderNoUtils.ONLINE_POS_ID);
		}
		// 销售类型
		orderInfo.put("saleType", ORDER_TYPE_A);
		// 订单来源
		orderInfo.put("orderResource", order.getOrderResource().ordinal());
		// 原价金额=商品实际金额和运费（实际金额+优惠金额)
		orderInfo.put("totalAmount", order.getTotalAmount());
		// 商家实收金额（包含运费）
		orderInfo.put("amount", order.getIncome());
		// 店铺优惠金额
		BigDecimal storePreferentialPrice = BigDecimal.ZERO;
		// 订单金额如果不等于店家收入金额，说明是店铺有优惠
		if (order.getTotalAmount().compareTo(order.getIncome()) != 0) {
			storePreferentialPrice = order.getPreferentialPrice();
		}
		// 店铺优惠金额
		orderInfo.put("discountAmount", storePreferentialPrice);
		// 运费
		orderInfo.put("freightAmount", order.getFare());
		// （会员）买家ID
		orderInfo.put("userId", order.getUserId());
		// 退货原单号
		orderInfo.put("referenceNo", null);
		// 收银员ID-对应发货人ID
		orderInfo.put("operatorId", order.getShipmentsUserId());
		// 提货码
		orderInfo.put("pickUpCode", order.getPickUpCode());
		// 备注
		orderInfo.put("remark", order.getRemark());
		// 创建人
		orderInfo.put("createrId", order.getCreateUserId());
		// 创建时间
		orderInfo.put("createTime", order.getCreateTime());
		return orderInfo;
	}

	/**
	 * 
	 * @Description: 构建订单信息JSON
	 * @param orderRefunds 订单信息
	 * @return JSONObject  订单信息JSON
	 * @author zengj
	 * @date 2016年9月7日
	 */
	private JSONObject buildOrderRefundsInfo(TradeOrderRefunds orderRefunds) {
		// 订单信息
		JSONObject refunds = new JSONObject();
		// 订单ID
		refunds.put("id", orderRefunds.getId());
		// 退款单编号
		refunds.put("orderNo", orderRefunds.getRefundNo());
		// 店铺ID
		refunds.put("storeId", orderRefunds.getStoreId());
		// 判断是POS单还是线上单，给posID
		if (orderRefunds.getOrderResource() == OrderResourceEnum.POS) {
			refunds.put("posId", OrderNoUtils.OFFLINE_POS_ID);
		} else {
			refunds.put("posId", OrderNoUtils.ONLINE_POS_ID);
		}
		// 销售类型
		refunds.put("saleType", ORDER_TYPE_B);
		// 订单来源
		refunds.put("orderResource", orderRefunds.getOrderResource().ordinal());
		// 原价金额=商品实际金额和运费
		refunds.put("totalAmount", orderRefunds.getTotalAmount());
		// 商家实收金额
		refunds.put("amount", orderRefunds.getTotalAmount());
		// 店铺优惠金额
		BigDecimal storePreferentialPrice = BigDecimal.ZERO;
		// 订单金额如果不等于店家收入金额，说明是店铺有优惠
		if (orderRefunds.getTotalAmount().compareTo(orderRefunds.getTotalIncome()) != 0) {
			storePreferentialPrice = orderRefunds.getTotalPreferentialPrice();
		}
		// 店铺优惠金额
		refunds.put("discountAmount", storePreferentialPrice);
		// 运费
		// orderInfo.put("freightAmount", refundOrder.getFare());
		// （会员）买家ID
		refunds.put("userId", orderRefunds.getUserId());
		// 退货原单号
		refunds.put("referenceNo", orderRefunds.getOrderNo());
		// 收银员ID
		refunds.put("operatorId", orderRefunds.getOperator());
		// // 提货码
		// orderInfo.put("pickUpCode", refundOrder.getPickUpCode());
		// // 备注
		refunds.put("remark", orderRefunds.getMemo());
		// 创建人
		refunds.put("createrId", orderRefunds.getUserId());
		// 创建时间
		refunds.put("createTime", orderRefunds.getCreateTime());
		return refunds;
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
			// 订单项ID
			item.put("id", orderItem.getId());
			// 订单ID
			item.put("orderId", orderItem.getOrderId());
			// 序号
			item.put("rowNo", i + 1);
			// 标准商品库ID
			item.put("skuId", goods.getSkuId());
			// 店铺优惠金额
			BigDecimal storePreferentialPrice = BigDecimal.ZERO;
			// 订单金额如果不等于店家收入金额，说明是店铺有优惠
			if (order.getTotalAmount().compareTo(order.getIncome()) != 0) {
				storePreferentialPrice = order.getPreferentialPrice();
			}
			// 实际单价=原单价减去店铺优惠
			BigDecimal actualPrice = orderItem.getUnitPrice().subtract(storePreferentialPrice);
			// 货号
			// item.put("skuCode", goods.geta);
			// 销售类型
			item.put("saleType", ORDER_TYPE_A);
			// 商品数量
			item.put("saleNum", orderItem.getQuantity() == null ? orderItem.getWeight() : orderItem.getQuantity());
			// 原单价
			item.put("originalPrice", orderItem.getUnitPrice());
			// 实际单价=原单价减去店铺优惠
			item.put("salePrice", actualPrice);
			// 原价金额=原单价*数量
			item.put("totalAmount",
					orderItem
							.getUnitPrice().multiply(orderItem.getWeight() == null
									? BigDecimal.valueOf(orderItem.getQuantity()) : orderItem.getWeight())
							.setScale(2, BigDecimal.ROUND_FLOOR));
			// 实际金额=实际交易单价*数量
			item.put("saleAmount", actualPrice.multiply(
					orderItem.getWeight() == null ? BigDecimal.valueOf(orderItem.getQuantity()) : orderItem.getWeight())
					.setScale(2, BigDecimal.ROUND_FLOOR));
			// 店铺优惠金额
			item.put("discountAmount", storePreferentialPrice);
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
	 * @Description: 构建退款单项JSON信息
	 * @param orderRefunds 退款单信息
	 * @param orderRefundsItemList 退款单项集合
	 * @return JSONArray 退款单项JSON信息  
	 * @throws Exception   异常信息
	 * @author zengj
	 * @date 2016年9月7日
	 */
	private JSONArray buildOrderRefundsItemList(TradeOrderRefunds orderRefunds,
			List<TradeOrderRefundsItem> orderRefundsItemList) throws Exception {
		JSONArray orderItemArr = new JSONArray();
		JSONObject item = null;
		int orderItemSize = orderRefundsItemList.size();
		for (int i = 0; i < orderItemSize; i++) {
			TradeOrderRefundsItem orderRefundsItem = orderRefundsItemList.get(i);
			GoodsStoreSku goods = goodsStoreSkuServiceApi.getById(orderRefundsItem.getStoreSkuId());
			item = new JSONObject();
			// 退款单项ID
			item.put("id", orderRefundsItem.getId());
			// 订单ID
			item.put("orderId", orderRefunds.getOrderId());
			// 序号
			item.put("rowNo", i + 1);
			// 标准商品库ID
			item.put("skuId", goods.getSkuId());
			// 店铺优惠金额
			BigDecimal storePreferentialPrice = BigDecimal.ZERO;
			// 订单金额如果不等于店家收入金额，说明是店铺有优惠
			if (orderRefunds.getTotalAmount().compareTo(orderRefunds.getTotalIncome()) != 0) {
				storePreferentialPrice = orderRefunds.getTotalPreferentialPrice();
			}
			// 实际单价=原单价减去店铺优惠
			BigDecimal actualPrice = orderRefundsItem.getUnitPrice().subtract(storePreferentialPrice);
			// 货号
			// item.put("skuCode", goods.geta);
			// 销售类型
			item.put("saleType", ORDER_TYPE_A);
			// 商品数量
			item.put("saleNum", orderRefundsItem.getQuantity() == null ? orderRefundsItem.getWeight()
					: orderRefundsItem.getQuantity());
			// 原单价
			item.put("originalPrice", orderRefundsItem.getUnitPrice());
			// 实际单价=原单价减去店铺优惠
			item.put("salePrice", actualPrice);
			// 原价金额=原单价*数量
			item.put("totalAmount",
					orderRefundsItem.getUnitPrice()
							.multiply(orderRefundsItem.getWeight() == null
									? BigDecimal.valueOf(orderRefundsItem.getQuantity()) : orderRefundsItem.getWeight())
							.setScale(2, BigDecimal.ROUND_FLOOR));
			// 实际金额=实际交易单价*数量
			item.put("saleAmount",
					actualPrice
							.multiply(orderRefundsItem.getWeight() == null
									? BigDecimal.valueOf(orderRefundsItem.getQuantity()) : orderRefundsItem.getWeight())
							.setScale(2, BigDecimal.ROUND_FLOOR));
			// 店铺优惠金额
			item.put("discountAmount", storePreferentialPrice);
			// item.put("activityType", order.getActivityType().ordinal());
			// item.put("activityId", order.getActivityId());
			// item.put("activityItemId", order.getActivityItemId());
			item.put("remark", orderRefunds.getMemo());
			item.put("createTime", orderRefunds.getCreateTime());
			orderItemArr.add(item);
		}
		return orderItemArr;
	}

	/**
	 * 
	 * @Description: 构建订单支付信息JSON
	 * @param order 订单信息
	 * @param orderPay 订单支付信息
	 * @return JSONObject 返回的订单支付信息JSON  
	 * @author zengj
	 * @date 2016年9月7日
	 */
	private JSONObject buildOrderPayInfo(TradeOrder order, TradeOrderPay orderPay) {
		JSONObject orderPayJson = new JSONObject();
		if (orderPay == null) {
			return orderPayJson;
		}
		// 支付ID
		orderPayJson.put("id", orderPay.getId());
		// 订单ID
		orderPayJson.put("orderId", orderPay.getOrderId());
		// 序号
		orderPayJson.put("rowNo", 1);
		// 支付类型
		orderPayJson.put("payTypeId", orderPay.getPayType().ordinal());
		// 支付金额
		orderPayJson.put("payAmount", orderPay.getPayAmount());
		// 代金券ID
		String payItemId = null;
		if (order.getActivityType() == ActivityTypeEnum.VONCHER) {
			payItemId = order.getActivityId();
		}
		orderPayJson.put("payItemId", payItemId);
		// 支付流水号
		orderPayJson.put("payTradeNo", orderPay.getReturns());
		// 备注
		orderPayJson.put("remark", order.getRemark());
		// 支付时间
		orderPayJson.put("payTime", orderPay.getPayTime());
		// 创建时间
		orderPayJson.put("createTime", orderPay.getCreateTime());
		return orderPayJson;
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
		if (logger.isInfoEnabled()) {
			logger.info(LogConstants.ORDER_OR_REFUNDS_COMPLETE_MSG_PARAM, tag, msg);
		}
		Message message = new Message(topic, tag, msg.getBytes(Charsets.UTF_8));
		SendResult sendResult = rocketMqProducer.send(message);
		if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
			throw new StockException("写mq数据失败");
		}
	}
}
