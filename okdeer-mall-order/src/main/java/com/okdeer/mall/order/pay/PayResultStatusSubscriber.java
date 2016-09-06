/** 
 * @Copyright: Copyright ©2005-2020 yschome.com Inc. All rights reserved
 * @项目名称: yschome-mall 
 * @文件名称: RefundsPayStatusSubscriberServiceImpl.java 
 * @Date: 2016年3月23日 
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的 
 */

package com.okdeer.mall.order.pay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.client.producer.TransactionSendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuService;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceServiceApi;
import com.okdeer.archive.store.entity.StoreAgentCommunity;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.group.entity.ActivityGroup;
import com.okdeer.mall.common.utils.HttpClientUtil;
import com.okdeer.mall.common.utils.LockUtil;
import com.okdeer.mall.common.utils.RandomStringUtil;
import com.okdeer.mall.common.utils.security.MD5;
import com.okdeer.mall.order.constant.OrderMessageConstant;
import com.okdeer.mall.order.constant.PayMessageConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.enums.ConsumeStatusEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.utils.JsonDateValueProcessor;
import com.yschome.api.pay.enums.TradeErrorEnum;
import com.yschome.base.common.utils.DateUtils;
import com.yschome.base.common.utils.StringUtils;
import com.yschome.base.common.utils.UuidUtils;
import com.yschome.base.framework.mq.AbstractRocketMQSubscriber;
import com.yschome.base.framework.mq.RocketMQTransactionProducer;
import com.yschome.base.framework.mq.RocketMqResult;
import com.okdeer.mall.activity.group.service.ActivityGroupService;
import com.okdeer.mall.order.mapper.TradeOrderItemDetailMapper;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderPayMapper;
import com.okdeer.mall.order.pay.entity.ResponseResult;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderLogisticsService;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.system.utils.mapper.JsonMapper;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * 余额支付结果消息订阅处理
 * 
 * @pr yschome-mall
 * @author guocp
 * @date 2016年3月23日 下午7:25:11
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月16日                               zengj				增加服务店订单下单处理流程
 *     12002           2016年8月5日                                 zengj				增加服务店订单下单成功增加销量
 *     重构4.1          2016年8月16日                               zengj				支付成功回调判断订单状态是不是买家支付中
 *     重构4.1          2016年8月22日                               maojj				余额支付失败，将订单状态更改为待付款状态
 *     重构4.1          2016年8月24日                               maojj				余额支付成功，如果是到店自提，生成提货码
 */
@Service
public class PayResultStatusSubscriber extends AbstractRocketMQSubscriber
		implements PayMessageConstant, OrderMessageConstant {

	private static final Logger logger = LoggerFactory.getLogger(PayResultStatusSubscriber.class);

	@Autowired
	private TradeOrderLogisticsService tradeOrderLogisticsService;

	@Autowired
	public TradeOrderRefundsService tradeOrderRefundsService;

	@Resource
	private TradeOrderPayService tradeOrderPayService;

	@Resource
	private TradeOrderService tradeOrderService;
	
	@Resource
	private TradeOrderItemService tradeOrderItemService;

	@Resource
	private TradeOrderPayMapper tradeOrderPayMapper;

	@Resource
	private TradeOrderItemMapper tradeOrderItemMapper;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceServiceApi goodsStoreSkuServiceService;

	@Resource
	private TradeOrderItemDetailMapper tradeOrderItemDetailMapper;

	@Autowired
	private ActivityGroupService activityGroupService;

	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	@Autowired
	private TradeOrderTimer tradeOrderTimer;

	@Autowired
	private RocketMQTransactionProducer rocketMQTransactionProducer;
	
	// Begin 12002 add by zengj
	/**
	 * 商品信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuService;
	// End 12002 add by zengj

	/**
	 * 开放平台Id
	 */
	@Value("${juhe.openId}")
	private String openId;
	/**
	 * 话费充值appKey
	 */
	@Value("${juhe.phonefee.appKey}")
	private String appKey;
	/**
	 * 流量充值appKey
	 */
	@Value("${juhe.dataplan.appKey}")
	private String dataPlanKey;
	/**
	 * 话费充值充值url
	 */
	@Value("${phonefee.onlineOrder}")
	private String submitOrderUrl;
	/**
	 * 流量套餐充值url
	 */
	@Value("${dataplan.onlineOrder}")
	private String dataplanOrderUrl;
	
	@Override
	public String getTopic() {
		return TOPIC_PAY_RESULT;
	}

	@Override
	public String getTags() {
		return TAG_PAY_RESULT_INSERT + JOINT + TAG_PAY_RESULT_CANCEL + JOINT + TAG_PAY_RESULT_CONFIRM + JOINT
				+ TAG_PAY_RESULT_REFUND + JOINT + TAG_PAY_RECHARGE_ORDER_BLANCE;
	}

	@Override
	public ConsumeConcurrentlyStatus subscribeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		MessageExt message = msgs.get(0);

		switch (message.getTags()) {
			case TAG_PAY_RESULT_INSERT:
				// 订单支付
				return insertProcessResult(message);
			case TAG_PAY_RESULT_CANCEL:
				// 订单取消
				return cancelProcessResult(message);
			case TAG_PAY_RESULT_CONFIRM:
				// 确认订单
				return confirmProcessResult(message);
			case TAG_PAY_RESULT_REFUND:
				// 订单退款
				return refundProcessResult(message);
			case TAG_PAY_RECHARGE_ORDER_BLANCE:
				return dealWithRechargeOrderPayResult(message);
			default:
				break;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	public synchronized void inserts(TradeOrder tradeOrder, ResponseResult result) throws Exception {
		List<TradeOrderItem> orderItem = tradeOrderItemMapper.selectOrderItemListById(tradeOrder.getId());
		TradeOrderItem item = orderItem.get(0);
		String storeSkuId = item.getStoreSkuId();
		int orderType = tradeOrder.getType().ordinal(); // 订单类型(0:实物订单,1:服务订单)

		tradeOrder.setTradeOrderItem(orderItem);
		int activityType = tradeOrder.getActivityType().ordinal(); // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		String activityId = tradeOrder.getActivityId();
		if (activityType == 4) { // 团购活动
			int skuNum = orderItem.get(0).getQuantity(); // 购买数量
			GoodsStoreSkuService skuService = new GoodsStoreSkuService();
			ActivityGroup activityGroup = new ActivityGroup();
			if (orderType == 1) {
				skuService = goodsStoreSkuServiceService.selectByStoreSkuId(storeSkuId);
			} else if (orderType == 0) {
				activityGroup = activityGroupService.selectServiceTime(activityId); // 查询团购活动时间
			}

			List<TradeOrderItemDetail> orderItemDetailList = new ArrayList<TradeOrderItemDetail>();
			for (int i = 0; i < skuNum; i++) {
				String OrderItemId = orderItem.get(0).getId();
				List<TradeOrderItemDetail> itemDetail = tradeOrderItemDetailMapper.selectByOrderItemById(OrderItemId);
				if (itemDetail.size() < skuNum) {
					TradeOrderItemDetail orderItemDetail = new TradeOrderItemDetail();
					orderItemDetail.setConsumeCode(RandomStringUtil.getRandomInt(8));
					orderItemDetail.setCreateTime(new Date());
					if (orderType == 1) {
						orderItemDetail.setEndTime(skuService.getEndTime());
						orderItemDetail.setStartTime(skuService.getStartTime());
					} else if (orderType == 0) {
						orderItemDetail.setEndTime(activityGroup.getEndTime());
						orderItemDetail.setStartTime(activityGroup.getStartTime());
					}
					orderItemDetail.setId(UuidUtils.getUuid());
					orderItemDetail.setOrderItemId(orderItem.get(0).getId());
					orderItemDetail.setStatus(ConsumeStatusEnum.noConsume);
					orderItemDetailList.add(orderItemDetail);
				}
			}
			if (result.getCode().equals(TradeErrorEnum.SUCCESS.getName())) {
				TradeOrderPay tradeOrderPay = new TradeOrderPay();
				tradeOrderPay.setId(UuidUtils.getUuid());
				tradeOrderPay.setCreateTime(new Date());
				tradeOrderPay.setPayAmount(tradeOrder.getActualAmount());
				tradeOrderPay.setOrderId(tradeOrder.getId());
				tradeOrderPay.setPayTime(new Date());
				tradeOrderPay.setPayType(PayTypeEnum.WALLET);
				if (orderType == 1) {
					tradeOrder.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
					tradeOrder.setDeliveryTime(new Date());
				} else {
					tradeOrder.setStatus(OrderStatusEnum.DROPSHIPPING);
				}
				tradeOrder.setUpdateTime(new Date());
				tradeOrder.setTradeOrderPay(tradeOrderPay);
			} else {
				logger.error("严重问题：订单支付失败,订单编号为：" + tradeOrder.getOrderNo());
			}
			if (orderItemDetailList.size() > 0) {
				tradeOrderItemDetailMapper.insertBatch(orderItemDetailList);
			}
			updateWithApply(tradeOrder);
		} else {
			if (result.getCode().equals(TradeErrorEnum.SUCCESS.getName())) {
				
				// Begin 12002 add by zengj
				if (tradeOrder.getType() == OrderTypeEnum.SERVICE_STORE_ORDER) {
					// 线上支付的，支付完成，销量增加
					GoodsStoreSku goodsStoreSku = this.goodsStoreSkuService.getById(item.getStoreSkuId());
					if (goodsStoreSku != null) {
						goodsStoreSku.setSaleNum(
								(goodsStoreSku.getSaleNum() == null ? 0 : goodsStoreSku.getSaleNum()) + item.getQuantity());
						goodsStoreSkuService.updateByPrimaryKeySelective(goodsStoreSku);
					}
				}
				// End 12002 add by zengj
				
				// Begin added by maojj 2016-08-24 付款成功生成提货码
				if (tradeOrder.getPickUpType() == PickUpTypeEnum.TO_STORE_PICKUP) {
					tradeOrder.setPickUpCode(RandomStringUtil.getRandomInt(6));
				}
				// Begin added by maojj 2016-08-24 付款成功生成提货码
				
				TradeOrderPay tradeOrderPay = new TradeOrderPay();
				tradeOrderPay.setId(UuidUtils.getUuid());
				tradeOrderPay.setCreateTime(new Date());
				tradeOrderPay.setPayAmount(tradeOrder.getActualAmount());
				tradeOrderPay.setOrderId(tradeOrder.getId());
				tradeOrderPay.setPayTime(new Date());
				tradeOrderPay.setPayType(PayTypeEnum.WALLET);
				if (orderType == 1) {
					tradeOrder.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
				} else {
					tradeOrder.setStatus(OrderStatusEnum.DROPSHIPPING);
				}
				tradeOrder.setUpdateTime(new Date());
				tradeOrder.setTradeOrderPay(tradeOrderPay);
			} else {
				logger.error("严重问题：订单支付失败,订单编号为：" + tradeOrder.getOrderNo());
				// Begin added by maojj 2016-08-22 订单支付失败，将订单状态更改为代付款状态
				tradeOrder.setStatus(OrderStatusEnum.UNPAID);
				// Begin added by maojj 2016-08-22
			}
			updateWithApply(tradeOrder);
		}
	}

	
	
	private StoreInfo getStoreInfo(String storeId) throws Exception {
		return storeInfoService.getStoreBaseInfoById(storeId);
	}

	/**
	 * 订单支付并发送消息(快送同步)
	 * @param tradeOrder
	 * @return
	 */
	public boolean updateWithApply(TradeOrder tradeOrder) throws Exception {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
		tradeOrder.setTradeOrderLogistics(tradeOrderLogisticsService.findByOrderId(tradeOrder.getId()));
		StoreInfo storeInfo = getStoreInfo(tradeOrder.getStoreId());
		JSONObject json = JSONObject.fromObject(tradeOrder, jsonConfig);
		json.put("storeType", storeInfo.getType());
		List<StoreAgentCommunity> communitys = storeInfoService.getAgentCommunitysByStoreId(tradeOrder.getStoreId());
		if (!Iterables.isEmpty(communitys)) {
			json.put("storeAgentCommunity", communitys.get(0));
		}

		Message msg = new Message(getTopicByStoreType(storeInfo.getType()), TAG_ORDER_APPLY,
				json.toString().getBytes(Charsets.UTF_8));
		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, tradeOrder,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
						try {
							tradeOrderService.updateMyOrderStatus((TradeOrder) object);
							return LocalTransactionState.COMMIT_MESSAGE;
						} catch (Exception e) {
							logger.error("执行支付回调失败", e);
							return LocalTransactionState.ROLLBACK_MESSAGE;
						}
					}
				}, new TransactionCheckListener() {

					@Override
					public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
						return LocalTransactionState.COMMIT_MESSAGE;
					}
				});
		return RocketMqResult.returnResult(sendResult);
	}

	/**
	 * 确认话费充值订单支付结果消息处理
	 */
	private ConsumeConcurrentlyStatus dealWithRechargeOrderPayResult(MessageExt message) {
		String tradeNum = null;
		try {
			String msg = new String(message.getBody(), Charsets.UTF_8);
			logger.info("订单支付状态消息:" + msg);
			ResponseResult result = JsonMapper.nonEmptyMapper().fromJson(msg, ResponseResult.class);
			tradeNum = result.getTradeNum();
			if (StringUtils.isEmpty(result.getTradeNum())) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
		
			//查询订单消息
			TradeOrder tradeOrder =  tradeOrderService.getByTradeNum(tradeNum);
			if(tradeOrder == null) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			if(tradeOrder.getStatus() == OrderStatusEnum.DROPSHIPPING || tradeOrder.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			List<TradeOrderItem> tradeOrderItems = tradeOrderItemService.selectOrderItemByOrderId(tradeOrder.getId());
			if(tradeOrderItems.isEmpty()) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			
			insertTradeOrderPay(tradeOrder);
			synchronized (LockUtil.getInitialize().synObject(tradeNum)) {
				//调用第三方聚合充值平台
				TradeOrderItem tradeOrderItem = tradeOrderItems.get(0);
				String phoneno = tradeOrderItem.getRechargeMobile();
				int cardnum = tradeOrder.getTotalAmount().intValue();
				String orderid = tradeOrder.getTradeNum();
				String pid = tradeOrderItem.getStoreSkuId();
				OrderTypeEnum orderType = tradeOrder.getType();
				String sign = null;
				String url = null;
				if (orderType == OrderTypeEnum.PHONE_PAY_ORDER) {
					//话费充值
					sign = MD5.md5(openId + appKey + phoneno + cardnum + orderid);
					url = submitOrderUrl + "?key=" + appKey + "&phoneno=" + phoneno + "&orderid=" + orderid + "&cardnum=" + cardnum + "&sign=" + sign;
					String resp = HttpClientUtil.get(url);
					JSONObject respJson = JSONObject.fromObject(resp);
					int errorCode = respJson.getInt("error_code");
					if (errorCode == 0) {
						JSONObject resultJson = respJson.getJSONObject("result");
						int gameState = Integer.parseInt(resultJson.getString("game_state"));
						if(gameState == 9) {
							//充值失败，走退款流程
							this.tradeOrderRefundsService.insertRechargeRefunds(tradeOrder);
						} else if (gameState == 0) {
							updateTradeOrderStatus(tradeOrder);
						}
					} else {
						//充值失败，走退款流程
						this.tradeOrderRefundsService.insertRechargeRefunds(tradeOrder);
					}
				} else if(orderType == OrderTypeEnum.TRAFFIC_PAY_ORDER) {
					//流量充值
					sign = MD5.md5(openId + dataPlanKey + phoneno + pid + orderid);
					url = dataplanOrderUrl + "?key=" + dataPlanKey + "&phone=" + phoneno + "&pid=" + pid + "&orderid=" + orderid + "&sign=" + sign;
					
					String resp = HttpClientUtil.get(url);
					JSONObject respJson = JSONObject.fromObject(resp);
					int errorCode = respJson.getInt("error_code");
					if(errorCode != 0) {
						//充值聚合订单提交失败，走退款流程
						this.tradeOrderRefundsService.insertRechargeRefunds(tradeOrder);
					} else {
						updateTradeOrderStatus(tradeOrder);
					}
				}
			}
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		} catch (Exception e) {
			logger.error("充值订单支付状态消息处理失败", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		} finally {
			if(tradeNum != null) {
				LockUtil.getInitialize().unLock(tradeNum);
			}
		}
	}
	
	private void insertTradeOrderPay(TradeOrder tradeOrder) throws Exception {
		//新增支付方式记录
		TradeOrderPay tradeOrderPay = new TradeOrderPay();
		tradeOrderPay.setId(UuidUtils.getUuid());
		tradeOrderPay.setCreateTime(new Date());
		tradeOrderPay.setPayAmount(tradeOrder.getActualAmount());
		tradeOrderPay.setOrderId(tradeOrder.getId());
		tradeOrderPay.setPayTime(new Date());
		tradeOrderPay.setPayType(PayTypeEnum.WALLET);
		this.tradeOrderPayService.insertSelective(tradeOrderPay);
	}
	
	private void updateTradeOrderStatus(TradeOrder tradeOrder) throws Exception {
		//修改订单状态为代发货
		tradeOrder.setStatus(OrderStatusEnum.DROPSHIPPING);
		tradeOrder.setUpdateTime(new Date());
		this.tradeOrderService.updateRechargeOrderByTradeNum(tradeOrder);
	}
	
	/**
	 * 订单支付消息处理
	 */
	private ConsumeConcurrentlyStatus insertProcessResult(MessageExt message) {
		try {
			String msg = new String(message.getBody(), Charsets.UTF_8);
			logger.info("订单支付状态消息:" + msg);
			ResponseResult result = JsonMapper.nonEmptyMapper().fromJson(msg, ResponseResult.class);
			if (StringUtils.isEmpty(result.getTradeNum())) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			TradeOrder tradeOrder = tradeOrderService.getByTradeNum(result.getTradeNum());
			// Begin 判断订单状态为不是待买家支付中，就过掉该消息 add by zengj 
			if (tradeOrder == null || (tradeOrder.getStatus() != OrderStatusEnum.UNPAID
					&& tradeOrder.getStatus() != OrderStatusEnum.BUYER_PAYING)) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			// End 判断订单状态为不是待买家支付中，就过掉该消息 add by zengj


			inserts(tradeOrder, result);

			// Begin 重构4.1 add by zengj
			// 判断是否是服务店订单，如果是服务店订单走单独流程
			if (tradeOrder.getType() == OrderTypeEnum.SERVICE_STORE_ORDER) {
				Date serviceTime = DateUtils.parseDate(tradeOrder.getPickUpTime(), "yyyy-MM-dd HH:mm");
				// 预约服务时间过后2小时未派单的自动取消订单
				tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_server_timeout, tradeOrder.getId(),
						(DateUtils.addHours(serviceTime, 2).getTime() - DateUtils.getSysDate().getTime()) / 1000);
			} else {
				// End 重构4.1 add by zengj
				// 发送计时消息
				if (ActivityTypeEnum.GROUP_ACTIVITY == tradeOrder.getActivityType()) {
					if (tradeOrder.getType() == OrderTypeEnum.SERVICE_ORDER) {
						List<TradeOrderItem> orderItem = tradeOrderItemMapper
								.selectOrderItemListById(tradeOrder.getId());
						if (orderItem != null && !Iterables.isEmpty(orderItem)) {
							for (TradeOrderItem item : orderItem) {
								GoodsStoreSkuService sku = goodsStoreSkuServiceService
										.selectBySkuId(item.getStoreSkuId());
								long delayTimeMillis = (sku.getEndTime().getTime() - System.currentTimeMillis()) / 1000;
								tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_service_order_refund_timeout,
										tradeOrder.getId(), delayTimeMillis);
							}
						}
					} else {
						tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_group_timeout,
								tradeOrder.getId());
					}
				} else {
					tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_timeout, tradeOrder.getId());
				}
			}
		} catch (Exception e) {
			logger.error("订单支付状态消息处理失败", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 取消订单支付结果消息处理
	 */
	private ConsumeConcurrentlyStatus cancelProcessResult(MessageExt message) {
		String msg = new String(message.getBody(), Charsets.UTF_8);
		logger.info("取消订单支付结果同步消息:" + msg);
		try {
			ResponseResult result = JsonMapper.nonEmptyMapper().fromJson(msg, ResponseResult.class);
			if (StringUtils.isEmpty(result.getTradeNum())) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			if (TradeErrorEnum.TRADE_REPEAT.name().equals(result.getCode())) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}

			TradeOrder tradeOrder = tradeOrderService.getByTradeNum(result.getTradeNum());
			// 设置当前订单状态
			tradeOrder.setCurrentStatus(tradeOrder.getStatus());
			if (OrderStatusEnum.CANCELING != tradeOrder.getStatus()
					&& OrderStatusEnum.REFUSING != tradeOrder.getStatus()) {
				logger.info(tradeOrder.getOrderNo() + "订单状态已改变，取消订单支付结果不做处理");
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}

			if (result.getCode().equals(TradeErrorEnum.SUCCESS.getName())) {
				// 判断是否取消订单
				if (OrderStatusEnum.CANCELING == tradeOrder.getStatus()) {
					tradeOrder.setStatus(OrderStatusEnum.CANCELED);
				} else if (OrderStatusEnum.REFUSING == tradeOrder.getStatus()) {
					tradeOrder.setStatus(OrderStatusEnum.REFUSED);
				}
				tradeOrderService.updateOrderStatus(tradeOrder);
			} else {
				logger.error("取消(拒收)订单退款支付失败,订单编号为：" + tradeOrder.getOrderNo() + "，问题原因" + result.getMsg());
			}
		} catch (Exception e) {
			logger.error("取消订单支付结果同步消息处理失败", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 确认收货支付结果消息处理
	 */
	private ConsumeConcurrentlyStatus confirmProcessResult(MessageExt message) {
		String msg = new String(message.getBody(), Charsets.UTF_8);
		logger.info("确认收货支付结果消息:" + msg);
		try {
			ResponseResult result = JsonMapper.nonEmptyMapper().fromJson(msg, ResponseResult.class);
			if (StringUtils.isEmpty(result.getTradeNum())) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			if (TradeErrorEnum.TRADE_REPEAT.name().equals(result.getCode())) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}

			TradeOrder tradeOrder = tradeOrderService.getByTradeNum(result.getTradeNum());
			if (result.getCode().equals(TradeErrorEnum.SUCCESS.getName())) {
				tradeOrder.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
				tradeOrderService.updateOrderStatus(tradeOrder);
			} else {
				logger.error("确认收货支付结果同步消息处理失败,订单编号为：" + tradeOrder.getOrderNo() + "，问题原因" + result.getMsg());
			}
		} catch (Exception e) {
			logger.error("确认收货支付结果同步消息处理失败", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 订单退款支付结果消息处理
	 */
	private ConsumeConcurrentlyStatus refundProcessResult(MessageExt message) {
		String msg = new String(message.getBody(), Charsets.UTF_8);
		logger.info("退款支付状态消息:" + msg);
		try {
			ResponseResult result = JsonMapper.nonEmptyMapper().fromJson(msg, ResponseResult.class);
			if (result.getCode().equals(TradeErrorEnum.TRADE_REPEAT)) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			if (StringUtils.isEmpty(result.getTradeNum())) {
				logger.error("退款支付状态消息处理失败,支付流水号为空：" + msg);
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundsService.getByTradeNum(result.getTradeNum());
			if (tradeOrderRefunds == null) {
				logger.error("退款支付状态消息处理失败,支付流水号查询数据为空：" + msg);
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}

			// 返回状态为success,更新订单状态
			if (result.getCode().equals(TradeErrorEnum.SUCCESS.getName())) {
				tradeOrderRefunds.setRefundMoneyTime(new Date());
				tradeOrderRefunds.setRefundsStatus(RefundsStatusEnum.REFUND_SUCCESS);
				tradeOrderRefundsService.updateRefunds(tradeOrderRefunds);
			} else {
				logger.error("退款支付状态消息处理失败,退款单编号为：" + tradeOrderRefunds.getRefundNo() + "，问题原因" + result.getMsg());

			}
		} catch (Exception e) {
			logger.error("退款支付状态消息处理失败", e);
			return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	/**
	 * 根据店铺类型获取TOPIC
	 *
	 * @param storeType
	 * @return
	 */
	private String getTopicByStoreType(StoreTypeEnum storeType) {
		switch (storeType) {
			case AROUND_STORE:
				return TOPIC_ORDER_AROUND;
			case FAST_DELIVERY_STORE:
				return TOPIC_ORDER_FAST;
			case CLOUD_STORE:
				return TOPIC_ORDER_CLOUD;
			case ACTIVITY_STORE:
				return TOPIC_ORDER_ACTIVITY;
			// Begin 重构4.1 add by zengj
			case SERVICE_STORE:
				return TOPIC_ORDER_SERVICE;
			// End 重构4.1 add by zengj
			
			default:
				break;
		}
		return null;
	}

}