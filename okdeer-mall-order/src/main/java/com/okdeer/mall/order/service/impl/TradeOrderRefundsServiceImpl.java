
package com.okdeer.mall.order.service.impl;

import static com.okdeer.common.consts.DescriptConstants.UPDATE_REFUNDS_STATUS_FAILE;
import static com.okdeer.common.consts.LogConstants.CUSTOMER_SERVICE_INTERVENE_FAIL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.client.producer.LocalTransactionExecuter;
import com.alibaba.rocketmq.client.producer.LocalTransactionState;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.client.producer.TransactionCheckListener;
import com.alibaba.rocketmq.client.producer.TransactionSendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.archive.goods.base.enums.GoodsTypeEnum;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.goods.store.enums.MeteringMethod;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerJxcServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.entity.StoreMemberRelation;
import com.okdeer.archive.store.enums.StoreUserTypeEnum;
import com.okdeer.archive.store.service.IStoreMemberRelationServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.entity.SysMsg;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRecordService;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.group.service.ActivityGroupRecordService;
import com.okdeer.mall.common.enums.IsRead;
import com.okdeer.mall.common.enums.MsgType;
import com.okdeer.mall.common.utils.RobotUserUtil;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.service.MemberConsigneeAddressService;
import com.okdeer.mall.order.constant.PayMessageConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsCertificate;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.entity.TradeOrderRefundsLog;
import com.okdeer.mall.order.entity.TradeOrderRefundsLogistics;
import com.okdeer.mall.order.enums.ActivityBelongType;
import com.okdeer.mall.order.enums.OrderItemStatusEnum;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.enums.SendMsgType;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsCertificateMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsLogMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsLogisticsMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsMapper;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.service.TradeMessageService;
import com.okdeer.mall.order.service.TradeOrderActivityService;
import com.okdeer.mall.order.service.TradeOrderCompleteProcessService;
import com.okdeer.mall.order.service.TradeOrderDisputeLogService;
import com.okdeer.mall.order.service.TradeOrderDisputeService;
import com.okdeer.mall.order.service.TradeOrderItemDetailService;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeOrderRefundsCertificateService;
import com.okdeer.mall.order.service.TradeOrderRefundsItemService;
import com.okdeer.mall.order.service.TradeOrderRefundsLogisticsService;
import com.okdeer.mall.order.service.TradeOrderRefundsService;
import com.okdeer.mall.order.service.TradeOrderRefundsServiceApi;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.order.vo.SendMsgParamVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsCertificateVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsChargeVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsExportVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsQueryVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsStatusVo;
import com.okdeer.mall.order.vo.TradeOrderRefundsVo;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;
import com.okdeer.mall.system.mapper.SysMsgMapper;
import com.okdeer.mall.system.mq.RollbackMQProducer;
import com.okdeer.mall.system.mq.StockMQProducer;
import com.yschome.api.pay.enums.BusinessTypeEnum;
import com.yschome.api.pay.enums.TradeErrorEnum;
import com.yschome.api.pay.service.IPayTradeServiceApi;
import com.yschome.api.pay.tradeLog.dto.BalancePayTradeVo;
import com.yschome.base.common.enums.Disabled;
import com.yschome.base.common.utils.DateUtils;
import com.yschome.base.common.utils.PageUtils;
import com.yschome.base.common.utils.StringUtils;
import com.yschome.base.common.utils.UuidUtils;
import com.yschome.base.framework.mq.RocketMQProducer;
import com.yschome.base.framework.mq.RocketMQTransactionProducer;
import com.yschome.base.framework.mq.RocketMqResult;
import com.yschome.common.BaseResultDto;

import net.sf.json.JSONObject;

/**
 * @DESC:
 * @author yangq
 * @date 2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 *=================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构4.1            2016-7-13            wusw              添加退款中、第三方支付的充值退款记录数方法、查询充值订单列表方法（用于财务系统）
 *    重构4.1            2016-7-26            maojj             库存修改地方添加分布式事务机制
 *    Bug:12917         2016-8-18            maojj             客服介入时增加对退款单状态的检查
 *    1.0.Z				2016-09-05			 zengj			        增加退款单操作记录
 *    1.0.Z	          2016年9月07日                 zengj              库存管理修改，采用商业管理系统校验
 *  
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderRefundsServiceApi")
public class TradeOrderRefundsServiceImpl
		implements TradeOrderRefundsService, TradeOrderRefundsServiceApi, PayMessageConstant {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderRefundsServiceImpl.class);

	/**
	 * 云存储店铺图片路径二级域名
	 */
	@Value("${storeImagePrefix}")
	private String storeImagePrefix;

	/**
	 * 云存储订单图片路径二级域名
	 */
	@Value("${orderImagePrefix}")
	private String orderImagePrefix;

	// @Value("${sysMsgContent}")
	private String sysMsgContent = "您有一条来自用户【#1】的退款申请需要处理，订单号【#2】";

	/**
	 * 消息发送
	 */
	@Autowired
	private TradeMessageService tradeMessageService;

	/**
	 * 满减满折DAO
	 */
	@Resource
	private ActivityDiscountService activityDiscountService;

	@Resource
	private GenerateNumericalService generateNumericalService;

	@Resource
	private TradeOrderItemService tradeOrderItemService;

	@Resource
	private TradeOrderPayService tradeOrderPayService;

	@Resource
	private TradeOrderMapper tradeOrderMapper;

	@Autowired
	private SysBuyerUserMapper sysBuyerUserMapper;

	@Resource
	private TradeOrderRefundsItemMapper tradeOrderRefundsItemMapper;

	/**
	 * 代金券DAO
	 */
	@Resource
	private ActivityCollectCouponsService activityCollectCouponsService;

	@Resource
	private TradeOrderRefundsCertificateMapper tradeOrderRefundsCertificateMapper;

	@Resource
	private TradeOrderRefundsMapper tradeOrderRefundsMapper;

	@Resource
	private TradeOrderRefundsLogMapper tradeOrderRefundsLogMapper;

	@Resource
	private TradeOrderRefundsLogisticsMapper tradeOrderRefundsLogisticsMapper;

	@Resource
	private TradeOrderDisputeLogService tradeOrderDisputeLogService;

	@Resource
	private TradeOrderDisputeService tradeOrderDisputeService;

	@Resource
	private MemberConsigneeAddressService memberConsigneeAddressService;

	@Resource
	private TradeOrderRefundsLogisticsService tradeOrderRefundsLogisticsService;

	@Resource
	private TradeOrderRefundsCertificateService tradeOrderRefundsCertificateService;

	@Resource
	private TradeOrderItemDetailService tradeOrderItemDetailService;

	@Reference(version = "1.0.0", check = false)
	private IPayTradeServiceApi payTradeServiceApi;

	@Autowired
	private RocketMQTransactionProducer rocketMQTransactionProducer;

	@Autowired
	private RocketMQProducer rocketMqProducer;

	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	@Autowired
	private TradeOrderActivityService tradeOrderActivityService;

	@Autowired
	private TradeOrderRefundsItemService tradeOrderRefundsItemService;

	// @Reference(version = "1.0.0", check = false)
	// private StockManagerServiceApi stockManagerService;

	// Begin 1.0.Z add by zengj
	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerJxcServiceApi stockManagerService;

	/**
	 * 订单完成后同步商业管理系统Service
	 */
	@Resource
	private TradeOrderCompleteProcessService tradeOrderCompleteProcessService;
	// End 1.0.Z add by zengj

	@Autowired
	private SysMsgMapper sysMsgMapper;

	/**
	 * 特惠活动记录信息mapper
	 */
	@Autowired
	private ActivitySaleRecordService activitySaleRecordService;

	/**
	 * 特惠活动Mapper
	 */
	@Autowired
	private ActivityGroupRecordService activityGroupRecordService;

	@Autowired
	private TradeOrderTimer tradeOrderTimer;

	/**
	 * 店铺用户关系
	 */
	@Reference(version = "1.0.0", check = false)
	private IStoreMemberRelationServiceApi storeMemberRelationService;

	// Begin added by maojj 2016-07-26
	/**
	 * 回滚消息生产者
	 */
	@Resource
	private RollbackMQProducer rollbackMQProducer;

	/**
	 * ERP库存消息生成者
	 */
	@Resource
	private StockMQProducer stockMQProducer;
	// End added by maojj 2016-07-26

	/**
	 * 根据主键查询退款单
	 *
	 * @param id
	 *            主键ID
	 */
	public TradeOrderRefunds getById(String id) {
		return findById(id);
	}

	/**
	 * 根据主键查询退款单
	 *
	 * @param id
	 *            主键ID
	 */
	public TradeOrderRefunds findById(String id) {
		return tradeOrderRefundsMapper.selectByPrimaryKey(id);
	}

	/**
	 * @desc 根据订单项ID判断是否已申请退款
	 */
	@Override
	public boolean isRefundOrderItemId(String orderId) {
		int count = tradeOrderRefundsItemMapper.selectCountOrderItemId(orderId);
		return count > 0;
	}

	/**
	 * 查询退款单详情
	 */
	@Override
	public TradeOrderRefunds findInfoById(String id) throws Exception {
		return tradeOrderRefundsMapper.findInfoById(id);
	}

	/**
	 * 根据主键查询退款单
	 */
	public List<TradeOrderRefunds> findByIds(List<String> ids) {
		return tradeOrderRefundsMapper.selectByPrimaryKeys(ids);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertRefunds(TradeOrderRefunds orderRefunds) throws Exception {
		tradeOrderRefundsMapper.insertSelective(orderRefunds);
	}

	/**
	 * @desc 新增退款单
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertRefunds(TradeOrderRefunds orderRefunds, TradeOrderRefundsCertificateVo certificate)
			throws Exception {
		// 保存退款单
		insertRefunds(orderRefunds);
		// 批量保存退款单项
		this.tradeOrderRefundsItemService.insert(orderRefunds.getTradeOrderRefundsItem());
		// 保存退款凭证
		tradeOrderRefundsCertificateService.addCertificate(certificate);

		// 服务订单：调用退款
		if (orderRefunds.getType() == OrderTypeEnum.SERVICE_ORDER) {
			orderRefunds.setTradeNum(TradeNumUtil.getTradeNum());
			doRefundPay(orderRefunds);
		} else if (orderRefunds.getType() == OrderTypeEnum.PHYSICAL_ORDER) {
			// 自动同意申请计时消息
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_refund_agree_timeout, orderRefunds.getId());
		}

		// 实物订单、团购订单、服务订单发送保存系统消息 update by zhaoqc
		if (orderRefunds.getType() == OrderTypeEnum.SERVICE_ORDER
				|| orderRefunds.getType() == OrderTypeEnum.PHYSICAL_ORDER
				|| orderRefunds.getType() == OrderTypeEnum.SERVICE_STORE_ORDER) {
			// 保存系统消息
			saveSysMsg(orderRefunds);
			// 推送消息给POS和商家中心
			SendMsgParamVo sendMsgParamVo = new SendMsgParamVo(orderRefunds);
			// 推送消息给POS
			tradeMessageService.sendPosMessage(sendMsgParamVo, SendMsgType.applyReturn);
			// 推送消息给商家版APP
			tradeMessageService.sendSellerAppMessage(sendMsgParamVo, SendMsgType.applyReturn);
		}

		// Begin 1.0.Z 增加退款单操作记录 add by zengj
		tradeOrderRefundsLogMapper
				.insertSelective(new TradeOrderRefundsLog(orderRefunds.getId(), orderRefunds.getOperator(),
						orderRefunds.getRefundsStatus().getName(), orderRefunds.getRefundsStatus().getValue()));
		// End 1.0.Z 增加退款单操作记录 add by zengj
	}

	@Override
	public void insertRechargeRefunds(TradeOrder tradeOrder) throws Exception {
		tradeOrder.setStatus(OrderStatusEnum.TRADE_CLOSED);
		this.tradeOrderMapper.updateByPrimaryKeySelective(tradeOrder);

		TradeOrderRefunds refunds = new TradeOrderRefunds();
		List<TradeOrderItem> tradeOrderItems = this.tradeOrderItemService.selectOrderItemByOrderId(tradeOrder.getId());
		TradeOrderItem tradeOrderItem = null;
		if (!tradeOrderItems.isEmpty()) {
			tradeOrderItem = tradeOrderItems.get(0);
		}

		String refundsId = UuidUtils.getUuid();
		refunds.setId(refundsId);
		refunds.setUserId(tradeOrder.getUserId());
		refunds.setRefundsStatus(RefundsStatusEnum.SELLER_REFUNDING);

		TradeOrderPay tradeOrderPay = this.tradeOrderPayService.selectByOrderId(tradeOrder.getId());
		refunds.setPaymentMethod(tradeOrderPay.getPayType());
		refunds.setType(tradeOrder.getType());
		refunds.setOrderResource(OrderResourceEnum.YSCAPP);
		refunds.setStoreId("");
		refunds.setOrderId(tradeOrder.getId());

		refunds.setRefundNo(this.generateNumericalService.generateNumber("XT"));
		refunds.setTradeNum(TradeNumUtil.getTradeNum());
		refunds.setOrderNo(tradeOrder.getOrderNo());
		refunds.setStatus(OrderItemStatusEnum.ALL_REFUND);
		refunds.setTotalAmount(tradeOrder.getActualAmount());
		refunds.setTotalPreferentialPrice(new BigDecimal("0.00"));
		refunds.setCreateTime(new Date());
		refunds.setUpdateTime(new Date());
		refunds.setRefundsReason("聚合平台充值请求失败");
		refunds.setDisabled(Disabled.valid);

		// 创建退款单项
		List<TradeOrderRefundsItem> refundsItems = new ArrayList<TradeOrderRefundsItem>();
		TradeOrderRefundsItem refundsItem = new TradeOrderRefundsItem();
		refundsItem.setId(UuidUtils.getUuid());
		refundsItem.setRefundsId(refundsId);
		refundsItem.setOrderItemId(tradeOrderItem.getId());
		refundsItem.setSkuName(tradeOrderItem.getSkuName());
		refundsItem.setMainPicUrl("");
		refundsItem.setSpuType(GoodsTypeEnum.SERVICE_GOODS);
		refundsItem.setAmount(tradeOrder.getActualAmount());
		refundsItem.setPreferentialPrice(new BigDecimal("0"));
		refundsItem.setQuantity(1);
		refundsItem.setStatus(OrderItemStatusEnum.ALL_REFUND);
		refundsItem.setRechargeMobile(tradeOrderItem.getRechargeMobile());
		refundsItems.add(refundsItem);
		refunds.setTradeOrderRefundsItem(refundsItems);

		// 创建退款凭证
		TradeOrderRefundsCertificateVo certificate = new TradeOrderRefundsCertificateVo();
		certificate.setId(UuidUtils.getUuid());
		certificate.setRefundsId(refundsId);
		certificate.setCreateTime(new Date());
		certificate.setRemark("同意退款");

		// 创建退款单，退款单项和凭证
		this.insertRefunds(refunds, certificate);

		// 充值订单,余额支付退款
		// if (isOldWayBack(orderRefunds.getPaymentMethod())) {
		// 原路返回
		// String remark = "同意退款";
		// } else {
		// 余额支付退款
		if (refunds.getPaymentMethod().equals(PayTypeEnum.WALLET)) {
			this.updateRechargeWallet(refunds);
		}
	}

	public void updateRechargeWallet(TradeOrderRefunds orderRefunds) throws Exception {
		// 添加交易记录
		Message msg = new Message(TOPIC_BALANCE_PAY_TRADE, TAG_PAY_TRADE_MALL,
				buildRechargeBalancePayTrade(orderRefunds).getBytes("UTF-8"));

		rocketMqProducer.send(msg);
		// 发送事务消息
		/*
		 * TransactionSendResult sendResult =
		 * rocketMQTransactionProducer.send(msg, orderRefunds, new
		 * LocalTransactionExecuter() {
		 * 
		 * @Override public LocalTransactionState
		 * executeLocalTransactionBranch(Message msg, Object arg) { //执行同意退款操作
		 * 
		 * 
		 * return null; } }, new TransactionCheckListener() {
		 * 
		 * @Override public LocalTransactionState
		 * checkLocalTransactionState(MessageExt msg) { return
		 * LocalTransactionState.COMMIT_MESSAGE; } } ); return
		 * RocketMqResult.returnResult(sendResult);
		 */
	}

	// 构建支付对象
	private String buildRechargeBalancePayTrade(TradeOrderRefunds orderRefunds) throws Exception {
		BalancePayTradeVo payTradeVo = new BalancePayTradeVo();
		payTradeVo.setAmount(orderRefunds.getTotalAmount());
		payTradeVo.setIncomeUserId(orderRefunds.getUserId());
		// payTradeVo.setPayUserId("");
		payTradeVo.setTradeNum(orderRefunds.getTradeNum());
		payTradeVo.setTitle("充值订单退款(余额支付)，退款交易号：" + orderRefunds.getRefundNo());

		// TradeOrder order =
		// tradeOrderMapper.selectByPrimaryKey(orderRefunds.getOrderId());
		// 业务类型
		payTradeVo.setBusinessType(BusinessTypeEnum.RECHARGE_ORDER_REFUND);
		payTradeVo.setServiceFkId(orderRefunds.getId());
		payTradeVo.setServiceNo(orderRefunds.getOrderNo());
		payTradeVo.setRemark("关联订单号：" + orderRefunds.getOrderNo());
		// 接受返回消息的tag
		payTradeVo.setTag(PayMessageConstant.TAG_PAY_RESULT_REFUND);

		return JSONObject.fromObject(payTradeVo).toString();
	}

	/**
	 * 保存系统消息
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveSysMsg(TradeOrderRefunds orderRefunds) {
		SysMsg sysMsg = new SysMsg();
		sysMsg.setId(UuidUtils.getUuid());
		sysMsg.setTitle("退款通知");
		// 消息内容
		sysMsg.setContext(sysMsgContent.replace("#1", getUserPhone(orderRefunds.getUserId())).replace("#2",
				orderRefunds.getOrderNo()));
		sysMsg.setCreateTime(new Date());
		sysMsg.setDisabled(Disabled.valid);
		// 消息发送人Id
		sysMsg.setFromUserId(RobotUserUtil.getRobotUser().getId());
		// 是否已读：0未读，1已读
		sysMsg.setIsRead(IsRead.UNREAD);
		// 消息超链接
		sysMsg.setLink("");
		sysMsg.setStoreId(orderRefunds.getStoreId());
		sysMsg.setTargetId(orderRefunds.getId());
		// 消息类型：0提现通知，1下单通知，2退款申请，4退款准备超时未处理 5补货通知 6用户投诉通知7运营商下发公告
		sysMsg.setType(MsgType.REFUND_APPLY_MSG);
		sysMsgMapper.insertSelective(sysMsg);
	}

	/**
	 * 获取用户手机号
	 */
	public String getUserPhone(String buyerUserId) {
		return sysBuyerUserMapper.selectMemberMobile(buyerUserId);
	}

	/**
	 * 更新退款单并添加凭证
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRefunds(TradeOrderRefunds orderRefunds, TradeOrderRefundsCertificateVo certificate) {
		// 保存退款凭证
		if (certificate != null) {
			tradeOrderRefundsCertificateService.addCertificate(certificate);
		}
		// 更新退款单
		updateRefunds(orderRefunds);
	}

	/**
	 * @desc 更新退款单（根据状态判断是否需要支付）
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateRefunds(TradeOrderRefunds orderRefunds) {
		return tradeOrderRefundsMapper.updateByPrimaryKeySelective(orderRefunds);
	}

	/**
	 * 更新退款单并添加凭证
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void alterRefunds(TradeOrderRefunds orderRefunds, TradeOrderRefundsCertificateVo certificate) {
		TradeOrderRefundsCertificate oldCertificate = tradeOrderRefundsCertificateMapper
				.findFirstByRefundsId(orderRefunds.getId());
		tradeOrderRefundsCertificateMapper.deleteByPrimaryKey(oldCertificate.getId());
		// 保存退款凭证
		tradeOrderRefundsCertificateService.addCertificate(certificate);
		// 更新退款单
		updateRefunds(orderRefunds);
	}

	// Begin modified by maojj 2016-07-26 添加dubbo分布式事务处理机制
	/**
	 * @desc 保存退款单相关数据
	 */
	@Transactional(rollbackFor = Exception.class)
	private void save(TradeOrderRefunds orderRefunds) throws Exception {
		List<String> rpcIdList = new ArrayList<String>();
		try {
			// 判断当前状态是否需要回退库存
			// if (RefundsStatusEnum.SELLER_REFUNDING != status &&
			// RefundsStatusEnum.SELLER_REFUNDING_FINANCING != status) {
			// new Exception("订单状态错误，不能执行更新订单状态和回收库存");
			// }
			// 更新订单状态
			updateRefunds(orderRefunds);

			// 回收库存
			List<StockAdjustVo> stockAdjustList = recycleBuildStock(orderRefunds, rpcIdList);

			// 服务订单修改消费码状态
			if (orderRefunds.getType() == OrderTypeEnum.SERVICE_ORDER) {
				for (TradeOrderRefundsItem refundsItem : orderRefunds.getTradeOrderRefundsItem()) {
					// 设置消费码为已退款
					tradeOrderItemDetailService.updateStatusWithRefund(refundsItem.getOrderItemId());
				}
			}
			TradeOrder order = tradeOrderMapper.selectByPrimaryKey(orderRefunds.getOrderId());
			if (order.getActivityType() == ActivityTypeEnum.GROUP_ACTIVITY) {
				// 团购活动释放限购数量
				activityGroupRecordService.updateDisabledByOrderId(orderRefunds.getOrderId());
			}
			// 特惠活动释放限购数量
			for (TradeOrderRefundsItem refundsItem : orderRefunds.getTradeOrderRefundsItem()) {
				Map<String, Object> params = Maps.newHashMap();
				params.put("orderId", orderRefunds.getOrderId());
				params.put("storeSkuId", refundsItem.getStoreSkuId());
				activitySaleRecordService.updateDisabledByOrderId(params);
			}

			// 新增退货操作记录
			String remark = "同意退款";
			addRefundsCerticate(orderRefunds.getId(), remark, orderRefunds.getOperator());

			// 发消息给ERP生成库存单据 added by maojj
			// stockMQProducer.sendMessage(stockAdjustList);
		} catch (Exception e) {
			// 发消息回滚库存的修改 added by maojj
			rollbackMQProducer.sendStockRollbackMsg(rpcIdList);
			throw e;
		}
	}

	/**
	 * 修改退单库存
	 * 
	 * @param tradeOrder
	 *            订单
	 * @return StockAdjustVo
	 */
	private List<StockAdjustVo> recycleBuildStock(TradeOrderRefunds orderRefunds, List<String> rpcIdList)
			throws Exception {
		// Begin added by maojj 2016-07-26
		List<StockAdjustVo> stockAdjustList = new ArrayList<StockAdjustVo>();
		// End added by maojj 2016-07-26
		List<TradeOrderRefundsItem> tradeOrderRefundsItems = orderRefunds.getTradeOrderRefundsItem();
		if (Iterables.isEmpty(tradeOrderRefundsItems)) {
			tradeOrderRefundsItems = tradeOrderRefundsItemService
					.getTradeOrderRefundsItemByRefundsId(orderRefunds.getId());
		}
		for (TradeOrderRefundsItem item : tradeOrderRefundsItems) {
			StockAdjustVo stockAdjustVo = new StockAdjustVo();
			// Begin added by maojj 2016-07-26
			String rpcId = UuidUtils.getUuid();
			rpcIdList.add(rpcId);
			stockAdjustVo.setRpcId(rpcId);
			// End added by maojj 2016-07-26
			stockAdjustVo.setOrderId(orderRefunds.getOrderId());
			stockAdjustVo.setOrderNo(orderRefunds.getOrderNo());
			stockAdjustVo.setOrderResource(orderRefunds.getOrderResource());
			stockAdjustVo.setOrderType(orderRefunds.getType());
			stockAdjustVo.setStoreId(orderRefunds.getStoreId());
			// 取消销售单
			TradeOrder order = tradeOrderMapper.selectByPrimaryKey(orderRefunds.getOrderId());
			if (order.getActivityType() == ActivityTypeEnum.GROUP_ACTIVITY
					|| isAttendSale(orderRefunds.getOrderId(), item.getStoreSkuId())) {
				stockAdjustVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_RETURN_OF_GOODS);
			} else {
				stockAdjustVo.setStockOperateEnum(StockOperateEnum.RETURN_OF_GOODS);
			}
			stockAdjustVo.setUserId(orderRefunds.getUserId());

			AdjustDetailVo detail = new AdjustDetailVo();
			detail.setStoreSkuId(item.getStoreSkuId());
			detail.setGoodsSkuId("");
			detail.setMultipleSkuId("");
			detail.setGoodsName(item.getSkuName());
			detail.setPrice(item.getUnitPrice());
			detail.setPropertiesIndb(item.getPropertiesIndb());
			detail.setStyleCode(item.getStyleCode());
			detail.setBarCode(item.getBarCode());
			detail.setSpuType(
					order.getType() == OrderTypeEnum.SERVICE_ORDER ? SpuTypeEnum.serviceSpu : SpuTypeEnum.physicalSpu);
			Integer quantity;
			if (order.getType() == OrderTypeEnum.SERVICE_ORDER) {
				quantity = tradeOrderItemDetailService.selectUnConsumerCount(item.getOrderItemId());
			} else {
				quantity = item.getQuantity();
			}
			if (quantity == null) {
				if (item.getWeight() != null) {
					quantity = item.getWeight().multiply(new BigDecimal(1000)).intValue();
					detail.setIsWeightSku("Y");
				}
			}
			detail.setNum(quantity);
			List<AdjustDetailVo> adjustDetailList = Lists.newArrayList(detail);
			stockAdjustVo.setAdjustDetailList(adjustDetailList);
			stockManagerService.updateStock(stockAdjustVo);

			// Begin added by maojj 2016-07-26
			stockAdjustList.add(stockAdjustVo);
			// End added by maojj 2016-07-26
		}
		return stockAdjustList;
	}
	// End modified by maojj 2016-07-26

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
	 * @desc 执行退款更新云钱包余额
	 */
	@Override
	public boolean updateWallet(TradeOrderRefunds orderRefunds) throws Exception {

		orderRefunds.setRefundsStatus(RefundsStatusEnum.SELLER_REFUNDING);
		// 构建余额支付（或添加交易记录）对象
		Message msg = new Message(TOPIC_BALANCE_PAY_TRADE, TAG_PAY_TRADE_MALL,
				buildBalancePayTrade(orderRefunds).getBytes(Charsets.UTF_8));
		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, orderRefunds,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
						try {
							// 执行同意退款操作
							save((TradeOrderRefunds) object);
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
		return RocketMqResult.returnResult(sendResult);

	}

	/**
	 * 构建支付对象
	 */
	private String buildBalanceThirdPayTrade(TradeOrderRefunds orderRefunds) throws Exception {

		BigDecimal amount = BigDecimal.ZERO;
		if (OrderTypeEnum.SERVICE_ORDER == orderRefunds.getType()) {
			List<TradeOrderRefundsItem> items = orderRefunds.getTradeOrderRefundsItem();
			for (TradeOrderRefundsItem item : items) {
				int unConsumerCount = tradeOrderItemDetailService.selectUnConsumerCount(item.getOrderItemId());
				amount = amount.add(item.getUnitPrice().multiply(BigDecimal.valueOf(unConsumerCount)));
			}
		} else {
			amount = orderRefunds.getTotalAmount();
		}

		BalancePayTradeVo payTradeVo = new BalancePayTradeVo();
		payTradeVo.setAmount(amount);
		payTradeVo.setIncomeUserId(orderRefunds.getUserId());
		payTradeVo.setPayUserId(storeInfoService.getBossIdByStoreId(orderRefunds.getStoreId()));
		payTradeVo.setTradeNum(orderRefunds.getTradeNum());
		payTradeVo.setTitle("订单退款，退款交易号：" + orderRefunds.getRefundNo());

		TradeOrder order = tradeOrderMapper.selectByPrimaryKey(orderRefunds.getOrderId());
		payTradeVo.setBusinessType(BusinessTypeEnum.AGREEN_REFUND);
		payTradeVo.setServiceFkId(orderRefunds.getId());
		payTradeVo.setServiceNo(orderRefunds.getOrderNo());
		payTradeVo.setRemark("关联订单号：" + orderRefunds.getOrderNo());
		// 优惠额退款 判断是否有优惠劵
		ActivityBelongType activityResource = tradeOrderActivityService.findActivityType(order);
		if (activityResource == ActivityBelongType.OPERATOR || activityResource == ActivityBelongType.AGENT
				&& (orderRefunds.getTotalPreferentialPrice().compareTo(BigDecimal.ZERO) > 0)) {
			payTradeVo.setPrefeAmount(orderRefunds.getTotalPreferentialPrice());
			payTradeVo.setActivitier(tradeOrderActivityService.findActivityUserId(order));
		}
		// 接受返回消息的tag
		payTradeVo.setTag(null);
		return JSONObject.fromObject(payTradeVo).toString();
	}

	/**
	 * 构建支付对象
	 */
	private String buildBalancePayTrade(TradeOrderRefunds orderRefunds) throws Exception {

		BalancePayTradeVo payTradeVo = new BalancePayTradeVo();
		payTradeVo.setAmount(orderRefunds.getTotalAmount());
		payTradeVo.setIncomeUserId(orderRefunds.getUserId());
		payTradeVo.setPayUserId(storeInfoService.getBossIdByStoreId(orderRefunds.getStoreId()));
		payTradeVo.setTradeNum(orderRefunds.getTradeNum());
		payTradeVo.setTitle("订单退款(余额支付)，退款交易号：" + orderRefunds.getRefundNo());

		TradeOrder order = tradeOrderMapper.selectByPrimaryKey(orderRefunds.getOrderId());
		if (order.getType() == OrderTypeEnum.PHYSICAL_ORDER) {
			payTradeVo.setBusinessType(BusinessTypeEnum.REFUND_ORDER);
		} else {
			payTradeVo.setBusinessType(BusinessTypeEnum.REFUND_SERVICE_ORDER);
		}
		payTradeVo.setServiceFkId(orderRefunds.getId());
		payTradeVo.setServiceNo(orderRefunds.getOrderNo());
		payTradeVo.setRemark("关联订单号：" + orderRefunds.getOrderNo());
		// 优惠额退款 判断是否有优惠劵
		ActivityBelongType activityResource = tradeOrderActivityService.findActivityType(order);
		if (activityResource == ActivityBelongType.OPERATOR || activityResource == ActivityBelongType.AGENT
				&& (orderRefunds.getTotalPreferentialPrice().compareTo(BigDecimal.ZERO) > 0)) {
			payTradeVo.setPrefeAmount(orderRefunds.getTotalPreferentialPrice());
			payTradeVo.setActivitier(tradeOrderActivityService.findActivityUserId(order));
		}
		// 接受返回消息的tag
		payTradeVo.setTag(PayMessageConstant.TAG_PAY_RESULT_REFUND);
		return JSONObject.fromObject(payTradeVo).toString();
	}

	/**
	 * 卖家操作同意退单
	 *
	 * @param id
	 *            退货单ID
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatusWithAgree(String id, String addressId, String remark, String userId) throws Exception {
		try {

			TradeOrderRefunds vo = tradeOrderRefundsMapper.selectByPrimaryKey(id);
			if (vo != null && vo.getRefundsStatus() == RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS) {
				throw new Exception("卖家同意退货异常");
			}

			TradeOrderRefunds tradeOrderRefunds = new TradeOrderRefunds();
			tradeOrderRefunds.setId(id);
			tradeOrderRefunds.setRefundsStatus(RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS);
			tradeOrderRefunds.setUpdateTime(new Date());
			tradeOrderRefunds.setOperator(userId);
			updateRefunds(tradeOrderRefunds);

			// 新增退货地址
			addRefundsLogistics(id, addressId);
			// 新增退货操作记录
			addRefundsCerticate(id, remark, userId);
			// Begin 1.0.Z 增加退款单操作记录 add by zengj
			tradeOrderRefundsLogMapper.insertSelective(new TradeOrderRefundsLog(tradeOrderRefunds.getId(),
					tradeOrderRefunds.getOperator(), tradeOrderRefunds.getRefundsStatus().getName(),
					tradeOrderRefunds.getRefundsStatus().getValue()));
			// End 1.0.Z 增加退款单操作记录 add by zengj
			// 自动撤销申请计时消息
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_refund_cancel_by_agree_timeout, id);
		} catch (Exception e) {
			logger.error("卖家操作同意退货错误", e);
			throw new Exception("卖家操作同意退货错误", e);
		}
	}

	/**
	 * 卖家操作拒绝退单
	 * 
	 * @param id
	 *            退款单ID
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateStatusWithRefuse(String id, String remark, String userId) throws Exception {

		TradeOrderRefunds vo = tradeOrderRefundsMapper.selectByPrimaryKey(id);
		if (vo != null && vo.getRefundsStatus() == RefundsStatusEnum.SELLER_REJECT_REFUND) {
			throw new Exception("卖家拒绝退货异常");
		}

		TradeOrderRefunds tradeOrderRefunds = new TradeOrderRefunds();
		tradeOrderRefunds.setId(id);
		tradeOrderRefunds.setRefundsStatus(RefundsStatusEnum.SELLER_REJECT_APPLY);
		tradeOrderRefunds.setUpdateTime(new Date());
		tradeOrderRefunds.setRefuseReson(remark);
		updateRefunds(tradeOrderRefunds);

		// 添加操作记录
		addRefundsCerticate(id, remark, userId);

		// Begin 1.0.Z 增加退款单操作记录 add by zengj
		tradeOrderRefundsLogMapper.insertSelective(new TradeOrderRefundsLog(tradeOrderRefunds.getId(), userId,
				tradeOrderRefunds.getRefundsStatus().getName(), tradeOrderRefunds.getRefundsStatus().getValue()));
		// End 1.0.Z 增加退款单操作记录 add by zengj
		// 售后申请计时消息
		tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_refund_cancel_by_refuse_apply_timeout, id);
	}

	/**
	 * 卖家同意退款
	 * 
	 * @param id
	 *            退款单ID
	 * @param userId
	 *            卖家ID
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateAgreePayment(String id, String userId) throws Exception {

		TradeOrderRefunds orderRefunds = this.findInfoById(id);
		if (orderRefunds.getRefundsStatus() != RefundsStatusEnum.WAIT_SELLER_REFUND) {
			throw new Exception("卖家同意退款异常");
		}
		orderRefunds.setUpdateTime(new Date());
		orderRefunds.setOperator(userId);
		orderRefunds.setTradeNum(TradeNumUtil.getTradeNum());
		// 执行退款操作
		doRefundPay(orderRefunds);
	}

	/**
	 * 执行退款操作
	 */
	private void doRefundPay(TradeOrderRefunds orderRefunds) throws Exception {
		TradeOrder order = tradeOrderMapper.selectByPrimaryKey(orderRefunds.getOrderId());
		if (Iterables.isEmpty(orderRefunds.getTradeOrderRefundsItem())) {
			orderRefunds.setTradeOrderRefundsItem(
					tradeOrderRefundsItemService.getTradeOrderRefundsItemByRefundsId(orderRefunds.getId()));
		}
		// 判断非在线支付
		if (PayWayEnum.PAY_ONLINE != order.getPayWay()) {
			// 非线上支付
			orderRefunds.setRefundMoneyTime(new Date());
			orderRefunds.setRefundsStatus(RefundsStatusEnum.REFUND_SUCCESS);
			this.save(orderRefunds);
		} else if (isOldWayBack(orderRefunds.getPaymentMethod())) {
			// 原路返回
			orderRefunds.setRefundsStatus(RefundsStatusEnum.SELLER_REFUNDING);
			// 判断实物订单：先更新卖家余额，后由财务处理退款操作
			if (order.getType() == OrderTypeEnum.PHYSICAL_ORDER) {
				this.updateWalletByThird(orderRefunds);
			} else {
				this.save(orderRefunds);
			}
		} else {
			// 余额退款
			this.updateWallet(orderRefunds);
		}

		// Begin 1.0.Z 增加退款单操作记录 add by zengj
		tradeOrderRefundsLogMapper
				.insertSelective(new TradeOrderRefundsLog(orderRefunds.getId(), orderRefunds.getOperator(),
						orderRefunds.getRefundsStatus().getName(), orderRefunds.getRefundsStatus().getValue()));

		// 订单完成后同步到商业管理系统
		tradeOrderCompleteProcessService.orderRefundsCompleteSyncToJxc(orderRefunds.getId());
		// End 1.0.Z 增加退款单操作记录 add by zengj
		// 发送短信
		this.tradeMessageService.sendSmsByAgreePay(orderRefunds, order.getPayWay());
	}

	@Transactional(rollbackFor = Exception.class)
	private boolean updateWalletByThird(TradeOrderRefunds orderRefunds) throws Exception {

		// 构建余额支付（或添加交易记录）对象
		Message msg = new Message(TOPIC_BALANCE_PAY_TRADE, TAG_PAY_TRADE_MALL,
				buildBalanceThirdPayTrade(orderRefunds).getBytes(Charsets.UTF_8));
		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, orderRefunds,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
						try {
							// 执行同意退款操作
							save((TradeOrderRefunds) object);
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
		return RocketMqResult.returnResult(sendResult);
	}

	/**
	 * 客服处理更新订单状态
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateByCustomer(String refundsId, RefundsStatusEnum status, String userId) throws Exception {

		if (RefundsStatusEnum.YSC_REFUND != status && RefundsStatusEnum.FORCE_SELLER_REFUND != status
				&& RefundsStatusEnum.CUSTOMER_SERVICE_CANCEL_INTERVENE != status) {
			new Exception("更新退款单异常，退款单状态错误");
		}

		TradeOrderRefunds refunds = this.findById(refundsId);

		// Begin added by maojj 2016-08-18
		if (refunds.getRefundsStatus() != RefundsStatusEnum.APPLY_CUSTOMER_SERVICE_INTERVENE) {
			logger.error(CUSTOMER_SERVICE_INTERVENE_FAIL, refunds.getRefundsStatus());
			throw new Exception(UPDATE_REFUNDS_STATUS_FAILE);
		}
		// End added by maojj 2016-08-180

		refunds.setRefundsStatus(status);
		refunds.setOperator(userId);

		// Begin 1.0.Z 增加退款单操作记录 add by zengj
		tradeOrderRefundsLogMapper.insertSelective(new TradeOrderRefundsLog(refunds.getId(), refunds.getOperator(),
				refunds.getRefundsStatus().getName(), refunds.getRefundsStatus().getValue()));
		// End 1.0.Z 增加退款单操作记录 add by zengj
		if (RefundsStatusEnum.YSC_REFUND == status) {
			// 更新退款单
			this.save(refunds);
			// 发送短信
			this.tradeMessageService.sendSmsByYschomePay(refunds);
		} else if (RefundsStatusEnum.FORCE_SELLER_REFUND == status) {
			// 执行退款操作
			refunds.setTradeNum(TradeNumUtil.getTradeNum());
			doRefundPay(refunds);
		} else if (RefundsStatusEnum.CUSTOMER_SERVICE_CANCEL_INTERVENE == status) {
			// 解冻订单项金额
			updateWithRevocatory(refunds, null);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateWithRevocatory(TradeOrderRefunds refunds, TradeOrderRefundsCertificateVo certificate)
			throws Exception {
		TradeOrder tradeOrder = tradeOrderMapper.selectByPrimaryKey(refunds.getOrderId());
		if (tradeOrder.getPayWay() != PayWayEnum.PAY_ONLINE) {
			updateRefunds(refunds, certificate);
			// Begin 12858 add by zengj 如果不是线上支付订单，不执行资金解冻
			return;
			// Begin 12858 add by zengj 如果不是线上支付订单，不执行资金解冻
		}

		// Begin 1.0.Z 增加退款单操作记录 add by zengj
		tradeOrderRefundsLogMapper.insertSelective(new TradeOrderRefundsLog(refunds.getId(), refunds.getOperator(),
				refunds.getRefundsStatus().getName(), refunds.getRefundsStatus().getValue()));
		// End 1.0.Z 增加退款单操作记录 add by zengj

		// 执行资金解冻
		refunds.setTradeNum(TradeNumUtil.getTradeNum());
		Message msg = new Message(PayMessageConstant.TOPIC_BALANCE_PAY_TRADE, PayMessageConstant.TAG_PAY_TRADE_MALL,
				buildBalanceFinish(refunds, tradeOrder).getBytes(Charsets.UTF_8));
		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, refunds,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
						try {
							updateRefunds((TradeOrderRefunds) object);
						} catch (Exception e) {
							logger.error("执行撤销售后单异常", e);
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
		if (sendResult.getSendStatus() == SendStatus.SEND_OK && certificate != null) {
			// 保存退款凭证
			tradeOrderRefundsCertificateService.addCertificate(certificate);
		}
	}

	private String buildBalanceFinish(TradeOrderRefunds refunds, TradeOrder order) throws Exception {
		// 是否店铺优惠
		BigDecimal totalAmount = refunds.getTotalAmount();
		// 优惠金额
		BigDecimal preferentialPrice = BigDecimal.ZERO;
		// 优惠额退款 判断是否有优惠劵
		if (order.getActivityType() == ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES
				|| order.getActivityType() == ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES
				|| order.getActivityType() == ActivityTypeEnum.VONCHER) {
			ActivityBelongType activityBelong = tradeOrderActivityService.findActivityType(order);
			if (ActivityBelongType.SELLER == activityBelong) {
				// totalAmount = refunds.getTotalAmount();
			} else {
				// totalAmount =
				// refunds.getTotalAmount().add(refunds.getTotalPreferentialPrice());
				preferentialPrice = refunds.getTotalPreferentialPrice();
			}
		}

		BalancePayTradeVo payTradeVo = new BalancePayTradeVo();
		payTradeVo.setAmount(totalAmount);
		payTradeVo.setIncomeUserId(storeInfoService.getBossIdByStoreId(order.getStoreId()));
		payTradeVo.setTradeNum(refunds.getTradeNum());
		payTradeVo.setTitle("解冻余额，交易号：" + refunds.getTradeNum());
		payTradeVo.setBusinessType(BusinessTypeEnum.COMPLETE_ORDER);
		payTradeVo.setServiceFkId(order.getId());
		payTradeVo.setServiceNo(order.getOrderNo());
		// Begin 12205 add by zengj
		// 优惠金额
		if (preferentialPrice != null && preferentialPrice.compareTo(BigDecimal.ZERO) > 0) {
			// 优化活动发起人，比如代理商id或者运营商id
			payTradeVo.setActivitier(tradeOrderActivityService.findActivityUserId(order));
			payTradeVo.setPrefeAmount(preferentialPrice);
		}
		// End 12205 add by zengj
		payTradeVo.setRemark("无");
		// 接受返回消息的tag
		payTradeVo.setTag(null);
		return JSONObject.fromObject(payTradeVo).toString();
	}

	/**
	 * 是否资金原来返回
	 */
	private boolean isOldWayBack(PayTypeEnum payType) {
		if (PayTypeEnum.ALIPAY == payType || PayTypeEnum.WXPAY == payType) {
			return true;
		}
		return false;
	}

	/**
	 * 卖家拒绝退款
	 * 
	 * @param id
	 *            退款单ID
	 * @param userId
	 *            卖家ID
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRefusePayment(String id, String userId, String reason) throws Exception {
		TradeOrderRefunds refund = getById(id);
		if (refund.getRefundsStatus() != RefundsStatusEnum.WAIT_SELLER_REFUND) {
			throw new Exception("卖家拒绝退款异常");
		}

		TradeOrderRefunds tradeOrderRefunds = new TradeOrderRefunds();
		tradeOrderRefunds.setId(id);
		tradeOrderRefunds.setRefundsStatus(RefundsStatusEnum.SELLER_REJECT_REFUND);
		tradeOrderRefunds.setUpdateTime(new Date());
		tradeOrderRefunds.setOperator(userId);
		tradeOrderRefunds.setRefuseReson(reason);
		this.updateRefunds(tradeOrderRefunds);

		// 新增退货操作记录
		String remark = "您拒绝了退款,拒绝原因：";
		addRefundsCerticate(id, remark + reason, userId);

		// Begin 1.0.Z 增加退款单操作记录 add by zengj
		tradeOrderRefundsLogMapper.insertSelective(new TradeOrderRefundsLog(tradeOrderRefunds.getId(),
				tradeOrderRefunds.getOperator(), tradeOrderRefunds.getRefundsStatus().getName(),
				tradeOrderRefunds.getRefundsStatus().getValue()));
		// End 1.0.Z 增加退款单操作记录 add by zengj
		// 发送超时消息
		tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_refund_cancel_by_refuse_timeout, id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int delete(TradeOrderRefunds tradeOrderRefunds) throws Exception {
		return tradeOrderRefundsMapper.delete(tradeOrderRefunds);
	}

	/**
	 * 新增退货操作记录
	 * 
	 * @param id
	 *            退款单ID
	 * @param remark
	 *            操作凭证说明
	 */
	private void addRefundsCerticate(String id, String remark, String userId) throws Exception {
		TradeOrderRefundsCertificate certificate = new TradeOrderRefundsCertificate();
		certificate.setId(UuidUtils.getUuid());
		certificate.setOperator(userId);
		certificate.setRemark(remark);
		certificate.setCreateTime(new Date());
		certificate.setRefundsId(id);
		// 添加操作记录
		tradeOrderRefundsCertificateMapper.insert(certificate);
	}

	/**
	 * 新增退货地址
	 * 
	 * @param id
	 *            退款单ID
	 * @param addressId
	 *            退货地址ID
	 */
	private void addRefundsLogistics(String id, String addressId) throws Exception {

		MemberConsigneeAddress address = findMemberAddress(addressId);
		TradeOrderRefundsLogistics logistics = new TradeOrderRefundsLogistics();
		logistics.setId(UuidUtils.getUuid());
		logistics.setRefundsId(id);
		logistics.setArea(address.getArea());
		logistics.setAddress(address.getAddress());
		logistics.setAreaId(address.getAreaId());
		logistics.setCityId(address.getCityId());
		logistics.setConsigneeName(address.getConsigneeName());
		logistics.setMobile(address.getMobile());
		logistics.setProvinceId(address.getProvinceId());
		logistics.setTelephone(address.getTelephone());
		logistics.setZipCode(address.getZipCode());
		tradeOrderRefundsLogisticsService.add(logistics);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveLogistics(TradeOrderRefunds refunds, TradeOrderRefundsLogistics logistics) throws Exception {
		// 更新售后单
		this.updateRefunds(refunds);
		// 新增退货操作记录
		String remark = "买家选择：" + refunds.getLogisticsType().getValue();

		// 物流发货：保存物流信息
		if (logistics != null) {
			tradeOrderRefundsLogisticsService.modifyById(logistics);
			remark += "， 物流公司：" + logistics.getLogisticsCompanyName() + "，物流单号：" + logistics.getLogisticsNo();
		}
		addRefundsCerticate(refunds.getId(), remark, refunds.getUserId());

		TradeOrder order = this.tradeOrderMapper.selectByPrimaryKey(refunds.getOrderId());
		// 卖家退款计时消息
		if (order.getActivityType() == ActivityTypeEnum.GROUP_ACTIVITY) {
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_refund_confirm_group_timeout, refunds.getId());
		} else {
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_refund_confirm_timeout, refunds.getId());
		}
	}

	/**
	 * 
	 * @desc 退款单搜索
	 * @author zengj
	 * @param map
	 *            查询条件
	 * @param pageNumber
	 *            当前页
	 * @param pageSize
	 *            每页展示记录数
	 */
	public PageUtils<TradeOrderRefundsVo> searchOrderRefundByParams(Map<String, Object> map, int pageNumber,
			int pageSize) {
		if (pageNumber > 0) {
			PageHelper.startPage(pageNumber == 0 ? 1 : pageNumber, pageSize, pageNumber != 0, false);
		}
		return new PageUtils<TradeOrderRefundsVo>(tradeOrderRefundsMapper.searchOrderRefundByParams(map));
	}

	/**
	 * 查询退款单导出列表
	 * 
	 * @author zengj
	 * @param map
	 *            查询条件
	 * @param maxSize
	 *            导出最大值，如为空，不限制导出数量
	 */
	public List<TradeOrderRefundsExportVo> selectExportList(Map<String, Object> map, Integer maxSize) {
		PageUtils<TradeOrderRefundsVo> pages = searchOrderRefundByParams(map, 0,
				maxSize == null ? Integer.MAX_VALUE : maxSize);
		List<TradeOrderRefundsExportVo> list = new ArrayList<TradeOrderRefundsExportVo>();
		if (pages != null && pages.getList() != null && !pages.getList().isEmpty()) {
			// 退款单状态Map
			Map<String, String> orderRefundsStatusMap = RefundsStatusEnum.convertViewStatus();
			// 循环退款单
			for (TradeOrderRefundsVo refundsVo : pages.getList()) {
				if (refundsVo.getTradeOrderRefundsItem() != null) {
					// 循环退款单项
					for (TradeOrderRefundsItem item : refundsVo.getTradeOrderRefundsItem()) {
						TradeOrderRefundsExportVo exportVo = new TradeOrderRefundsExportVo();
						// 退款单编号
						exportVo.setRefundNo(refundsVo.getRefundNo());
						// 实付款
						exportVo.setAmount(item.getAmount());
						// 下单时间
						exportVo.setCreateTime(DateUtils.formatDate(refundsVo.getTradeOrderVo().getCreateTime(),
								"yyyy-MM-dd HH:mm:ss"));
						// 订单编号
						exportVo.setOrderNo(refundsVo.getTradeOrderVo().getOrderNo());
						// 退的商品数量
						exportVo.setQuantity(item.getQuantity());
						// 退款金额，应该和实付款一样
						exportVo.setRefundAmount(item.getAmount());
						// 退款单状态
						exportVo.setRefundsStatus(orderRefundsStatusMap.get(refundsVo.getRefundsStatus().getName()));
						// 商品数量
						exportVo.setSkuName(item.getSkuName());
						// 单价
						exportVo.setUnitPrice(item.getUnitPrice());
						// 买家
						exportVo.setUserPhone(refundsVo.getUserPhone());
						if (!OrderStatusEnum.UNPAID.equals(refundsVo.getTradeOrderVo().getStatus())
								&& !OrderStatusEnum.BUYER_PAYING.equals(refundsVo.getTradeOrderVo().getStatus())) {
							// 支付方式
							if (refundsVo.getTradeOrderVo().getTradeOrderPay() == null) {
								exportVo.setPayType(refundsVo.getTradeOrderVo().getPayWay().getValue());
							} else {
								exportVo.setPayType(
										refundsVo.getTradeOrderVo().getTradeOrderPay().getPayType().getValue());
							}
						}

						list.add(exportVo);
					}
				}
			}
		}
		return list;
	}

	/**
	 * @desc 根据退款状态，查询线上退款单信息（pos--线上退货列表）
	 */
	@Override
	public List<TradeOrderRefunds> findOnlineByRefundsStatus(String refundsStatus) throws Exception {

		List<TradeOrderRefunds> result = null;
		if (StringUtils.isNotEmpty(refundsStatus)) {
			List<RefundsStatusEnum> refundsStatusList = new ArrayList<RefundsStatusEnum>();
			// 售后申请
			if ("1".equals(refundsStatus)) {
				refundsStatusList.add(RefundsStatusEnum.WAIT_SELLER_VERIFY);
				refundsStatusList.add(RefundsStatusEnum.BUYER_REPEAL_REFUND);

			} else if ("2".equals(refundsStatus)) {
				// 已拒绝
				refundsStatusList.add(RefundsStatusEnum.SELLER_REJECT_APPLY);
				refundsStatusList.add(RefundsStatusEnum.SELLER_REJECT_REFUND);
				refundsStatusList.add(RefundsStatusEnum.APPLY_CUSTOMER_SERVICE_INTERVENE);
				refundsStatusList.add(RefundsStatusEnum.CUSTOMER_SERVICE_CANCEL_INTERVENE);
				refundsStatusList.add(RefundsStatusEnum.YSC_REFUND);
				// refundsStatusList.add(RefundsStatusEnum.ysc_refund_success);

			} else if ("3".equals(refundsStatus)) {
				// 已同意
				refundsStatusList.add(RefundsStatusEnum.WAIT_BUYER_RETURN_GOODS);
				refundsStatusList.add(RefundsStatusEnum.WAIT_SELLER_REFUND);

			} else if ("4".equals(refundsStatus)) {
				// 退款成功
				refundsStatusList.add(RefundsStatusEnum.REFUND_SUCCESS);
				refundsStatusList.add(RefundsStatusEnum.YSC_REFUND_SUCCESS);
			}

			result = tradeOrderRefundsMapper.selectOnlineByRefundsStatus(refundsStatusList, OrderResourceEnum.POS);
		}

		if (result == null) {
			result = new ArrayList<TradeOrderRefunds>();
		}

		return result;
	}

	/**
	 * @desc 根据退款单id，查询退款单详细信息（包括退款单、商品、订单、支付等信息）
	 * @author wusw
	 */
	@Override
	public TradeOrderRefundsQueryVo findDetailById(String id) throws Exception {

		TradeOrderRefundsQueryVo refundsVo = tradeOrderRefundsMapper.selectDetailById(id);
		// if
		// (StringUtils.isNotEmpty(refundsVo.getTradeOrderItem().getMainPicPrl()))
		// {
		// refundsVo.getTradeOrderItem()
		// .setMainPicPrl(storeImagePrefix +
		// refundsVo.getTradeOrderItem().getMainPicPrl());
		// }

		// 拼接退款凭证图片前缀
		List<String> certificateImg = tradeOrderRefundsCertificateMapper.findImageByRefundsId(refundsVo.getId());
		refundsVo.setTradeOrderRefundsCertificateImgs(certificateImg);
		return refundsVo;
	}

	/**
	 * @desc 根据退款单id，查询退款单详细信息
	 * @author zengj
	 */
	@Override
	public TradeOrderRefundsVo selectRefundOrderDetailById(String id) throws Exception {
		TradeOrderRefundsVo refundsVo = tradeOrderRefundsMapper.selectRefundOrderDetailById(id);
		if (refundsVo != null && refundsVo.getTradeOrderVo() != null) {
			String activityId = refundsVo.getTradeOrderVo().getActivityId();
			// 如果有活动ID，说明该订单参与了活动
			if (StringUtils.isNotBlank(activityId) && !"0".equals(activityId)) {
				// 代金券活动
				if (ActivityTypeEnum.VONCHER.equals(refundsVo.getTradeOrderVo().getActivityType())) {
					ActivityCollectCoupons activityCollectCoupons = activityCollectCouponsService.get(activityId);
					if (activityCollectCoupons != null) {
						refundsVo.getTradeOrderVo().setActivityName(activityCollectCoupons.getName());
					}
				} else if (ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES
						.equals(refundsVo.getTradeOrderVo().getActivityType())) {
					// 满减活动
					ActivityDiscount activityDiscount = activityDiscountService.selectByPrimaryKey(activityId);
					if (activityDiscount != null) {
						refundsVo.getTradeOrderVo().setActivityName(activityDiscount.getName());
					}
				}
			}
		}
		return refundsVo;
	}

	@Override
	public Integer getTradeOrderRefundsCount(Map<String, Object> map) {
		return tradeOrderRefundsMapper.getTradeOrderRefundsCount(map);
	}

	@Override
	public PageUtils<TradeOrderRefunds> getOrderRefundByParams(Map<String, Object> map, int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber == 0 ? 1 : pageNumber, pageSize, pageNumber != 0, false);

		List<TradeOrderRefunds> list = tradeOrderRefundsMapper.getOrderRefundByParams(map);
		return new PageUtils<TradeOrderRefunds>(list);
	}

	/**
	 * 根据订单id查询订单退退款数量
	 * 
	 * @param orderId
	 *            String
	 * @return Integer
	 */
	@Override
	public Integer getTradeOrderRefundsCountByOrderId(String orderId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", orderId);
		return tradeOrderRefundsMapper.getTradeOrderRefundsCountByOrderId(map);
	}

	/**
	 * 商家版APP查询退款单信息
	 *
	 * @param map
	 *            查询条件
	 * @param pageNumber
	 *            当前页
	 * @param pageSize
	 *            每页展示数量
	 */
	public PageUtils<TradeOrderRefundsVo> selectMallAppByParams(Map<String, Object> map, int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		// 先分页查询退款单信息
		List<TradeOrderRefundsVo> list = tradeOrderRefundsMapper.selectMallAppByParams(map);
		if (list != null && !list.isEmpty()) {
			for (TradeOrderRefundsVo orderRefundsVo : list) {
				// 查询退款单下的订单项
				orderRefundsVo.setTradeOrderRefundsItem(
						tradeOrderRefundsItemMapper.getTradeOrderRefundsItemByRefundsId(orderRefundsVo.getId()));
			}
		}
		return new PageUtils<TradeOrderRefundsVo>(list);
	}

	/**
	 * 查询退款单状态下对应的退款单数量
	 *
	 * @param storeId
	 *            店铺ID
	 */
	public List<TradeOrderRefundsStatusVo> getOrderRefundsCount(String storeId) {
		return tradeOrderRefundsMapper.getOrderRefundsCount(storeId);
	}

	/**
	 * 商家版APP查询退款单信息
	 *
	 * @param map
	 *            查询条件
	 * @param pageNumber
	 *            当前页
	 * @param pageSize
	 *            每页展示数量
	 */
	public PageUtils<TradeOrderRefundsVo> selectWXRefundsOrder(Map<String, Object> map, int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		return new PageUtils<TradeOrderRefundsVo>(tradeOrderRefundsMapper.selectWXRefundsOrder(map));
	}

	// Begin modified by maojj 2016-07-26 增加dubbo分布式事务管理机制
	/**
	 * @desc POS申请退款单
	 * @param items
	 *            订单项ID
	 * @param userId
	 *            操作用户ID
	 */
	@Transactional(rollbackFor = Exception.class)
	public String updateTradeRefundsWithOfflinePos(List<TradeOrderRefundsItem> items, String orderNo, String refundNo,
			String userId, Date currenTime) throws Exception {
		String result = "退款成功";
		List<String> rpcIdList = new ArrayList<String>();
		try {
			if (items != null && items.size() > 0) {

				TradeOrderItem orderItem = tradeOrderItemService.selectByPrimaryKey(items.get(0).getId());
				TradeOrder tradeOrder = tradeOrderMapper.selectOrderPayInvoiceById(orderItem.getOrderId());

				TradeOrderRefunds tradeOrderRefunds = new TradeOrderRefunds();
				tradeOrderRefunds.setId(UuidUtils.getUuid());
				tradeOrderRefunds.setUserId(userId);
				tradeOrderRefunds.setStoreId(tradeOrder.getStoreId());
				tradeOrderRefunds.setType(tradeOrder.getType());
				tradeOrderRefunds.setRefundNo(refundNo);
				tradeOrderRefunds.setCreateTime(currenTime);
				tradeOrderRefunds.setUpdateTime(currenTime);
				tradeOrderRefunds.setRefundsReason("");
				tradeOrderRefunds.setMemo("");
				tradeOrderRefunds.setOrderId(tradeOrder.getId());
				tradeOrderRefunds.setOrderNo(orderNo);
				tradeOrderRefunds.setOperator(userId);
				tradeOrderRefunds.setRefundsStatus(RefundsStatusEnum.REFUND_SUCCESS);
				tradeOrderRefunds.setOrderResource(OrderResourceEnum.POS);
				tradeOrderRefunds.setDisabled(Disabled.valid);
				tradeOrderRefunds.setRefundMoneyTime(currenTime);
				tradeOrderRefunds.setTotalPreferentialPrice(tradeOrder.getPreferentialPrice());
				BigDecimal totalIncome = BigDecimal.ZERO;

				BigDecimal totalAmount = new BigDecimal(0);
				List<TradeOrderRefundsItem> tradeOrderRefundsItems = new ArrayList<TradeOrderRefundsItem>();

				for (TradeOrderRefundsItem item : items) {
					logger.info("POS线下退货订单项 item：" + JSONObject.fromObject(item));
					TradeOrderItem tradeOrderItem = tradeOrderItemService.selectByPrimaryKey(item.getId());
					if (tradeOrderItem != null) {
						try {
							TradeOrderRefundsItem itemVo = new TradeOrderRefundsItem();
							itemVo.setId(UuidUtils.getUuid());
							itemVo.setOrderItemId(item.getId());
							itemVo.setRefundsId(tradeOrderRefunds.getId());
							itemVo.setSkuName(tradeOrderItem.getSkuName());
							itemVo.setPropertiesIndb(tradeOrderItem.getPropertiesIndb());
							itemVo.setMainPicUrl(tradeOrderItem.getMainPicPrl());
							itemVo.setBarCode(tradeOrderItem.getBarCode());
							itemVo.setStyleCode(tradeOrderItem.getStyleCode());
							itemVo.setUnitPrice(tradeOrderItem.getUnitPrice());
							// 退款金额
							BigDecimal amount = new BigDecimal(0);
							BigDecimal income = new BigDecimal(0);
							logger.info("POS线下退货Quantity：" + item.getQuantity() + "==UnitPrice："
									+ tradeOrderItem.getUnitPrice() + "==itemId" + item.getId());
							if (item.getQuantity() != null && item.getQuantity().intValue() > 0) {
								itemVo.setQuantity(item.getQuantity());
								BigDecimal quantity = new BigDecimal(item.getQuantity());
								amount = quantity.multiply(tradeOrderItem.getUnitPrice());
								income = amount;
							} else if (item.getWeight() != null) {
								// 称重商品全退
								amount = tradeOrderItem.getActualAmount();
								itemVo.setWeight(tradeOrderItem.getWeight());
								income = tradeOrderItem.getIncome();
							}
							itemVo.setSpuType(item.getSpuType());
							logger.info("POS线下退货amount" + amount + "==weight:" + item.getWeight());
							itemVo.setAmount(amount);
							itemVo.setIncome(income);
							itemVo.setWeight(tradeOrderItem.getWeight());
							itemVo.setStoreSkuId(tradeOrderItem.getStoreSkuId());
							totalIncome = totalIncome.add(income);
							totalAmount = totalAmount.add(amount);
							// 订单项状态(0:无退货退款,1:部分退货退款,2:全部退货退款)
							if (String.valueOf(MeteringMethod.WEIGH.ordinal()).equals(itemVo.getSpuType())) {
								itemVo.setStatus(OrderItemStatusEnum.ALL_REFUND);
							} else {
								if (tradeOrderItem.getQuantity() == item.getQuantity()) {
									itemVo.setStatus(OrderItemStatusEnum.ALL_REFUND);
								} else {
									itemVo.setStatus(OrderItemStatusEnum.A_PART_REFUND);
								}
							}
							tradeOrderRefundsItems.add(itemVo);
							tradeOrderRefundsItemMapper.insertTradeOrderRefundsItem(itemVo);

						} catch (Exception e) {
							logger.error("pos机线下退货退款异常", e);
							throw new Exception("退款失败");
						}
					}
				}

				tradeOrderRefunds.setTotalAmount(totalAmount);
				tradeOrderRefunds.setTotalIncome(totalIncome);

				if (tradeOrder.getTradeOrderPay() != null) {
					// 第三方支付退款
					if (PayTypeEnum.ALIPAY.equals(tradeOrder.getTradeOrderPay().getPayType())
							|| PayTypeEnum.WXPAY.equals(tradeOrder.getTradeOrderPay().getPayType())) {
						tradeOrderRefunds.setPaymentMethod(tradeOrder.getTradeOrderPay().getPayType());
						// 若退款超过退款期限则退货失败并提示“订单已超过14天退款期限，无法退款！”，以下单时间为准，加上退款期限14天判定订单是否超过期限
						if (DateUtils.pastDays(tradeOrder.getCreateTime()) > 14) {
							throw new Exception("订单已超过14天退款期限，无法退款！");
						}
						StoreMemberRelation queryParams = new StoreMemberRelation();
						queryParams.setStoreId(tradeOrder.getStoreId());
						queryParams.setMemberType(StoreUserTypeEnum.STORE_KEEPER);
						logger.info("StoreId:" + tradeOrder.getStoreId());
						StoreMemberRelation storeMemberRelation = null;
						try {
							storeMemberRelation = storeMemberRelationService.findByTypeStoreId(queryParams);
							if (storeMemberRelation == null) {
								throw new Exception("店铺没有老板信息！");
							}
						} catch (Exception e) {
							logger.info("查询店铺老板信息异常{}", e);
							throw new Exception("店铺信息异常，请与管理员联系！");
						}

						BalancePayTradeVo payTradeVo = new BalancePayTradeVo();
						payTradeVo.setAmount(totalIncome); // 交易金额
						payTradeVo.setCheckAmount(totalAmount);// 订单总金额
						payTradeVo.setPayUserId(storeMemberRelation.getSysUserId());// 用户id
						payTradeVo.setTradeNum(refundNo);// 交易号
						payTradeVo.setTitle("订单退款");// 标题
						payTradeVo.setBusinessType(BusinessTypeEnum.POS_AGREEN_REFUND);// 业务类型
						payTradeVo.setServiceFkId(tradeOrderRefunds.getId());// 服务参照ID
																				// 比如订单ID，缴费记录ID，充值记录ID，保证金记录ID
						payTradeVo.setServiceNo(orderNo);// 退单号
						payTradeVo.setRemark("订单[" + orderNo + "]");// 备注信息
						// payTradeVo.setIncomeUserId(storeUserId);//
						// 收款人，根据业务不同设置不同的id
						logger.info("退款请求 payTradeVo:" + JSONObject.fromObject(payTradeVo));
						BaseResultDto baseResultDto = payTradeServiceApi.balanceTrade(payTradeVo);
						logger.info("baseResultDto:" + JSONObject.fromObject(baseResultDto));
						if (baseResultDto != null && TradeErrorEnum.SUCCESS.getName().equals(baseResultDto.getCode())) {
							result = "退货完成，退款将在2-3个工作日内到达账户！";
							tradeOrderRefunds.setRefundsStatus(RefundsStatusEnum.SELLER_REFUNDING);
						} else if (baseResultDto != null
								&& TradeErrorEnum.AMOUNT_NOT_ENOUGH.getName().equals(baseResultDto.getCode())) {
							throw new Exception("云钱包余额不足，无法退款，请商家充值后再试！");
						} else {
							throw new Exception(baseResultDto != null ? baseResultDto.getMsg() : "");
						}
					} else {
						// POS线下退货的都是现金退
						tradeOrderRefunds.setPaymentMethod(PayTypeEnum.CASH);
					}
				}

				// 添加退款单记录
				tradeOrderRefundsMapper.insert(tradeOrderRefunds);

				tradeOrderRefunds.setTradeOrderRefundsItem(tradeOrderRefundsItems);
				logger.info("POS线下退货tradeOrderRefunds" + JSONObject.fromObject(tradeOrderRefunds));
				// 回收库存
				List<StockAdjustVo> stockAdjustList = recycleBuildStock(tradeOrderRefunds, rpcIdList);

				// 添加退款操作记录
				addRefundsCerticate(tradeOrderRefunds.getId(), "", userId);

				// 发消息给ERP增加出库单 added by maojj
				// stockMQProducer.sendMessage(stockAdjustList);
			}
		} catch (Exception e) {
			// 发消息回滚库存的更改 added by maojj
			rollbackMQProducer.sendStockRollbackMsg(rpcIdList);
			throw e;
		}
		return result;
	}

	// End modified by maojj 2016-07-26
	/**
	 * 退货单列表(pos销售查询用)
	 * 
	 * @author zhangkeneng
	 */
	@Override
	public List<TradeOrderRefunds> listForPos(Map<String, Object> map) throws Exception {
		return tradeOrderRefundsMapper.listForPos(map);
	}

	@Override
	public PageUtils<TradeOrderRefundsVo> findPageByFinance(Map<String, Object> map, int pageNumber, int pageSize)
			throws Exception {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrderRefundsVo> list = tradeOrderRefundsMapper.selectRefundsByFinance(map);
		return new PageUtils<TradeOrderRefundsVo>(list);
	}

	@Override
	public List<TradeOrderRefundsVo> findListByFinance(Map<String, Object> map) throws Exception {
		return tradeOrderRefundsMapper.selectRefundsByFinance(map);
	}

	@Override
	public Integer findCountByFinance(Map<String, Object> map) throws Exception {
		return tradeOrderRefundsMapper.selectRefundsCountByFinance(map);
	}

	/**
	 * @desc 查询退款订单详细信息（财务）
	 * @param refundId
	 *            退款ID
	 */
	public TradeOrderRefundsVo findDetailByFinance(String refundId) throws Exception {
		return tradeOrderRefundsMapper.selectDetailByFinance(refundId);
	}

	/**
	 * 根据地址id查询会员地址
	 *
	 * @param addressId
	 *            地址ID
	 */
	private MemberConsigneeAddress findMemberAddress(String addressId) {
		return memberConsigneeAddressService.findById(addressId);
	}

	/**
	 * @desc 根据订单id，统计退款金额
	 * @author wusw
	 */
	@Override
	public Double getSumAmountByOrderId(String orderId) throws Exception {
		return tradeOrderRefundsMapper.selectSumAmountByOrderId(orderId);
	}

	/**
	 * @desc 退款单未支付统计
	 */
	@Override
	public Integer findRefundUnPayCount() {
		return tradeOrderRefundsMapper.findRefundUnPayCount();
	}

	/**
	 * @desc 投诉订单未支付统计
	 */
	@Override
	public Integer findComplainUnPayCount() {
		return tradeOrderRefundsMapper.findComplainUnPayCount();
	}

	/**
	 * @desc 根据退款交易号查询退款单
	 *
	 * @param tradeNum
	 *            退款交易号
	 * @return 退款单
	 */
	public TradeOrderRefunds getByTradeNum(String tradeNum) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("tradeNum", tradeNum);
		return getByParams(params);
	}

	/**
	 * @desc 根据订单号查询订单
	 */
	@Override
	public TradeOrderRefunds getByRefundNo(String refundNo) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("refundNo", refundNo);
		return getByParams(params);
	}

	/**
	 * @desc 根据订单号查询退款单
	 */
	@Override
	public List<TradeOrderRefunds> findByOrderNo(String orderNo) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("orderNo", orderNo);
		return tradeOrderRefundsMapper.selectByParams(params);
	}

	/**
	 * 根据条件查询退款单（）
	 */
	public TradeOrderRefunds getByParams(Map<String, Object> params) {
		List<TradeOrderRefunds> tradeOrderRefunds = tradeOrderRefundsMapper.selectByParams(params);
		if (Iterables.isEmpty(tradeOrderRefunds)) {
			return null;
		}
		return Iterables.getOnlyElement(tradeOrderRefunds);
	}

	/**
	 * zengj:查询店铺的退款数量
	 *
	 * @param storeId
	 *            店铺
	 */
	public Long selectRefundsCount(String storeId) {
		return tradeOrderRefundsMapper.selectRefundsCount(storeId);
	}

	@Override
	public List<TradeOrderRefunds> getTradeOrderRefundsByOrderItemId(String orderItemId) {
		return tradeOrderRefundsMapper.getTradeOrderRefundsByOrderItemId(orderItemId);
	}

	@Override
	public PageUtils<TradeOrderRefunds> findByUserIdAndType(Map<String, Object> params, int pageNumber, int pageSize)
			throws Exception {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrderRefunds> list = tradeOrderRefundsMapper.selectByUserIdAndType(params);
		return new PageUtils<TradeOrderRefunds>(list);
	}

	@Override
	public TradeOrderRefunds getTradeOrderRefundsByOrderNo(String orderNo) {
		return tradeOrderRefundsMapper.getTradeOrderRefundsByOrderNo(orderNo);
	}

	/**
	 * 商家中心首页 根据状态统计退款单数量(张克能加)
	 */
	@Override
	public Integer selectRefundsCountForIndex(String storeId, List<Integer> refundsStatusList) {
		return tradeOrderRefundsMapper.selectRefundsCountForIndex(storeId, refundsStatusList);
	}

	/**
	 * 查询pos退款单导出列表
	 * @author zengj
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> selectPosRefundExportList(Map<String, Object> params) {
		return tradeOrderRefundsMapper.selectPosRefundExportList(params);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderRefundsServiceApi#findByOrderId(java.lang.String)
	 */
	@Transactional(readOnly = true)
	@Override
	public List<TradeOrderRefunds> findByOrderId(String orderId) {
		return tradeOrderRefundsMapper.selectByOrderId(orderId);
	}

	// Begin 重构4.1 add by wusw 20160722
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderService#findCountChargeForFinance(java.util.Map)
	 */
	@Override
	public Integer findCountChargeForFinance() {
		// 默认订单类型为话费充值、流量充值订单
		Map<String, Object> params = new HashMap<String, Object>();
		List<OrderTypeEnum> typeList = new ArrayList<OrderTypeEnum>();
		typeList.add(OrderTypeEnum.PHONE_PAY_ORDER);
		typeList.add(OrderTypeEnum.TRAFFIC_PAY_ORDER);
		params.put("type", typeList);
		// 默认状态为退款中
		params.put("refundsStatus", RefundsStatusEnum.SELLER_REFUNDING);
		// 默认查询第三方支付的充值订单
		List<PayTypeEnum> paymentMethodList = new ArrayList<PayTypeEnum>();
		paymentMethodList.add(PayTypeEnum.ALIPAY);
		paymentMethodList.add(PayTypeEnum.WXPAY);
		params.put("paymentMethod", paymentMethodList);
		params.put("disabled", Disabled.valid);
		return tradeOrderRefundsMapper.findCountChargeForFinance(params);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderRefundsService#findeChargeRefundsByParams(java.util.Map)
	 */
	@Override
	public PageUtils<TradeOrderRefundsChargeVo> findeChargeRefundsByParams(Map<String, Object> params, int pageNumber,
			int pageSize) throws Exception {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		// 参数处理（例如设置默认参数等）
		this.convertParamsForFinance(params);
		List<TradeOrderRefundsChargeVo> result = tradeOrderRefundsMapper.findeChargeRefundsByParams(params);
		return new PageUtils<TradeOrderRefundsChargeVo>(result);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderRefundsService#findeChargeRefundsListByParams(java.util.Map, int, int)
	 */
	@Override
	public List<TradeOrderRefundsChargeVo> findeChargeRefundsListByParams(Map<String, Object> params) throws Exception {
		// 参数处理（例如设置默认参数等）
		this.convertParamsForFinance(params);
		return tradeOrderRefundsMapper.findeChargeRefundsByParams(params);
	}

	/**
	 * 
	 * @Description: 充值订单列表参数处理（用于财务系统）
	 * @param params   
	 * @return void 
	 * @author wusw
	 * @date 2016年7月22日
	 */
	private void convertParamsForFinance(Map<String, Object> params) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		// 如果没有选择支付方式，默认查询第三方支付的充值订单
		if (params.get("paymentMethod") == null || StringUtils.isBlank(params.get("paymentMethod").toString())) {
			List<Integer> paymentMethodList = new ArrayList<Integer>();
			paymentMethodList.add(PayTypeEnum.ALIPAY.ordinal());
			paymentMethodList.add(PayTypeEnum.WXPAY.ordinal());
			params.put("paymentMethod", paymentMethodList);
		} else {
			List<Integer> paymentMethodList = new ArrayList<Integer>();
			paymentMethodList.add(new Integer(params.get("paymentMethod").toString()));
			params.put("paymentMethod", paymentMethodList);
		}
		if (params.get("startTime") == null || StringUtils.isBlank(params.get("startTime").toString())) {
			params.remove("startTime");
		}
		if (params.get("endTime") == null || StringUtils.isBlank(params.get("endTime").toString())) {
			params.remove("endTime");
		}
		// 默认订单类型为话费充值、流量充值订单
		List<OrderTypeEnum> typeList = new ArrayList<OrderTypeEnum>();
		typeList.add(OrderTypeEnum.PHONE_PAY_ORDER);
		typeList.add(OrderTypeEnum.TRAFFIC_PAY_ORDER);
		params.put("type", typeList);
		// 默认退款状态为退款中和退款完成
		List<RefundsStatusEnum> refundsStatusList = new ArrayList<RefundsStatusEnum>();
		refundsStatusList.add(RefundsStatusEnum.SELLER_REFUNDING);
		refundsStatusList.add(RefundsStatusEnum.REFUND_SUCCESS);
		params.put("refundsStatus", refundsStatusList);
		params.put("disabled", Disabled.valid);
	}
	// End 重构4.1 add by wusw 20160722

}