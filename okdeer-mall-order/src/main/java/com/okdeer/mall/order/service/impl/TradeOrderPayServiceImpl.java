
package com.okdeer.mall.order.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.client.producer.TransactionSendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.okdeer.api.pay.enums.AmountUpdateType;
import com.okdeer.api.pay.enums.BusinessTypeEnum;
import com.okdeer.api.pay.enums.PayTradeServiceTypeEnum;
import com.okdeer.api.pay.enums.PayTradeTypeEnum;
import com.okdeer.api.pay.enums.PayTypeEnum;
import com.okdeer.api.pay.enums.SystemEnum;
import com.okdeer.api.pay.pay.dto.PayReqestDto;
import com.okdeer.api.pay.pay.dto.PayTradeDto;
import com.okdeer.api.pay.service.IPayServiceApi;
import com.okdeer.api.pay.tradeLog.dto.BalancePayTradeVo;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.base.framework.mq.RocketMQTransactionProducer;
import com.okdeer.base.framework.mq.RocketMqResult;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsService;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.order.constant.mq.PayMessageConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderLog;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.ActivityBelongType;
import com.okdeer.mall.order.enums.OrderComplete;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderLogMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.mapper.TradeOrderPayMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsItemMapper;
import com.okdeer.mall.order.service.TradeOrderActivityService;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeOrderPayServiceApi;
import com.okdeer.base.common.utils.mapper.JsonMapper;

/**
 * @DESC:
 * @author YSCGD
 * @date 2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderPayServiceApi")
public class TradeOrderPayServiceImpl implements TradeOrderPayService, TradeOrderPayServiceApi {

	public static final Logger logger = LoggerFactory.getLogger(TradeOrderPayServiceImpl.class);

	/**
	 * 友门鹿云钱包账户
	 */
	@Value("${yscWalletAccount}")
	private String yscWalletAccount;

	@Resource
	private TradeOrderPayMapper tradeOrderPayMapper;

	@Resource
	private TradeOrderMapper tradeOrderMapper;

	@Resource
	private TradeOrderItemMapper tradeOrderItemMapper;

	@Resource
	private TradeOrderRefundsItemMapper tradeOrderRefundsItemMapper;

	@Resource
	private TradeOrderLogMapper tradeOrderLogMapper;

	@Reference(version = "1.0.0", check = false)
	private IPayServiceApi iPayServiceApi;

	@Resource
	private RocketMQTransactionProducer rocketMQTransactionProducer;

	@Resource
	private RocketMQProducer rocketMQProducer;

	@Resource
	private ActivityDiscountService activityDiscountService;

	@Resource
	private ActivityCollectCouponsService activityCollectCouponsService;

	@Resource
	private TradeOrderActivityService tradeOrderActivityService;

	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	/**
	 * 支付接口
	 */
	@Reference(version = "1.0.0", check = false)
	private IPayServiceApi ipayServiceApi;

	@Resource
	private ActivityCouponsService activityCouponsService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertSelective(TradeOrderPay tradeOrderPay) throws ServiceException {

		tradeOrderPayMapper.insertSelective(tradeOrderPay);

	}

	@Override
	public TradeOrderPay selectByOrderId(String orderId) throws ServiceException {
		return tradeOrderPayMapper.selectByOrderId(orderId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertOrderPay(TradeOrderPay tradeOrderPay) throws ServiceException {

		// 1、订单金额 支付联调,如果支付成功才做订单支付数据的新增 暂未开始

		boolean flag = true;

		// 缺少 订单支付方法

		TradeOrderLog tradeOrderLog = new TradeOrderLog();

		TradeOrderLog orderLog = tradeOrderPay.getTradeOrderLog();

		try {
			BeanUtils.copyProperties(tradeOrderLog, orderLog);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		tradeOrderLogMapper.insertSelective(tradeOrderLog);
		if (flag) { // 如果订单支付回调状态为true

		}

	}

	@Override
	public Map<String, Object> getMapInfo(int paymentType, TradeOrder tradeOrder, String ip, String filePath,
			String openId) throws Exception {

		TradeOrder order = tradeOrderMapper.selectTradeDetailInfoById(tradeOrder.getId());

		// int acType = order.getActivityType().ordinal(); // 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		// String activityId = order.getActivityItemId(); // 活动项ID

		// 设置订单信息
		PayReqestDto payReqest = new PayReqestDto();
		payReqest.setOpenid(openId);

		// 是否平台优惠
		boolean isPlatformPreferential = false;
		// 优惠额退款 判断是否有优惠劵
		if (order.getActivityType() == ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES
				|| order.getActivityType() == ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES
				|| order.getActivityType() == ActivityTypeEnum.VONCHER) {
			ActivityBelongType activityBelong = tradeOrderActivityService.findActivityType(order);
			if (ActivityBelongType.OPERATOR == activityBelong || ActivityBelongType.AGENT == activityBelong) {
				isPlatformPreferential = true;
			}
		}

		// 优惠金额
		if (isPlatformPreferential && order.getPreferentialPrice() != null
				&& order.getPreferentialPrice().compareTo(BigDecimal.ZERO) > 0) {
			// 优化活动发起人，比如代理商id或者运营商id
			payReqest.setActivitier(tradeOrderActivityService.findActivityUserId(order));
			payReqest.setPrefeAmount(order.getPreferentialPrice());
		}

		BigDecimal actualAmount = order.getActualAmount();
		String tradeNum = order.getTradeNum();

		// 买家ID
		payReqest.setUserId(order.getUserId());
		// 订单编号
		payReqest.setServiceNo(order.getOrderNo());
		// 交易号
		payReqest.setTradeNum(tradeNum);
		// 交易名称
		payReqest.setTradeName("订单支付");
		// 交易金额
		payReqest.setTradeAmount(actualAmount);
		// 用户端IP
		payReqest.setIp(ip);
		// 交易描述
		payReqest.setTradeDescription("订单支付");
		// 业务类型
		if (order.getType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
			payReqest.setServiceType(PayTradeServiceTypeEnum.STORE_CONSUME_ORDER);
			payReqest.setReceiver(storeInfoService.getBossIdByStoreId(order.getStoreId()));
		} else {
			payReqest.setServiceType(PayTradeServiceTypeEnum.ORDER);
		}

		// 系统类型
		payReqest.setSystemEnum(SystemEnum.MALL);
		// 业务ID，如订单ID，服务ID
		payReqest.setServiceId(tradeOrder.getId());
		// 支付类型 0:云钱包,1:支付宝支付,2:微信支付,3:京东支付,4:现金支付
		if (1 == paymentType) {
			payReqest.setReturnUrl("http://202.104.122.130:62032/yscpay/alipay/return");
			payReqest.setTradeType(PayTradeTypeEnum.APP_ALIPAY);
		} else if (2 == paymentType) {
			payReqest.setReturnUrl("http://202.104.122.130:62032/yscpay/alipay/return");
			payReqest.setTradeType(PayTradeTypeEnum.APP_WXPAY);
		} else if (3 == paymentType) {
			payReqest.setReturnUrl("http://202.104.122.130:62032/yscpay/alipay/return");
			payReqest.setTradeType(PayTradeTypeEnum.WAP_JDPAY);
		} else if (6 == paymentType) {
			payReqest.setReturnUrl("");
			payReqest.setTradeType(PayTradeTypeEnum.WX_WXPAY);
		}
		logger.info("payReqest==" + payReqest.toString());
		// 0:云钱包,1:支付宝支付,2:微信支付,3:京东支付,4:现金支付,6:微信公众号支付
		String result = null;
		if (paymentType == 1) { // 支付宝支付
			result = iPayServiceApi.appPay(payReqest);
		} else if (paymentType == 2) { // 微信支付
			result = iPayServiceApi.appPay(payReqest);
		} else if (paymentType == 3) { // 京东支付
			result = ipayServiceApi.pay(payReqest);
		} else if (paymentType == 0) { // 余额
			result = ipayServiceApi.pay(payReqest);
		} else if (paymentType == 6) { // 微信公众号支付
			result = ipayServiceApi.appPay(payReqest);
		}

		return getStrToMap(result, paymentType, tradeOrder.getId(), filePath);
	}

	private Map<String, Object> getStrToMap(String str, int paymentType, String orderId, String filePath) {
		logger.error("支付信息：{}", str);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("orderId", orderId);
		// 0:云钱包,1:支付宝支付,2:微信支付,3:京东支付,4:现金支付
		if (1 == paymentType) {
			result.put("alipay", str);
		} else if (2 == paymentType) {
			JSONObject obj = JSONObject.parseObject(str);
			String code = "0";
			if (!"0".equals(code)) {
				return null;
			}
			result.putAll((JSONObject) obj.get("data"));
		} else if (3 == paymentType) {
			if (!createHtml(orderId, str, filePath)) {
				return null;
			}
			result.put("url", "/trade/jdPay?orderId=" + orderId);
		} else if (6 == paymentType) {
			JSONObject obj = JSONObject.parseObject(str);
			result.putAll((JSONObject) obj.get("data"));
		}
		result.put("paymentType", paymentType);
		return result;
	}

	/**
	 * 生成html代码
	 * 
	 * @param orderId 订单ID
	 * @param html html字符串
	 */
	public static boolean createHtml(String orderId, String html, String filePath) {
		long beginDate = (new Date()).getTime();
		File formFile = new File(filePath);

		if (!formFile.exists()) {
			formFile.mkdir();
			File files = new File(filePath + "/" + orderId + ".html");
			BufferedWriter writer = null;
			try {
				if (!files.exists()) {
					files.createNewFile();
				}
				writer = new BufferedWriter(new FileWriter(files));
				writer.write(html);
			} catch (IOException e) {
				logger.error("生成京东页面异常：{}", e);
				return false;
			} finally {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		logger.error("共用时：：{}", ((new Date()).getTime() - beginDate) + "ms");
		return true;
	}

	/**
	 * 
	 * @author yangq
	 * @desc 更新订单并支付
	 * @param tradeOrder
	 * @return
	 * @throws MQClientException
	 * @throws ServiceException
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateAndPay(TradeOrder tradeOrder) throws Exception {

		tradeOrder.setStatus(OrderStatusEnum.DROPSHIPPING);

		JSONObject jsonObject = new JSONObject();
		List<PayTradeDto> payTrades = buildPay(tradeOrder);

		jsonObject.put("list", payTrades);
		jsonObject.put("topic_pay", PayMessageConstant.TOPIC_PAY);

		String json = JsonMapper.nonDefaultMapper().toJson(jsonObject);
		Message msg = new Message(PayMessageConstant.TOPIC_BALANCE_PAY_TRADE, PayMessageConstant.TAG_PAY_RESULT_THIRD,
				json.getBytes(Charsets.UTF_8));
		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, tradeOrder,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
						insertTradeOrderPayLog((TradeOrder) object);
						return LocalTransactionState.COMMIT_MESSAGE;
					}
				}, new TransactionCheckListener() {

					public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
						return LocalTransactionState.COMMIT_MESSAGE;
					}
				});
		return RocketMqResult.returnResult(sendResult);
	}

	/**
	 * 构建支付对象
	 */
	private List<PayTradeDto> buildPay(TradeOrder order) throws ServiceException {

		List<PayTradeDto> payTrades = Lists.newArrayList();

		// 优惠额退款 判断是否有优惠劵
		if (order.getPreferentialPrice().compareTo(BigDecimal.ZERO) > 0) {
			PayTradeDto payUserPrivilege = new PayTradeDto();
			payUserPrivilege.setAmount(order.getActualAmount());
			payUserPrivilege.setAmountUpdateType(AmountUpdateType.LOG_ONLY);
			payUserPrivilege.setPayType(PayTypeEnum.ALIPAY);
			payUserPrivilege.setServiceFkId(order.getId());
			payUserPrivilege.setServiceNo(order.getOrderNo());
			// payUserPrivilege.setServiceType(PayServiceTypeEnum.ORDER);
			payUserPrivilege.setTitle("订单下单");
			payUserPrivilege.setAmount(order.getPreferentialPrice());

			if (ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES == order.getActivityType()
					|| ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES == order.getActivityType()) {
				// 满折:店铺；满减活动：店铺、运营商
				ActivityDiscount discount = activityDiscountService.selectByPrimaryKey(order.getActivityId());
				if (!ActivityCollectCoupons.OPERATOR_CODE.equals(discount.getStoreId())) {
					payUserPrivilege.setUserId(discount.getStoreId());
				} else {
					// 运营商
					payUserPrivilege.setUserId(yscWalletAccount);
				}

			} else if (ActivityTypeEnum.VONCHER == order.getActivityType()) {
				// 代金卷：运营商、代理商
				ActivityCollectCoupons coupons = activityCollectCouponsService.get(order.getActivityId());
				// 判断是否代理商发的
				if (!ActivityCollectCoupons.OPERATOR_CODE.equals(coupons.getBelongType())) {
					// TODO 代理商ID coupons.getBelongType()
					payUserPrivilege.setUserId("");
				} else {
					// 运营商
					payUserPrivilege.setUserId(yscWalletAccount);
				}
			}
			payTrades.add(payUserPrivilege);
		}

		// 买家帐户余额更改
		PayTradeDto payTrade = new PayTradeDto();
		payTrade.setAmount(order.getActualAmount());
		payTrade.setAmountUpdateType(AmountUpdateType.LOG_ONLY);
		payTrade.setPayType(PayTypeEnum.ALIPAY);
		payTrade.setServiceFkId(order.getId());
		payTrade.setServiceNo(order.getOrderNo());
		// payTrade.setServiceType(PayServiceTypeEnum.ORDER);
		payTrade.setTitle("订单支付");
		payTrade.setUserId(order.getUserId());

		// 卖家账户余额更改
		PayTradeDto payTradeSeller = new PayTradeDto();
		payTradeSeller.setServiceFkId(order.getId());
		payTradeSeller.setServiceNo(order.getOrderNo());
		// payTradeSeller.setServiceType(PayServiceTypeEnum.ORDER);
		payTradeSeller.setTitle("订单支付");
		payTradeSeller.setAmountUpdateType(AmountUpdateType.UPDATE_FROZEN);
		payTradeSeller.setPayType(PayTypeEnum.ALIPAY);
		payTradeSeller.setAmount(order.getTotalAmount().negate());
		payTradeSeller.setUserId(order.getUserId());

		payTrades.add(payTrade);
		payTrades.add(payTradeSeller);
		return payTrades;
	}

	/**
	 * 
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public void insertTradeOrderPayLog(TradeOrder tradeOrder) {
		tradeOrderMapper.updateByPrimaryKeySelective(tradeOrder);
	}

	/**
	 * 取消订单付款
	 */
	@Override
	public boolean cancelOrderPay(TradeOrder tradeOrder) throws Exception {
		// 判断非在线支付
		if (tradeOrder.getPayWay() != PayWayEnum.PAY_ONLINE) {
			return true;
		}
		// 判断非取消中状态和拒绝中的状态则不需要退款
		if (OrderStatusEnum.CANCELING != tradeOrder.getStatus() && OrderStatusEnum.REFUSING != tradeOrder.getStatus()) {
			return true;
		}

		TradeOrderPay orderPay = this.selectByOrderId(tradeOrder.getId());
		if (orderPay.getPayType() == com.okdeer.mall.order.enums.PayTypeEnum.WALLET) {
			String tradesPaymentJson = buildBalanceCancelPay(tradeOrder);
			Message msg = new Message(PayMessageConstant.TOPIC_BALANCE_PAY_TRADE, PayMessageConstant.TAG_PAY_TRADE_MALL,
					tradesPaymentJson.getBytes(Charsets.UTF_8));
			// 发送消息
			SendResult sendResult = rocketMQProducer.send(msg);
			if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
				throw new Exception("取消订单余额支付发送消息失败");
			}
		} else if (tradeOrder.getType() == OrderTypeEnum.SERVICE_STORE_ORDER
				&& tradeOrder.getIsBreach() == WhetherEnum.whether
				&& (com.okdeer.mall.order.enums.PayTypeEnum.ALIPAY == orderPay.getPayType()
						|| com.okdeer.mall.order.enums.PayTypeEnum.WXPAY == orderPay.getPayType())) {
			// 如果是上门服务订单，并且违约了，还是第三方支付订单，需要赔偿违约金给商家
			// 构建支付违约金信息
			String tradesPaymentJson = buildBreachMoneyPay(tradeOrder);

			Message msg = new Message(PayMessageConstant.TOPIC_BALANCE_PAY_TRADE, PayMessageConstant.TAG_PAY_TRADE_MALL,
					tradesPaymentJson.getBytes(Charsets.UTF_8));
			// 发送消息
			SendResult sendResult = rocketMQProducer.send(msg);
			if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
				throw new Exception("取消订单违约金收入发送消息失败");
			}
		}
		return true;
	}

	/**
	 * 构建取消订单支付对象
	 */
	private String buildBalanceCancelPay(TradeOrder order) throws ServiceException {

		BigDecimal preferentialAmount = null;
		// 优惠额退款 判断是否有优惠劵
		if (order.getPreferentialPrice().compareTo(BigDecimal.ZERO) > 0) {
			ActivityBelongType activityBelong = tradeOrderActivityService.findActivityType(order);
			if (ActivityBelongType.AGENT == activityBelong) {
				// 代理商发起的活动，需要退回冻结余额
				preferentialAmount = order.getPreferentialPrice();
			}
		}
		BalancePayTradeVo payTradeVo = new BalancePayTradeVo();
		// Begin V1.2 modified by maojj 2016-11-09
		if (order.getIsBreach() == WhetherEnum.whether) {
			// 如果订单需要支付违约金，则退款金额为：实付金额-收取的违约金
			payTradeVo.setAmount(order.getActualAmount().subtract(order.getBreachMoney()));
		} else {
			payTradeVo.setAmount(order.getActualAmount());
		}
		//用于云钱包校验是否需要收取违约金
		payTradeVo.setCheckAmount(order.getActualAmount());
		// End V1.2 modified by maojj 2016-11-09
		payTradeVo.setIncomeUserId(order.getUserId());
		payTradeVo.setTradeNum(order.getTradeNum());
		payTradeVo.setTitle("取消订单(余额支付)，交易号：" + order.getTradeNum());
		
		if (order.getType() == OrderTypeEnum.SERVICE_STORE_ORDER) {
			payTradeVo.setBusinessType(BusinessTypeEnum.SERVICE_STORE_ORDER_CANCEL);
		} else {
			payTradeVo.setBusinessType(BusinessTypeEnum.CANCEL_ORDER);
		}
		payTradeVo.setServiceFkId(order.getId());
		payTradeVo.setServiceNo(order.getOrderNo());
		// 支付人:友门鹿
		payTradeVo.setPayUserId(storeInfoService.getBossIdByStoreId(order.getStoreId()));
		// 优惠金额
		if (preferentialAmount != null && preferentialAmount.compareTo(BigDecimal.ZERO) >= 0) {
			// 优化活动发起人，比如代理商id或者运营商id
			payTradeVo.setActivitier(tradeOrderActivityService.findActivityUserId(order));
			payTradeVo.setPrefeAmount(preferentialAmount);
		}
		// 接受返回消息的tag
		payTradeVo.setTag(PayMessageConstant.TAG_PAY_RESULT_CANCEL);
		return JSONObject.toJSONString(payTradeVo);
	}
	
	/**
	 * @Description: 构建违约金消息
	 * @param order 订单信息
	 * @return
	 * @throws ServiceException
	 * @author zengjizu
	 * @date 2016年11月11日
	 */
	private String buildBreachMoneyPay(TradeOrder order) throws ServiceException {
		BalancePayTradeVo payTradeVo = new BalancePayTradeVo();
		payTradeVo.setAmount(order.getBreachMoney());
		payTradeVo.setIncomeUserId(storeInfoService.getBossIdByStoreId(order.getStoreId()));
		payTradeVo.setTradeNum(order.getTradeNum());
		payTradeVo.setTitle("取消订单违约金收入[" + order.getTradeNum()+"]");
		payTradeVo.setBusinessType(BusinessTypeEnum.PAY_BREACH_FEE);
		payTradeVo.setServiceFkId(order.getId());
		payTradeVo.setServiceNo(order.getOrderNo());
		// 接受返回消息的tag
		payTradeVo.setTag(null);
		return JSONObject.toJSONString(payTradeVo);
	}
	/**
	 * 确认订单付款
	 */
	@Override
	public boolean confirmOrderPay(TradeOrder tradeOrder) throws Exception {

		if (tradeOrder.getPayWay() != PayWayEnum.PAY_ONLINE) {
			return true;
		}
		// Begin 12205 add by zengj
		// 查询订单项信息
		List<TradeOrderItem> tradeOrderItemList = this.tradeOrderItemMapper.selectOrderItemListById(tradeOrder.getId());

		// 构建转可用支付对象json
		String payByUsableJson = buildBalanceConfirmPayByUsable(tradeOrder, tradeOrderItemList);
		// 如果转可用支付对象json不为空
		if (StringUtils.isNotBlank(payByUsableJson)) {
			// 发送MQ消息
			Message msg = new Message(PayMessageConstant.TOPIC_BALANCE_PAY_TRADE, PayMessageConstant.TAG_PAY_TRADE_MALL,
					payByUsableJson.getBytes(Charsets.UTF_8));
			SendResult sendResult = rocketMQProducer.send(msg);
			if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
				throw new Exception("确认订单付款发送消息失败");
			}
		}

		// 构建转冻结支付对象json
		String payByFreezeJson = buildBalanceConfirmPayByFreeze(tradeOrder, tradeOrderItemList);
		// 如果转冻结支付对象json不为空
		if (StringUtils.isNotBlank(payByFreezeJson)) {
			// 发送MQ消息
			Message msg = new Message(PayMessageConstant.TOPIC_BALANCE_PAY_TRADE, PayMessageConstant.TAG_PAY_TRADE_MALL,
					payByFreezeJson.getBytes(Charsets.UTF_8));

			// 发送消息
			SendResult sendResult = rocketMQProducer.send(msg);
			if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
				throw new Exception("确认订单付款发送消息失败");
			}
		}
		// End 12205 add by zengj
		return true;
	}

	/**
	 * 
	 * @Description: 构建确认收货支付对象-不可售后金额和配送费（直接转可用）
	 * @param order 订单对象
	 * @param tradeOrderItemList 订单项集合对象
	 * @return 支付对象
	 * @throws ServiceException   异常处理
	 * @author zengj
	 * @date 2016年8月6日
	 */
	private String buildBalanceConfirmPayByUsable(TradeOrder order, List<TradeOrderItem> tradeOrderItemList)
			throws ServiceException {
		// 订单金额,初始化等于配送费金额
		BigDecimal tradeAmount = order.getFare() == null ? BigDecimal.ZERO : order.getFare();
		// 优惠金额
		BigDecimal preferentialAmount = BigDecimal.ZERO;
		// 是否平台优惠
		boolean isPlatformPreferential = false;
		// 优惠额退款 判断是否有优惠劵
		if (order.getActivityType() == ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES
				|| order.getActivityType() == ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES
				|| order.getActivityType() == ActivityTypeEnum.VONCHER) {
			ActivityBelongType activityBelong = tradeOrderActivityService.findActivityType(order);
			if (ActivityBelongType.OPERATOR == activityBelong || ActivityBelongType.AGENT == activityBelong) {
				isPlatformPreferential = true;
			}
		}
		// 循环订单项列表
		if (!CollectionUtils.isEmpty(tradeOrderItemList)) {

			// 需要更新为已完成的订单项ID集合
			List<String> ordreItemIds = new ArrayList<String>();
			for (TradeOrderItem orderItem : tradeOrderItemList) {
				// 不可申请售后的商品金额和配送费直接转可用
				if (orderItem.getServiceAssurance() == null || orderItem.getServiceAssurance() == 0) {
					// 订单金额直接加上店铺收益
					tradeAmount = tradeAmount
							.add(orderItem.getActualAmount() == null ? BigDecimal.ZERO : orderItem.getActualAmount());
					if (isPlatformPreferential) {
						preferentialAmount = preferentialAmount.add(orderItem.getPreferentialPrice() == null
								? BigDecimal.ZERO : orderItem.getPreferentialPrice());
					}

					ordreItemIds.add(orderItem.getId());
				}
			}

			// 如果有需要更新为完成的订单项ID，更新订单项为已完成
			if (!CollectionUtils.isEmpty(ordreItemIds)) {
				this.tradeOrderItemMapper.updateCompleteById(ordreItemIds);
			}
		}
		// 如果订单金额为0，说明该订单全部商品都是可售后的且没有配送费。会转冻结
		if (BigDecimal.ZERO.compareTo(tradeAmount) == 0) {
			return null;
		}
		BalancePayTradeVo payTradeVo = new BalancePayTradeVo();
		payTradeVo.setAmount(tradeAmount);
		payTradeVo.setIncomeUserId(storeInfoService.getBossIdByStoreId(order.getStoreId()));
		payTradeVo.setTradeNum(order.getTradeNum());
		payTradeVo.setTitle("确认收货(余额支付)，交易号：" + order.getTradeNum());
		payTradeVo.setBusinessType(BusinessTypeEnum.CONFIRM_ORDER_NOSERVICE);
		payTradeVo.setServiceFkId(order.getId());
		payTradeVo.setServiceNo(order.getOrderNo());
		// 优惠金额
		if (preferentialAmount != null && preferentialAmount.compareTo(BigDecimal.ZERO) > 0) {
			// 优化活动发起人，比如代理商id或者运营商id
			payTradeVo.setActivitier(tradeOrderActivityService.findActivityUserId(order));
			payTradeVo.setPrefeAmount(preferentialAmount);
		}
		payTradeVo.setRemark("无");
		// 接受返回消息的tag
		payTradeVo.setTag(PayMessageConstant.TAG_PAY_RESULT_CONFIRM);
		return JSONObject.toJSONString(payTradeVo);
	}

	/**
	 * 
	 * @Description: 构建确认收货支付对象-可退货的（转冻结）
	 * @param order 订单对象
	 * @param tradeOrderItemList 订单项集合对象
	 * @return 支付对象
	 * @throws ServiceException 处理异常   
	 * @author zengj
	 * @date 2016年8月6日
	 */
	private String buildBalanceConfirmPayByFreeze(TradeOrder order, List<TradeOrderItem> tradeOrderItemList)
			throws ServiceException {
		// 订单金额
		BigDecimal tradeAmount = BigDecimal.ZERO;
		// 优惠金额
		BigDecimal preferentialAmount = BigDecimal.ZERO;
		// 优惠额退款 判断是否有优惠劵
		// if (order.getPreferentialPrice().compareTo(BigDecimal.ZERO) > 0) {
		// ActivityBelongType activityBelong =
		// tradeOrderActivityService.findActivityType(order);
		// if (ActivityBelongType.AGENT == activityBelong ||
		// ActivityBelongType.OPERATOR == activityBelong) {
		// preferentialAmount = order.getPreferentialPrice();
		// }
		// }

		// 是否平台优惠
		boolean isPlatformPreferential = false;
		// 优惠额退款 判断是否有优惠劵
		if (order.getActivityType() == ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES
				|| order.getActivityType() == ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES
				|| order.getActivityType() == ActivityTypeEnum.VONCHER) {
			ActivityBelongType activityBelong = tradeOrderActivityService.findActivityType(order);
			if (ActivityBelongType.OPERATOR == activityBelong || ActivityBelongType.AGENT == activityBelong) {
				isPlatformPreferential = true;
			}
		}
		// Begin 12205 add by zengj
		if (!CollectionUtils.isEmpty(tradeOrderItemList)) {
			for (TradeOrderItem orderItem : tradeOrderItemList) {
				// 可申请售后的商品金额转冻结
				if (orderItem.getServiceAssurance() != null && orderItem.getServiceAssurance() > 0) {
					tradeAmount = tradeAmount
							.add(orderItem.getActualAmount() == null ? BigDecimal.ZERO : orderItem.getActualAmount());
					if (isPlatformPreferential) {
						preferentialAmount = preferentialAmount.add(orderItem.getPreferentialPrice() == null
								? BigDecimal.ZERO : orderItem.getPreferentialPrice());
					}
				}
			}
		}
		// 如果订单金额为0，只能说明该订单所有商品都不可退货，所以直接转可用了
		if (BigDecimal.ZERO.compareTo(tradeAmount) == 0) {
			return null;
		}
		// End 12205 add by zengj
		BalancePayTradeVo payTradeVo = new BalancePayTradeVo();
		payTradeVo.setAmount(tradeAmount);
		payTradeVo.setIncomeUserId(storeInfoService.getBossIdByStoreId(order.getStoreId()));
		payTradeVo.setTradeNum(order.getTradeNum());
		payTradeVo.setTitle("确认收货(余额支付)，交易号：" + order.getTradeNum());
		// Begin 12205 modify zengj
		// if (isServiceAssurance(order)) {
		payTradeVo.setBusinessType(BusinessTypeEnum.CONFIRM_ORDER);
		// } else {
		// payTradeVo.setBusinessType(BusinessTypeEnum.CONFIRM_ORDER_NOSERVICE);
		// }
		// End 12205 modify by zengj
		payTradeVo.setServiceFkId(order.getId());
		payTradeVo.setServiceNo(order.getOrderNo());
		// 优惠金额
		if (preferentialAmount != null && preferentialAmount.compareTo(BigDecimal.ZERO) > 0) {
			// 优化活动发起人，比如代理商id或者运营商id
			payTradeVo.setActivitier(tradeOrderActivityService.findActivityUserId(order));
			payTradeVo.setPrefeAmount(preferentialAmount);
		}
		payTradeVo.setRemark("无");
		// 接受返回消息的tag
		payTradeVo.setTag(PayMessageConstant.TAG_PAY_RESULT_CONFIRM);
		return JSONObject.toJSONString(payTradeVo);
	}

	@Override
	public boolean updateBalanceByFinish(TradeOrder tradeOrder) throws Exception {
		if (tradeOrder.getPayWay() != PayWayEnum.PAY_ONLINE) {
			return true;
		}
		String msgStr = buildBalanceFinish(tradeOrder);
		// 为空说明没有金额需要解除冻结
		if (StringUtils.isBlank(msgStr)) {
			return true;
		}
		Message msg = new Message(PayMessageConstant.TOPIC_BALANCE_PAY_TRADE, PayMessageConstant.TAG_PAY_TRADE_MALL,
				msgStr.getBytes(Charsets.UTF_8));
		// 发送消息
		SendResult sendResult = rocketMQProducer.send(msg);
		return sendResult.getSendStatus() == SendStatus.SEND_OK;
	}

	/**
	 * 构建确认收货支付对象
	 */
	private String buildBalanceFinish(TradeOrder order) throws Exception {
		// 是否店铺优惠
		boolean isStorePreferential = false;
		// 是否平台优惠
		boolean isPlatformPreferential = false;
		// Begin add by zengj
		List<TradeOrderItem> orderItemList = this.tradeOrderItemMapper.selectOrderItemListById(order.getId());
		// End add by zengj
		BigDecimal totalAmount = BigDecimal.ZERO;
		// 优惠金额
		BigDecimal preferentialAmount = BigDecimal.ZERO;

		// 优惠额退款 判断是否有优惠劵
		if (order.getActivityType() == ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES
				|| order.getActivityType() == ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES
				|| order.getActivityType() == ActivityTypeEnum.VONCHER) {
			ActivityBelongType activityBelong = tradeOrderActivityService.findActivityType(order);
			if (ActivityBelongType.SELLER == activityBelong) {
				isStorePreferential = true;
				// totalAmount = order.getActualAmount();
			} else if (ActivityBelongType.OPERATOR == activityBelong || ActivityBelongType.AGENT == activityBelong) {
				isPlatformPreferential = true;
			}
		}

		// 需要更新为已完成的订单项ID集合
		List<String> orderItemIds = new ArrayList<String>();
		Map<String, String> tmpOrderItemMap = new HashMap<String, String>();
		// 循环订单项信息
		if (!CollectionUtils.isEmpty(orderItemList)) {
			for (TradeOrderItem orderItem : orderItemList) {
				// 判断订单项是否已完成，未完成则需要调用云钱包
				if (orderItem.getIsComplete() == null || orderItem.getIsComplete() == OrderComplete.NO) {
					totalAmount = totalAmount.add(orderItem.getActualAmount());
					// 平台优惠也需要转云钱包
					if (isPlatformPreferential) {
						preferentialAmount = preferentialAmount.add(orderItem.getPreferentialPrice() == null
								? BigDecimal.ZERO : orderItem.getPreferentialPrice());
					}
					tmpOrderItemMap.put(orderItem.getId(), orderItem.getId());
				}
			}
		}

		// 需要减掉退款的金额，退款金额会在退款操作的时候转占用
		List<TradeOrderRefundsItem> refundsItem = tradeOrderRefundsItemMapper.selectByOrderId(order.getId());
		for (TradeOrderRefundsItem item : refundsItem) {
			// 判定是否是店铺活动，店铺活动：订单实付金额减退款金额，否则订单总金额减退款金额和优惠金额
			if (isStorePreferential) {
				totalAmount = totalAmount.subtract(item.getAmount());
			} else {
				totalAmount = totalAmount.subtract(item.getAmount().add(item.getPreferentialPrice()));
			}
			// 如果map中存在了该订单项ID，但是该订单项存在退款，该订单项不需要标记为已完成
			if (tmpOrderItemMap.containsKey(item.getOrderItemId())) {
				tmpOrderItemMap.remove(item.getOrderItemId());
			}
		}

		for (String key : tmpOrderItemMap.keySet()) {
			orderItemIds.add(key);
		}
		// 如果有需要更新为完成的订单项ID，更新订单项为已完成
		if (!CollectionUtils.isEmpty(orderItemIds)) {
			this.tradeOrderItemMapper.updateCompleteById(orderItemIds);
		}
		// 如果没有金额直接返回空
		if (BigDecimal.ZERO.compareTo(totalAmount) == 0) {
			return null;
		}
		BalancePayTradeVo payTradeVo = new BalancePayTradeVo();
		payTradeVo.setAmount(totalAmount);
		payTradeVo.setIncomeUserId(storeInfoService.getBossIdByStoreId(order.getStoreId()));
		payTradeVo.setTradeNum(order.getTradeNum());
		payTradeVo.setTitle("解冻余额，交易号：" + order.getTradeNum());
		payTradeVo.setBusinessType(BusinessTypeEnum.COMPLETE_ORDER);
		payTradeVo.setServiceFkId(order.getId());
		payTradeVo.setServiceNo(order.getOrderNo());
		// 优惠金额
		if (preferentialAmount != null && preferentialAmount.compareTo(BigDecimal.ZERO) > 0) {
			// 优化活动发起人，比如代理商id或者运营商id
			payTradeVo.setActivitier(tradeOrderActivityService.findActivityUserId(order));
			payTradeVo.setPrefeAmount(preferentialAmount);
		}
		payTradeVo.setRemark("无");
		// 接受返回消息的tag
		payTradeVo.setTag(null);
		return JSONObject.toJSONString(payTradeVo);
	}

	/**
	 * 判断是否有售后 
	 */
	@Override
	public boolean isServiceAssurance(TradeOrder order) {
		boolean noService = false;
		List<TradeOrderItem> items = order.getTradeOrderItem();
		if (items == null || Iterables.isEmpty(items)) {
			items = tradeOrderItemMapper.selectTradeOrderItem(order.getId());
		}
		for (TradeOrderItem item : items) {
			if (item.getServiceAssurance() != null && item.getServiceAssurance() > 0) {
				noService = true;
			}
		}
		return noService;
	}

	@Override
	public boolean payOrder(TradeOrder tradeOrder) throws Exception {
		boolean flag = updateOrderAndPay(tradeOrder);
		return flag;
	}

	@Override
	public boolean wlletPay(String orderMoney, TradeOrder order) throws Exception {

		TradeOrderPay orderPay = new TradeOrderPay();

		orderPay.setId(UuidUtils.getUuid());
		orderPay.setCreateTime(new Date());
		orderPay.setOrderId(order.getId());
		orderPay.setPayAmount(order.getActualAmount());
		orderPay.setPayTime(new Date());

		orderPay.setPayType(com.okdeer.mall.order.enums.PayTypeEnum.WALLET);
		order.setStatus(OrderStatusEnum.BUYER_PAYING);
		order.setTradeOrderPay(orderPay);

		BalancePayTradeVo payTrade = buildBalancePay(order);

		String json = JsonMapper.nonEmptyMapper().toJson(payTrade);

		System.out.println(json);

		Message msg = new Message(PayMessageConstant.TOPIC_BALANCE_PAY_TRADE, PayMessageConstant.TAG_PAY_TRADE_MALL,
				json.getBytes(Charsets.UTF_8));

		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, order, new LocalTransactionExecuter() {

			@Override
			public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
				insertTradeOrderPayLog((TradeOrder) object);
				return LocalTransactionState.COMMIT_MESSAGE;
			}
		}, new TransactionCheckListener() {

			public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
				return LocalTransactionState.COMMIT_MESSAGE;
			}
		});
		return RocketMqResult.returnResult(sendResult);
	}

	/**
	 * 构建云钱包(余额)支付对象
	 */
	private BalancePayTradeVo buildBalancePay(TradeOrder order) throws ServiceException {

		BalancePayTradeVo payTradeVo = new BalancePayTradeVo();

		// 优惠额支付 判断是否有优惠劵
		if (!order.getActivityId().equals("") && order.getActivityType().ordinal() != 4
				&& order.getActivityType().ordinal() != 0) {
			if (ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES == order.getActivityType()
					|| ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES == order.getActivityType()) {
				// 满折:店铺；满减活动：店铺、运营商
				ActivityDiscount discount = activityDiscountService.selectByPrimaryKey(order.getActivityId());
				if (!ActivityCollectCoupons.OPERATOR_CODE.equals(discount.getStoreId())) {
					payTradeVo.setActivitier(discount.getStoreId());
				} else {
					// 运营商
					payTradeVo.setActivitier(yscWalletAccount);
				}

			} else if (ActivityTypeEnum.VONCHER == order.getActivityType()) {
				// 代金卷：运营商、代理商
				ActivityCollectCoupons coupons = activityCollectCouponsService.get(order.getActivityId());
				// 判断是否代理商发的
				if (!ActivityCollectCoupons.OPERATOR_CODE.equals(coupons.getBelongType())) {
					// TODO 代理商ID coupons.getBelongType()
					payTradeVo.setActivitier(coupons.getBelongType());
				} else {
					// 运营商
					payTradeVo.setActivitier(yscWalletAccount);
				}
			}
		}

		payTradeVo.setAmount(order.getActualAmount()); // 交易金额
		payTradeVo.setPayUserId(order.getUserId());// 用户id
		payTradeVo.setTradeNum(order.getTradeNum());// 交易号
		payTradeVo.setTitle("订单支付");// 标题
		if (order.getType() == OrderTypeEnum.PHONE_PAY_ORDER || order.getType() == OrderTypeEnum.TRAFFIC_PAY_ORDER) {
			payTradeVo.setBusinessType(BusinessTypeEnum.RECHARGE_ORDER_PAY);
			payTradeVo.setTag(PayMessageConstant.TAG_PAY_RECHARGE_ORDER_BLANCE);// 接受返回消息的tag
		} else if (order.getType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
			payTradeVo.setBusinessType(BusinessTypeEnum.STORE_CONSUME_ORDER);
			payTradeVo.setTag(PayMessageConstant.TAG_PAY_RESULT_INSERT);// 接受返回消息的tag
			payTradeVo.setIncomeUserId(storeInfoService.getBossIdByStoreId(order.getStoreId()));
		} else {
			payTradeVo.setBusinessType(BusinessTypeEnum.ORDER_PAY);// 业务类型
			payTradeVo.setTag("tag_pay_result_mall_insert");// 接受返回消息的tag
		}

		payTradeVo.setServiceFkId(order.getId());// 服务单id
		payTradeVo.setServiceNo(order.getOrderNo());// 服务单号，例如订单号、退单号
		payTradeVo.setRemark("订单");// 备注信息
		payTradeVo.setPrefeAmount(order.getPreferentialPrice());// 优惠金额

		return payTradeVo;

	}

	/**
	 * 余额支付更新数据并支付
	 * </p>
	 * 
	 * @author yangq
	 * 
	 * @param tradeOrder
	 * @return
	 * @throws Exception
	 */
	public boolean updateOrderAndPay(TradeOrder tradeOrder) throws Exception {

		TradeOrder order = new TradeOrder();

		order.setStatus(OrderStatusEnum.ALREADY_CONSUME);

		List<PayTradeDto> payTrades = buildPay(order);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("list", payTrades);
		jsonObject.put("tag_pay_trade_result", PayMessageConstant.TAG_PAY_RESULT_REFUND);

		String json = JsonMapper.nonEmptyMapper().toJson(jsonObject);

		Message msg = new Message(PayMessageConstant.TOPIC_BALANCE_PAY_TRADE, PayMessageConstant.TAG_PAY_TRADE_MALL,
				json.getBytes(Charsets.UTF_8));

		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, tradeOrder,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
						insertTradeOrderPayLog((TradeOrder) object);
						return LocalTransactionState.COMMIT_MESSAGE;
					}
				}, new TransactionCheckListener() {

					public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
						return LocalTransactionState.COMMIT_MESSAGE;
					}
				});
		return RocketMqResult.returnResult(sendResult);

	}

	@Override
	public int selectTradeOrderPayByOrderId(String orderId) throws Exception {
		return tradeOrderPayMapper.selectTradeOrderPayByOrderId(orderId);
	}

}