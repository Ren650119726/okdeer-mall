
package com.okdeer.mall.order.service.impl;

import static com.okdeer.common.consts.DescriptConstants.ORDER_EXECUTE_CANCEL_FAIL;
import static com.okdeer.mall.order.constant.PayMessageConstant.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.client.producer.TransactionSendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.api.pay.enums.BusinessTypeEnum;
import com.okdeer.api.pay.tradeLog.dto.BalancePayTradeVo;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuService;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceServiceApi;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.service.IStoreInfoExtServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.framework.mq.RocketMQTransactionProducer;
import com.okdeer.base.framework.mq.RocketMqResult;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleMapper;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRecordService;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillService;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.common.utils.RobotUserUtil;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.enums.AddressDefault;
import com.okdeer.mall.member.member.service.MemberConsigneeAddressServiceApi;
import com.okdeer.mall.order.constant.PayMessageConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.ActivityBelongType;
import com.okdeer.mall.order.enums.ConsumeStatusEnum;
import com.okdeer.mall.order.enums.ConsumerCodeStatusEnum;
import com.okdeer.mall.order.enums.OrderAppStatusAdaptor;
import com.okdeer.mall.order.enums.OrderComplete;
import com.okdeer.mall.order.enums.OrderItemStatusEnum;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.job.StoreConsumeOrderExpireJob;
import com.okdeer.mall.order.mapper.TradeOrderItemDetailMapper;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsMapper;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.service.StoreConsumeOrderService;
import com.okdeer.mall.order.service.StoreConsumeOrderServiceApi;
import com.okdeer.mall.order.service.TradeOrderActivityService;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.timer.constant.TimerMessageConstant;
import com.okdeer.mall.order.vo.ExpireStoreConsumerOrderVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;
import com.okdeer.mall.order.vo.UserTradeOrderDetailVo;
import com.okdeer.mall.system.mq.RollbackMQProducer;

/**
 * ClassName: ConsumerCodeOrderServiceImpl
 * 
 * @Description: 到店消费接口实现类
 * @author zengjizu
 * @date 2016年9月20日
 *
 *       ================================================================================================= 
 *       Task ID              Date               Author               Description
 *       ----------------+----------------+-------------------+------------------------------------------- 
 *       v1.1.0             2016-9-20            zengjz             增加查询消费码订单列表
 *       V1.1.0             2016-10-8            zhaoqc             新增通过消费码消费状态判断订单能否投诉
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.StoreConsumeOrderServiceApi")
public class StoreConsumeOrderServiceImpl implements StoreConsumeOrderServiceApi, StoreConsumeOrderService {

	private static final Logger logger = LoggerFactory.getLogger(StoreConsumeOrderServiceImpl.class);

	@Autowired
	private TradeOrderMapper tradeOrderMapper;

	@Autowired
	private TradeOrderItemMapper tradeOrderItemMapper;

	@Autowired
	private TradeOrderItemDetailMapper tradeOrderItemDetailMapper;

	@Autowired
	private TradeOrderRefundsMapper tradeOrderRefundsMapper;

	@Autowired
	private TradeOrderRefundsItemMapper tradeOrderRefundsItemMapper;

	@Reference(version = "1.0.0", check = false)
	private IStoreInfoExtServiceApi storeInfoExtService;

	@Reference(version = "1.0.0", check = false)
	private MemberConsigneeAddressServiceApi memberConsigneeAddressService;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceServiceApi goodsStoreSkuServiceServiceApi;

	/**
	 * 售后service
	 */
	@Autowired
	private TradeOrderRefundsService tradeOrderRefundsService;

	/**
	 * 单号生成器
	 */
	@Resource
	private GenerateNumericalService generateNumericalService;

	@Autowired
	private RocketMQTransactionProducer rocketMQTransactionProducer;

	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	@Autowired
	private TradeOrderActivityService tradeOrderActivityService;

	/**
	 * 回滚消息生产者
	 */
	@Resource
	private RollbackMQProducer rollbackMQProducer;
	
	/**
	 * 特惠活动记录信息service
	 */
	@Autowired
	private ActivitySaleRecordService activitySaleRecordService;
	
	/**
	 * 秒杀活动service
	 */
	@Autowired
	private ActivitySeckillService sctivitySeckillService;
	
	/**
	 * 特惠Dao
	 */
	@Autowired
	private ActivitySaleMapper activitySaleMapper;
	
	@Reference(version = "1.0.0", check = false)
	private StockManagerServiceApi serviceStockManagerService;
	
	
	@Override
	public PageUtils<TradeOrder> findStoreConsumeOrderList(Map<String, Object> map, Integer pageNo, Integer pageSize) {
		PageHelper.startPage(pageNo, pageSize, true, false);

		String status = (String) map.get("status");
		List<TradeOrder> list = new ArrayList<TradeOrder>();

		if (status != null) {
			// 订单状态
			List<String> orderStatus = new ArrayList<String>();

			if (status.equals(String.valueOf(Constant.ONE))) {
				// 查询未支付的消费码订单
				orderStatus.add(String.valueOf(OrderStatusEnum.UNPAID.ordinal()));
				orderStatus.add(String.valueOf(OrderStatusEnum.BUYER_PAYING.ordinal()));
				map.put("orderStatus", orderStatus);
			} else if (status.equals(String.valueOf(Constant.TWO))) {
				// 查询已经取消的消费码订单
				orderStatus.add(String.valueOf(OrderStatusEnum.CANCELED.ordinal()));
				orderStatus.add(String.valueOf(OrderStatusEnum.CANCELING.ordinal()));
				map.put("orderStatus", orderStatus);
			} else if (status.equals(String.valueOf(Constant.THREE))) {
				// 查询未消费的消费码订单
				orderStatus.add(String.valueOf(OrderStatusEnum.HAS_BEEN_SIGNED.ordinal()));
				map.put("orderStatus", orderStatus);
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.WAIT_CONSUME.ordinal());
			} else if (status.equals(String.valueOf(Constant.FOUR))) {
				// 查询已过期的消费码订单
				orderStatus.add(String.valueOf(OrderStatusEnum.HAS_BEEN_SIGNED.ordinal()));
				map.put("orderStatus", orderStatus);
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.EXPIRED.ordinal());
			} else if (status.equals(String.valueOf(Constant.FIVE))) {
				// 查询已消费的消费码订单
				orderStatus.add(String.valueOf(OrderStatusEnum.HAS_BEEN_SIGNED.ordinal()));
				map.put("orderStatus", orderStatus);
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.WAIT_EVALUATE.ordinal());
			} else if (status.equals(String.valueOf(Constant.SIX))) {
				// 查询已退款的消费码订单
				orderStatus.add(String.valueOf(OrderStatusEnum.HAS_BEEN_SIGNED.ordinal()));
				map.put("orderStatus", orderStatus);
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.REFUNDED.ordinal());
			} else if (status.equals(String.valueOf(Constant.SEVEN))) {
				// 查询已完成的消费码订单
				orderStatus.add(String.valueOf(OrderStatusEnum.HAS_BEEN_SIGNED.ordinal()));
				map.put("orderStatus", orderStatus);
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.COMPLETED.ordinal());
			}
			list = tradeOrderMapper.selectStoreConsumeOrderList(map);
		}

		for (TradeOrder vo : list) {
			List<TradeOrderItem> items = tradeOrderItemMapper.selectTradeOrderItem(vo.getId());
			vo.setTradeOrderItem(items);
		}
		return new PageUtils<TradeOrder>(list);
	}

	@Override
	public JSONObject findStoreConsumeOrderDetail(String orderId) throws Exception {

		UserTradeOrderDetailVo userTradeOrderDetailVo = tradeOrderMapper.findStoreConsumeOrderById(orderId);

		List<TradeOrderItem> tradeOrderItems = tradeOrderItemMapper.selectTradeOrderItemOrRefund(orderId);
		// 判断订单是否评价appraise大于0，已评价
		Integer appraise = tradeOrderItemMapper.selectTradeOrderItemIsAppraise(orderId);
		// 查询店铺扩展信息
		JSONObject json = new JSONObject();
		try {
			// 1 订单信息
			json.put("orderId", userTradeOrderDetailVo.getId() == null ? "" : userTradeOrderDetailVo.getId());

			ConsumerCodeStatusEnum consumerCodeStatusEnum = OrderAppStatusAdaptor.convertAppStoreConsumeOrderStatus(
					userTradeOrderDetailVo.getStatus(), userTradeOrderDetailVo.getConsumerCodeStatus());

			json.put("orderStatus", consumerCodeStatusEnum.ordinal());
			json.put("orderStatusName", consumerCodeStatusEnum.getValue());

			// 2 订单支付倒计时计算
			Integer remainingTime = userTradeOrderDetailVo.getRemainingTime();
			if (remainingTime != null) {
				remainingTime = remainingTime + 1800;
				json.put("remainingTime", remainingTime <= 0 ? "0" : remainingTime);
			} else {
				json.put("remainingTime", "0");
			}

			// 支付信息
			TradeOrderPay payInfo = userTradeOrderDetailVo.getTradeOrderPay();

			//Begin added by zhaoqc 2016-10-08
			if (payInfo != null) {
				// 0:余额支付 1:支付宝 2:微信支付
				json.put("payMethod", payInfo.getPayType().ordinal());
				json.put("orderPayTime", DateUtils.formatDate(payInfo.getPayTime(), "yyyy-MM-dd HH:mm:ss"));
				// 是否支持投诉 0：不支持  1:支持
				if(userTradeOrderDetailVo.getConsumerCodeStatus() == ConsumerCodeStatusEnum.WAIT_EVALUATE 
				           || userTradeOrderDetailVo.getConsumerCodeStatus() == ConsumerCodeStatusEnum.COMPLETED) {
				    json.put("isSupportComplain", 1);
				} else {
				    json.put("isSupportComplain", 0);
				}
			} else {
				json.put("isSupportComplain", 0);
			}
			//End added by zhaoqc 2016-10-08
			
			// 交易号
			json.put("tradeNum",
					userTradeOrderDetailVo.getTradeNum() == null ? "" : userTradeOrderDetailVo.getTradeNum());
			json.put("remark", userTradeOrderDetailVo.getRemark() == null ? "" : userTradeOrderDetailVo.getRemark());
			json.put("orderAmount",
					userTradeOrderDetailVo.getTotalAmount() == null ? "0" : userTradeOrderDetailVo.getTotalAmount());
			json.put("actualAmount",
					userTradeOrderDetailVo.getActualAmount() == null ? "0" : userTradeOrderDetailVo.getActualAmount());
			json.put("orderNo", userTradeOrderDetailVo.getOrderNo() == null ? "" : userTradeOrderDetailVo.getOrderNo());
			json.put("cancelReason",
					userTradeOrderDetailVo.getReason() == null ? "" : userTradeOrderDetailVo.getReason());
			json.put("orderSubmitOrderTime", userTradeOrderDetailVo.getCreateTime() != null
					? DateUtils.formatDate(userTradeOrderDetailVo.getCreateTime(), "yyyy-MM-dd HH:mm:ss") : "");

			json.put("activityType", userTradeOrderDetailVo.getActivityType() == null ? ""
					: userTradeOrderDetailVo.getActivityType().ordinal());
			json.put("preferentialPrice", userTradeOrderDetailVo.getPreferentialPrice() == null ? ""
					: userTradeOrderDetailVo.getPreferentialPrice());
			// 订单评价类型0：未评价，1：已评价
			json.put("orderIsComment", appraise > 0 ? Constant.ONE : Constant.ZERO);
			// 订单投诉状态
			json.put("compainStatus", userTradeOrderDetailVo.getCompainStatus() == null ? ""
					: userTradeOrderDetailVo.getCompainStatus().ordinal());

			json.put("leaveMessage", userTradeOrderDetailVo.getRemark());
			//订单状态为已取消且 更新时间不为null时设置正常的取消时间 start 涂志定
			if (userTradeOrderDetailVo.getStatus() == OrderStatusEnum.CANCELED && userTradeOrderDetailVo.getUpdateTime() != null) {
				json.put("cancelTime",
						DateUtils.formatDate(userTradeOrderDetailVo.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
			} else {
				json.put("cancelTime", "");
			}
			//end 涂志定
			// 店铺信息
			getStoreInfo(json, userTradeOrderDetailVo);
			// 商品信息
			getTradeItemInfo(json, tradeOrderItems);
			// 订单明细信息
			getTradeOrderItemDetail(json, userTradeOrderDetailVo, tradeOrderItems.get(0), orderId);
		} catch (ServiceException e) {
			throw new RuntimeException("查询店铺信息出错");
		}
		return json;
	}

	/**
	 * @Description: 获取店铺信息
	 * @param json
	 *            返回的json对象
	 * @param userTradeOrderDetailVo
	 *            订单信息
	 * @throws ServiceException
	 *             抛出异常
	 * @author zengjizu
	 * @date 2016年9月24日
	 */
	private void getStoreInfo(JSONObject json, UserTradeOrderDetailVo userTradeOrderDetailVo) throws ServiceException {
		StoreInfo storeInfo = userTradeOrderDetailVo.getStoreInfo();
		String storeName = "";
		String storeMobile = "";
		String address = "";
		String storeId = "";
		if (storeInfo != null) {
			storeId = storeInfo.getId();
			storeName = storeInfo.getStoreName();
			storeMobile = storeInfo.getMobile();

			// 确认订单时，没有将地址保存到trade_order_logistics订单物流表，暂时取收货地址表的默认地址
			MemberConsigneeAddress memberConsigneeAddress = new MemberConsigneeAddress();
			memberConsigneeAddress.setUserId(storeId);
			memberConsigneeAddress.setIsDefault(AddressDefault.YES);

			List<MemberConsigneeAddress> memberAddressList = memberConsigneeAddressService
					.getList(memberConsigneeAddress);
			if (memberAddressList != null && memberAddressList.size() > 0) {
				MemberConsigneeAddress memberAddress = memberAddressList.get(0);
				address = memberAddress.getArea() + memberAddress.getAddress();
			}
		}

		json.put("orderShopid", storeId);
		json.put("orderShopName", storeName);
		json.put("orderShopMobile", storeMobile);
		json.put("orderExtractShopName", storeName);
		json.put("orderShopAddress", address);
		json.put("storeLogo", storeInfo.getLogoUrl() == null ? "" : storeInfo.getLogoUrl());

	}

	/**
	 * @Description: 获取订单项信息
	 * @param json
	 *            返回json对象
	 * @param tradeOrderItems
	 *            订单项
	 * @author zengjizu
	 * @date 2016年9月24日
	 */
	private void getTradeItemInfo(JSONObject json, List<TradeOrderItem> tradeOrderItems) {

		// TradeOrderItem tradeOrderItem = tradeOrderItems.get(0);

		JSONArray itemArry = new JSONArray();

		if (tradeOrderItems != null && tradeOrderItems.size() > 0) {
			JSONObject itemObject = null;
			GoodsStoreSkuService goodsStoreSkuService = null;
			for (TradeOrderItem tradeOrderItem : tradeOrderItems) {
				itemObject = new JSONObject();
				itemObject.put("itemId", tradeOrderItem.getId());
				itemObject.put("productId",
						tradeOrderItem.getStoreSkuId() == null ? "" : tradeOrderItem.getStoreSkuId());
				itemObject.put("mainPicPrl",
						tradeOrderItem.getMainPicPrl() == null ? "" : tradeOrderItem.getMainPicPrl());
				itemObject.put("skuName", tradeOrderItem.getSkuName() == null ? "" : tradeOrderItem.getSkuName());
				itemObject.put("unitPrice",
						tradeOrderItem.getUnitPrice() == null ? "0" : tradeOrderItem.getUnitPrice());
				itemObject.put("quantity", tradeOrderItem.getQuantity() == null ? "0" : tradeOrderItem.getQuantity());
				itemObject.put("skuTotalAmount",
						tradeOrderItem.getTotalAmount() == null ? "0" : tradeOrderItem.getTotalAmount());
				itemObject.put("skuActualAmount",
						tradeOrderItem.getActualAmount() == null ? "0" : tradeOrderItem.getActualAmount());
				itemObject.put("skuPreferPrice",
						tradeOrderItem.getPreferentialPrice() == null ? "0" : tradeOrderItem.getPreferentialPrice());

				goodsStoreSkuService = goodsStoreSkuServiceServiceApi
						.selectByStoreSkuId(tradeOrderItem.getStoreSkuId());

				if (goodsStoreSkuService != null) {
					// 是否需要预约0：不需要，1：需要
					itemObject.put("isPrecontract", goodsStoreSkuService.getIsAppointment().ordinal());
					itemObject.put("appointmentHour", goodsStoreSkuService.getAppointmentHour());
					// 是否支持退订0：不支持，1：支持
					itemObject.put("isUnsubscribe", goodsStoreSkuService.getIsUnsubscribe().ordinal());

					String startDate = DateUtils.formatDate(goodsStoreSkuService.getStartTime(), "yyyy-MM-dd");
					String endDate = DateUtils.formatDate(goodsStoreSkuService.getEndTime(), "yyyy-MM-dd");
					itemObject.put("orderInDate", startDate + "至" + endDate);
					itemObject.put("notAvailableDate", goodsStoreSkuService.getInvalidDate());
				} else {
					// 是否需要预约0：不需要，1：需要
					itemObject.put("isPrecontract", 0);
					itemObject.put("appointmentHour", 0);
					// 是否支持退订0：不支持，1：支持
					itemObject.put("isUnsubscribe", 0);
					itemObject.put("orderInDate", "");
					itemObject.put("notAvailableDate", "");
				}

				itemArry.add(itemObject);

				itemObject = null;
				goodsStoreSkuService = null;
			}
		}
		json.put("items", itemArry);
	}

	/**
	 * @Description: 获取订单明细列表信息
	 * @param json
	 *            返回的json对象
	 * @param userTradeOrderDetailVo
	 *            订单信息
	 * @param tradeOrderItem
	 *            订单项信息
	 * @param orderId
	 *            订单id
	 * @author zengjizu
	 * @date 2016年9月24日
	 */
	private void getTradeOrderItemDetail(JSONObject json, UserTradeOrderDetailVo userTradeOrderDetailVo,
			TradeOrderItem tradeOrderItem, String orderId) {

		// 消费码列表
		List<TradeOrderItemDetail> detailList = tradeOrderItemDetailMapper.selectByOrderItemDetailByOrderId(orderId);

		JSONArray consumeCodeList = new JSONArray();

		if (CollectionUtils.isNotEmpty(detailList)) {
			JSONObject detail = null;
			for (TradeOrderItemDetail tradeOrderItemDetail : detailList) {
				detail = new JSONObject();

				detail.put("consumeId", tradeOrderItemDetail.getId());
				detail.put("consumeCode", tradeOrderItemDetail.getConsumeCode());
				// 0：未消费，1：已消费，2：已退款，3：已过期
				detail.put("consumeStatus", tradeOrderItemDetail.getStatus().ordinal());
				if (tradeOrderItemDetail.getUseTime() != null) {
					detail.put("consumeTime",
							DateUtils.formatDate(tradeOrderItemDetail.getUseTime(), "yyyy-MM-dd HH:mm:ss"));
				} else {
					detail.put("consumeTime", "");
				}

				if (tradeOrderItem != null) {
					detail.put("consumePrice", tradeOrderItem.getUnitPrice());
				} else {
					detail.put("consumePrice", "0");
				}

				if (tradeOrderItemDetail.getStatus() == ConsumeStatusEnum.refund) {
					detail.put("refundTime",
							DateUtils.formatDate(tradeOrderItemDetail.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
				} else {
					detail.put("refundTime", "");
				}
				consumeCodeList.add(detail);
				detail = null;
			}
		}
		json.put("consumeCodeList", consumeCodeList);
	}

	@Override
	public PageUtils<TradeOrderRefunds> findUserRefundOrderList(Map<String, Object> params, Integer pageNo,
			Integer pageSize) {
		PageHelper.startPage(pageNo, pageSize, true, false);
		List<TradeOrderRefunds> list = tradeOrderRefundsMapper.getListByParams(params);

		// List<TradeOrderRefundsItem> itemList = null;
		// for (TradeOrderRefunds tradeOrderRefunds : list) {
		// itemList = tradeOrderRefundsItemMapper.getTradeOrderRefundsItemByRefundsId(tradeOrderRefunds.getId());
		// tradeOrderRefunds.setTradeOrderRefundsItem(itemList);
		// itemList = null;
		// }
		return new PageUtils<TradeOrderRefunds>(list);
	}

	@Override
	public TradeOrderRefunds getRefundOrderDetail(String refundId) {
		TradeOrderRefunds refunds = tradeOrderRefundsMapper.findStoreConsumeOrderDetailById(refundId);
		List<TradeOrderRefundsItem> itemList = tradeOrderRefundsItemMapper
				.getTradeOrderRefundsItemByRefundsId(refunds.getId());
		refunds.setTradeOrderRefundsItem(itemList);
		return refunds;
	}

	@Override
	public List<TradeOrderItemDetail> getStoreConsumeOrderDetailList(String orderId, int status) {
		List<TradeOrderItemDetail> list = tradeOrderItemDetailMapper.selectItemDetailByOrderIdAndStatus(orderId,
				status);

		return list;
	}

	@Override
	public List<ExpireStoreConsumerOrderVo> findExpireOrder() {

		return tradeOrderItemDetailMapper.findExpireList();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void handleExpireOrder(TradeOrder order) throws Exception {

		List<TradeOrderItem> orderItem = tradeOrderItemMapper.selectOrderItemListById(order.getId());
		order.setTradeOrderItem(orderItem);
		if (orderItem != null && !Iterables.isEmpty(orderItem)) {

			for (TradeOrderItem item : order.getTradeOrderItem()) {
				if (item.getServiceAssurance() != null && item.getServiceAssurance() > 0) {
					logger.info("超时未消费，系统自动退款,订单号：" + order.getOrderNo());
					// 超时未消费，系统自动申请退款
					refundOrder(order, item);
				} else {
					// 不支持退款，将消费码状态改为已经过期，同时要将商家的冻结金额还回给平台
					expireOrder(order, item);
				}
			}
		}
	}

	private void refundOrder(TradeOrder order, TradeOrderItem item) throws Exception {

		TradeOrderRefunds orderRefunds = new TradeOrderRefunds();
		String refundsId = UuidUtils.getUuid();
		orderRefunds.setId(refundsId);
		orderRefunds.setRefundNo(generateNumericalService.generateNumber("XT"));
		orderRefunds.setOrderId(order.getId());
		orderRefunds.setOrderNo(order.getOrderNo());
		orderRefunds.setStoreId(order.getStoreId());
		orderRefunds.setOperator(RobotUserUtil.getRobotUser().getId());
		// 退款原因
		orderRefunds.setRefundsReason("到店消费订单超时未消费，系统自动退款");
		// 说明
		orderRefunds.setRefundsStatus(RefundsStatusEnum.SELLER_REFUNDING);
		orderRefunds.setStatus(OrderItemStatusEnum.ALL_REFUND);
		orderRefunds.setType(order.getType());
		// 退款单来源
		orderRefunds.setOrderResource(order.getOrderResource());
		orderRefunds.setOrderNo(order.getOrderNo());
		// 支付类型
		if (order.getTradeOrderPay() != null) {
			orderRefunds.setPaymentMethod(order.getTradeOrderPay().getPayType());
		} else if (order.getPayWay() == PayWayEnum.CASH_DELIERY) {
			orderRefunds.setPaymentMethod(PayTypeEnum.CASH);
		}
		orderRefunds.setUserId(order.getUserId());
		orderRefunds.setCreateTime(new Date());
		orderRefunds.setUpdateTime(new Date());
		BigDecimal totalIncome = new BigDecimal("0.00");

		// 退款金额
		BigDecimal refundAmount = new BigDecimal("0.00");
		// 退款优惠金额
		BigDecimal refundPrefeAmount = new BigDecimal("0.00");

		// 退款数量
		int quantity = 0;

		// 查询未退款的消费码列表
		List<TradeOrderItemDetail> detailList = tradeOrderItemDetailMapper
				.selectItemDetailByItemIdAndStatus(item.getId(), ConsumerCodeStatusEnum.WAIT_CONSUME.ordinal());

		if (CollectionUtils.isNotEmpty(detailList)) {
			for (TradeOrderItemDetail tradeOrderItemDetail : detailList) {
				if (tradeOrderItemDetail.getOrderItemId().equals(item.getId())) {
					quantity++;
					refundAmount = refundAmount.add(tradeOrderItemDetail.getActualAmount());
					refundPrefeAmount = refundPrefeAmount.add(tradeOrderItemDetail.getPreferentialPrice());
				}
			}
		}

		TradeOrderRefundsItem refundsItem = new TradeOrderRefundsItem();
		refundsItem.setId(UuidUtils.getUuid());
		refundsItem.setRefundsId(refundsId);
		refundsItem.setOrderItemId(item.getId());
		refundsItem.setPropertiesIndb(item.getPropertiesIndb());
		refundsItem.setQuantity(quantity);
		refundsItem.setAmount(refundAmount);
		refundsItem.setBarCode(item.getBarCode());
		refundsItem.setMainPicUrl(item.getMainPicPrl());
		refundsItem.setSkuName(item.getSkuName());
		refundsItem.setSpuType(item.getSpuType());
		refundsItem.setStyleCode(item.getStyleCode());
		refundsItem.setPreferentialPrice(item.getPreferentialPrice());
		refundsItem.setStatus(OrderItemStatusEnum.ALL_REFUND);
		refundsItem.setStoreSkuId(item.getStoreSkuId());
		refundsItem.setUnitPrice(item.getUnitPrice());
		refundsItem.setWeight(item.getWeight());
		refundsItem.setIncome(item.getIncome());

		if (item.getIncome() != null) {
			totalIncome = totalIncome.add(item.getIncome());
		}

		List<TradeOrderRefundsItem> items = Lists.newArrayList(refundsItem);
		orderRefunds.setTradeOrderRefundsItem(items);
		orderRefunds.setTotalAmount(refundAmount);
		orderRefunds.setTotalPreferentialPrice(refundPrefeAmount);
		orderRefunds.setTotalIncome(totalIncome);
		// 调用退款操作
		tradeOrderRefundsService.insertRefunds(orderRefunds, buildRefundCertificate(refundsId));
	}

	private TradeOrderRefundsCertificateVo buildRefundCertificate(String refundsId) {
		TradeOrderRefundsCertificateVo certificate = new TradeOrderRefundsCertificateVo();
		String certificateId = UuidUtils.getUuid();
		certificate.setId(certificateId);
		certificate.setRefundsId(refundsId);
		certificate.setCreateTime(new Date());
		// 买家用户ID buyerUserId
		certificate.setOperator(RobotUserUtil.getRobotUser().getId());
		certificate.setRemark("到店消费商品超时未消费，系统自动退款");
		return certificate;
	}

	@Transactional(rollbackFor = Exception.class)
	private void expireOrder(final TradeOrder order, final TradeOrderItem item) throws Exception {

		// 构建余额支付（或添加交易记录）对象
		Message msg = new Message(TOPIC_BALANCE_PAY_TRADE, TAG_PAY_TRADE_MALL,
				buildBalancePayTrade(order, item).getBytes(Charsets.UTF_8));
		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, order, new LocalTransactionExecuter() {

			@Override
			public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
				try {
					// 执行同意退款操作
					updateOrderStatus(order, item);
				} catch (Exception e) {
					logger.error("执行同意退款操作异常", e);
					return LocalTransactionState.ROLLBACK_MESSAGE;
				}
				return LocalTransactionState.COMMIT_MESSAGE;
			}
		}, new TransactionCheckListener() {

			@Override
			public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
				return LocalTransactionState.COMMIT_MESSAGE;
			}
		});
		RocketMqResult.returnResult(sendResult);
	}

	private String buildBalancePayTrade(TradeOrder order, TradeOrderItem item) throws ServiceException {
		// 退款金额
		BigDecimal refundAmount = new BigDecimal("0.00");
		// 退款优惠金额
		BigDecimal refundPrefeAmount = new BigDecimal("0.00");
		// 查询未退款的消费码列表
		List<TradeOrderItemDetail> detailList = tradeOrderItemDetailMapper
				.selectItemDetailByItemIdAndStatus(item.getId(), ConsumerCodeStatusEnum.WAIT_CONSUME.ordinal());

		if (CollectionUtils.isNotEmpty(detailList)) {
			for (TradeOrderItemDetail tradeOrderItemDetail : detailList) {
				if (tradeOrderItemDetail.getOrderItemId().equals(item.getId())) {
					refundAmount = refundAmount.add(tradeOrderItemDetail.getActualAmount());
					refundPrefeAmount = refundPrefeAmount.add(tradeOrderItemDetail.getPreferentialPrice());
				}
			}
		}

		BalancePayTradeVo payTradeVo = new BalancePayTradeVo();
		payTradeVo.setAmount(refundAmount);
		payTradeVo.setIncomeUserId("1");
		payTradeVo.setPayUserId(storeInfoService.getBossIdByStoreId(order.getStoreId()));
		payTradeVo.setTradeNum(order.getTradeNum());
		payTradeVo.setTitle("到店消费订单过期");
		payTradeVo.setBusinessType(BusinessTypeEnum.CONSUME_CODE_EXPIRE);
		payTradeVo.setExt(item.getId());
		
		payTradeVo.setServiceFkId(order.getId());
		payTradeVo.setServiceNo(order.getOrderNo());
		// payTradeVo.setRemark("关联订单号：" + order.getOrderNo());
		// 优惠额退款 判断是否有优惠劵
		ActivityBelongType activityResource = tradeOrderActivityService.findActivityType(order);
		if (activityResource == ActivityBelongType.OPERATOR
				|| activityResource == ActivityBelongType.AGENT && (refundPrefeAmount.compareTo(BigDecimal.ZERO) > 0)) {
			payTradeVo.setPrefeAmount(refundPrefeAmount);
			payTradeVo.setActivitier(tradeOrderActivityService.findActivityUserId(order));
		}
		// 接受返回消息的tag
		payTradeVo.setTag(null);
		return JSONObject.fromObject(null).toString();
	}

	@Transactional(rollbackFor = Exception.class)
	private void updateOrderStatus(TradeOrder order, TradeOrderItem item) throws Exception {
		List<String> rpcIdList = new ArrayList<String>();
		try {
			// 更新消费码为全部过期状态
			int result = tradeOrderItemDetailMapper.updateStatusWithExpire(item.getId());
			if (result < 1) {
				// 如果没有更新到，说明已经处理过了，需要回滚本次事务
				throw new Exception("更新消费码状态失败");
			}
			List<String> updateIds = Lists.newArrayList();
			updateIds.add(item.getId());
			tradeOrderItemMapper.updateCompleteById(updateIds);
			// 更新订单的的消费状态为已过期
			order.setConsumerCodeStatus(ConsumerCodeStatusEnum.EXPIRED);

			List<TradeOrderItem> itemList = order.getTradeOrderItem();

			// 判断是否所有订单项都已经完成，如果都已经完成了，就将订单的IsComplete字断改为 已经完成
			boolean isAllComplete = true;
			for (TradeOrderItem tradeOrderItem : itemList) {
				if (tradeOrderItem.getIsComplete() != OrderComplete.NO
						&& !tradeOrderItem.getId().equals(item.getId())) {
					isAllComplete = false;
				}
			}

			if (isAllComplete) {
				order.setIsComplete(OrderComplete.YES);
			}
			// 更新订单状态
			tradeOrderMapper.updateOrderStatus(order);
			
			// 回收库存
			List<StockAdjustVo> stockAdjustList = recycleStockInfo(order,item,rpcIdList);
		} catch (Exception e) {
			rollbackMQProducer.sendStockRollbackMsg(rpcIdList);
			throw e;
		}

	}
	
	private List<StockAdjustVo> recycleStockInfo(TradeOrder tradeOrder, TradeOrderItem item, List<String> rpcIdList)
			throws Exception {
		List<StockAdjustVo> stockAdjustList = new ArrayList<StockAdjustVo>();

		List<TradeOrderItem> tradeOrderItems = tradeOrder.getTradeOrderItem();
		if (tradeOrderItems == null) {
			tradeOrderItems = tradeOrderItemMapper.selectTradeOrderItem(tradeOrder.getId());
		}
		StockAdjustVo stockAdjustVo = new StockAdjustVo();
		String rpcId = UuidUtils.getUuid();
		rpcIdList.add(rpcId);
		stockAdjustVo.setRpcId(rpcId);

		stockAdjustVo.setOrderId(tradeOrder.getId());
		stockAdjustVo.setOrderNo(tradeOrder.getOrderNo());
		stockAdjustVo.setOrderResource(tradeOrder.getOrderResource());
		stockAdjustVo.setOrderType(tradeOrder.getType());
		stockAdjustVo.setStoreId(tradeOrder.getStoreId());

		stockAdjustVo.setStockOperateEnum(StockOperateEnum.RETURN_OF_GOODS);
		stockAdjustVo.setUserId(tradeOrder.getUserId());
		// 判断是否是团购和特惠商品
		boolean isGoodActivity = ActivityTypeEnum.GROUP_ACTIVITY == tradeOrder.getActivityType()
				|| isAttendSale(tradeOrder.getId(), item.getStoreSkuId());

		// 如果秒杀活动已经结束，则当成普通商品
		if (ActivityTypeEnum.SECKILL_ACTIVITY == tradeOrder.getActivityType()) {
			isGoodActivity = true;
			ActivitySeckill seckill = sctivitySeckillService.findSeckillById(tradeOrder.getActivityId());
			SeckillStatusEnum seckillStatus = seckill.getSeckillStatus();
			if (seckillStatus.ordinal() == SeckillStatusEnum.end.ordinal()
					|| seckillStatus.ordinal() == SeckillStatusEnum.closed.ordinal()) {
				// 如果秒杀活动已经结束，则当成普通商品
				isGoodActivity = false;
			}
		}
		// 判断是否是特惠活动，如果是，则判断特惠活动是否正在进行中，不在进行中则当成普通的商品减库存
		String saleId = findSaleId(tradeOrder.getId(), item.getStoreSkuId());
		if (!StringUtils.isNullOrEmpty(saleId)) {
			isGoodActivity = true;
			ActivitySale entity = activitySaleMapper.get(saleId);
			if (entity.getStatus() != 1) {
				isGoodActivity = false;
			}
		}

		AdjustDetailVo detail = new AdjustDetailVo();
		detail.setStoreSkuId(item.getStoreSkuId());
		detail.setGoodsSkuId("");
		detail.setMultipleSkuId("");
		detail.setGoodsName(item.getSkuName());
		detail.setPrice(item.getUnitPrice());
		detail.setPropertiesIndb(item.getPropertiesIndb());
		detail.setStyleCode(item.getStyleCode());
		detail.setBarCode(item.getBarCode());
		detail.setNum(item.getQuantity());
		detail.setIsEvent(isGoodActivity);
		List<AdjustDetailVo> adjustDetailList = Lists.newArrayList();
		adjustDetailList.add(detail);
		stockAdjustVo.setAdjustDetailList(adjustDetailList);
		
		// 走商城库存
		serviceStockManagerService.updateStock(stockAdjustVo);

		stockAdjustList.add(stockAdjustVo);
		return stockAdjustList;
	}

	/**
	 * 订单商品是否参与特惠活动
	 */
	public boolean isAttendSale(String orderId, String storeGoodSkuId) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("orderId", orderId);
		map.put("saleGoodsId", storeGoodSkuId);
		int count = activitySaleRecordService.selectOrderGoodsCount(map);
		return count > 0;
	}
	
	/**
	 * 订单商品是否参与特惠活动
	 */
	public String findSaleId(String orderId, String storeGoodSkuId) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("orderId", orderId);
		map.put("saleGoodsId", storeGoodSkuId);
		String saleId = activitySaleRecordService.selectOrderGoodsActivity(map);
		return saleId;
	}
}
