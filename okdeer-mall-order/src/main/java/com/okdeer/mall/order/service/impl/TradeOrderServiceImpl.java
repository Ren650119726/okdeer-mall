package com.okdeer.mall.order.service.impl;

import static com.okdeer.common.consts.DescriptConstants.ORDER_EXECUTE_CANCEL_FAIL;
import static com.okdeer.common.consts.DescriptConstants.ORDER_EXECUTE_REFUSE_FAIL;
import static com.okdeer.common.consts.DescriptConstants.ORDER_NOT_EXSITS_DELETE;
import static com.okdeer.common.consts.DescriptConstants.ORDER_STATUS_CHANGE;
import static com.okdeer.common.consts.DescriptConstants.ORDER_STATUS_CHANGE_ID;
import static com.okdeer.common.consts.DescriptConstants.ORDER_STATUS_OVERDUE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
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
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceServiceApi;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerJxcServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.entity.StoreAgentCommunity;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.enums.StoreTypeEnum;
import com.okdeer.archive.store.service.IStoreInfoExtServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.archive.system.entity.PsmsAgent;
import com.okdeer.archive.system.pos.entity.PosShiftExchange;
import com.okdeer.archive.system.service.IPsmsAgentServiceApi;
import com.okdeer.mall.activity.coupons.entity.ActivityCollectCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleRecord;
import com.okdeer.mall.activity.coupons.enums.ActivitySourceEnum;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleMapper;
import com.okdeer.mall.activity.coupons.service.ActivityCollectCouponsService;
import com.okdeer.mall.activity.coupons.service.ActivityCouponsRecordService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRecordService;
import com.okdeer.mall.activity.coupons.service.ActivitySaleService;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.group.entity.ActivityGroup;
import com.okdeer.mall.activity.group.service.ActivityGroupRecordService;
import com.okdeer.mall.activity.group.service.ActivityGroupService;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.activity.seckill.mapper.ActivitySeckillMapper;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillRecordService;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillService;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.common.enums.LogisticsType;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.member.member.enums.AddressDefault;
import com.okdeer.mall.member.member.service.MemberConsigneeAddressServiceApi;
import com.okdeer.mall.operate.column.service.ServerColumnService;
import com.okdeer.mall.operate.entity.ServerColumn;
import com.okdeer.mall.operate.entity.ServerColumnStore;
import com.okdeer.mall.order.constant.OrderMessageConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderLog;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRechargeVo;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderThirdRelation;
import com.okdeer.mall.order.enums.ConsumeStatusEnum;
import com.okdeer.mall.order.enums.OrderAppStatusAdaptor;
import com.okdeer.mall.order.enums.OrderComplete;
import com.okdeer.mall.order.enums.OrderIsShowEnum;
import com.okdeer.mall.order.enums.OrderPayTypeEnum;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PaymentStatusEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.enums.SendMsgType;
import com.okdeer.mall.order.mapper.TradeOrderCommentMapper;
import com.okdeer.mall.order.mapper.TradeOrderComplainMapper;
import com.okdeer.mall.order.mapper.TradeOrderInvoiceMapper;
import com.okdeer.mall.order.mapper.TradeOrderItemDetailMapper;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderLogMapper;
import com.okdeer.mall.order.mapper.TradeOrderLogisticsMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.mapper.TradeOrderPayMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsMapper;
import com.okdeer.mall.order.mapper.TradeOrderThirdRelationMapper;
import com.okdeer.mall.order.service.TradeMessageService;
import com.okdeer.mall.order.service.TradeOrderCompleteProcessService;
import com.okdeer.mall.order.service.TradeOrderLogService;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.service.TradeOrderServiceApi;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.order.utils.JsonDateValueProcessor;
import com.okdeer.mall.order.vo.ERPTradeOrderVo;
import com.okdeer.mall.order.vo.PhysicsOrderVo;
import com.okdeer.mall.order.vo.SendMsgParamVo;
import com.okdeer.mall.order.vo.TradeOrderCommentVo;
import com.okdeer.mall.order.vo.TradeOrderExportVo;
import com.okdeer.mall.order.vo.TradeOrderItemVo;
import com.okdeer.mall.order.vo.TradeOrderOperateParamVo;
import com.okdeer.mall.order.vo.TradeOrderPayQueryVo;
import com.okdeer.mall.order.vo.TradeOrderQueryVo;
import com.okdeer.mall.order.vo.TradeOrderStatisticsVo;
import com.okdeer.mall.order.vo.TradeOrderStatusVo;
import com.okdeer.mall.order.vo.TradeOrderVo;
import com.okdeer.mall.order.vo.UserTradeOrderDetailVo;
import com.okdeer.mall.points.service.PointsBuriedService;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;
import com.okdeer.mall.system.mq.RollbackMQProducer;
import com.okdeer.mall.system.mq.StockMQProducer;
import com.yschome.api.pay.enums.BusinessTypeEnum;
import com.yschome.api.pay.enums.TradeErrorEnum;
import com.yschome.api.pay.service.IPayTradeServiceApi;
import com.yschome.api.pay.tradeLog.dto.BalancePayTradeVo;
import com.yschome.base.common.enums.Disabled;
import com.yschome.base.common.exception.ServiceException;
import com.yschome.base.common.utils.DateUtils;
import com.yschome.base.common.utils.PageUtils;
import com.yschome.base.common.utils.StringUtils;
import com.yschome.base.common.utils.UuidUtils;
import com.yschome.base.framework.mq.RocketMQTransactionProducer;
import com.yschome.base.framework.mq.RocketMqResult;
import com.yschome.common.BaseResultDto;
import com.yschome.mcm.service.ISmsService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @DESC:
 * @author YSCGD
 * @date 2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *    重构4.1            2016-7-13            wusw               添加服务店订单列表查询
 *    重构4.1            2016-7-15            wusw               添加服务店订单详情查询,服务店订单派单的短信发送
 *    重构4.1            2016-7-16            zengj              服务店订单下单后不推送消息和发送短信
 *    重构4.1            2016-7-17            wusw               服务订单管理（商城后台）
 *    重构4.1            2016-7-17            wushp              服务订单
 *    重构4.1            2016-7-19            wusw               添加财务系统的订单接口（包含服务店订单情况）
 *    重构4.1            2016-7-19            wusw               修改商城后台的服务订单管理，线下确认价格并当面支付的支付方式判断
 *    重构4.1            2016-7-27            wusw               添加相应注释，修改财务系统订单接口默认参数处理，修改服务店订单导出的支付方式判断
 *    重构4.1            2016-7-29            wusw               添加设置拒绝服务原因
 *    重构4.1            2016-7-29            wusw               添加服务店订单详情查询（商城后台）
 *    重构4.1            2016-7-30            zhaoqc             添加根据订单状态查询订单列表
 *    重构4.1            2016-7-29            wusw               修改发货时，服务店订单超时未派单的计时消息发送
 *    重构4.1            2016-8-3             wushp              用户app订单列表或者订单详情--再逛逛按钮
 *    12170             2016-8-9            wusw                修改订单交易参数处理，支付方式为现金支付时包括货到付款的实物订单
 *    12051             2016-8-11             wusw               修改活动信息
 *    重构4.1            2016-8-16            wusw               修改订单发货、取消、拒收的日志描述
 *    重构4.1            2016-8-16            zhaoqc             新增根据交易号修改订单状态的方法
 *    重构4.1（代码评审优化）    2016-8-18            wusw               优化服务店订单列表和导出代码
 *    重构4.1			    2016-8-24            maojj              支付成功时，才发送提货码的信息
 *    V1.0.3            2016-09-01             wusw               修改自动确认收货期限为7天      
 *    1.0.Z			2016-09-05			zengj			增加订单操作记录        
 *     1.0.Z	          2016年9月07日                 zengj              库存管理修改，采用商业管理系统校验
 *   V1.1.0	       2016-9-12             zengjz            财务系统订单交易接口拆分，手机充值类型订单增加字段判断,增加财务系统订单交易统计接口  
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderServiceApi")
public class TradeOrderServiceImpl implements TradeOrderService, TradeOrderServiceApi, OrderMessageConstant {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderServiceImpl.class);

	/**
	 * 友门鹿云钱包账户
	 */
	@Value("${yscWalletAccount}")
	private String yscWalletAccount;

	/**
	 * 短信接口
	 */
	@Reference
	ISmsService smsService;

	@Value("${mcm.sys.code}")
	private String msgSysCode;

	@Value("${mcm.sys.token}")
	private String msgToken;

	@Value("${mcm.verifyCode.templateId}")
	private String templateId;

	/**
	 * 支付有效时长
	 */
	@Value("${payEffectiveTime}")
	private long payEffectiveTime;

	@Value("${order_notice_msg}")
	private String orderNoticeMsg;

	@Autowired
	private TradeOrderMapper tradeOrderMapper;

	@Autowired
	private TradeOrderRefundsMapper tradeOrderRefundsMapper;

	@Autowired
	private TradeOrderPayMapper tradeOrderPayMapper;

	@Autowired
	private TradeOrderItemMapper tradeOrderItemMapper;

	@Autowired
	private TradeOrderInvoiceMapper tradeOrderInvoiceMapper;

	@Autowired
	private TradeOrderLogMapper tradeOrderLogMapper;

	/**
	 * 订单和第三方平台订单关系
	 */
	@Resource
	private TradeOrderThirdRelationMapper tradeOrderThirdRelationMapper;

	/**
	 * 满减(满折)DAO
	 */
	@Resource
	private ActivityDiscountMapper activityDiscountMapper;

	// begin add by wangf01 2016.08.06
	/**
	 * 特惠Dao
	 */
	@Autowired
	private ActivitySaleMapper activitySaleMapper;

	// end add by wangf01 2016.08.06

	@Autowired
	private ActivityCouponsRecordService activityCouponsRecordService;

	/**
	 * 代金券DAO
	 */
	@Autowired
	private ActivityCollectCouponsService activityCollectCouponsService;

	/**
	 * 支付service
	 */
	@Autowired
	private TradeOrderPayService tradeOrderPayService;

	/**
	 * 订单评价DAO
	 */
	@Autowired
	private TradeOrderCommentMapper tradeOrderCommentMapper;

	/**
	 * 订单投诉DAO
	 */
	@Autowired
	private TradeOrderComplainMapper tradeOrderComplainMapper;

	/**
	 * 店铺基本信息mapper
	 */
	@Reference(version = "1.0.0", check = false)
	private IStoreInfoExtServiceApi storeInfoExtService;

	/**
	 * 订单收货信息
	 */
	@Autowired
	private TradeOrderLogisticsMapper tradeOrderLogisticsMapper;

	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	@Autowired
	private RocketMQTransactionProducer rocketMQTransactionProducer;

	@Reference(version = "1.0.0", check = false)
	IPayTradeServiceApi payTradeServiceApi;

	/**
	 * 特惠活动Mapper
	 */
	@Autowired
	private ActivitySaleService activitySaleService;

	/**
	 * 特惠活动Mapper
	 */
	@Autowired
	private ActivityGroupRecordService activityGroupRecordService;

	/**
	 * 团购活动Mapper
	 */
	@Autowired
	private ActivityGroupService activityGroupService;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceServiceApi goodsStoreSkuServiceService;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuService;

	@Autowired
	private TradeOrderItemDetailMapper tradeOrderItemDetailMapper;

	// @Reference(version = "1.0.0", check = false)
	// private StockManagerServiceApi stockManagerService;

	// Begin 1.0.Z add by zengj
	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerJxcServiceApi stockManagerService;

	// End 1.0.Z add by zengj

	@Reference(version = "1.0.0", check = false)
	private MemberConsigneeAddressServiceApi memberConsigneeAddressService;

	/**
	 * 订单消费赠送积分service
	 */
	@Autowired
	private PointsBuriedService pointsBuriedService;

	/**
	 * 特惠活动记录信息mapper
	 */
	@Autowired
	private ActivitySaleRecordService activitySaleRecordService;

	@Resource
	private SysBuyerUserMapper sysBuyerUserMapper;

	/**
	 * 消息发送
	 */
	@Autowired
	private TradeMessageService tradeMessageService;

	@Autowired
	private TradeOrderTimer tradeOrderTimer;

	/**
	 * 退款单项mapper
	 */
	@Autowired
	private TradeOrderRefundsItemMapper tradeOrderRefundsItemMapper;

	@Autowired
	private ActivitySeckillRecordService activitySeckillRecordService;

	/**
	 * 秒杀活动service
	 */
	@Autowired
	private ActivitySeckillService sctivitySeckillService;

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

	// begin 重构4.1 add by wushp 20160803
	/**
	 * 注入service
	 */
	@Autowired
	ServerColumnService serverColumnService;

	// end 重构4.1 add by wushp 20160803

	// begin 12051 add by wusw 20160811
	/**
	 * 注入秒杀活动Mapper接口
	 */
	@Autowired
	ActivitySeckillMapper activitySeckillMapper;

	// end 12051 add by wusw 20160811

	// Begin sql优化，将复杂sql拆分开来 add by zengj
	/**
	 * 店铺信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	StoreInfoServiceApi storeInfoServiceApi;

	/**
	 * 代理商Service
	 */
	@Reference(version = "1.0.0", check = false)
	IPsmsAgentServiceApi psmsAgentServiceApi;

	// End sql优化，将复杂sql拆分开来 add by zengj

	// Begin 1.0.Z 增加订单操作记录Service add by zengj
	/**
	 * 订单操作记录Service
	 */
	@Resource
	private TradeOrderLogService tradeOrderLogService;

	/**
	 * 订单完成后同步商业管理系统Service
	 */
	@Resource
	private TradeOrderCompleteProcessService tradeOrderCompleteProcessService;

	// End 1.0.Z 增加订单操作记录Service add by zengj

	@Override
	public PageUtils<TradeOrder> selectByParams(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		return new PageUtils<TradeOrder>(tradeOrderMapper.selectOrderList(map));
	}

	/**
	 * 
	 * @desc 查询订单导出的列表
	 */
	public List<TradeOrderExportVo> selectExportList(Map<String, Object> map) {
		// 查询订单信息
		List<TradeOrder> orderPay = tradeOrderMapper.selectRealOrderList(map);
		List<TradeOrderExportVo> exportList = new ArrayList<TradeOrderExportVo>();
		if (orderPay != null) {
			// 退款单状态Map
			Map<String, String> orderRefundsStatusMap = RefundsStatusEnum.convertViewStatus();
			for (int i = 0; i < orderPay.size(); i++) {
				TradeOrder order = orderPay.get(i);
				// 订单状态Map
				Map<String, String> orderStatusMap = OrderStatusEnum.convertViewStatus(order.getType());
				String id = order.getId();
				map.put("orderId", id);
				// 订单项信息
				List<TradeOrderItem> orderItemList = order.getTradeOrderItem();
				if (orderItemList != null) {
					for (TradeOrderItem item : orderItemList) {
						if (item == null) {
							continue;
						}
						TradeOrderExportVo exportVo = new TradeOrderExportVo();
						// 实付款取订单的实际付款金额(2016-5-3 13:43:35确认于高沛)
						exportVo.setActualAmount(order.getActualAmount());
						exportVo.setUserId(order.getUserId());
						exportVo.setCreateTime(DateUtils.formatDate(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
						exportVo.setOrderNo(order.getOrderNo());
						exportVo.setQuantity(item.getQuantity() == null ? "" : item.getQuantity().toString());
						exportVo.setUserPhone(order.getUserPhone());
						exportVo.setSkuName(item.getSkuName());
						exportVo.setCategoryName(item.getCategoryName());
						exportVo.setStatus(orderStatusMap.get(order.getStatus().getName()));
						exportVo.setUnitPrice(item.getUnitPrice());
						exportVo.setTotalAmount(order.getTotalAmount());
						if (!OrderStatusEnum.UNPAID.equals(order.getStatus())
								&& !OrderStatusEnum.BUYER_PAYING.equals(order.getStatus())) {
							// 支付方式
							if (order.getTradeOrderPay() == null) {
								exportVo.setPayType(order.getPayWay().getValue());
							} else {
								exportVo.setPayType(order.getTradeOrderPay().getPayType().getValue());
							}
						}
						exportVo.setBarCode(item.getBarCode() == null ? "" : item.getBarCode());
						exportVo.setStyleCode(item.getStyleCode() == null ? "" : item.getStyleCode());
						// 售后单状态
						if (item.getRefundsStatus() != null) {
							exportVo.setAfterService(orderRefundsStatusMap.get(item.getRefundsStatus().getName()));
						}
						exportVo.setOperator(order.getSysUser() == null ? null : order.getSysUser().getLoginName());
						exportList.add(exportVo);
					}
				}
			}
		}
		return exportList;
	}

	public List<TradeOrderExportVo> selectPosExportList(Map<String, Object> map) {
		return null;
	}

	@Override
	public TradeOrder selectById(String id) throws ServiceException {
		return tradeOrderMapper.selectByPrimaryKey(id);
	}

	@Override
	public Integer selectOrderNum(OrderStatusEnum orderStatus, String storeId) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("status", orderStatus);
		map.put("storeId", storeId);
		return tradeOrderMapper.selectOrderNum(map);
	}

	/**
	 * @desc 根据查询条件,查询订单详细信息列表（用于历史回款记录，注意：支付状态条件为大于等于）
	 * @author wusw
	 */
	@Override
	public List<TradeOrderQueryVo> findShippedOrderByParams(Map<String, Object> params, int pageNumber, int pageSize)
			throws ServiceException {
		// PageHelper.startPage(pageNumber, pageSize);
		List<TradeOrderQueryVo> result = tradeOrderMapper.selectShippedOrderByParams(params);
		if (result == null) {
			result = new ArrayList<TradeOrderQueryVo>();
		}
		// return new PageUtils<TradeOrderQueryVo>(result);
		return result;
	}

	/**
	 * @desc 根据查询条件,查询订单详细信息列表（参数为实体类型，用于历史回款记录，注意：支付状态条件为大于等于，分页）
	 * @author wusw
	 */
	@Override
	public PageUtils<TradeOrderQueryVo> findShippedOrderByEntity(TradeOrderQueryVo tradeOrderQueryVo, int pageNumber,
			int pageSize) throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrderQueryVo> result = tradeOrderMapper.selectShippedOrderByEntity(tradeOrderQueryVo);
		if (result == null) {
			result = new ArrayList<TradeOrderQueryVo>();
		} else {
			for (TradeOrderQueryVo tradeOrder : result) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("orderId", tradeOrder.getId());
				tradeOrder.setTradeOrderItem(tradeOrderMapper.selectShippedOrderItemByEntity(params));
			}
		}
		return new PageUtils<TradeOrderQueryVo>(result);

	}

	/**
	 * @desc 将订单批量回款
	 * @author wusw
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void receivableOrder(String[] ids) throws ServiceException {
		if (ids != null && ids.length > 0) {
			tradeOrderMapper.updatePaymentStatusByIds(ids, PaymentStatusEnum.BACK_SECTION, new Date());
		}
	}

	@Override
	public int selectPaymentStatusCountByIds(String[] ids, PaymentStatusEnum paymentStatus) throws ServiceException {
		int count = 0;
		if (ids != null && ids.length > 0) {
			count = tradeOrderMapper.selectPaymentStatusCountByIds(ids, paymentStatus);
		}
		return count;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateIsShowById(String orderId) throws ServiceException {

		tradeOrderMapper.updateIsShowById(OrderIsShowEnum.no, new Date(), orderId);
	}

	@Override
	public Map<String, Object> getPayRemainTime(String orderId) throws ServiceException {
		// TradeOrder tradeOrder = tradeOrderMapper.selectByPrimaryKey(orderId);
		// Date payTime = new Date(tradeOrder.getCreateTime().getTime() +
		// payEffectiveTime);
		// Date nowDate = new Date();
		// long remainTime = (payTime.getTime() - nowDate.getTime())/1000;

		// 1 订单支付倒计时计算
		UserTradeOrderDetailVo orders = tradeOrderMapper.selectRemainTimeById(orderId);
		Integer remainingTime = orders.getRemainingTime();
		Map<String, Object> map = new HashMap<String, Object>();
		if (remainingTime != null) {
			remainingTime = remainingTime + 1800;
			if (remainingTime >= 0) {
				map.put("remainTime", remainingTime);
				map.put("orderId", orders.getId());
				map.put("actualAmount", orders.getActualAmount().toString());
				map.put("tradeNum", orders.getTradeNum());
			}
		}
		return map;
	}

	@Override
	public PageUtils<TradeOrderPayQueryVo> findByStatusPayType(Map<String, Object> params, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize);

		// Begin v1.1.0 modify by zengjz 20160912 增加退款状态的判断
		convertParamsForFinance(params);
		// End v1.1.0 modify by zengjz 20160912

		List<OrderPayTypeEnum> orderPayList = new ArrayList<OrderPayTypeEnum>();
		if (params == null) {
			params = new HashMap<String, Object>();
		} else {
			if (params.get("payType") != null && !"".equals(params.get("payType"))) {
				if (params.get("payType").equals("1")) {
					orderPayList.add(OrderPayTypeEnum.ALIPAY_PAY);
				} else if (params.get("payType").equals("2")) {
					orderPayList.add(OrderPayTypeEnum.WEBCHAT_PAY);
				} else {
					orderPayList.add(OrderPayTypeEnum.WEBCHAT_PAY);
					orderPayList.add(OrderPayTypeEnum.ALIPAY_PAY);
				}
			} else {
				orderPayList.add(OrderPayTypeEnum.WEBCHAT_PAY);
				orderPayList.add(OrderPayTypeEnum.ALIPAY_PAY);
			}
		}
		params.put("payWay", PayWayEnum.PAY_ONLINE);
		params.put("payType", orderPayList);
		List<TradeOrderPayQueryVo> result = tradeOrderMapper.selectByStatusPayType(params);
		if (result == null) {
			result = new ArrayList<TradeOrderPayQueryVo>();
		}
		return new PageUtils<TradeOrderPayQueryVo>(result);
	}

	@Override
	public List<TradeOrderPayQueryVo> findListByStatusPayType(Map<String, Object> params) throws ServiceException {
		List<TradeOrderPayQueryVo> result = tradeOrderMapper.selectByStatusPayType(params);
		if (result == null) {
			result = new ArrayList<TradeOrderPayQueryVo>();
		}
		return result;
	}

	@Override
	public int selectCountByStatusPayType(Map<String, Object> params) throws ServiceException {

		// Begin v1.1.0 modify by zengjz 20160912 增加退款状态的判断
		convertParamsForFinance(params);
		// End v1.1.0 modify by zengjz 20160912

		List<OrderPayTypeEnum> orderPayList = new ArrayList<OrderPayTypeEnum>();
		if (params == null) {
			params = new HashMap<String, Object>();
		} else {
			if (params.get("payType") != null && !"".equals(params.get("payType"))) {
				if (params.get("payType").equals("1")) {
					orderPayList.add(OrderPayTypeEnum.ALIPAY_PAY);
				} else if (params.get("payType").equals("2")) {
					orderPayList.add(OrderPayTypeEnum.WEBCHAT_PAY);
				} else {
					orderPayList.add(OrderPayTypeEnum.WEBCHAT_PAY);
					orderPayList.add(OrderPayTypeEnum.ALIPAY_PAY);
				}
			} else {
				orderPayList.add(OrderPayTypeEnum.WEBCHAT_PAY);
				orderPayList.add(OrderPayTypeEnum.ALIPAY_PAY);
			}
		}

		params.put("payWay", PayWayEnum.PAY_ONLINE);
		params.put("payType", orderPayList);
		return tradeOrderMapper.selectCountByStatusPayType(params);
	}

	@Override
	public int selectCountForUnRefund() throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();

		List<OrderStatusEnum> orderStatusList = new ArrayList<OrderStatusEnum>();
		orderStatusList.add(OrderStatusEnum.CANCELING);
		orderStatusList.add(OrderStatusEnum.REFUSING);

		List<OrderPayTypeEnum> orderPayList = new ArrayList<OrderPayTypeEnum>();
		orderPayList.add(OrderPayTypeEnum.WEBCHAT_PAY);
		orderPayList.add(OrderPayTypeEnum.ALIPAY_PAY);

		params.put("status", orderStatusList);
		params.put("payWay", PayWayEnum.PAY_ONLINE);
		params.put("payType", orderPayList);
		return tradeOrderMapper.selectCountByStatusPayType(params);
	}

	@Override
	public List<TradeOrderPayQueryVo> findByOrderIdList(List<String> orderIds) throws ServiceException {
		List<TradeOrderPayQueryVo> result = null;
		if (orderIds != null && orderIds.size() > 0) {
			result = tradeOrderMapper.selectByOrderIdList(orderIds);
		}
		return result;
	}

	/**
	 * @desc 根据订单id，获取订单、支付信息
	 * 
	 * @author wusw
	 */
	@Override
	public TradeOrder findOrderPayInvoiceById(String id) throws ServiceException {

		return tradeOrderMapper.selectOrderPayInvoiceById(id);
	}

	/**
	 * @desc 根据订单id，获取订单详细信息（包括订单基本信息、支付信息、发票信息、店铺基本信息等）
	 * 
	 * @author wusw
	 */
	@Override
	public TradeOrder findOrderDetail(String orderId) throws ServiceException {

		// 订单
		TradeOrder tradeOrder = tradeOrderMapper.selectByPrimaryKey(orderId);
		// 获取订单参与活动信息
		Map<String, Object> map = getActivity(tradeOrder.getActivityType(), tradeOrder.getActivityId());
		String activityName = map.get("activityName") == null ? null : map.get("activityName").toString();
		ActivitySourceEnum activitySource = map.get("activitySource") == null ? null : (ActivitySourceEnum) map
				.get("activitySource");
		tradeOrder.setActivityName(activityName);
		tradeOrder.setActivitySource(activitySource);
		// 交易项信息（包括商品基本信息）
		List<TradeOrderItem> tradeOrderItemList = tradeOrderItemMapper.selectOrderItemDetailById(orderId);
		tradeOrder.setTradeOrderItem(tradeOrderItemList);
		tradeOrder.setTradeOrderPay(tradeOrderPayMapper.selectByOrderId(orderId));
		return tradeOrder;
	}

	@Override
	public PageUtils<PhysicsOrderVo> findOrderBackStage(PhysicsOrderVo vo, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		// 避免数组ids不为空，但是长度为0的情况
		if (vo.getIds() != null && vo.getIds().length <= 0) {
			vo.setIds(null);
		}
		List<PhysicsOrderVo> result = tradeOrderMapper.selectOrderBackStage(vo);
		return new PageUtils<PhysicsOrderVo>(result);
	}

	/**
	 * 
	 * @Description: 运营后台查询实物订单列表-sql优化后
	 * @param vo 查询参数
	 * @param pageNumber 当前页
	 * @param pageSize 页大小
	 * @return 实物订单列表
	 * @throws ServiceException 异常    
	 * @author zengj
	 * @date 2016年8月17日
	 */
	@Transactional(readOnly = true)
	@Override
	public PageUtils<PhysicsOrderVo> findOrderBackStageNew(PhysicsOrderVo vo, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		// 避免数组ids不为空，但是长度为0的情况
		if (vo.getIds() != null && vo.getIds().length <= 0) {
			vo.setIds(null);
		}
		List<PhysicsOrderVo> result = tradeOrderMapper.selectOrderBackStageNew(vo);
		// 如果有订单信息
		if (CollectionUtils.isNotEmpty(result)) {
			// 店铺ID集合
			List<String> storeIds = null;
			// 订单ID集合
			List<String> orderIds = null;
			// 代理商ID集合
			List<String> agentIds = null;
			// 如果没有根据店铺来搜索，sql中不会加载店铺信息，需要再次查询店铺信息
			if (StringUtils.isBlank(vo.getStoreName()) && vo.getType() == null) {
				storeIds = new ArrayList<String>();
			}
			// 如果没有根据支付类型来搜索，sql中不会加载支付信息，需要再次支付信息
			if (vo.getPayType() == null) {
				orderIds = new ArrayList<String>();
			}
			// 如果没有根据代理商来搜索，sql中不会加载代理商信息，需要再次代理商信息
			if (StringUtils.isBlank(vo.getFullName())) {
				agentIds = new ArrayList<String>();
			}
			for (PhysicsOrderVo order : result) {
				if (storeIds != null && StringUtils.isNotBlank(order.getStoreId())) {
					storeIds.add(order.getStoreId());
				}
				if (orderIds != null && StringUtils.isNotBlank(order.getId())) {
					orderIds.add(order.getId());
				}
				if (agentIds != null && StringUtils.isNotBlank(order.getAgentId())) {
					agentIds.add(order.getAgentId());
				}
			}

			// 构建实物订单实体
			buildPhysicsOrderVo(result, storeIds, orderIds, agentIds);
		}
		return new PageUtils<PhysicsOrderVo>(result);
	}

	/**
	 * 
	 * @Description: 构建实物订单实体
	 * @param result 查询后的实物订单对象
	 * @param storeIds 店铺ID集合
	 * @param orderIds 订单ID集合
	 * @param agentIds 代理商ID集合
	 * @author zengj
	 * @date 2016年8月17日
	 */
	private void buildPhysicsOrderVo(List<PhysicsOrderVo> result, List<String> storeIds, List<String> orderIds,
			List<String> agentIds) {
		// 店铺集合
		List<StoreInfo> storeInfoList = null;
		// 订单支付集合
		List<TradeOrderPay> orderPayList = null;
		// 代理商集合
		List<PsmsAgent> agentList = null;
		if (CollectionUtils.isNotEmpty(storeIds)) {
			storeInfoList = this.storeInfoService.selectByIds(storeIds);
		}
		if (CollectionUtils.isNotEmpty(orderIds)) {
			orderPayList = this.tradeOrderPayMapper.selectByOrderIds(orderIds);
		}
		if (CollectionUtils.isNotEmpty(agentIds)) {
			agentList = this.psmsAgentServiceApi.selectByIds(agentIds);
		}
		// 循环将对应信息加入到订单实体中
		for (PhysicsOrderVo order : result) {
			if (CollectionUtils.isNotEmpty(storeInfoList)) {
				for (StoreInfo storeInfo : storeInfoList) {
					if (order.getStoreId() != null && order.getStoreId().equals(storeInfo.getId())) {
						order.setStoreName(storeInfo.getStoreName());
						order.setType(storeInfo.getType());
						break;
					}
				}
			}
			if (CollectionUtils.isNotEmpty(orderPayList)) {
				for (TradeOrderPay orderPay : orderPayList) {
					if (order.getId() != null && order.getId().equals(orderPay.getOrderId())) {
						order.setPayType(OrderPayTypeEnum.enumOrdinalOf(orderPay.getPayType().ordinal()));
						break;
					}
				}
			}

			if (CollectionUtils.isNotEmpty(agentList)) {
				for (PsmsAgent agent : agentList) {
					if (order.getAgentId() != null && order.getAgentId().equals(agent.getId())) {
						order.setFullName(agent.getFullName());
						break;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @desc 商家APP订单查询
	 *
	 * @param map
	 *            查询条件
	 * @param pageSize
	 *            每页大小
	 * @param pageNumber
	 *            当前页
	 * @return
	 */
	@Override
	public PageUtils<TradeOrderVo> selectMallAppOrderInfo(Map<String, Object> map, int pageSize, int pageNumber) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrderVo> list = tradeOrderMapper.selectOrderInfo(map);
		if (list != null && !list.isEmpty()) {
			for (TradeOrderVo tradeOrderVo : list) {
				// 查询订单项表。
				tradeOrderVo.setTradeOrderItem(tradeOrderItemMapper.selectTradeOrderItem(tradeOrderVo.getId()));
				// 查询投诉信息
				tradeOrderVo.setTradeOrderComplainVoList(tradeOrderComplainMapper
						.findOrderComplainByParams(tradeOrderVo.getId()));

				// 获取订单活动信息
				Map<String, Object> activityMap = getActivity(tradeOrderVo.getActivityType(),
						tradeOrderVo.getActivityId());
				String activityName = activityMap.get("activityName") == null ? null : activityMap.get("activityName")
						.toString();
				ActivitySourceEnum activitySource = activityMap.get("activitySource") == null ? null
						: (ActivitySourceEnum) activityMap.get("activitySource");
				tradeOrderVo.setActivityName(activityName);
				tradeOrderVo.setActivitySource(activitySource);
			}
		}
		return new PageUtils<TradeOrderVo>(list);
	}

	/**
	 * 获取活动信息
	 * 
	 * @author zengj
	 * @param activityType活动类型
	 * @param activityId
	 *            活动ID
	 */
	private Map<String, Object> getActivity(ActivityTypeEnum activityType, String activityId) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 活动名称
		String activityName = null;
		// 活动来源，（活动发起者身份）
		ActivitySourceEnum activitySource = null;
		// 如果有活动ID，说明该订单参与了活动
		if (StringUtils.isNotBlank(activityId) && !"0".equals(activityId)) {
			// 代金券活动
			if (ActivityTypeEnum.VONCHER.equals(activityType)) {
				ActivityCollectCoupons activityCollectCoupons = activityCollectCouponsService.get(activityId);
				if (activityCollectCoupons != null) {
					activityName = activityCollectCoupons.getName();
					// 所属代理商id，运营商以0标识
					if ("0".equals(activityCollectCoupons.getBelongType())) {
						activitySource = ActivitySourceEnum.OPERATOR;
					} else {
						activitySource = ActivitySourceEnum.AGENT;
					}
				}
			} else if (ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES.equals(activityType)
					|| ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES.equals(activityType)) {
				// 满减活动
				ActivityDiscount activityDiscount = activityDiscountMapper.selectByPrimaryKey(activityId);
				if (activityDiscount != null) {
					activityName = activityDiscount.getName();

					// 所属店铺ID，运营商用0表示
					if ("0".equals(activityDiscount.getStoreId())) {
						activitySource = ActivitySourceEnum.OPERATOR;
					} else {
						activitySource = ActivitySourceEnum.STORE;
					}
				}
			} else if (ActivityTypeEnum.GROUP_ACTIVITY.equals(activityType)) {
				// 团购活动
				ActivityGroup activityGroup = activityGroupService.selectByPrimaryKey(activityId);
				if (activityGroup != null) {
					activityName = activityGroup.getName();
					// 所属代理商id，运营商以0标识
					if ("0".equals(activityGroup.getBelongType())) {
						activitySource = ActivitySourceEnum.OPERATOR;
					} else {
						activitySource = ActivitySourceEnum.AGENT;
					}
				}
			} else if (ActivityTypeEnum.SALE_ACTIVITIES.equals(activityType)) {
				// 特惠活动
				ActivitySale activitySale = activitySaleService.get(activityId);
				if (activitySale != null) {
					activityName = activitySale.getName();
					// 特惠活动只有店铺能发
					activitySource = ActivitySourceEnum.STORE;
				}
			}
		}
		map.put("activityName", activityName);
		map.put("activitySource", activitySource);
		return map;
	}

	/**
	 * 
	 * @desc 获取订单详情信息
	 *
	 * @param orderId
	 *            订单ID
	 */
	@Override
	public TradeOrderVo getOrderDetail(String orderId) {
		TradeOrderVo tradeOrderVo = tradeOrderMapper.getOrderDetail(orderId);
		if (tradeOrderVo != null) {
			// 订单状态已完成后需要查询订单评论信息
			if (OrderStatusEnum.HAS_BEEN_SIGNED.equals(tradeOrderVo.getStatus())
					|| OrderStatusEnum.HAS_BEEN_SIGNED.equals(tradeOrderVo.getStatus())
					|| OrderStatusEnum.TRADE_CLOSED.equals(tradeOrderVo.getStatus())) {
				Map<String, Object> map = new HashMap<String, Object>();
				if (tradeOrderVo.getTradeOrderItemVoList() != null) {
					for (TradeOrderItemVo item : tradeOrderVo.getTradeOrderItemVoList()) {
						map.put("orderItemId", item.getId());
						// 查询订单评论详情
						List<TradeOrderCommentVo> tradeOrderCommentVoList = tradeOrderCommentMapper
								.selectCommentByParams(map);
						item.setTradeOrderCommentVoList(tradeOrderCommentVoList);
						// 查询订单评论图片
						// if (tradeOrderCommentVoList != null &&
						// !tradeOrderCommentVoList.isEmpty()) {
						// for (TradeOrderCommentVo comment :
						// tradeOrderCommentVoList) {
						// comment.setImagePaths(tradeOrderCommentImageMapper.selectByCommentId(comment.getId()));
						// }
						// }
						map.clear();
					}
				}
				map.clear();
				map.put("orderId", orderId);
				// 订单状态已完成后才能有投诉信息
				tradeOrderVo.setTradeOrderComplainVoList(tradeOrderComplainMapper.findOrderComplainByParams(orderId));
			}

			// 获取订单活动信息
			Map<String, Object> activityMap = getActivity(tradeOrderVo.getActivityType(), tradeOrderVo.getActivityId());
			String activityName = activityMap.get("activityName") == null ? null : activityMap.get("activityName")
					.toString();
			ActivitySourceEnum activitySource = activityMap.get("activitySource") == null ? null
					: (ActivitySourceEnum) activityMap.get("activitySource");
			tradeOrderVo.setActivityName(activityName);
			tradeOrderVo.setActivitySource(activitySource);
		}
		return tradeOrderVo;
	}

	/**
	 * 商家版APP获取订单状态对应的订单数量
	 */
	@Override
	public List<TradeOrderStatusVo> getOrderCount(Map<String, Object> map) {
		return tradeOrderMapper.getOrderCount(map);
	}

	/**
	 * 保存订单及相关信息
	 */
	@Transactional(rollbackFor = Exception.class)
	private void insertOrder(Object entity) throws ServiceException, Exception {

		if (entity instanceof TradeOrder) {
			TradeOrder tradeOrder = (TradeOrder) entity;
			tradeOrderMapper.insertSelective(tradeOrder);
			TradeOrderPay tradeOrderPay = tradeOrder.getTradeOrderPay();
			TradeOrderLogistics orderLogistics = tradeOrder.getTradeOrderLogistics();
			TradeOrderInvoice invoice = tradeOrder.getTradeOrderInvoice();
			ActivitySaleRecord saleRecord = tradeOrder.getActiviySaleRecord();
			if (tradeOrderPay != null) {
				tradeOrderPayMapper.insertSelective(tradeOrderPay);
			}
			if (orderLogistics != null) {
				tradeOrderLogisticsMapper.insertSelective(tradeOrder.getTradeOrderLogistics());
			}
			if (invoice != null) {
				tradeOrderInvoiceMapper.insertSelective(invoice);
			}
			if (saleRecord != null) {
				activitySaleRecordService.insertSelective(saleRecord);
			}

			List<TradeOrderItem> itemList = tradeOrder.getTradeOrderItem();

			// for (TradeOrderItem item : itemList) {
			// tradeOrderItemMapper.insertSelective(item);
			// }
			tradeOrderItemMapper.insertBatch(itemList);

			// 判断如果是货到付款就发送推送消息,有支付的话，在付款的时候会发送。
			if (PayWayEnum.CASH_DELIERY.equals(tradeOrder.getPayWay())
					|| PayWayEnum.OFFLINE_CONFIRM_AND_PAY.equals(tradeOrder.getPayWay())) {
				// Begin 重构4.1 add by zengj
				SendMsgParamVo sendMsgParamVo = new SendMsgParamVo(tradeOrder);
				// 发送POS消息
				tradeMessageService.sendPosMessage(sendMsgParamVo, SendMsgType.createOrder);
				// 发送商家版APP消息
				tradeMessageService.sendSellerAppMessage(sendMsgParamVo, SendMsgType.createOrder);
				// 保存消息中心
				tradeMessageService.saveSysMsg(tradeOrder, SendMsgType.createOrder);
				// End 重构4.1 add by zengj
			}
		}

	}

	/**
	 * @desc 插入订单并发送订单消息到中间件
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean insertTradeOrder(TradeOrder tradeOrder) throws Exception {

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());

		StoreInfo storeInfo = getStoreInfo(tradeOrder.getStoreId());
		JSONObject json = JSONObject.fromObject(tradeOrder, jsonConfig);
		json.put("storeType", storeInfo.getType());
		List<StoreAgentCommunity> communitys = storeInfoService.getAgentCommunitysByStoreId(tradeOrder.getStoreId());
		if (!Iterables.isEmpty(communitys)) {
			json.put("storeAgentCommunity", communitys.get(0));
		}
		return insertOrderAndSendMsg(storeInfo, json, tradeOrder);
	}

	/**
	 * 发送消息(快送同步)并插入订单
	 */
	@Transactional(rollbackFor = Exception.class)
	private boolean insertOrderAndSendMsg(StoreInfo storeInfo, JSONObject json, TradeOrder tradeOrder) throws Exception {
		Message msg = new Message(getTopicByStoreType(storeInfo.getType()), TAG_ORDER_ADD, json.toString().getBytes(
				Charsets.UTF_8));
		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, tradeOrder,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
						try {
							insertOrder(object);
							return LocalTransactionState.COMMIT_MESSAGE;
						} catch (Exception e) {
							logger.error("发送消息并插入订单信息失败", e);
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
	 * 支付订单并发送消息
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateWithApply(TradeOrder tradeOrder) throws Exception {
		StoreInfo storeInfo = getStoreInfo(tradeOrder.getStoreId());
		Message msg = new Message(getTopicByStoreType(storeInfo.getType()), TAG_ORDER_APPLY, tradeOrder.getId()
				.getBytes());
		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, tradeOrder,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
						// todo 执行本地业务

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
	 * 取消订单并发送消息(快送同步)
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean updateCancelOrder(TradeOrder tradeOrder, boolean isBuyerOperate) throws Exception {
		final JSONObject json = new JSONObject();
		json.put("id", tradeOrder.getId());
		json.put("reason", tradeOrder.getReason());
		json.put("status", tradeOrder.getStatus());
		if (isBuyerOperate) {
			json.put("operator", tradeOrder.getUserId());
		} else {
			json.put("operator", tradeOrder.getUpdateUserId());
		}
		json.put("isBuyerOperator", isBuyerOperate);
		StoreInfo storeInfo = getStoreInfo(tradeOrder.getStoreId());
		Message msg = new Message(getTopicByStoreType(storeInfo.getType()), TAG_ORDER_CANCEL, json.toString().getBytes(
				Charsets.UTF_8));
		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, tradeOrder,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
						try {
							updateCancelOrder(object, json.optString("operator"));
							return LocalTransactionState.COMMIT_MESSAGE;
						} catch (Exception e) {
							// Begin 重构4.1 update by wusw 20160816
							logger.error(ORDER_EXECUTE_CANCEL_FAIL, e);
							// End 重构4.1 update by wusw 20160816
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

	// Begin modified by maojj 2016-07-26 添加分布式事务处理机制
	/**
	 * 取消订单处理
	 *
	 * @param entity
	 *            订单对象
	 */
	@Transactional(rollbackFor = Exception.class)
	private void updateCancelOrder(Object entity, String operator) throws Exception {
		List<String> rpcIdList = new ArrayList<String>();
		try {
			if (entity instanceof TradeOrder) {
				TradeOrder tradeOrder = (TradeOrder) entity;
				// 判断是否付款，如果付款需要先退款--->取消订单完成
				TradeOrder oldOrder = this.findOrderDetail(tradeOrder.getId());
				tradeOrder.setCurrentStatus(oldOrder.getStatus());
				// 订单状态为已发货或者待发货，全部变为取消中
				if (OrderStatusEnum.DROPSHIPPING == oldOrder.getStatus()) {
					if (oldOrder.getPayWay() == PayWayEnum.PAY_ONLINE) {
						tradeOrder.setStatus(OrderStatusEnum.CANCELING);
					} else {
						tradeOrder.setStatus(OrderStatusEnum.CANCELED);
					}
					// 设置短信取取消订单原因
					oldOrder.setReason(tradeOrder.getReason());
				} else if (OrderStatusEnum.TO_BE_SIGNED == oldOrder.getStatus()) {
					if (oldOrder.getPayWay() == PayWayEnum.PAY_ONLINE) {
						tradeOrder.setStatus(OrderStatusEnum.REFUSING);
					} else {
						tradeOrder.setStatus(OrderStatusEnum.REFUSED);
					}
					// Begin 重构4.1 add by wusw 20160729
					// 设置短信拒绝订单原因
					oldOrder.setReason(tradeOrder.getReason());
					// End 重构4.1 add by wusw 20160729
				} else if (OrderStatusEnum.UNPAID == oldOrder.getStatus()) {
					// 未支付订单变成已取消
					tradeOrder.setStatus(OrderStatusEnum.CANCELED);
				} else {
					// Begin 重构4.1 update by wusw 20160816
					throw new Exception(ORDER_STATUS_CHANGE);
					// End 重构4.1 update by wusw 20160816
				}

				if (tradeOrder.getActivityType() == ActivityTypeEnum.VONCHER) {
					// 释放所有代金卷
					activityCouponsRecordService.updateUseStatus(tradeOrder.getId());
				} else if (tradeOrder.getActivityType() == ActivityTypeEnum.GROUP_ACTIVITY) {
					// 团购活动释放限购数量
					activityGroupRecordService.updateDisabledByOrderId(tradeOrder.getId());
				} else if (tradeOrder.getActivityType() == ActivityTypeEnum.SECKILL_ACTIVITY) {
					// 秒杀活动释放购买记录
					activitySeckillRecordService.updateStatusBySeckillId(tradeOrder.getId());
				}
				if (tradeOrder.getTradeOrderItem() == null) {
					tradeOrder.setTradeOrderItem(tradeOrderItemMapper.selectTradeOrderItem(tradeOrder.getId()));
				}
				// 特惠活动释放限购数量
				for (TradeOrderItem item : tradeOrder.getTradeOrderItem()) {
					Map<String, Object> params = Maps.newHashMap();
					params.put("orderId", item.getOrderId());
					params.put("storeSkuId", item.getStoreSkuId());
					activitySaleRecordService.updateDisabledByOrderId(params);
				}

				// 更新订单状态
				Integer alterCount = this.updateOrderStatus(tradeOrder);
				if (alterCount <= 0) {
					// Begin 重构4.1 update by wusw 20160816
					throw new Exception(ORDER_STATUS_CHANGE_ID + tradeOrder.getOrderNo());
					// End 重构4.1 update by wusw 20160816
				}
				// 退款
				this.tradeOrderPayService.cancelOrderPay(tradeOrder);

				// 添加日志
				// if (tradeOrder.getTradeOrderLog() != null) {
				// tradeOrderLogMapper.insertSelective(tradeOrder.getTradeOrderLog());
				// }
				// Begin 1.0.Z 增加订单操作记录 add by zengj
				tradeOrderLogService.insertSelective(new TradeOrderLog(tradeOrder.getId(), operator, tradeOrder
						.getStatus().getName(), tradeOrder.getStatus().getValue()));
				// End 1.0.Z 增加订单操作记录 add by zengj

				// 发送短信
				if (OrderStatusEnum.DROPSHIPPING == oldOrder.getStatus()
						|| OrderStatusEnum.TO_BE_SIGNED == oldOrder.getStatus()) {
					tradeMessageService.sendSmsByCancel(oldOrder, oldOrder.getStatus());
				}

				// 回收库存
				List<StockAdjustVo> stockAdjustList = recycleStockInfo(tradeOrder, rpcIdList);
				// added by maojj 给ERP发消息去生成出入库单据
				// stockMQProducer.sendMessage(stockAdjustList);
			}
		} catch (Exception e) {
			// added by maojj 通知回滚库存修改
			rollbackMQProducer.sendStockRollbackMsg(rpcIdList);
			throw e;
		}
	}

	// End modified by maojj 2016-07-26

	/**
	 * 获取库存操作类型
	 * 
	 * @param statusType
	 *            订单状态
	 * @param isActivity
	 *            是否活动商品
	 */
	private StockOperateEnum getStockOperateType(OrderStatusEnum statusType, boolean isActivity) {
		boolean isCancelOrder = false;
		if (OrderStatusEnum.CANCELING == statusType || OrderStatusEnum.CANCELED == statusType) {
			isCancelOrder = true;
		}
		if (isCancelOrder) {
			if (isActivity) {
				// 活动商品 取消
				return StockOperateEnum.ACTIVITY_CANCEL_ORDER;
			} else {
				// 非活动商品 取消
				return StockOperateEnum.CANCEL_ORDER;
			}
		} else {
			if (isActivity) {
				// 活动商品 拒收
				return StockOperateEnum.ACTIVITY_REFUSED_SIGN;
			} else {
				// 非活动商品 拒收
				return StockOperateEnum.REFUSED_SIGN;
			}
		}
	}

	// Begin modified by maojj 2016-07-06
	/**
	 * 修改库存JSON
	 * 
	 * @param tradeOrder
	 *            订单
	 */
	private List<StockAdjustVo> recycleStockInfo(TradeOrder tradeOrder, List<String> rpcIdList) throws Exception {
		List<StockAdjustVo> stockAdjustList = new ArrayList<StockAdjustVo>();

		List<TradeOrderItem> tradeOrderItems = tradeOrder.getTradeOrderItem();
		if (tradeOrderItems == null) {
			tradeOrderItems = tradeOrderItemMapper.selectTradeOrderItem(tradeOrder.getId());
		}
		StockAdjustVo stockAdjustVo = new StockAdjustVo();
		// Begin added by maojj 2016-07-26
		String rpcId = UuidUtils.getUuid();
		rpcIdList.add(rpcId);
		stockAdjustVo.setRpcId(rpcId);
		// End added by maojj 2016-07-26

		stockAdjustVo.setOrderId(tradeOrder.getId());
		stockAdjustVo.setOrderNo(tradeOrder.getOrderNo());
		stockAdjustVo.setOrderResource(tradeOrder.getOrderResource());
		stockAdjustVo.setOrderType(tradeOrder.getType());
		stockAdjustVo.setStoreId(tradeOrder.getStoreId());

		// end add by wangf01 2016.08.06
		stockAdjustVo.setStockOperateEnum(getStockOperateType(tradeOrder.getStatus(), Boolean.FALSE));
		stockAdjustVo.setUserId(tradeOrder.getUserId());
		for (TradeOrderItem item : tradeOrderItems) {
			// 判断是否是团购和特惠商品
			boolean isGoodActivity = ActivityTypeEnum.GROUP_ACTIVITY == tradeOrder.getActivityType()
					|| isAttendSale(tradeOrder.getId(), item.getStoreSkuId());

			// begin add by wushp 如果秒杀活动已经结束，则当成普通商品
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
			// end add by wushp
			// begin add by wangf01 2016.08.06
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
			stockManagerService.updateStock(stockAdjustVo);

			stockAdjustList.add(stockAdjustVo);
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
	 * 订单商品是否参与特惠活动
	 */
	public String findSaleId(String orderId, String storeGoodSkuId) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("orderId", orderId);
		map.put("saleGoodsId", storeGoodSkuId);
		String saleId = activitySaleRecordService.selectOrderGoodsActivity(map);
		return saleId;
	}

	/**
	 * 构建服务型订单库存VO
	 * 
	 * @author zengj
	 * @param tradeOrder
	 *            订单
	 * @param stockOperateEnum
	 *            库存操作枚举
	 * @param adjustGoodsNum
	 *            调整商品数量，如为null,取下单时的商品数量，消费时是一件件商品消费，所以需要一件件来调整库存
	 * @return StockAdjustVo 库存VO
	 */
	private StockAdjustVo buildServiceOrderStock(TradeOrder tradeOrder, StockOperateEnum stockOperateEnum,
			Integer adjustGoodsNum, List<String> rpcIdList) {
		StockAdjustVo stockAdjustVo = new StockAdjustVo();
		// Begin added by maojj 2016-07-26
		String rpcId = UuidUtils.getUuid();
		rpcIdList.add(rpcId);
		stockAdjustVo.setRpcId(rpcId);
		// End added by maojj 2016-07-26
		// 订单ID
		stockAdjustVo.setOrderId(tradeOrder.getId());
		stockAdjustVo.setOrderNo(tradeOrder.getOrderNo());
		stockAdjustVo.setOrderResource(tradeOrder.getOrderResource());
		stockAdjustVo.setOrderType(tradeOrder.getType());
		// 店铺ID
		stockAdjustVo.setStoreId(tradeOrder.getStoreId());
		// 库存操作枚举
		stockAdjustVo.setStockOperateEnum(stockOperateEnum);
		// 操作用户ID
		stockAdjustVo.setUserId(tradeOrder.getUserId());
		// 库存调整详情VO
		List<AdjustDetailVo> adjustDetailList = Lists.newArrayList();
		// 订单项信息
		List<TradeOrderItem> tradeOrderItems = tradeOrder.getTradeOrderItem();
		// 如果订单项为空，手动查询下订单项信息
		if (tradeOrderItems == null) {
			tradeOrderItems = tradeOrderItemMapper.selectTradeOrderItem(tradeOrder.getId());
		}

		// 循环订单项信息
		for (TradeOrderItem item : tradeOrderItems) {
			AdjustDetailVo detail = new AdjustDetailVo();
			detail.setStoreSkuId(item.getStoreSkuId());
			detail.setGoodsSkuId("");
			detail.setMultipleSkuId("");
			detail.setGoodsName(item.getSkuName());
			detail.setPrice(item.getUnitPrice());
			detail.setPropertiesIndb(item.getPropertiesIndb());
			detail.setStyleCode(item.getStyleCode());
			detail.setBarCode(item.getBarCode());
			// 调整商品库存数量,如果有传调整数量，取传递过来的，否则取订单下单时的商铺数量
			detail.setNum(adjustGoodsNum == null || adjustGoodsNum < 1 ? item.getQuantity() : adjustGoodsNum);
			adjustDetailList.add(detail);
		}
		stockAdjustVo.setAdjustDetailList(adjustDetailList);
		return stockAdjustVo;
	}

	/**
	 * 确认收货并发送消息(快送同步)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateWithConfirm(TradeOrder tradeOrder) throws Exception {
		JSONObject json = new JSONObject();
		json.put("id", tradeOrder.getId());
		json.put("operator", tradeOrder.getUpdateUserId());
		json.put("isBuyerOperator", true);
		StoreInfo storeInfo = getStoreInfo(tradeOrder.getStoreId());
		Message msg = new Message(getTopicByStoreType(storeInfo.getType()), TAG_ORDER_CONFIRM, json.toString()
				.getBytes(Charsets.UTF_8));
		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, tradeOrder,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object entity) {
						try {
							updateWithConfirm(entity);
							return LocalTransactionState.COMMIT_MESSAGE;
						} catch (Exception e) {
							logger.error("执行确认收货失败", e);
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

	// Begin modified by maojj 2016-07-26 添加分布式事务处理机制
	/**
	 * 确认收货
	 *
	 * @param entity
	 *            订单对象
	 */
	@Transactional(rollbackFor = Exception.class)
	private void updateWithConfirm(Object entity) throws Exception {
		String rpcId = null;
		try {
			if (entity instanceof TradeOrder) {
				TradeOrder tradeOrder = (TradeOrder) entity;
				StockAdjustVo stockAdjustVo = null;
				// 更新订单状态
				Integer alterCount = this.updateOrderStatus(tradeOrder);
				if (alterCount <= 0) {
					throw new Exception("操作异常，订单状态已经改变：订单号：" + tradeOrder.getOrderNo());
				}

				if (CollectionUtils.isEmpty(tradeOrder.getTradeOrderItem())) {
					tradeOrder.setTradeOrderItem(tradeOrderItemMapper.selectTradeOrderItem(tradeOrder.getId()));
				}
				// 给卖家打款
				this.tradeOrderPayService.confirmOrderPay(tradeOrder);

				// begin update by wushp
				if (tradeOrder.getType().ordinal() == 2) {
					// 服务店订单，没有售后时间，确认订单完成即送积分
					pointsBuriedService.doConsumePoints(tradeOrder.getUserId(), tradeOrder.getActualAmount());
				} else {
					// 赠送积分计时消息
					if (ActivityTypeEnum.GROUP_ACTIVITY == tradeOrder.getActivityType()) {
						tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_finish_group_timeout,
								tradeOrder.getId());
					} else {
						tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_finish_timeout, tradeOrder.getId());
					}
				}
				// end update by wushp

				// 自动评价计时消息
				tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_finish_evaluate_timeout, tradeOrder.getId());

				// 添加日志
				if (tradeOrder.getTradeOrderLog() != null) {
					tradeOrderLogMapper.insertSelective(tradeOrder.getTradeOrderLog());
				}

				// Begin 1.0.Z 增加订单操作记录 add by zengj
				tradeOrderLogService.insertSelective(new TradeOrderLog(tradeOrder.getId(),
						tradeOrder.getUpdateUserId(), tradeOrder.getStatus().getName(), tradeOrder.getStatus()
								.getValue()));

				// 锁定库存
				// Begin modified by maojj 2016-07-26
				rpcId = UuidUtils.getUuid();
				stockAdjustVo = buildDeliveryStock(tradeOrder, rpcId);
				stockManagerService.updateStock(stockAdjustVo);
				// End modified by maojj 2016-07-26

				// 订单完成后同步到商业管理系统
				tradeOrderCompleteProcessService.orderCompleteSyncToJxc(tradeOrder.getId());
				// End 1.0.Z 增加订单操作记录 add by zengj

				// added by maojj 给ERP发消息去生成出入库单据
				// stockMQProducer.sendMessage(stockAdjustVo);

			}
		} catch (Exception e) {
			// added by maojj 通知回滚库存修改
			rollbackMQProducer.sendStockRollbackMsg(rpcId);
			throw e;
		}
	}

	// End modified by maojj 2016-07-26

	/**
	 * @desc 用户拒收并发送消息(快送同步)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateWithUserRefuse(TradeOrder tradeOrder) throws Exception {
		final JSONObject json = new JSONObject();
		json.put("id", tradeOrder.getId());
		json.put("reason", tradeOrder.getReason());
		json.put("operator", tradeOrder.getUpdateUserId());
		json.put("isBuyerOperator", false);
		StoreInfo storeInfo = getStoreInfo(tradeOrder.getStoreId());
		Message msg = new Message(getTopicByStoreType(storeInfo.getType()), TAG_ORDER_REFUSE_RECEIVE, json.toString()
				.getBytes(Charsets.UTF_8));
		// 发送事务消息
		TransactionSendResult sendResult = rocketMQTransactionProducer.send(msg, tradeOrder,
				new LocalTransactionExecuter() {

					@Override
					public LocalTransactionState executeLocalTransactionBranch(Message msg, Object object) {
						try {
							updateCancelOrder(object, json.optString("operator"));
						} catch (Exception e) {
							// Begin 重构4.1 update by wusw 20160816
							logger.error(ORDER_EXECUTE_REFUSE_FAIL, e);
							// End 重构4.1 update by wusw 20160816
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateWithComplete(TradeOrder order) throws Exception {

		if (order.getTradeOrderItem() == null) {
			order.setTradeOrderItem(tradeOrderItemMapper.selectTradeOrderItem(order.getId()));
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("orderNo", order.getOrderNo());
		List<TradeOrderRefunds> list = tradeOrderRefundsMapper.selectByParams(params);
		// 判断无售后
		if (list == null || Iterables.isEmpty(list)) {
			if (order.getPayWay() == PayWayEnum.PAY_ONLINE) {
				pointsBuriedService.doConsumePoints(order.getUserId(), order.getActualAmount());
			}
		}
		// 更新余额
		if (tradeOrderPayService.isServiceAssurance(order)) {
			tradeOrderPayService.updateBalanceByFinish(order);
		}
		order.setIsComplete(OrderComplete.YES);
		updateOrderStatus(order);
	}

	private StoreInfo getStoreInfo(String storeId) throws ServiceException {
		return storeInfoService.getStoreBaseInfoById(storeId);
	}

	/**
	 * 根据店铺类型获取TOPIC
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
				// Begin 重构4.1 add by wusw
			case SERVICE_STORE:
				return TOPIC_ORDER_SERVICE;
				// End 重构4.1 add by wusw
			default:
				break;
		}
		return null;
	}

	/**
	 * 更新订单支付信息
	 * 
	 * @author yangq
	 */
	@Transactional(rollbackFor = Exception.class)
	public void update(TradeOrder tradeOrder) throws Exception {

		if (tradeOrder.getTradeOrderPay() != null) {
			TradeOrderPay orderPay = new TradeOrderPay();
			orderPay = tradeOrder.getTradeOrderPay();
			int count = tradeOrderPayMapper.selectTradeOrderPayByOrderId(tradeOrder.getId());
			if (count == 0) {
				tradeOrderPayMapper.insertSelective(orderPay);
			}
		}

	}

	// Begin modified by maojj 2016-07-26 添加分布式事务处理机制
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateOrderShipment(TradeOrderOperateParamVo param) throws ServiceException, Exception {
		String rpcId = null;
		try {
			// 根据订单ID查询出订单信息
			TradeOrder tradeOrder = this.tradeOrderMapper.selectTradeDetailInfoById(param.getOrderId());
			// 判断订单是否存在
			if (tradeOrder == null || !tradeOrder.getStoreId().equals(param.getStoreId())) {
				// Begin 重构4.1 update by wusw 20160816
				// 如果订单不存在，抛出异常
				throw new ServiceException(ORDER_NOT_EXSITS_DELETE);
				// End 重构4.1 update by wusw 20160816
			}

			// 判断订单状态是否是待发货。只有待发货才能发货
			if (!OrderStatusEnum.DROPSHIPPING.equals(tradeOrder.getStatus())) {
				// Begin 重构4.1 update by wusw 20160816
				// 如果不是待发货状态，直接抛出异常
				throw new ServiceException(ORDER_STATUS_OVERDUE);
				// End 重构4.1 update by wusw 20160816
			}

			// 将实际库存-1 订单占用库存 -1
			// 发货不在修改库存，得订单完成后才有
			// StockAdjustVo stockAdjustVo = new StockAdjustVo();
			// // Begin added by maojj 2016-07-26
			// rpcId = UuidUtils.getUuid();
			// stockAdjustVo.setRpcId(rpcId);
			// // End added by maojj 2016-07-26
			// stockAdjustVo.setStoreId(tradeOrder.getStoreId());
			// stockAdjustVo.setUserId(tradeOrder.getUserId());
			// stockAdjustVo.setOrderId(tradeOrder.getId());
			// ActivityTypeEnum activityType = tradeOrder.getActivityType();
			// if (activityType != null) {
			// if (activityType.equals(ActivityTypeEnum.GROUP_ACTIVITY)) {
			// stockAdjustVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_SEND_OUT_GOODS);
			// } else {
			// stockAdjustVo.setStockOperateEnum(StockOperateEnum.SEND_OUT_GOODS);
			// }
			// }
			//
			// List<AdjustDetailVo> adjustDetailVos = new
			// ArrayList<AdjustDetailVo>();
			// AdjustDetailVo adjustDetailVo = null;
			// List<TradeOrderItem> orderItems = tradeOrder.getTradeOrderItem();
			// for (TradeOrderItem item : orderItems) {
			// adjustDetailVo = new AdjustDetailVo();
			// adjustDetailVo.setStoreSkuId(item.getStoreSkuId());
			// adjustDetailVo.setGoodsName(item.getSkuName());
			// adjustDetailVo.setBarCode(item.getBarCode());
			// adjustDetailVo.setStyleCode(item.getStyleCode());
			// adjustDetailVo.setPropertiesIndb(item.getPropertiesIndb());
			// adjustDetailVo.setNum(item.getQuantity());
			// // 下单时SKU的价格
			// adjustDetailVo.setPrice(item.getUnitPrice());
			// adjustDetailVos.add(adjustDetailVo);
			// }
			// stockAdjustVo.setAdjustDetailList(adjustDetailVos);

			// 修改订单状态为已发货
			tradeOrder.setStatus(OrderStatusEnum.TO_BE_SIGNED);
			// 修改最后修改时间
			tradeOrder.setUpdateTime(new Date());
			// 修改最后修改人
			tradeOrder.setUpdateUserId(param.getUserId());
			// 发货时间为当前时间
			tradeOrder.setDeliveryTime(new Date());
			// 更新订单信息
			this.updateOrderStatus(tradeOrder);

			// 判断是否有物流信息
			if (StringUtils.isNotBlank(param.getLogisticsCompanyName())) {
				// 有物流信息表示物流发货,需要更新物流信息到 订单收货地址信息表
				TradeOrderLogistics tradeOrderLogistics = tradeOrderLogisticsMapper.selectByOrderId(tradeOrder.getId());
				if (tradeOrderLogistics != null) {
					// 表示有物流
					tradeOrderLogistics.setType(LogisticsType.HAS);
					// 物流公司
					tradeOrderLogistics.setLogisticsCompanyName(param.getLogisticsCompanyName());
					// 物流单号
					tradeOrderLogistics.setLogisticsNo(param.getLogisticsNo());

					// 更新物流信息
					tradeOrderLogisticsMapper.updateByPrimaryKeySelective(tradeOrderLogistics);
				}
			}

			// 保存订单操作日志
			// tradeOrderLogMapper.insertSelective(getTradeOrderLog(param,
			// OrderStatusEnum.TO_BE_SIGNED));

			// Begin 1.0.Z 增加订单操作记录 add by zengj
			tradeOrderLogService.insertSelective(new TradeOrderLog(tradeOrder.getId(), param.getUserId(), tradeOrder
					.getStatus().getName(), tradeOrder.getStatus().getValue()));
			// End 1.0.Z 增加订单操作记录 add by zengj

			// 调整库存 Tips:发货不再修改库存，等订单完成才会
			// this.stockManagerService.updateStock(stockAdjustVo);

			// 获取店铺信息
			StoreInfo storeInfo = storeInfoService.findById(tradeOrder.getStoreId());

			// 发送计时消息
			// Begin 重构4.1 add by wusw 20160801
			if (storeInfo.getType() == StoreTypeEnum.SERVICE_STORE) {
				Date serviceTime = DateUtils.parseDate(tradeOrder.getPickUpTime(), "yyyy-MM-dd HH:mm");
				// 服务店订单，预约服务时间过后24小时未派单的自动确认收货
				// tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_confirm_server_timeout,
				// tradeOrder.getId(),
				// DateUtils.addHours(serviceTime, 24).getTime());
				// Begin V1.0.3修改自动确认收货期限为7天 update by wusw 20160901
				tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_confirm_server_timeout, tradeOrder.getId(),
						(DateUtils.addHours(serviceTime, 24 * 7).getTime() - DateUtils.getSysDate().getTime()) / 1000);
				// End V1.0.3修改自动确认收货期限为7天 update by wusw 20160901
				// 服务店派单发送短信
				tradeMessageService.sendSmsByServiceStoreShipments(tradeOrder);
			} else {// End 重构4.1 add by wusw 20160801
				if (ActivityTypeEnum.GROUP_ACTIVITY == tradeOrder.getActivityType()) {
					tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_confirm_group_timeout, tradeOrder.getId());
				} else {
					tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_confirm_timeout, tradeOrder.getId());
				}
			}

			// 只有便利店发货才需要短信
			if (StoreTypeEnum.CLOUD_STORE.equals(storeInfo.getType())) {
				// 发送短信
				tradeMessageService.sendSmsByShipments(tradeOrder);
			}

			// added by maojj 给ERP发消息去生成出入库单据
			// stockMQProducer.sendMessage(stockAdjustVo);
		} catch (Exception e) {
			// added by maojj 通知回滚库存修改
			// rollbackMQProducer.sendStockRollbackMsg(rpcId);
			throw e;
		}
	}

	// End modified by maojj 2016-07-26

	/**
	 * 获取订单操作日志
	 * 
	 * @author zengj
	 * @param param
	 * @return
	 */
	private TradeOrderLog getTradeOrderLog(TradeOrderOperateParamVo param, OrderStatusEnum orderStatusEnum) {
		// 订单操作日志信息
		TradeOrderLog tradeOrderLog = new TradeOrderLog();
		tradeOrderLog.setId(UuidUtils.getUuid());
		tradeOrderLog.setOperate(orderStatusEnum.getValue() + "---" + orderStatusEnum.getName());
		tradeOrderLog.setOperateUser(param.getUserId());
		tradeOrderLog.setRecordTime(new Date());
		tradeOrderLog.setOrderId(param.getOrderId());
		return tradeOrderLog;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer updateOrderStatus(TradeOrder tradeOrder) throws ServiceException {
		return tradeOrderMapper.updateOrderStatus(tradeOrder);
	}

	@Override
	public PageUtils<TradeOrder> getTradeOrderByParams(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrder> list = tradeOrderMapper.getTradeOrderByParams(map);
		return new PageUtils<TradeOrder>(list);
	}

	@Override
	public Integer getTradeOrderCount(Map<String, Object> map) {
		return tradeOrderMapper.getTradeOrderCount(map);
	}

	@Override
	public List<TradeOrderItem> getTradeOrderItems(Map<String, Object> map) throws ServiceException {
		List<TradeOrderItem> items = tradeOrderMapper.selectTraderOrderList(map);
		return items;
	}

	@Override
	public List<TradeOrderItem> findTradeOrderItems(Map<String, Object> map) throws ServiceException {
		List<TradeOrderItem> items = tradeOrderMapper.findTraderOrderList(map);
		return items;
	}

	@Override
	public PageUtils<TradeOrder> getOnlineTradeOrderList(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrder> list = tradeOrderMapper.getTradeOrderByParams(map);
		/*
		 * if (list != null && list.size() > 0) { for (TradeOrder order : list)
		 * { if (StringUtils.isNotEmpty(order.getActivityId()) &&
		 * !"0".equals(order.getActivityId())) { if
		 * (order.getActivityType().equals(ActivityTypeEnum.
		 * FULL_REDUCTION_ACTIVITIES)) { // 满减活动 ActivityDiscount
		 * activityDiscount = activityDiscountMapper
		 * .selectByPrimaryKey(order.getActivityId()); if (activityDiscount !=
		 * null && "0".equals(activityDiscount.getStoreId())) { // 所属店铺 // 运营商类型
		 * // 没有优惠 order.setPreferentialPrice(null); } } } else {
		 * order.setPreferentialPrice(null); } } }
		 */
		return new PageUtils<TradeOrder>(list);
	}

	@Override
	public TradeOrder getOnlineOrderDetail(String orderId) throws ServiceException {
		// 订单、支付、发票信息
		TradeOrder tradeOrder = tradeOrderMapper.selectOrderPayInvoiceById(orderId);

		// 活动名称
		String activityName = "";
		// 如果有活动ID，说明该订单参与了活动
		if (StringUtils.isNotEmpty(tradeOrder.getActivityId()) && !"0".equals(tradeOrder.getActivityId())) {
			// 代金券活动
			if (ActivityTypeEnum.VONCHER.equals(tradeOrder.getActivityType())) {
				ActivityCollectCoupons activityCollectCoupons = activityCollectCouponsService.get(tradeOrder
						.getActivityId());
				activityName = activityCollectCoupons.getName();
			} else if (ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES.equals(tradeOrder.getActivityType())) {
				// 满减活动
				ActivityDiscount activityDiscount = activityDiscountMapper.selectByPrimaryKey(tradeOrder
						.getActivityId());
				activityName = activityDiscount.getName();
			}
		}

		// 店铺基本信息
		StoreInfo storeInfo = storeInfoService.selectByPrimaryKey(tradeOrder.getStoreId());

		// 订单物流信息
		TradeOrderLogistics tradeOrderlogistics = tradeOrderLogisticsMapper.selectByOrderId(orderId);

		// 交易项信息（包括商品基本信息）
		List<TradeOrderItem> tradeOrderItemList = tradeOrderItemMapper.selectOrderItemDetailById(orderId);

		tradeOrder.setActivityName(activityName);
		tradeOrder.setStoreName(storeInfo.getStoreName());
		tradeOrder.setStoreMobile(storeInfo.getMobile());
		tradeOrder.setStoreType(storeInfo.getType());
		tradeOrder.setTradeOrderLogistics(tradeOrderlogistics);
		tradeOrder.setTradeOrderItem(tradeOrderItemList);

		return tradeOrder;
	}

	@Override
	public PageUtils<TradeOrderVo> selectWXAppOrderInfo(Map<String, Object> map, int pageSize, int pageNumber)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrderVo> list = tradeOrderMapper.selectOrderInfoByUserId(map);
		if (list != null && !list.isEmpty()) {
			for (TradeOrderVo tradeOrderVo : list) {
				// 查询订单项表。
				tradeOrderVo.setTradeOrderItem(tradeOrderItemMapper.selectTradeOrderItem(tradeOrderVo.getId()));
				// 如果有活动ID，说明该订单参与了活动
				if (StringUtils.isNotBlank(tradeOrderVo.getActivityId()) && !"0".equals(tradeOrderVo.getActivityId())) {
					// 代金券活动
					if (ActivityTypeEnum.VONCHER.equals(tradeOrderVo.getActivityType())) {
						ActivityCollectCoupons activityCollectCoupons = activityCollectCouponsService.get(tradeOrderVo
								.getActivityId());
						if (activityCollectCoupons != null) {
							tradeOrderVo.setActivityName(activityCollectCoupons.getName());
						}
					} else if (ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES.equals(tradeOrderVo.getActivityType())) {
						// 满减活动
						ActivityDiscount activityDiscount = activityDiscountMapper.selectByPrimaryKey(tradeOrderVo
								.getActivityId());
						if (activityDiscount != null) {
							tradeOrderVo.setActivityName(activityDiscount.getName());
						}
					} else if (ActivityTypeEnum.GROUP_ACTIVITY.equals(tradeOrderVo.getActivityType())) {
						// 团购活动
						ActivityGroup activityGroup = activityGroupService.selectByPrimaryKey(tradeOrderVo
								.getActivityId());
						if (activityGroup != null) {
							tradeOrderVo.setActivityName(activityGroup.getName());
						}
					} else if (ActivityTypeEnum.SALE_ACTIVITIES.equals(tradeOrderVo.getActivityType())) {
						// 特惠活动
						ActivitySale activitySale = activitySaleService.get(tradeOrderVo.getActivityId());
						if (activitySale != null) {
							tradeOrderVo.setActivityName(activitySale.getName());
						}
					}
				}
			}
		}
		return new PageUtils<TradeOrderVo>(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.okdeer.mall.trade.order.serivce.TradeOrderService#getWXOrderDetail(
	 * java.lang.String)
	 */
	@Override
	public TradeOrderVo getWXOrderDetail(String orderId) {
		TradeOrderVo tradeOrderVo = tradeOrderMapper.getOrderDetail(orderId);
		if (tradeOrderVo != null) {
			// 订单状态已完成后需要查询订单评论信息
			if (OrderStatusEnum.HAS_BEEN_SIGNED.equals(tradeOrderVo.getStatus())) {
				Map<String, Object> map = new HashMap<String, Object>();
				if (tradeOrderVo.getTradeOrderItemVoList() != null) {
					for (TradeOrderItemVo item : tradeOrderVo.getTradeOrderItemVoList()) {
						map.put("orderItemId", item.getId());
						item.setTradeOrderCommentVoList(tradeOrderCommentMapper.selectByParams(map));
						map.clear();

						// 判断该订单项是否已申请售后
						int refundsItemCount = tradeOrderRefundsItemMapper.selectCountOrderItemId(item.getId());
						// 订单状态为交易完成、订单未完成、订单项有服务保障且未申请过售后，则可以申请售后
						if (OrderComplete.NO.equals(tradeOrderVo.getIsComplete()) && item.getServiceAssurance() > 0
								&& refundsItemCount <= 0) {
							item.setIsApplyRefund("1");
						} else {
							item.setIsApplyRefund("0");
						}
					}
				}
				map.clear();
				map.put("orderId", orderId);
				// 订单状态已完成后才能有投诉信息
				tradeOrderVo.setTradeOrderComplainVoList(tradeOrderComplainMapper.findOrderComplainByParams(orderId));
			}

			// 如果有活动ID，说明该订单参与了活动
			if (StringUtils.isNotBlank(tradeOrderVo.getActivityId()) && !"0".equals(tradeOrderVo.getActivityId())) {
				// 代金券活动
				if (ActivityTypeEnum.VONCHER.equals(tradeOrderVo.getActivityType())) {
					ActivityCollectCoupons activityCollectCoupons = activityCollectCouponsService.get(tradeOrderVo
							.getActivityId());
					if (activityCollectCoupons != null) {
						tradeOrderVo.setActivityName(activityCollectCoupons.getName());
					}
				} else if (ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES.equals(tradeOrderVo.getActivityType())) {
					// 满减活动
					ActivityDiscount activityDiscount = activityDiscountMapper.selectByPrimaryKey(tradeOrderVo
							.getActivityId());
					if (activityDiscount != null) {
						tradeOrderVo.setActivityName(activityDiscount.getName());
					}
				} else if (ActivityTypeEnum.GROUP_ACTIVITY.equals(tradeOrderVo.getActivityType())) {
					// 团购活动
					ActivityGroup activityGroup = activityGroupService.selectByPrimaryKey(tradeOrderVo.getActivityId());
					if (activityGroup != null) {
						tradeOrderVo.setActivityName(activityGroup.getName());
					}
				} else if (ActivityTypeEnum.SALE_ACTIVITIES.equals(tradeOrderVo.getActivityType())) {
					// 特惠活动
					ActivitySale activitySale = activitySaleService.get(tradeOrderVo.getActivityId());
					if (activitySale != null) {
						tradeOrderVo.setActivityName(activitySale.getName());
					}
				}
			}
			TradeOrderPay orderPay = tradeOrderPayMapper.selectByOrderId(orderId);
			if (orderPay != null) {
				tradeOrderVo.setTradeOrderPay(orderPay);
			}
		}
		return tradeOrderVo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.okdeer.mall.trade.order.serivce.TradeOrderService#
	 * selectWXUnpaidOrderInfo(java.util.Map, int, int)
	 */
	@Override
	public PageUtils<TradeOrderVo> selectWXUnpaidOrderInfo(Map<String, Object> map, int pageSize, int pageNumber)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrderVo> list = tradeOrderMapper.selectUnpaidOrderInfoByUserId(map);
		if (list != null && !list.isEmpty()) {
			for (TradeOrderVo tradeOrderVo : list) {
				// 查询订单项表。
				tradeOrderVo.setTradeOrderItem(tradeOrderItemMapper.selectTradeOrderItem(tradeOrderVo.getId()));
				// 如果有活动ID，说明该订单参与了活动
				if (StringUtils.isNotBlank(tradeOrderVo.getActivityId()) && !"0".equals(tradeOrderVo.getActivityId())) {
					// 代金券活动
					if (ActivityTypeEnum.VONCHER.equals(tradeOrderVo.getActivityType())) {
						ActivityCollectCoupons activityCollectCoupons = activityCollectCouponsService.get(tradeOrderVo
								.getActivityId());
						if (activityCollectCoupons != null) {
							tradeOrderVo.setActivityName(activityCollectCoupons.getName());
						}
					} else if (ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES.equals(tradeOrderVo.getActivityType())) {
						// 满减活动
						ActivityDiscount activityDiscount = activityDiscountMapper.selectByPrimaryKey(tradeOrderVo
								.getActivityId());
						if (activityDiscount != null) {
							tradeOrderVo.setActivityName(activityDiscount.getName());
						}
					} else if (ActivityTypeEnum.GROUP_ACTIVITY.equals(tradeOrderVo.getActivityType())) {
						// 团购活动
						ActivityGroup activityGroup = activityGroupService.selectByPrimaryKey(tradeOrderVo
								.getActivityId());
						if (activityGroup != null) {
							tradeOrderVo.setActivityName(activityGroup.getName());
						}
					} else if (ActivityTypeEnum.SALE_ACTIVITIES.equals(tradeOrderVo.getActivityType())) {
						// 特惠活动
						ActivitySale activitySale = activitySaleService.get(tradeOrderVo.getActivityId());
						if (activitySale != null) {
							tradeOrderVo.setActivityName(activitySale.getName());
						}
					}
				}
			}
		}
		return new PageUtils<TradeOrderVo>(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.okdeer.mall.trade.order.serivce.TradeOrderService#
	 * selectWXDropShippingOrderInfo(java. util .Map, int, int)
	 */
	@Override
	public PageUtils<TradeOrderVo> selectWXDropShippingOrderInfo(Map<String, Object> map, int pageSize, int pageNumber)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrderVo> list = tradeOrderMapper.selectDropShippingOrderInfoByUserId(map);
		if (list != null && !list.isEmpty()) {
			for (TradeOrderVo tradeOrderVo : list) {
				// 查询订单项表。
				tradeOrderVo.setTradeOrderItem(tradeOrderItemMapper.selectTradeOrderItem(tradeOrderVo.getId()));
				// 如果有活动ID，说明该订单参与了活动
				if (StringUtils.isNotBlank(tradeOrderVo.getActivityId()) && !"0".equals(tradeOrderVo.getActivityId())) {
					// 代金券活动
					if (ActivityTypeEnum.VONCHER.equals(tradeOrderVo.getActivityType())) {
						ActivityCollectCoupons activityCollectCoupons = activityCollectCouponsService.get(tradeOrderVo
								.getActivityId());
						if (activityCollectCoupons != null) {
							tradeOrderVo.setActivityName(activityCollectCoupons.getName());
						}
					} else if (ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES.equals(tradeOrderVo.getActivityType())) {
						// 满减活动
						ActivityDiscount activityDiscount = activityDiscountMapper.selectByPrimaryKey(tradeOrderVo
								.getActivityId());
						if (activityDiscount != null) {
							tradeOrderVo.setActivityName(activityDiscount.getName());
						}
					} else if (ActivityTypeEnum.GROUP_ACTIVITY.equals(tradeOrderVo.getActivityType())) {
						// 团购活动
						ActivityGroup activityGroup = activityGroupService.selectByPrimaryKey(tradeOrderVo
								.getActivityId());
						if (activityGroup != null) {
							tradeOrderVo.setActivityName(activityGroup.getName());
						}
					} else if (ActivityTypeEnum.SALE_ACTIVITIES.equals(tradeOrderVo.getActivityType())) {
						// 特惠活动
						ActivitySale activitySale = activitySaleService.get(tradeOrderVo.getActivityId());
						if (activitySale != null) {
							tradeOrderVo.setActivityName(activitySale.getName());
						}
					}
				}
			}
		}
		return new PageUtils<TradeOrderVo>(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.okdeer.mall.trade.order.serivce.TradeOrderService#
	 * selectWXToBeOrderInfo(java.util.Map, int, int)
	 */
	@Override
	public PageUtils<TradeOrderVo> selectWXToBeOrderInfo(Map<String, Object> map, int pageSize, int pageNumber)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrderVo> list = tradeOrderMapper.selectToBeOrderInfoByUserId(map);
		if (list != null && !list.isEmpty()) {
			for (TradeOrderVo tradeOrderVo : list) {
				// 查询订单项表。
				tradeOrderVo.setTradeOrderItem(tradeOrderItemMapper.selectTradeOrderItem(tradeOrderVo.getId()));
				// 如果有活动ID，说明该订单参与了活动
				if (StringUtils.isNotBlank(tradeOrderVo.getActivityId()) && !"0".equals(tradeOrderVo.getActivityId())) {
					// 代金券活动
					if (ActivityTypeEnum.VONCHER.equals(tradeOrderVo.getActivityType())) {
						ActivityCollectCoupons activityCollectCoupons = activityCollectCouponsService.get(tradeOrderVo
								.getActivityId());
						if (activityCollectCoupons != null) {
							tradeOrderVo.setActivityName(activityCollectCoupons.getName());
						}
					} else if (ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES.equals(tradeOrderVo.getActivityType())) {
						// 满减活动
						ActivityDiscount activityDiscount = activityDiscountMapper.selectByPrimaryKey(tradeOrderVo
								.getActivityId());
						if (activityDiscount != null) {
							tradeOrderVo.setActivityName(activityDiscount.getName());
						}
					} else if (ActivityTypeEnum.GROUP_ACTIVITY.equals(tradeOrderVo.getActivityType())) {
						// 团购活动
						ActivityGroup activityGroup = activityGroupService.selectByPrimaryKey(tradeOrderVo
								.getActivityId());
						if (activityGroup != null) {
							tradeOrderVo.setActivityName(activityGroup.getName());
						}
					} else if (ActivityTypeEnum.SALE_ACTIVITIES.equals(tradeOrderVo.getActivityType())) {
						// 特惠活动
						ActivitySale activitySale = activitySaleService.get(tradeOrderVo.getActivityId());
						if (activitySale != null) {
							tradeOrderVo.setActivityName(activitySale.getName());
						}
					}
				}
			}
		}
		return new PageUtils<TradeOrderVo>(list);
	}

	@Override
	public PageUtils<TradeOrder> posOrderReceivedList(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrder> list = tradeOrderMapper.posOrderReceivedList(map);
		return new PageUtils<TradeOrder>(list);
	}

	@Override
	public List<TradeOrderItem> orderReceivedDetail(Map<String, Object> map) throws ServiceException {
		List<TradeOrderItem> list = tradeOrderMapper.orderReceivedDetail(map);
		return list;
	}

	/**
	 * 统计店铺pos订单收支额
	 *
	 * @param start
	 * @param end
	 * @return
	 */
	public BigDecimal findCashCount(String storeId, Date start, Date end) {
		// pos订单
		BigDecimal posTotal = tradeOrderMapper.findPosCashCount(joinParams(storeId, start, end));
		// 货到付款
		BigDecimal onlineTotal = tradeOrderMapper.findOnlineCashCount(joinParams(storeId, start, end));
		// 退款单
		BigDecimal refundsTotal = tradeOrderRefundsMapper.findRefundCashCount(joinParams(storeId, start, end));
		// 现金计算： pos订单+货到付款—退款单
		BigDecimal total = posTotal.add(onlineTotal).subtract(refundsTotal);
		return total;
	}

	/**
	 * 店铺交班统计
	 *
	 * @param start
	 *            开始时间
	 * @param end
	 *            结束时间
	 * @param storeId
	 *            店铺ID
	 */
	public Map<String, BigDecimal> findShiftCount(String storeId, Date start, Date end) {
		// 订单数据统计
		PosShiftExchange posShiftExchange = tradeOrderMapper.findPosShiftExchangeByStoreId(storeId, start, end);
		logger.info("posShiftExchange:" + JSONObject.fromObject(posShiftExchange));
		// 退款订单统计
		PosShiftExchange posShiftExchangeRefund = tradeOrderRefundsMapper.findPosShiftExchangeByStoreId(storeId, start,
				end);
		logger.info("posShiftExchangeRefund:" + JSONObject.fromObject(posShiftExchangeRefund));

		Map<String, BigDecimal> shiftCount = Maps.newHashMap();
		// 线上订单统计
		// BigDecimal onlineTotal =
		// tradeOrderMapper.findOnlineSum(joinParams(storeId, start, end));
		shiftCount.put("onlineOrderTotal", posShiftExchange.getOnlineOrderTotal());
		// #线上支付退款统计
		// BigDecimal onlineRefundsTotal =
		// tradeOrderRefundsMapper.findOnlineSum(joinParams(storeId, start,
		// end));
		shiftCount.put("onlinePaymentRefund", posShiftExchangeRefund.getOnlinePaymentRefund());

		// 货到付款退款统计
		// BigDecimal cashDelieryTotal =
		// tradeOrderRefundsMapper.findCashDelierySum(joinParams(storeId, start,
		// end));
		BigDecimal cashDelieryTotal = posShiftExchangeRefund.getPayDeliveryRefund();
		shiftCount.put("payDeliveryRefund", cashDelieryTotal);

		// 货到付款统计
		// BigDecimal onlineCashTotal =
		// tradeOrderMapper.findOnlineCashCount(joinParams(storeId, start,
		// end));
		BigDecimal onlineCashTotal = posShiftExchange.getPayDelivery();
		shiftCount.put("payDelivery", onlineCashTotal);

		// 配送费总额统计
		// BigDecimal fareTotal =
		// tradeOrderMapper.findFareSum(joinParams(storeId, start, end));
		shiftCount.put("deliverFee", posShiftExchange.getDeliverFee());

		// pos销售总额统计
		// BigDecimal posTotal = tradeOrderMapper.findPosSum(joinParams(storeId,
		// start, end));
		shiftCount.put("posTotalSales", posShiftExchange.getPosTotalSales());

		// POS销售退款统计
		// BigDecimal posRefundTotal =
		// tradeOrderRefundsMapper.findPosSum(joinParams(storeId, start, end));
		shiftCount.put("posSalesRefund", posShiftExchangeRefund.getPosSalesRefund());
		// 退款单
		// BigDecimal refundsTotal =
		// tradeOrderRefundsMapper.findRefundCashCount(joinParams(storeId,
		// start, end));

		// 实际现金统计 (货到付款 - 货到付款退款 + pos销售总额 - 退款单)
		// BigDecimal total =
		// onlineCashTotal.subtract(cashDelieryTotal).add(posTotal).subtract(posRefundTotal);
		BigDecimal total = onlineCashTotal.subtract(cashDelieryTotal).add(posShiftExchange.getActualCashTotal())
				.subtract(posShiftExchangeRefund.getActualCashTotal());
		shiftCount.put("actualCashTotal", total);
		logger.info("店铺交班统计:" + JSONObject.fromObject(shiftCount));
		return shiftCount;
	}

	@Override
	public PosShiftExchange getPosShiftExchangeCount(PosShiftExchange posShiftExchange) {
		if (posShiftExchange != null && StringUtils.isNotBlank(posShiftExchange.getStoreId())) {
			logger.info("根据交班获取Pos交班统计  storeId:" + posShiftExchange.getStoreId() + "==loginTime:"
					+ posShiftExchange.getLoginTime());
			// 订单数据统计
			PosShiftExchange posShiftExchange2 = tradeOrderMapper.findPosShiftExchangeByStoreId(
					posShiftExchange.getStoreId(), posShiftExchange.getLoginTime(), new Date());
			// 退款订单统计
			PosShiftExchange posShiftExchangeRefund = tradeOrderRefundsMapper.findPosShiftExchangeByStoreId(
					posShiftExchange.getStoreId(), posShiftExchange.getLoginTime(), new Date());
			// 线上订单总额
			BigDecimal onlineOrderTotal = posShiftExchange2.getOnlineOrderTotal();
			// 线上支付退款
			BigDecimal onlinePaymentRefund = posShiftExchangeRefund.getOnlinePaymentRefund();
			// 货到付款退款
			BigDecimal payDeliveryRefund = posShiftExchangeRefund.getPayDeliveryRefund();
			// 货到付款总额
			BigDecimal payDelivery = posShiftExchange2.getPayDelivery();
			// 配送费总额
			BigDecimal deliverFee = posShiftExchange2.getDeliverFee();
			// pos销售总额
			BigDecimal posTotalSales = posShiftExchange2.getPosTotalSales();
			// pos销售退款
			BigDecimal posSalesRefund = posShiftExchangeRefund.getPosSalesRefund();
			// 实际现金总额
			BigDecimal actualCashTotal = payDelivery.subtract(payDeliveryRefund)
					.add(posShiftExchange2.getActualCashTotal()).subtract(posShiftExchangeRefund.getActualCashTotal());

			posShiftExchange.setOnlineOrderTotal(onlineOrderTotal);
			posShiftExchange.setOnlinePaymentRefund(onlinePaymentRefund);
			posShiftExchange.setPayDeliveryRefund(payDeliveryRefund);
			posShiftExchange.setPayDelivery(payDelivery);
			posShiftExchange.setDeliverFee(deliverFee);
			posShiftExchange.setPosTotalSales(posTotalSales);
			posShiftExchange.setPosSalesRefund(posSalesRefund);
			posShiftExchange.setActualCashTotal(actualCashTotal);
		}
		return posShiftExchange;
	}

	private Map<String, Object> joinParams(String storeId, Date start, Date end) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("storeId", storeId);
		params.put("start", start);
		params.put("end", end);
		return params;
	}

	/**
	 * 在线订单来源
	 * 
	 * @return List
	 */
	private List<Integer> getOnlineOrderResource() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(OrderResourceEnum.YSCAPP.ordinal());
		list.add(OrderResourceEnum.WECHAT.ordinal());
		return list;
	}

	/**
	 * pos订单来源
	 * 
	 * @return List
	 */
	private List<Integer> getPosOrderResource() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(OrderResourceEnum.POS.ordinal());
		return list;
	}

	/**
	 * 订单状态 （已发货和已签收）
	 * 
	 * @return List
	 */
	private List<Integer> getfinishStatus() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(OrderStatusEnum.TO_BE_SIGNED.ordinal());
		list.add(OrderStatusEnum.HAS_BEEN_SIGNED.ordinal());
		return list;
	}

	/**
	 * 退款订单状态（已退款和强制卖家退款）
	 * 
	 * @return List
	 */
	private List<Integer> getfinishRefundsStatus() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(RefundsStatusEnum.REFUND_SUCCESS.ordinal());
		list.add(RefundsStatusEnum.FORCE_SELLER_REFUND_SUCCESS.ordinal());
		return list;
	}

	/**
	 * 活动类型（平台）
	 * 
	 * @return List
	 */
	private List<Integer> getPlatformDiscountActivityType() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES.ordinal());
		list.add(ActivityTypeEnum.VONCHER.ordinal());
		return list;
	}

	/**
	 * 活动类型（店铺）
	 * 
	 * @return List
	 */
	private List<Integer> getDiscountActivityType() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES.ordinal());
		list.add(ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES.ordinal());
		list.add(ActivityTypeEnum.GROUP_ACTIVITY.ordinal());
		list.add(ActivityTypeEnum.SALE_ACTIVITIES.ordinal());
		return list;
	}

	/**
	 * 活动类型（代金券）
	 * 
	 * @return List
	 */
	private List<Integer> getVcheronActivityType() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(ActivityTypeEnum.VONCHER.ordinal());
		return list;
	}

	/**
	 * 获取统计条件map
	 * 
	 * @param parames
	 *            Map
	 * @return Map
	 */
	private Map<String, Object> getOrderMap(Map<String, Object> parames) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (parames.get("storeId") != null) {
			String storeId = parames.get("storeId").toString();
			map.put("storeId", storeId);
		}
		/*
		 * if (parames.get("sellerId") != null) { String sellerId =
		 * parames.get("sellerId").toString(); map.put("sellerId", sellerId); }
		 */
		if (parames.get("startTime") != null) {
			String startTime = parames.get("startTime").toString();
			map.put("startTime", startTime);
		}
		if (parames.get("endTime") != null) {
			String endTime = parames.get("endTime").toString();
			map.put("endTime", endTime);
		}
		return map;
	}

	/**
		 * 统计订单数量
		 * @author zengj
		 * @param json 组装的json对象
		 * @param params 查询条件
		 */
	private void statisticsOrderCount(JSONObject json, Map<String, Object> params) {
		// 统计订单数量
		Map<String, Object> result = tradeOrderMapper.selectOrderCount(params);
		// 线上订单数量
		int onlineOrderCount = 0;
		// 货到付款订单数量
		int deliveryOrderCount = 0;
		// POS订单数量
		int posOrderCount = 0;
		if (result != null) {
			// 线上订单数量
			onlineOrderCount = result.get("onlineOrderCount") == null ? 0 : Integer.valueOf(result.get(
					"onlineOrderCount").toString());
			// 货到付款订单数量
			deliveryOrderCount = result.get("deliveryOrderCount") == null ? 0 : Integer.valueOf(result.get(
					"deliveryOrderCount").toString());
			// POS订单数量
			posOrderCount = result.get("posOrderCount") == null ? 0 : Integer.valueOf(result.get("posOrderCount")
					.toString());
		}

		json.put("onlineOrderCount", onlineOrderCount); // 线上-订单数量
		json.put("deliveryOrderCount", deliveryOrderCount); // 货到付款-订单数量
		json.put("posOrderCount", posOrderCount); // pos销售-订单数量
		json.put("totalOrderCount", (onlineOrderCount + deliveryOrderCount + posOrderCount)); // 总订单数量
	}

	/**
	 * 统计订单金额
	 * @author zengj
	 * @param json 组装的json对象
	 * @param params 查询条件
	 */
	private void statisticsOrderIncome(JSONObject json, Map<String, Object> params) {
		/*
		 * 统计订单金额
		 */
		Map<String, Object> result = tradeOrderMapper.selectOrderIncome(params);
		// 线上订单金额
		BigDecimal onlineOrderAmount = BigDecimal.ZERO;
		// 货到付款订单金额
		BigDecimal deliveryOrderAmount = BigDecimal.ZERO;
		// POS订单金额
		BigDecimal posOrderAmount = BigDecimal.ZERO;
		// 订单总金额=线上订单金额+货到付款订单金额+POS订单金额
		BigDecimal totalOrderAmount = BigDecimal.ZERO;
		if (result != null) {
			// 线上订单金额
			onlineOrderAmount = result.get("onlineOrderAmount") == null ? BigDecimal.ZERO : new BigDecimal(result.get(
					"onlineOrderAmount").toString());
			// 货到付款订单金额
			deliveryOrderAmount = result.get("deliveryOrderAmount") == null ? BigDecimal.ZERO : new BigDecimal(result
					.get("deliveryOrderAmount").toString());
			// POS订单金额
			posOrderAmount = result.get("posOrderAmount") == null ? BigDecimal.ZERO : new BigDecimal(result.get(
					"posOrderAmount").toString());

			// 订单总金额=线上订单金额+货到付款订单金额+POS订单金额
			totalOrderAmount = onlineOrderAmount.add(deliveryOrderAmount).add(posOrderAmount);
		}

		json.put("onlineOrderAmount", moneyFormat(onlineOrderAmount)); // 线上-订单金额
		json.put("deliveryOrderAmount", moneyFormat(deliveryOrderAmount));// 货到付款-订单金额
		json.put("posOrderAmount", moneyFormat(posOrderAmount));// pos销售-订单金额
		json.put("totalOrderAmount", moneyFormat(totalOrderAmount)); // 总订单金额
	}

	/**
	 * 统计订单退款金额及需退款的优惠金额
	 * @author zengj
	 * @param json 组装的json对象
	 * @param params 查询条件
	 */
	private void statisticsRefundIncome(JSONObject json, Map<String, Object> params) {
		/*
		 * 统计退款金额
		 */
		Map<String, Object> result = tradeOrderMapper.selectRefundIncome(params);
		// 线上订单退款金额
		BigDecimal onlineRefundAmount = BigDecimal.ZERO;
		// 货到付款订单退款金额
		BigDecimal deliveryRefundAmount = BigDecimal.ZERO;
		// POS订单退款金额
		BigDecimal posRefundAmount = BigDecimal.ZERO;
		// 总退款金额=线上订单退款金额+货到付款订单退款金额+POS订单退款金额
		BigDecimal totalRefundAmount = BigDecimal.ZERO;

		// 在线订单平台 需退款的优惠金额（满减）
		BigDecimal onlineRefundPlatformPreferentialAmount = BigDecimal.ZERO;
		// 货到付款订单平台优惠金额（满减）
		BigDecimal deliveryRefundPlatformPreferentialAmount = BigDecimal.ZERO;
		// 在线订单代金券 需退款的优惠金额
		BigDecimal onlineRefundPlatformCouponAmount = BigDecimal.ZERO;
		// 货到付款订单代金券 需退款的优惠金额
		BigDecimal deliveryRefundPlatformCouponAmount = BigDecimal.ZERO;

		if (result != null) {
			// 线上订单退款金额
			onlineRefundAmount = result.get("onlineRefundAmount") == null ? BigDecimal.ZERO : new BigDecimal(result
					.get("onlineRefundAmount").toString());
			// 货到付款订单退款金额
			deliveryRefundAmount = result.get("deliveryRefundAmount") == null ? BigDecimal.ZERO : new BigDecimal(result
					.get("deliveryRefundAmount").toString());
			// POS订单退款金额
			posRefundAmount = result.get("posRefundAmount") == null ? BigDecimal.ZERO : new BigDecimal(result.get(
					"posRefundAmount").toString());
			// 总退款金额=线上订单退款金额+货到付款订单退款金额+POS订单退款金额
			totalRefundAmount = onlineRefundAmount.add(deliveryRefundAmount).add(posRefundAmount);

			// 在线订单平台 需退款的优惠金额（满减）
			onlineRefundPlatformPreferentialAmount = result.get("onlineRefundPlatformPreferentialAmount") == null ? BigDecimal.ZERO
					: new BigDecimal(result.get("onlineRefundPlatformPreferentialAmount").toString());
			// 货到付款订单平台优惠金额（满减）
			deliveryRefundPlatformPreferentialAmount = result.get("deliveryRefundPlatformPreferentialAmount") == null ? BigDecimal.ZERO
					: new BigDecimal(result.get("deliveryRefundPlatformPreferentialAmount").toString());
			// 在线订单代金券 需退款的优惠金额
			onlineRefundPlatformCouponAmount = result.get("onlineRefundPlatformCouponAmount") == null ? BigDecimal.ZERO
					: new BigDecimal(result.get("onlineRefundPlatformCouponAmount").toString());
			// 货到付款订单代金券 需退款的优惠金额
			deliveryRefundPlatformCouponAmount = result.get("deliveryRefundPlatformCouponAmount") == null ? BigDecimal.ZERO
					: new BigDecimal(result.get("deliveryRefundPlatformCouponAmount").toString());
		}

		json.put("onlineRefundAmount", moneyFormat(onlineRefundAmount)); // 线上-退款金额
		json.put("deliveryRefundAmount", moneyFormat(deliveryRefundAmount));// 货到付款-退款金额
		json.put("posRefundAmount", moneyFormat(posRefundAmount));// pos销售-退款金额
		json.put("totalRefundAmount", moneyFormat(totalRefundAmount)); // 总退款金额

		json.put("onlineRefundPlatformPreferentialAmount", moneyFormat(onlineRefundPlatformPreferentialAmount)); // 在线订单平台
																													// 需退款的优惠金额（满减）
		json.put("deliveryRefundPlatformPreferentialAmount", moneyFormat(deliveryRefundPlatformPreferentialAmount)); // 货到付款订单平台优惠金额（满减）
		json.put("onlineRefundPlatformCouponAmount", moneyFormat(onlineRefundPlatformCouponAmount)); // 在线订单代金券
																										// 需退款的优惠金额
		json.put("deliveryRefundPlatformCouponAmount", moneyFormat(deliveryRefundPlatformCouponAmount)); // 货到付款订单代金券
																											// 需退款的优惠金额
	}

	/**
	 * 统计订单代金券金额
	 * @author zengj
	 * @param json 组装的json对象
	 * @param params 查询条件
	 */
	private void statisticsCouponAmount(JSONObject json, Map<String, Object> params) {
		/*
		 * 统计代金券金额
		 */
		Map<String, Object> result = tradeOrderMapper.selectCouponAmount(params);
		// 线上订单代金券金额
		BigDecimal onlineCouponAmount = BigDecimal.ZERO;
		// 货到付款订单代金券金额
		BigDecimal deliveryCouponAmount = BigDecimal.ZERO;
		// 总的代金券金额=线上订单代金券金额+货到付款订单代金券金额
		BigDecimal totalCouponAmount = BigDecimal.ZERO;
		if (result != null) {
			// 线上订单代金券金额
			onlineCouponAmount = result.get("onlineCouponAmount") == null ? BigDecimal.ZERO : new BigDecimal(result
					.get("onlineCouponAmount").toString());
			// 货到付款订单代金券金额
			deliveryCouponAmount = result.get("deliveryCouponAmount") == null ? BigDecimal.ZERO : new BigDecimal(result
					.get("deliveryCouponAmount").toString());
			// 总的代金券金额=线上订单代金券金额+货到付款订单代金券金额
			totalCouponAmount = onlineCouponAmount.add(deliveryCouponAmount);
		}
		/*
		 * 需退款的代金券金额 begin
		 */
		// 线上订单 需退款的代金券金额
		BigDecimal onlineRefundPlatformCouponAmount = BigDecimal.valueOf(json.optDouble(
				"onlineRefundPlatformCouponAmount", 0));
		// 货到付款订单 需退款的代金券金额
		BigDecimal deliveryRefundPlatformCouponAmount = BigDecimal.valueOf(json.optDouble(
				"deliveryRefundPlatformCouponAmount", 0));
		/*
		 * 需退款的代金券金额 end
		 */
		json.put("onlineCouponAmount", moneyFormat(onlineCouponAmount.subtract(onlineRefundPlatformCouponAmount)));// 线上-代金劵金额
		json.put("deliveryCouponAmount", moneyFormat(deliveryCouponAmount.subtract(deliveryRefundPlatformCouponAmount)));// 货到付款-代金劵金额
		// 总代金劵金额，需要减掉线上订单 需退款的代金券金额和货到付款订单 需退款的代金券金额
		json.put("totalCouponAmount", moneyFormat(totalCouponAmount.subtract(onlineRefundPlatformCouponAmount)
				.subtract(deliveryRefundPlatformCouponAmount)));
	}

	/**
	 * 统计订单优惠金额
	 * @author zengj
	 * @param json 组装的json对象
	 * @param params 查询条件
	 */
	private void statisticsPreferentialAmount(JSONObject json, Map<String, Object> params) {
		/*
		 * 统计优惠金额
		 */
		Map<String, Object> result = tradeOrderMapper.selectPreferentialAmount(params);
		// 线上订单店铺优惠金额
		BigDecimal onlineStoreDiscount = BigDecimal.ZERO;
		// 货到付款订单店铺优惠金额
		BigDecimal deliveryStoreDiscount = BigDecimal.ZERO;
		// 总的店铺优惠金额=线上订单店铺优惠金额+货到付款订单店铺优惠金额
		BigDecimal totalStoreDiscount = BigDecimal.ZERO;
		// 线上订单平台优惠金额
		BigDecimal onlinePlatformDiscount = BigDecimal.ZERO;
		// 货到付款订单平台优惠金额
		BigDecimal deliveryPlatformDiscount = BigDecimal.ZERO;
		// 总的平台优惠金额=线上订单平台优惠金额+货到付款订单平台优惠金额
		BigDecimal totalPlatformDiscount = BigDecimal.ZERO;
		if (result != null) {
			// 线上订单店铺优惠金额
			onlineStoreDiscount = result.get("onlineStoreDiscount") == null ? BigDecimal.ZERO : new BigDecimal(result
					.get("onlineStoreDiscount").toString());
			// 货到付款订单店铺优惠金额
			deliveryStoreDiscount = result.get("deliveryStoreDiscount") == null ? BigDecimal.ZERO : new BigDecimal(
					result.get("deliveryStoreDiscount").toString());
			// 总的店铺优惠金额=线上订单店铺优惠金额+货到付款订单店铺优惠金额
			totalStoreDiscount = onlineStoreDiscount.add(deliveryStoreDiscount);

			// 线上订单平台优惠金额
			onlinePlatformDiscount = result.get("onlinePlatformDiscount") == null ? BigDecimal.ZERO : new BigDecimal(
					result.get("onlinePlatformDiscount").toString());
			// 货到付款订单平台优惠金额
			deliveryPlatformDiscount = result.get("deliveryPlatformDiscount") == null ? BigDecimal.ZERO
					: new BigDecimal(result.get("deliveryPlatformDiscount").toString());
			// 总的平台优惠金额=线上订单平台优惠金额+货到付款订单平台优惠金额
			totalPlatformDiscount = onlinePlatformDiscount.add(deliveryPlatformDiscount);
		}

		/*
		 * 需退款的优惠金额 begin
		 */
		// 线上订单 需退款的优惠金额
		BigDecimal onlineRefundPlatformPreferentialAmount = BigDecimal.valueOf(json.optDouble(
				"onlineRefundPlatformPreferentialAmount", 0));
		// 货到付款订单 需退款的优惠金额
		BigDecimal deliveryRefundPlatformPreferentialAmount = BigDecimal.valueOf(json.optDouble(
				"deliveryRefundPlatformPreferentialAmount", 0));
		// 线上订单 代金券金额
		BigDecimal onlineCouponAmount = BigDecimal.valueOf(json.optDouble("onlineCouponAmount", 0));
		// 货到付款订单 代金券金额
		BigDecimal deliveryCouponAmount = BigDecimal.valueOf(json.optDouble("deliveryCouponAmount", 0));
		/*
		 * 需退款的优惠金额 end
		 */

		json.put("onlineStoreDiscount", moneyFormat(onlineStoreDiscount));// 线上-店铺优惠金额
		json.put("deliveryStoreDiscount", moneyFormat(deliveryStoreDiscount));// 货到付款-店铺优惠金额
		json.put("totalStoreDiscount", moneyFormat(totalStoreDiscount)); // 总店铺优惠金额

		// 线上-平台优惠金额=线上订单平台优惠金额-需退款的平台优惠金额
		onlinePlatformDiscount = onlinePlatformDiscount.subtract(onlineRefundPlatformPreferentialAmount);
		// 货到付款-平台优惠金额=货到付款订单平台优惠金额-需退款的平台优惠金额
		deliveryPlatformDiscount = deliveryPlatformDiscount.subtract(deliveryRefundPlatformPreferentialAmount);
		json.put("onlinePlatformDiscount", moneyFormat(onlinePlatformDiscount));// 线上-平台优惠金额
		json.put("deliveryPlatformDiscount", moneyFormat(deliveryPlatformDiscount));// 货到付款-平台优惠金额
		// 总平台优惠金额，需要减掉线上订单平台优惠和货到付款订单平台优惠
		json.put(
				"totalPlatformDiscount",
				moneyFormat(totalPlatformDiscount.subtract(onlineRefundPlatformPreferentialAmount).subtract(
						deliveryRefundPlatformPreferentialAmount)));

		// 线上订单平台补贴=线上订单平台优惠+代金券
		BigDecimal onlinePlatformSubsidy = onlinePlatformDiscount.add(onlineCouponAmount);
		// 货到付款平台补贴=货到付款平台优惠+代金券
		BigDecimal deliveryPlatformSubsidy = deliveryPlatformDiscount.add(deliveryCouponAmount);
		// 总的平台补贴=总的平台优惠
		BigDecimal totalPlatformSubsidy = onlinePlatformSubsidy.add(deliveryPlatformSubsidy);
		json.put("onlinePlatformSubsidy", moneyFormat(onlinePlatformSubsidy));// 线上-平台补贴
		json.put("deliveryPlatformSubsidy", moneyFormat(deliveryPlatformSubsidy));// 货到付款-平台补贴
		json.put("totalPlatformSubsidy", moneyFormat(totalPlatformSubsidy));// 总平台补贴额
	}

	/**
	 * 统计配送费金额
	 * @author zengj
	 * @param json 组装的json对象
	 * @param params 查询条件
	 */
	private void statisticsFareAmount(JSONObject json, Map<String, Object> params) {
		/*
		 * 统计配送费
		 */
		Map<String, Object> result = tradeOrderMapper.selectFareAmount(params);
		// 线上订单配送费
		BigDecimal onlineFare = BigDecimal.ZERO;
		// 货到付款订单配送费
		BigDecimal deliveryFare = BigDecimal.ZERO;
		// 总配送费=线上订单配送费+货到付款订单配送费
		BigDecimal totalFare = BigDecimal.ZERO;
		if (result != null) {
			// 线上订单配送费
			onlineFare = result.get("onlineFare") == null ? BigDecimal.ZERO : new BigDecimal(result.get("onlineFare")
					.toString());
			// 货到付款订单配送费
			deliveryFare = result.get("deliveryFare") == null ? BigDecimal.ZERO : new BigDecimal(result.get(
					"deliveryFare").toString());
			// 总配送费=线上订单配送费+货到付款订单配送费
			totalFare = onlineFare.add(deliveryFare);
		}

		json.put("onlineFare", moneyFormat(onlineFare));// 线上-配送费
		json.put("deliveryFare", moneyFormat(deliveryFare));// 货到付款-配送费
		json.put("totalFare", moneyFormat(totalFare)); // 总配送费
	}

	/**
	 * 统计各种支付方式金额及实收
	 * @author zengj
	 * @param json 组装的json对象
	 * @param params 查询条件
	 */
	private void statisticsPayTypeAmount(JSONObject json, Map<String, Object> params) {
		/**
		 * 统计订单各种支付方式的实收
		 */
		Map<String, Object> result = tradeOrderMapper.selectOrderIncomeByPayType(params);
		// 线上订单余额支付金额
		BigDecimal yunPayAmount = BigDecimal.ZERO;
		// 线上订单支付宝支付金额
		BigDecimal aliPayAmount = BigDecimal.ZERO;
		// 线上订单微信支付金额
		BigDecimal weiPayAmount = BigDecimal.ZERO;
		// POS订单支付宝支付金额
		BigDecimal aliPosAmount = BigDecimal.ZERO;
		// POS订单微信支付金额
		BigDecimal weiPosAmount = BigDecimal.ZERO;
		// POS订单银联支付金额
		BigDecimal unionPosAmount = BigDecimal.ZERO;
		if (result != null) {
			// 线上订单余额支付金额
			yunPayAmount = result.get("yunPayAmount") == null ? BigDecimal.ZERO : new BigDecimal(result.get(
					"yunPayAmount").toString());
			// 线上订单支付宝支付金额
			aliPayAmount = result.get("aliPayAmount") == null ? BigDecimal.ZERO : new BigDecimal(result.get(
					"aliPayAmount").toString());
			// 线上订单微信支付金额
			weiPayAmount = result.get("weiPayAmount") == null ? BigDecimal.ZERO : new BigDecimal(result.get(
					"weiPayAmount").toString());
			// POS订单支付宝支付金额
			aliPosAmount = result.get("aliPosAmount") == null ? BigDecimal.ZERO : new BigDecimal(result.get(
					"aliPosAmount").toString());
			// POS订单微信支付金额
			weiPosAmount = result.get("weiPosAmount") == null ? BigDecimal.ZERO : new BigDecimal(result.get(
					"weiPosAmount").toString());
			// POS订单银联支付金额
			unionPosAmount = result.get("unionPosAmount") == null ? BigDecimal.ZERO : new BigDecimal(result.get(
					"unionPosAmount").toString());
		}

		/*
		 * 从JSON中取字段 begin
		 */
		// 线上订单金额
		BigDecimal onlineOrderAmount = BigDecimal.valueOf(json.optDouble("onlineOrderAmount", 0));
		// 线上退款金额
		BigDecimal onlineRefundAmount = BigDecimal.valueOf(json.optDouble("onlineRefundAmount", 0));
		// 货到付款订单金额
		BigDecimal deliveryOrderAmount = BigDecimal.valueOf(json.optDouble("deliveryOrderAmount", 0));
		// 货到付款订单退款金额
		BigDecimal deliveryRefundAmount = BigDecimal.valueOf(json.optDouble("deliveryRefundAmount", 0));
		// POS订单金额
		BigDecimal posOrderAmount = BigDecimal.valueOf(json.optDouble("posOrderAmount", 0));
		// POS退款金额
		BigDecimal posRefundAmount = BigDecimal.valueOf(json.optDouble("posRefundAmount", 0));
		// 订单总金额=线上订单金额+货到付款订单金额+POS订单金额
		BigDecimal totalOrderAmount = onlineOrderAmount.add(deliveryOrderAmount).add(posOrderAmount);
		// 总退款金额=线上订单退款金额+货到付款订单退款金额+POS订单退款金额
		BigDecimal totalRefundAmount = onlineRefundAmount.add(deliveryRefundAmount).add(posRefundAmount);
		// 货到付款订单平台优惠金额
		BigDecimal deliveryPlatformDiscount = BigDecimal.valueOf(json.optDouble("deliveryPlatformDiscount", 0));
		// 线上订单店铺优惠金额
		BigDecimal onlineStoreDiscount = BigDecimal.valueOf(json.optDouble("onlineStoreDiscount", 0));
		// 货到付款订单店铺优惠金额将
		BigDecimal deliveryStoreDiscount = BigDecimal.valueOf(json.optDouble("deliveryStoreDiscount", 0));
		/*
		 * 从JSON中取字段 end
		 */

		/**
		 * 统计退款单各支付方式的实退
		 */
		result = tradeOrderMapper.selectRefundIncomeByPayType(params);
		// 线上订单余额支付退款金额
		BigDecimal yunRefundAmount = BigDecimal.ZERO;
		// 线上订单支付宝支付退款金额
		BigDecimal aliRefundAmount = BigDecimal.ZERO;
		// 线上订单微信支付退款金额
		BigDecimal weiRefundAmount = BigDecimal.ZERO;
		// POS订单支付宝支付退款金额
		BigDecimal aliPosRefundAmount = BigDecimal.ZERO;
		// POS订单微信支付退款金额
		BigDecimal weiPosRefundAmount = BigDecimal.ZERO;
		// POS订单银联卡支付退款金额
		BigDecimal unionPosRefundAmount = BigDecimal.ZERO;
		if (result != null) {
			// 线上订单余额支付退款金额
			yunRefundAmount = result.get("yunRefundAmount") == null ? BigDecimal.ZERO : new BigDecimal(result.get(
					"yunRefundAmount").toString());
			// 线上订单支付宝支付退款金额
			aliRefundAmount = result.get("aliRefundAmount") == null ? BigDecimal.ZERO : new BigDecimal(result.get(
					"aliRefundAmount").toString());
			// 线上订单微信支付退款金额
			weiRefundAmount = result.get("weiRefundAmount") == null ? BigDecimal.ZERO : new BigDecimal(result.get(
					"weiRefundAmount").toString());

			// POS订单支付宝支付退款金额
			aliPosRefundAmount = result.get("aliPosRefundAmount") == null ? BigDecimal.ZERO : new BigDecimal(result
					.get("aliPosRefundAmount").toString());
			// POS订单微信支付退款金额
			weiPosRefundAmount = result.get("weiPosRefundAmount") == null ? BigDecimal.ZERO : new BigDecimal(result
					.get("weiPosRefundAmount").toString());
			// POS订单银联卡支付退款金额
			unionPosRefundAmount = result.get("unionPosRefundAmount") == null ? BigDecimal.ZERO : new BigDecimal(result
					.get("unionPosRefundAmount").toString());

		}
		// 线上订单实收金额=线上订单金额-线上订单退款-店铺优惠金额
		BigDecimal onlineActualAmout = onlineOrderAmount.subtract(onlineRefundAmount).subtract(onlineStoreDiscount);
		// 货到付款订单实收金额=货到付款订单金额-货到付款订单退款-店铺优惠金额
		BigDecimal deliveryActualAmout = deliveryOrderAmount.subtract(deliveryRefundAmount).subtract(
				deliveryStoreDiscount);
		// POS订单实收金额=POS订单金额-POS订单退款-店铺优惠金额,tips：POS订单金额实际上是店铺收入，已经减掉了店铺优惠金额了
		BigDecimal posActualAmout = posOrderAmount.subtract(posRefundAmount);
		// 实收金额=总的订单金额-总的退款金额
		BigDecimal totalActualAmout = onlineActualAmout.add(deliveryActualAmout).add(posActualAmout);

		json.put("onlineActualAmout", moneyFormat(onlineActualAmout));// 线上-实收金额
		json.put("deliveryActualAmout", moneyFormat(deliveryActualAmout));// 货到付款-实收金额
		json.put("posActualAmout", moneyFormat(posActualAmout));// pos销售-实收金额
		json.put("totalActualAmout", moneyFormat(totalActualAmout)); // 总实收金额

		// 线上余额金额=余额支付订单金额-余额支付订单退款金额
		BigDecimal onlineWalletAmount = yunPayAmount.subtract(yunRefundAmount);
		// 总的余额金额=线上余额金额，tips：只有线上订单才能余额支付
		BigDecimal totalWalletAmount = onlineWalletAmount;
		json.put("onlineWalletAmount", moneyFormat(onlineWalletAmount));// 线上-余额
		json.put("totalWalletAmount", moneyFormat(totalWalletAmount)); // 总余额

		// 线上订单支付宝支付金额=线上订单支付宝支付金额-线上订单支付宝支付退款金额
		BigDecimal onlineAlipayAmount = aliPayAmount.subtract(aliRefundAmount);
		// POS订单支付宝金额=POS订单支付宝金额-POS订单支付宝退款金额
		BigDecimal posAlipayAmount = aliPosAmount.subtract(aliPosRefundAmount);
		// 总的支付宝金额=线上订单支付宝支付金额+POS订单支付宝支付金额
		BigDecimal totalAlipayAmount = onlineAlipayAmount.add(posAlipayAmount);
		json.put("onlineAlipayAmount", moneyFormat(onlineAlipayAmount));// 线上-支付宝
		json.put("posAlipayAmount", moneyFormat(posAlipayAmount));// pos销售-支付宝
		json.put("totalAlipayAmount", moneyFormat(totalAlipayAmount)); // 总支付宝额

		// 线上订单微信支付=线上订单微信支付金额-线上订单微信支付退款金额
		BigDecimal onlineWeiXinAmount = weiPayAmount.subtract(weiRefundAmount);
		// POS订单微信支付金额=POS订单微信支付金额-POS订单微信支付退款金额
		BigDecimal posWeiXinAmount = weiPosAmount.subtract(weiPosRefundAmount);
		// 总的微信支付金额=线上订单微信支付金额+POS订单微信支付金额
		BigDecimal totalWeiXinAmount = onlineWeiXinAmount.add(posWeiXinAmount);
		json.put("onlineWeiXinAmount", moneyFormat(onlineWeiXinAmount));// 线上-微信
		json.put("posWeiXinAmount", moneyFormat(posWeiXinAmount));// pos销售-微信
		json.put("totalWeiXinAmount", moneyFormat(totalWeiXinAmount)); // 总微信额

		json.put("onlineJDAmount", moneyFormat(new BigDecimal(0)));// 线上-京东
																	// ------暂时屏蔽
		json.put("totalJDAmount", moneyFormat(new BigDecimal(0)));// 总京东额
																	// ------暂时屏蔽

		// POS订单银联支付=POS订单银联支付金额-POS订单银联卡支付退款金额
		BigDecimal posUnionpayAmount = unionPosAmount.subtract(unionPosRefundAmount);
		// 银联支付总额
		BigDecimal totalUnionpayAmount = posUnionpayAmount;
		json.put("posUnionpayAmount", moneyFormat(posUnionpayAmount));// pos销售-银联卡
		json.put("totalUnionpayAmount", moneyFormat(totalUnionpayAmount));// 总银联卡额

		// 货到付款订单现金支付=实收-平台补贴
		BigDecimal deliveryCashPayAmount = deliveryActualAmout.subtract(deliveryPlatformDiscount);
		// POS现金金额=实收-支付宝-微信-银联卡
		BigDecimal posCashPayAmount = posActualAmout.subtract(posAlipayAmount).subtract(posWeiXinAmount)
				.subtract(posUnionpayAmount);
		// 总的现金支付金额=货到付款订单现金支付金额+POS订单现金支付金额
		BigDecimal totalCashPayAmount = deliveryCashPayAmount.add(posCashPayAmount);
		json.put("deliveryCashPayAmount", moneyFormat(deliveryCashPayAmount));// 货到付款-现金支付
		json.put("posCashPayAmount", moneyFormat(posCashPayAmount));// pos-现金支付
		json.put("totalCashPayAmount", moneyFormat(totalCashPayAmount));// 总现金支付额
	}

	/**
	 * 销售统计
	 * @desc TODO Add a description 
	 * @author zengj
	 * @param parames
	 * @return
	 */
	public JSONObject getSaleOrderStatisticsNew(Map<String, Object> params) {
		// Tips:注意，方法调用顺序一定不能乱，耦合性比较高，下个计算可能需要上个方法的值，从json中取

		// 返回的josn对象
		JSONObject json = new JSONObject();
		// 统计订单数量
		statisticsOrderCount(json, params);
		// 统计订单金额
		statisticsOrderIncome(json, params);
		// 统计订单退款金额
		statisticsRefundIncome(json, params);
		// 统计订单代金券金额
		statisticsCouponAmount(json, params);
		// 统计优惠金额(店铺优惠和平台优惠)及平台补贴
		statisticsPreferentialAmount(json, params);
		// 统计配送费金额
		statisticsFareAmount(json, params);
		// 统计各种支付方式金额及实收
		statisticsPayTypeAmount(json, params);

		return json;
	}

	/**
	 * 该方法暂时废弃，采用getSaleOrderStatisticsNew
	 * @desc TODO Add a description 
	 * @param parames
	 * @return
	 */
	@Override
	@Deprecated
	public JSONObject getSaleOrderStatistics(Map<String, Object> parames) {
		JSONObject json = new JSONObject();

		Map<String, Object> map = null;

		// //////////////// 线上-订单数量////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("status", getfinishStatus());
		map.put("payWay", String.valueOf(PayWayEnum.PAY_ONLINE.ordinal()));
		int onlineOrderCount = tradeOrderMapper.getOrderCountByParames(map);
		// //////////////// 货到付款-订单数量////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("payWay", String.valueOf(PayWayEnum.CASH_DELIERY.ordinal()));
		map.put("paymentStatus", String.valueOf(PaymentStatusEnum.BACK_SECTION.ordinal()));
		int deliveryOrderCount = tradeOrderMapper.getOrderCountByParames(map);
		// //////////////// pos销售-订单数量////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getPosOrderResource());
		map.put("status", getfinishStatus());
		int posOrderCount = tradeOrderMapper.getOrderCountByParames(map);

		// //////////////// 线上-订单金额////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("status", getfinishStatus());
		// map.put("orderResourceType", "0");
		map.put("payWay", String.valueOf(PayWayEnum.PAY_ONLINE.ordinal()));
		BigDecimal onlineOrderAmount = tradeOrderMapper.getOrderAmountByParames(map);
		if (onlineOrderAmount == null) {
			onlineOrderAmount = new BigDecimal(0);
		}
		// //////////////// 货到付款-订单金额////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		// map.put("orderResourceType", "1");
		map.put("payWay", String.valueOf(PayWayEnum.CASH_DELIERY.ordinal()));
		map.put("paymentStatus", String.valueOf(PaymentStatusEnum.BACK_SECTION.ordinal()));
		BigDecimal deliveryOrderAmount = tradeOrderMapper.getOrderAmountByParames(map);
		if (deliveryOrderAmount == null) {
			deliveryOrderAmount = new BigDecimal(0);
		}
		// //////////////// POS销售订单金额////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getPosOrderResource());
		// map.put("orderResourceType", "2");
		map.put("status", getfinishStatus());
		BigDecimal posOrderAmount = tradeOrderMapper.getOrderAmountByParames(map);
		if (posOrderAmount == null) {
			posOrderAmount = new BigDecimal(0);
		}
		// //////////////// 总订单金额////////////////////////////
		BigDecimal totalOrderAmount = new BigDecimal(0);
		totalOrderAmount = totalOrderAmount.add(onlineOrderAmount).add(deliveryOrderAmount).add(posOrderAmount);

		// //////////////// 线上-退款金额////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("refundsStatus", getfinishRefundsStatus());
		map.put("payWay", String.valueOf(PayWayEnum.PAY_ONLINE.ordinal()));
		BigDecimal onlineRefundAmount = tradeOrderMapper.getRefundAmountByParames(map);
		if (onlineRefundAmount == null) {
			onlineRefundAmount = new BigDecimal(0);
		}
		// //////////////// 货到付款-退款金额////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("refundsStatus", getfinishRefundsStatus());
		map.put("payWay", String.valueOf(PayWayEnum.CASH_DELIERY.ordinal()));
		BigDecimal deliveryRefundAmount = tradeOrderMapper.getRefundAmountByParames(map);
		if (deliveryRefundAmount == null) {
			deliveryRefundAmount = new BigDecimal(0);
		}
		// //////////////// pos销售-退款金额////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getPosOrderResource());
		map.put("refundsStatus", getfinishRefundsStatus());
		map.put("payWay", String.valueOf(PayWayEnum.LINE_PAY.ordinal()));
		BigDecimal posRefundAmount = tradeOrderMapper.getRefundAmountByParames(map);
		if (posRefundAmount == null) {
			posRefundAmount = new BigDecimal(0);
		}

		// //////////////// 线上-店铺优惠金额////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("status", getfinishStatus());
		map.put("payWay", String.valueOf(PayWayEnum.PAY_ONLINE.ordinal()));
		map.put("activityType", getDiscountActivityType());
		BigDecimal onlineStoreDiscount = tradeOrderMapper.getStoreDiscountByParames(map);
		if (onlineStoreDiscount == null) {
			onlineStoreDiscount = new BigDecimal(0);
		}
		// //////////////// 货到付款-店铺优惠金额////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("payWay", String.valueOf(PayWayEnum.CASH_DELIERY.ordinal()));
		map.put("paymentStatus", String.valueOf(PaymentStatusEnum.BACK_SECTION.ordinal()));
		map.put("activityType", getDiscountActivityType());
		BigDecimal deliveryStoreDiscount = tradeOrderMapper.getStoreDiscountByParames(map);
		if (deliveryStoreDiscount == null) {
			deliveryStoreDiscount = new BigDecimal(0);
		}
		// //////////////// 总店铺优惠金额////////////////////////////
		BigDecimal totalStoreDiscount = new BigDecimal(0);
		totalStoreDiscount = totalStoreDiscount.add(onlineStoreDiscount).add(deliveryStoreDiscount);

		// //////////////// 线上-平台优惠金额////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("status", getfinishStatus());
		map.put("payWay", String.valueOf(PayWayEnum.PAY_ONLINE.ordinal()));
		map.put("activityType", getPlatformDiscountActivityType());
		BigDecimal onlinePlatformDiscount = tradeOrderMapper.getPlatformDiscountByParames(map);
		if (onlinePlatformDiscount == null) {
			onlinePlatformDiscount = new BigDecimal(0);
		}

		// / 减去线上退款单的平台优惠
		map.put("refundsStatus", getfinishRefundsStatus());
		BigDecimal onlineRefundPlatformDiscount = tradeOrderMapper.getRefundPlatformDiscountByParames(map);
		if (onlineRefundPlatformDiscount != null) {
			onlinePlatformDiscount = onlinePlatformDiscount.subtract(onlineRefundPlatformDiscount);

			onlineRefundAmount = onlineRefundAmount.add(onlineRefundPlatformDiscount);
		}

		// //////////////// 货到付款-平台优惠金额////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("payWay", String.valueOf(PayWayEnum.CASH_DELIERY.ordinal()));
		map.put("paymentStatus", String.valueOf(PaymentStatusEnum.BACK_SECTION.ordinal()));
		map.put("activityType", getPlatformDiscountActivityType());
		BigDecimal deliveryPlatformDiscount = tradeOrderMapper.getPlatformDiscountByParames(map);
		if (deliveryPlatformDiscount == null) {
			deliveryPlatformDiscount = new BigDecimal(0);
		}

		// / 减去货到付款退款单的平台优惠
		map.put("refundsStatus", getfinishRefundsStatus());
		BigDecimal deliveryRefundPlatformDiscount = tradeOrderMapper.getRefundPlatformDiscountByParames(map);
		if (deliveryRefundPlatformDiscount != null) {
			deliveryPlatformDiscount = deliveryPlatformDiscount.subtract(deliveryRefundPlatformDiscount);

			deliveryRefundAmount = deliveryRefundAmount.add(deliveryRefundPlatformDiscount);
		}

		// //////////////// 总平台优惠金额////////////////////////////
		BigDecimal totalPlatformDiscount = new BigDecimal(0);
		totalPlatformDiscount = totalPlatformDiscount.add(onlinePlatformDiscount).add(deliveryPlatformDiscount);

		// //////////////// 线上-代金卷金额////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("status", getfinishStatus());
		map.put("payWay", String.valueOf(PayWayEnum.PAY_ONLINE.ordinal()));
		map.put("activityType", getVcheronActivityType());
		BigDecimal onlineCouponAmount = tradeOrderMapper.getCouponAmountByParames(map);
		if (onlineCouponAmount == null) {
			onlineCouponAmount = new BigDecimal(0);
		}

		// 减去线上退款单的代金劵金额
		map.put("refundsStatus", getfinishRefundsStatus());
		BigDecimal onlineRefundCouponAmount = tradeOrderMapper.getRefundPlatformCouponByParames(map);
		if (onlineRefundCouponAmount != null) {
			onlineCouponAmount = onlineCouponAmount.subtract(onlineRefundCouponAmount);

			onlineRefundAmount = onlineRefundAmount.add(onlineRefundCouponAmount);
		}

		// ////////////// 货到付款-代金卷金额////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("paymentStatus", String.valueOf(PaymentStatusEnum.BACK_SECTION.ordinal()));
		map.put("payWay", String.valueOf(PayWayEnum.CASH_DELIERY.ordinal()));
		map.put("activityType", getVcheronActivityType());
		BigDecimal deliveryCouponAmount = tradeOrderMapper.getCouponAmountByParames(map);
		if (deliveryCouponAmount == null) {
			deliveryCouponAmount = new BigDecimal(0);
		}

		// 减去线上退款单的代金劵金额
		map.put("refundsStatus", getfinishRefundsStatus());
		BigDecimal deliveryRefundCouponAmount = tradeOrderMapper.getRefundPlatformCouponByParames(map);
		if (deliveryRefundCouponAmount != null) {
			deliveryCouponAmount = deliveryCouponAmount.subtract(deliveryRefundCouponAmount);

			deliveryRefundAmount = deliveryRefundAmount.add(deliveryRefundCouponAmount);
		}

		// ////////////// 总代金卷金额////////////////////////////
		BigDecimal totalCouponAmount = new BigDecimal(0);
		totalCouponAmount = totalCouponAmount.add(onlineCouponAmount).add(deliveryCouponAmount);

		// //////////////// 总退款金额////////////////////////////
		BigDecimal totalRefundAmount = new BigDecimal(0);
		totalRefundAmount = totalRefundAmount.add(onlineRefundAmount).add(deliveryRefundAmount).add(posRefundAmount);

		// ////////////// 线上-配送费////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("status", getfinishStatus());
		map.put("payWay", String.valueOf(PayWayEnum.PAY_ONLINE.ordinal()));
		BigDecimal onlineFare = tradeOrderMapper.getFareAmountByParames(map);
		if (onlineFare == null) {
			onlineFare = new BigDecimal(0);
		}
		// ////////////// 货到付款-配送费////////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("paymentStatus", String.valueOf(PaymentStatusEnum.BACK_SECTION.ordinal()));
		map.put("payWay", String.valueOf(PayWayEnum.CASH_DELIERY.ordinal()));
		BigDecimal deliveryFare = tradeOrderMapper.getFareAmountByParames(map);
		if (deliveryFare == null) {
			deliveryFare = new BigDecimal(0);
		}
		// ////////////// 总配送费////////////////////////////
		BigDecimal totalFare = new BigDecimal(0);
		totalFare = totalFare.add(onlineFare).add(deliveryFare);

		// ////////////// 线上-实收金额////////////////////////////
		/*
		 * map = getOrderMap(parames); map.put("orderResource",
		 * getOnlineOrderResource()); map.put("status", getfinishStatus());
		 * map.put("payWay", String.valueOf(PayWayEnum.PAY_ONLINE.ordinal()));
		 * BigDecimal onlineActualAmout =
		 * tradeOrderMapper.getActualAmoutByParames(map); if (onlineActualAmout
		 * == null) { onlineActualAmout = new BigDecimal(0); }
		 */
		BigDecimal onlineActualAmout = new BigDecimal(0);

		// 实收金额 = 订单金额-退款金额-店铺优惠金额
		onlineActualAmout = onlineOrderAmount.subtract(onlineRefundAmount).subtract(onlineStoreDiscount);

		// //////////// 货到付款-实收金额////////////////////////////
		/*
		 * map = getOrderMap(parames); map.put("orderResource",
		 * getOnlineOrderResource()); map.put("paymentStatus",
		 * String.valueOf(PaymentStatusEnum.BACK_SECTION.ordinal()));
		 * map.put("payWay",String.valueOf( PayWayEnum.CASH_DELIERY.ordinal()));
		 * BigDecimal deliveryActualAmout =
		 * tradeOrderMapper.getActualAmoutByParames(map); if
		 * (deliveryActualAmout == null) { deliveryActualAmout = new
		 * BigDecimal(0); }
		 */
		BigDecimal deliveryActualAmout = new BigDecimal(0);
		// 实收金额 = 订单金额-退款金额-商家优惠金额
		deliveryActualAmout = deliveryOrderAmount.subtract(deliveryRefundAmount).subtract(deliveryStoreDiscount);

		// //////////// pos销售-实收金额////////////////////////////
		/*
		 * map = getOrderMap(parames); map.put("orderResource",
		 * getPosOrderResource()); map.put("status", getfinishStatus());
		 * BigDecimal posActualAmout =
		 * tradeOrderMapper.getActualAmoutByParames(map); if (posActualAmout ==
		 * null) { posActualAmout = new BigDecimal(0); }
		 */
		BigDecimal posActualAmout = new BigDecimal(0);
		posActualAmout = posOrderAmount.subtract(posRefundAmount);

		// ////////// 总实收金额///////////////////////////
		BigDecimal totalActualAmout = new BigDecimal(0);
		totalActualAmout = totalActualAmout.add(onlineActualAmout).add(deliveryActualAmout).add(posActualAmout);

		// ////////// 线上-余额///////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("status", getfinishStatus());
		map.put("payType", String.valueOf(PayTypeEnum.WALLET.ordinal()));
		BigDecimal onlineWalletAmount = tradeOrderMapper.getPayAmoutByParames(map);
		if (onlineWalletAmount == null) {
			onlineWalletAmount = new BigDecimal(0);
		}

		// 线上余额 - 线上余额支付的 退款金额 = 线上余额
		// //////////////// 线上-余额支付的 退款金额////////////////////////////
		BigDecimal onlineRefundAmountByWallet = this.refundAmountByPaymentMethod(parames,
				String.valueOf(PayTypeEnum.WALLET.ordinal()));
		if (onlineRefundAmountByWallet != null) {
			onlineWalletAmount = onlineWalletAmount.subtract(onlineRefundAmountByWallet);
		}

		// ////////// 总余额///////////////////////////
		BigDecimal totalWalletAmount = new BigDecimal(0);
		totalWalletAmount = totalWalletAmount.add(onlineWalletAmount);

		// ////////// 线上-支付宝///////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("status", getfinishStatus());
		map.put("payType", String.valueOf(PayTypeEnum.ALIPAY.ordinal()));
		BigDecimal onlineAlipayAmount = tradeOrderMapper.getPayAmoutByParames(map);
		if (onlineAlipayAmount == null) {
			onlineAlipayAmount = new BigDecimal(0);
		}
		// 线上支付宝 - 线上支付宝的 退款金额 = 线上支付宝
		// //////////////// 线上-支付宝 支付的 退款金额////////////////////////////
		BigDecimal onlineRefundAmountByAlipay = this.refundAmountByPaymentMethod(parames,
				String.valueOf(PayTypeEnum.ALIPAY.ordinal()));
		if (onlineRefundAmountByAlipay != null) {
			onlineAlipayAmount = onlineAlipayAmount.subtract(onlineRefundAmountByAlipay);
		}

		// ////////// pos销售-支付宝///////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getPosOrderResource());
		map.put("status", getfinishStatus());
		map.put("payType", String.valueOf(PayTypeEnum.ALIPAY.ordinal()));
		BigDecimal posAlipayAmount = tradeOrderMapper.getPayAmoutByParames(map);
		if (posAlipayAmount == null) {
			posAlipayAmount = new BigDecimal(0);
		} else {
			/*
			 * BigDecimal posRefundAmountByPaymentMethod =
			 * this.posRefundAmountByPaymentMethod(parames,
			 * String.valueOf(PayTypeEnum.ALIPAY.ordinal())); if
			 * (posRefundAmountByPaymentMethod != null) { posAlipayAmount =
			 * posAlipayAmount.subtract(posRefundAmountByPaymentMethod); }
			 */
		}
		// ////////// 总支付宝额///////////////////////////
		BigDecimal totalAlipayAmount = new BigDecimal(0);
		totalAlipayAmount = totalAlipayAmount.add(onlineAlipayAmount).add(posAlipayAmount);

		// ////////// 线上-微信///////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("status", getfinishStatus());
		map.put("payType", String.valueOf(PayTypeEnum.WXPAY.ordinal()));
		BigDecimal onlineWeiXinAmount = tradeOrderMapper.getPayAmoutByParames(map);
		if (onlineWeiXinAmount == null) {
			onlineWeiXinAmount = new BigDecimal(0);
		}

		BigDecimal refundAmountByPaymentMethod = this.refundAmountByPaymentMethod(parames,
				String.valueOf(PayTypeEnum.WXPAY.ordinal()));
		if (refundAmountByPaymentMethod != null) {
			onlineWeiXinAmount = onlineWeiXinAmount.subtract(refundAmountByPaymentMethod);
		}

		// ////////// pos销售-微信///////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getPosOrderResource());
		map.put("status", getfinishStatus());
		map.put("payType", String.valueOf(PayTypeEnum.WXPAY.ordinal()));
		BigDecimal posWeiXinAmount = tradeOrderMapper.getPayAmoutByParames(map);
		if (posWeiXinAmount == null) {
			posWeiXinAmount = new BigDecimal(0);
		} else {
			/*
			 * BigDecimal posRefundAmountByPaymentMethod =
			 * this.posRefundAmountByPaymentMethod(parames,
			 * String.valueOf(PayTypeEnum.WXPAY.ordinal())); if
			 * (posRefundAmountByPaymentMethod != null) { posWeiXinAmount =
			 * posWeiXinAmount.subtract(posRefundAmountByPaymentMethod); }
			 */
		}
		// ////////// 总微信额///////////////////////////
		BigDecimal totalWeiXinAmount = new BigDecimal(0);
		totalWeiXinAmount = totalWeiXinAmount.add(onlineWeiXinAmount).add(posWeiXinAmount);

		/*
		 * //////////// 线上-京东/////////////////////////// map =
		 * getOrderMap(parames); map.put("orderResource",
		 * getOnlineOrderResource()); map.put("status", getfinishStatus());
		 * map.put("payType", String.valueOf(PayTypeEnum.JDPAY.ordinal()));
		 * BigDecimal onlineJDAmount =
		 * tradeOrderMapper.getPayAmoutByParames(map); if (onlineJDAmount ==
		 * null) { onlineJDAmount = new BigDecimal(0); } else { BigDecimal
		 * refundAmountByPaymentMethod =
		 * this.refundAmountByPaymentMethod(parames,
		 * String.valueOf(PayTypeEnum.JDPAY.ordinal())); if
		 * (refundAmountByPaymentMethod != null) { onlineJDAmount =
		 * onlineJDAmount.subtract(refundAmountByPaymentMethod); } }
		 * //////////// 总京东额/////////////////////////// BigDecimal totalJDAmount
		 * = new BigDecimal(0); totalJDAmount =
		 * totalJDAmount.add(onlineJDAmount);
		 */

		// ////////// pos销售-银联卡///////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getPosOrderResource());
		map.put("status", getfinishStatus());
		map.put("payType", String.valueOf(PayTypeEnum.OFFLINE_BANK.ordinal()));
		BigDecimal posUnionpayAmount = tradeOrderMapper.getPayAmoutByParames(map);
		if (posUnionpayAmount == null) {
			posUnionpayAmount = new BigDecimal(0);
		} else {
			/*
			 * BigDecimal posRefundAmountByPaymentMethod =
			 * this.posRefundAmountByPaymentMethod(parames,
			 * String.valueOf(PayTypeEnum.OFFLINE_BANK.ordinal())); if
			 * (posRefundAmountByPaymentMethod != null) { posUnionpayAmount =
			 * posUnionpayAmount.subtract(posRefundAmountByPaymentMethod); }
			 */
		}
		// ////////// 总银联卡额///////////////////////////
		BigDecimal totalUnionpayAmount = new BigDecimal(0);
		totalUnionpayAmount = totalUnionpayAmount.add(posUnionpayAmount);

		// 线上-平台补贴
		BigDecimal onlinePlatformSubsidy = onlinePlatformDiscount.add(onlineCouponAmount);
		// 货到付款-平台补贴
		BigDecimal deliveryPlatformSubsidy = deliveryPlatformDiscount.add(deliveryCouponAmount);
		// 总平台补贴额
		BigDecimal totalPlatformSubsidy = onlinePlatformSubsidy.add(deliveryPlatformSubsidy);

		// ////////// 货到付款-现金支付///////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("payWay", String.valueOf(PayWayEnum.CASH_DELIERY.ordinal()));
		map.put("paymentStatus", String.valueOf(PaymentStatusEnum.BACK_SECTION.ordinal()));
		// map.put("payType", String.valueOf(PayTypeEnum.CASH.ordinal()));
		BigDecimal deliveryCashPayAmount = tradeOrderMapper.getDeliveryCashPayAmoutByParames(map);
		if (deliveryCashPayAmount == null) {
			deliveryCashPayAmount = new BigDecimal(0);
		} else {
			/*
			 * BigDecimal deliveryRefundAmountByPaymentMethod =
			 * this.deliveryRefundAmountByPaymentMethod(parames,
			 * String.valueOf(PayTypeEnum.CASH.ordinal())); if
			 * (deliveryRefundAmountByPaymentMethod != null) {
			 * deliveryCashPayAmount = deliveryCashPayAmount.subtract(
			 * deliveryRefundAmountByPaymentMethod); }
			 */
		}
		deliveryCashPayAmount = deliveryCashPayAmount.subtract(deliveryRefundAmount).subtract(deliveryPlatformSubsidy);

		// ////////// pos-现金支付///////////////////////////
		map = getOrderMap(parames);
		map.put("orderResource", getPosOrderResource());
		map.put("status", getfinishStatus());
		map.put("payType", String.valueOf(PayTypeEnum.CASH.ordinal()));
		BigDecimal posCashPayAmount = tradeOrderMapper.getPayAmoutByParames(map);
		if (posCashPayAmount == null) {
			posCashPayAmount = new BigDecimal(0);
		} else {
			/*
			 * BigDecimal posRefundAmountByPaymentMethod =
			 * this.posRefundAmountByPaymentMethod(parames,
			 * String.valueOf(PayTypeEnum.CASH.ordinal())); if
			 * (posRefundAmountByPaymentMethod != null) { posCashPayAmount =
			 * posCashPayAmount.subtract(posRefundAmountByPaymentMethod); }
			 */
		}
		posCashPayAmount = posCashPayAmount.subtract(posRefundAmount);

		// ///////// 总现金支付额///////////////////////////
		BigDecimal totalCashPayAmount = new BigDecimal(0);
		totalCashPayAmount = totalCashPayAmount.add(deliveryCashPayAmount).add(posCashPayAmount);

		json.put("onlineOrderCount", onlineOrderCount); // 线上-订单数量
		json.put("deliveryOrderCount", deliveryOrderCount); // 货到付款-订单数量
		json.put("posOrderCount", posOrderCount); // pos销售-订单数量
		json.put("totalOrderCount", (onlineOrderCount + deliveryOrderCount + posOrderCount)); // 总订单数量

		json.put("onlineOrderAmount", onlineOrderAmount); // 线上-订单金额
		json.put("deliveryOrderAmount", deliveryOrderAmount);// 货到付款-订单金额
		json.put("posOrderAmount", posOrderAmount);// pos销售-订单金额
		json.put("totalOrderAmount", totalOrderAmount); // 总订单金额

		json.put("onlineRefundAmount", onlineRefundAmount); // 线上-退款金额
		json.put("deliveryRefundAmount", deliveryRefundAmount);// 货到付款-退款金额
		json.put("posRefundAmount", posRefundAmount);// pos销售-退款金额
		json.put("totalRefundAmount", totalRefundAmount); // 总退款金额

		json.put("onlineStoreDiscount", onlineStoreDiscount);// 线上-店铺优惠金额
		json.put("deliveryStoreDiscount", deliveryStoreDiscount);// 货到付款-店铺优惠金额
		json.put("totalStoreDiscount", totalStoreDiscount); // 总店铺优惠金额

		json.put("onlinePlatformDiscount", onlinePlatformDiscount);// 线上-平台优惠金额
		json.put("deliveryPlatformDiscount", deliveryPlatformDiscount);// 货到付款-平台优惠金额
		json.put("totalPlatformDiscount", totalPlatformDiscount); // 总平台优惠金额

		json.put("onlineCouponAmount", onlineCouponAmount);// 线上-代金劵金额
		json.put("deliveryCouponAmount", deliveryCouponAmount);// 货到付款-代金劵金额
		json.put("totalCouponAmount", totalCouponAmount); // 总代金劵金额

		json.put("onlineFare", onlineFare);// 线上-配送费
		json.put("deliveryFare", deliveryFare);// 货到付款-配送费
		json.put("totalFare", totalFare); // 总配送费

		json.put("onlineActualAmout", onlineActualAmout);// 线上-实收金额
		json.put("deliveryActualAmout", deliveryActualAmout);// 货到付款-实收金额
		json.put("posActualAmout", posActualAmout);// pos销售-实收金额
		json.put("totalActualAmout", totalActualAmout); // 总实收金额

		json.put("onlineWalletAmount", onlineWalletAmount);// 线上-余额
		json.put("totalWalletAmount", totalWalletAmount); // 总余额

		json.put("onlineAlipayAmount", onlineAlipayAmount);// 线上-支付宝
		json.put("posAlipayAmount", posAlipayAmount);// pos销售-支付宝
		json.put("totalAlipayAmount", totalAlipayAmount); // 总支付宝额

		json.put("onlineWeiXinAmount", onlineWeiXinAmount);// 线上-微信
		json.put("posWeiXinAmount", posWeiXinAmount);// pos销售-微信
		json.put("totalWeiXinAmount", totalWeiXinAmount); // 总微信额

		json.put("onlineJDAmount", new BigDecimal(0));// 线上-京东 ------暂时屏蔽
		json.put("totalJDAmount", new BigDecimal(0));// 总京东额 ------暂时屏蔽

		json.put("posUnionpayAmount", posUnionpayAmount);// pos销售-银联卡
		json.put("totalUnionpayAmount", totalUnionpayAmount);// 总银联卡额

		json.put("onlinePlatformSubsidy", onlinePlatformSubsidy);// 线上-平台补贴
		json.put("deliveryPlatformSubsidy", deliveryPlatformSubsidy);// 货到付款-平台补贴
		json.put("totalPlatformSubsidy", totalPlatformSubsidy);// 总平台补贴额

		json.put("deliveryCashPayAmount", deliveryCashPayAmount);// 货到付款-现金支付
		json.put("posCashPayAmount", posCashPayAmount);// pos-现金支付
		json.put("totalCashPayAmount", totalCashPayAmount);// 总现金支付额
		return json;
	}

	private BigDecimal refundAmountByPaymentMethod(Map<String, Object> parames, String paymentMethod) {
		Map<String, Object> map = getOrderMap(parames);
		map.put("orderResource", getOnlineOrderResource());
		map.put("refundsStatus", getfinishRefundsStatus());
		map.put("payWay", String.valueOf(PayWayEnum.PAY_ONLINE.ordinal()));
		map.put("paymentMethod", paymentMethod);
		BigDecimal refundAmountByPaymentMethod = tradeOrderMapper.getRefundAmountByParames(map);
		return refundAmountByPaymentMethod;
	}

	/*
	 * private BigDecimal posRefundAmountByPaymentMethod(Map<String, Object>
	 * parames, String paymentMethod) { Map<String, Object> map =
	 * getOrderMap(parames); map.put("orderResource", getPosOrderResource());
	 * map.put("refundsStatus", getfinishRefundsStatus()); map.put("payWay",
	 * String.valueOf(PayWayEnum.LINE_PAY.ordinal())); map.put("paymentMethod",
	 * paymentMethod); BigDecimal posRefundAmount =
	 * tradeOrderMapper.getRefundAmountByParames(map); return posRefundAmount; }
	 * 
	 * private BigDecimal deliveryRefundAmountByPaymentMethod(Map<String,
	 * Object> parames, String paymentMethod) { Map<String, Object> map =
	 * getOrderMap(parames); map.put("orderResource", getOnlineOrderResource());
	 * map.put("refundsStatus", getfinishRefundsStatus()); map.put("payWay",
	 * String.valueOf(PayWayEnum.CASH_DELIERY.ordinal()));
	 * map.put("paymentMethod", paymentMethod); BigDecimal deliveryRefundAmount
	 * = tradeOrderMapper.getRefundAmountByParames(map); return
	 * deliveryRefundAmount; }
	 */

	/**
	 * 查询订单实际金额-用于今日营收的实际收入(商家云钱包的实际入账金额) 注：该退款金额包括活动金额，如果该退款单参与的活动是运营商发布的，该金额=
	 * 订单实付金额+活动优惠金额， 如果该退款单参与的活动是商家发布的。那么该金额=订单实付金额
	 * 
	 * @author zengj
	 * @param params
	 * @return
	 */
	@Override
	public BigDecimal selectOrderAmount(Map<String, Object> params) {
		return tradeOrderMapper.selectOrderAmount(params);
	}

	/**
	 * 查询退款负收入金额-用于今日营收的负增长(商家云钱包的实际扣款金额) 注：该退款金额包括活动金额，如果该退款单参与的活动是运营商发布的，该金额=
	 * 订单实付金额+活动优惠金额， 如果该退款单参与的活动是商家发布的。那么该金额=订单实付金额
	 * 
	 * @author zengj
	 * @param params
	 * @return
	 */
	@Override
	public BigDecimal selectRefundAmount(Map<String, Object> params) {
		return tradeOrderMapper.selectRefundAmount(params);
	}

	/**
	 * 
	 * 今日（昨日）营收列表查询
	 * 
	 * @author zengj
	 * @param params
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	@Override
	public PageUtils<Map<String, Object>> selectOrderIncomeList(Map<String, Object> params, int pageSize, int pageNumber) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		return new PageUtils<Map<String, Object>>(tradeOrderMapper.selectOrderIncomeList(params));
	}

	@Override
	public PageUtils<TradeOrder> findUserTradeOrderList(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);

		// 该状态与前端定义状态统一,100:未评价
		String status = (String) map.get("status");
		List<TradeOrder> list = new ArrayList<TradeOrder>();
		if (status != null && status.equals(OrderMessageConstant.ONE_HUNDRED)) {
			// 待评价订单查询
			list = tradeOrderMapper.selectUserIsCommentOrderList(map);
		} else {
			if (status != null) {
				// 实物订单支付中状态
				List<String> orderStatus = new ArrayList<String>();
				orderStatus.add(status);
				// 0为查询未支付状态下的订单，包含对应的是未支付和支付确认中的数据
				if (status.equals(String.valueOf(Constant.ZERO))) {
					// 支付确认中状态
					orderStatus.add(String.valueOf(OrderStatusEnum.BUYER_PAYING.ordinal()));
				} else if (status.equals(String.valueOf(Constant.TWO))) {
					// 2为查询已取消状态下的订单，包含对应的是（已取消，取消中，已拒收，拒收中）
					orderStatus.add(String.valueOf(OrderStatusEnum.CANCELING.ordinal()));
					orderStatus.add(String.valueOf(OrderStatusEnum.REFUSING.ordinal()));
					orderStatus.add(String.valueOf(OrderStatusEnum.REFUSED.ordinal()));
				}
				map.put("status", orderStatus);
			}
			list = tradeOrderMapper.selectUserOrderList(map);
		}
		for (TradeOrder vo : list) {
			List<TradeOrderItem> items = tradeOrderItemMapper.selectTradeOrderItem(vo.getId());
			vo.setTradeOrderItem(items);
		}
		return new PageUtils<TradeOrder>(list);
	}

	/**
	 * zengj:根据参数查询订单信息
	 *
	 * @param params
	 * @return
	 */
	@Override
	public List<TradeOrder> selectByParams(Map<String, Object> params) {
		return tradeOrderMapper.selectByParams(params);
	}

	/**
	 * 
	 * zengj:提货码验证记录
	 *
	 * @param params
	 * @return
	 */
	@Override
	public PageUtils<Map<String, Object>> selectPickUpRecord(Map<String, Object> params, int pageSize, int pageNumber) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		return new PageUtils<Map<String, Object>>(tradeOrderMapper.selectPickUpRecord(params));
	}

	/**
	 * zengj:查询提货码订单总额
	 *
	 * @param params
	 * @return
	 */
	@Override
	public BigDecimal selectPickUpTotalAmount(Map<String, Object> params) {
		return tradeOrderMapper.selectPickUpTotalAmount(params);
	}

	/**
	 * zengj:查询消费码使用记录
	 *
	 * @param params
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@Override
	public PageUtils<Map<String, Object>> selectConsumeCodeUseRecord(Map<String, Object> params, int pageNumber,
			int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		return new PageUtils<Map<String, Object>>(tradeOrderMapper.selectConsumeCodeUseRecord(params));
	}

	/**
	 * 
	 * zengj:查询消费码订单总额
	 *
	 * @param params
	 * @return
	 */
	@Override
	public BigDecimal selectConsumeTotalAmount(Map<String, Object> params) {
		return tradeOrderMapper.selectConsumeTotalAmount(params);
	}

	/**
	 * 
	 * zengj:根据消费码查询订单信息
	 *
	 * @param params
	 * @return
	 */
	@Override
	public Map<String, Object> selectOrderDetailByConsumeCode(Map<String, Object> params) {
		return tradeOrderMapper.selectOrderDetailByConsumeCode(params);
	}

	@Override
	public JSONObject findUserOrderDetailList(String orderId) throws ServiceException {
		UserTradeOrderDetailVo orders = tradeOrderMapper.selectUserOrderDetail(orderId);
		// Begin 13113 待付款的订单不展示提货码 add by zengj
		if (orders != null
				&& (orders.getStatus() == OrderStatusEnum.UNPAID || orders.getStatus() == OrderStatusEnum.BUYER_PAYING || orders
						.getStatus() == OrderStatusEnum.CANCELED)) {
			orders.setPickUpCode(null);
		}
		// End 13113 add by zengj
		List<TradeOrderItem> tradeOrderItems = tradeOrderItemMapper.selectTradeOrderItemOrRefund(orderId);
		// 判断订单是否评价appraise大于0，已评价
		Integer appraise = tradeOrderItemMapper.selectTradeOrderItemIsAppraise(orderId);
		orders.setItems(tradeOrderItems);
		// 查询店铺扩展信息
		String storeId = orders.getStoreInfo().getId();
		StoreInfoExt storeInfoExt = storeInfoExtService.getByStoreId(storeId);
		JSONObject json = new JSONObject();
		try {
			json = getJsonObj(orders, appraise, storeInfoExt);
		} catch (Exception e) {
			logger.error("商品详细查询异常", e);
			throw new ServiceException();
		}
		return json;
	}

	// 组装返回数据
	private JSONObject getJsonObj(UserTradeOrderDetailVo orders, Integer appraise, StoreInfoExt storeInfoExt)
			throws Exception {
		JSONObject json = new JSONObject();
		// 1 订单信息
		json.put("orderId", orders.getId() == null ? "" : orders.getId());
		json.put("orderStatus", OrderAppStatusAdaptor.convertAppOrderStatus(orders.getStatus()));

		// 2 订单支付倒计时计算
		Integer remainingTime = orders.getRemainingTime();
		if (remainingTime != null) {
			remainingTime = remainingTime + 1800;
			json.put("remainingTime", remainingTime <= 0 ? "0" : remainingTime);
		} else {
			json.put("remainingTime", "0");
		}
		// 交易号
		json.put("tradeNum", orders.getTradeNum() == null ? "" : orders.getTradeNum());
		json.put("pickUpType", orders.getPickUpType() == null ? "" : orders.getPickUpType().ordinal());
		json.put("pickUpCode", orders.getPickUpCode() == null ? "" : orders.getPickUpCode());
		String strPickUpTime = orders.getPickUpTime();
		// 如果云团购订单隐藏该字段判断
		if (orders.getActivityType() == ActivityTypeEnum.GROUP_ACTIVITY) {
			json.put("pickUpTime", "");
		} else {
			json.put("pickUpTime", strPickUpTime == null ? "" : strPickUpTime);
		}
		json.put("remark", orders.getRemark() == null ? "" : orders.getRemark());
		json.put("orderAmount", orders.getTotalAmount() == null ? "0" : orders.getTotalAmount());
		json.put("actualAmount", orders.getActualAmount() == null ? "0" : orders.getActualAmount());
		json.put("orderNo", orders.getOrderNo() == null ? "" : orders.getOrderNo());
		json.put("cancelReason", orders.getReason() == null ? "" : orders.getReason());
		json.put("orderSubmitOrderTime",
				orders.getCreateTime() != null ? DateUtils.formatDate(orders.getCreateTime(), "yyyy-MM-dd HH:mm:ss")
						: "");
		json.put(
				"orderDeliveryTime",
				orders.getDeliveryTime() != null ? DateUtils.formatDate(orders.getDeliveryTime(), "yyyy-MM-dd HH:mm:ss")
						: "");
		json.put(
				"orderConfirmGoodTime",
				orders.getReceivedTime() != null ? DateUtils.formatDate(orders.getReceivedTime(), "yyyy-MM-dd HH:mm:ss")
						: "");
		json.put("activityType", orders.getActivityType() == null ? "" : orders.getActivityType().ordinal());
		json.put("preferentialPrice", orders.getPreferentialPrice() == null ? "" : orders.getPreferentialPrice());
		json.put("fare", orders.getFare() == null ? "" : orders.getFare());
		// 订单评价类型0：未评价，1：已评价
		json.put("orderIsComment", appraise > 0 ? Constant.ONE : Constant.ZERO);
		// 订单投诉状态
		json.put("compainStatus", orders.getCompainStatus() == null ? "" : orders.getCompainStatus().ordinal());

		// 2店铺信息
		StoreInfo storeInfo = orders.getStoreInfo();
		String storeId = "";
		String storeName = "";
		String storeMobile = "";
		String Address = "";
		if (storeInfo != null) {
			storeId = storeInfo.getId();
			storeName = storeInfo.getStoreName();
			storeMobile = storeInfo.getMobile();
			// 判断店铺类型，3：团购店
			if (storeInfoExt != null && StringUtils.isNotBlank(storeInfoExt.getServicePhone())) {
				storeMobile = storeInfoExt.getServicePhone();
			}
			// TODO 确认订单时，没有将地址保存到trade_order_logistics订单物流表，暂时取收货地址表的默认地址
			MemberConsigneeAddress memberConsigneeAddress = new MemberConsigneeAddress();
			memberConsigneeAddress.setUserId(storeId);
			memberConsigneeAddress.setIsDefault(AddressDefault.YES);
			List<MemberConsigneeAddress> memberAddressList = memberConsigneeAddressService
					.getList(memberConsigneeAddress);
			if (memberAddressList != null && memberAddressList.size() > 0) {
				MemberConsigneeAddress memberAddress = memberAddressList.get(0);
				Address = memberAddress.getArea() + memberAddress.getAddress();
			}
			// Address = storeInfo.getArea() + storeInfo.getAddress();
		}

		json.put("orderShopid", storeId);
		json.put("orderShopName", storeName);
		json.put("orderShopMobile", storeMobile);
		json.put("orderExtractShopName", storeName);
		json.put("pickUpAddress", Address);

		// 3 物流信息
		TradeOrderLogistics tradeOrderLogistics = orders.getTradeOrderLogistics();
		String consigneeName = "";
		String mobile = "";
		String allAddress = "";
		Integer type = 0;
		String orderlogisticsNo = "";
		String logisticsCmpany = "";
		if (tradeOrderLogistics != null) {
			consigneeName = tradeOrderLogistics.getConsigneeName();
			mobile = tradeOrderLogistics.getMobile();
			String area = tradeOrderLogistics.getArea() == null ? "" : tradeOrderLogistics.getArea();
			String address = tradeOrderLogistics.getAddress() == null ? "" : tradeOrderLogistics.getAddress();
			allAddress = area + address;
			if (tradeOrderLogistics.getType() != null) {
				type = tradeOrderLogistics.getType().ordinal();
			}
			orderlogisticsNo = tradeOrderLogistics.getLogisticsNo();
			logisticsCmpany = tradeOrderLogistics.getLogisticsCompanyName();
		}
		json.put("orderConsignee", consigneeName);
		json.put("orderConsigneeMobile", mobile);
		json.put("orderConsigneeAddress", allAddress);
		json.put("logisticsFlag", type);
		json.put("orderlogisticsNo", orderlogisticsNo);
		json.put("logisticsCmpany", logisticsCmpany);

		// 支付方式:(0:在线支付、1:货到付款,2:未付款,3:线下支付)
		json.put("payway", orders.getPayWay() == null ? "" : orders.getPayWay().ordinal());
		// 4 支付类型
		TradeOrderPay tradeOrderPay = orders.getTradeOrderPay();
		if (tradeOrderPay != null) {
			json.put("payType", tradeOrderPay.getPayType().ordinal());
			json.put("payAmount", tradeOrderPay.getPayAmount());
			json.put(
					"payTime",
					tradeOrderPay.getPayTime() == null ? "" : DateUtils.formatDate(tradeOrderPay.getPayTime(),
							"yyyy-MM-dd HH:mm:ss"));
		} else {
			json.put("payType", "");
			json.put("payAmount", "");
			json.put("payTime", "");
		}
		// 5 发票抬头信息
		TradeOrderInvoice tradeOrderInvoice = orders.getTradeOrderInvoice();
		String head = "";
		String context = "";
		if (tradeOrderInvoice != null) {
			head = tradeOrderInvoice.getHead();
			context = tradeOrderInvoice.getContext();
		}
		json.put("orderInvoiceTitle", head);
		json.put("orderInvoiceContent", context);

		// 6 订单项详细
		List<TradeOrderItem> items = orders.getItems();
		JSONArray array = new JSONArray();
		if (items != null && items.size() > 0) {
			// 订单收货时间
			Date receivedTime = orders.getReceivedTime();
			for (TradeOrderItem tradeOrderItem : items) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("itemId", tradeOrderItem.getId() == null ? "" : tradeOrderItem.getId());
				item.put("skuName", tradeOrderItem.getSkuName() == null ? "" : tradeOrderItem.getSkuName());
				item.put("productId", tradeOrderItem.getStoreSkuId() == null ? "" : tradeOrderItem.getStoreSkuId());
				item.put("mainPicPrl", tradeOrderItem.getMainPicPrl() == null ? "" : tradeOrderItem.getMainPicPrl());
				item.put("unitPrice", tradeOrderItem.getUnitPrice() == null ? "0" : tradeOrderItem.getUnitPrice());

				item.put("quantity", tradeOrderItem.getQuantity());
				item.put("skuTotalAmount", tradeOrderItem.getTotalAmount());
				item.put("skuActualAmount", tradeOrderItem.getActualAmount());
				item.put("preferentialPrice",
						tradeOrderItem.getPreferentialPrice() == null ? "0" : tradeOrderItem.getPreferentialPrice());
				// 服务保障
				String serviceAssurance = "0";
				// 订单是否完成
				if (orders.getIsComplete().ordinal() == 0) {
					serviceAssurance = getServiceAssurance(receivedTime, tradeOrderItem.getServiceAssurance());
				}

				item.put("serviceAssurance", serviceAssurance);
				item.put("propertiesIndb",
						tradeOrderItem.getPropertiesIndb() == null ? "" : tradeOrderItem.getPropertiesIndb());
				TradeOrderRefunds tradeOrderRefunds = tradeOrderItem.getTradeOrderRefunds();
				String refundId = "";
				// -1代表没有退款申请
				Integer refundStatus = -1;
				BigDecimal refundAmount = new BigDecimal(0);
				if (tradeOrderRefunds != null) {
					refundId = tradeOrderRefunds.getId();
					refundAmount = tradeOrderRefunds.getTotalAmount();
					if (tradeOrderRefunds.getRefundsStatus() != null) {
						// app退款状态转义
						refundStatus = OrderAppStatusAdaptor.convertAppRefundStatus(tradeOrderRefunds
								.getRefundsStatus());
					}
				}
				item.put("refundId", refundId);
				item.put("refundStatus", refundStatus);
				item.put("refundAmount", refundAmount);
				array.add(item);
			}
		}
		json.put("orderItems", array);
		json.put("height", 126);
		json.put("width", 126);
		return json;
	}

	// 退换货状态计算0:不支持，1:支持退换货
	private String getServiceAssurance(Date receivedTime, Integer serviceAssurance) {
		String flag = "0";
		if (receivedTime != null && serviceAssurance != 0) {
			Date nowDate = new Date();
			long nowTime = nowDate.getTime();
			long endTime = receivedTime.getTime() + serviceAssurance * 24 * 60 * 60 * 1000;
			if (nowTime < endTime) {
				flag = "1";
			}
		}
		return flag;
	}

	@Override
	public PageUtils<TradeOrder> findUserServiceOrderList(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		// begin update by wushp
		// 该状态与前端定义状态统一,100:未评价
		String status = (String) map.get("status");
		List<TradeOrder> list = new ArrayList<TradeOrder>();
		if (StringUtils.isNotBlank(status) && "100".equals(status)) {
			// 待评价订单查询
			list = tradeOrderMapper.findNotCommentServiceOrderList(map);
		} else {
			if (StringUtils.isNotBlank(status)) {
				// 订单支付中状态
				List<String> orderStatus = new ArrayList<String>();
				orderStatus.add(status);
				// 0为查询未支付状态下的订单，包含对应的是未支付和支付确认中的数据
				if (status.equals(String.valueOf(Constant.ZERO))) {
					// 支付确认中状态
					orderStatus.add(String.valueOf(OrderStatusEnum.BUYER_PAYING.ordinal()));
				} else if (status.equals(String.valueOf(Constant.TWO))) {
					// 2为查询已取消状态下的订单，包含对应的是（已取消，取消中，已拒收，拒收中）
					orderStatus.add(String.valueOf(OrderStatusEnum.CANCELING.ordinal()));
					orderStatus.add(String.valueOf(OrderStatusEnum.REFUSING.ordinal()));
					orderStatus.add(String.valueOf(OrderStatusEnum.REFUSED.ordinal()));
				}
				map.put("status", orderStatus);
			} else {
				map.put("status", null);
			}
			list = tradeOrderMapper.selectUserOrderList(map);
		}
		for (TradeOrder vo : list) {
			List<TradeOrderItem> items = tradeOrderItemMapper.selectTradeOrderItem(vo.getId());
			vo.setTradeOrderItem(items);
		}
		return new PageUtils<TradeOrder>(list);
		// end update by wushp
	}

	@Override
	public JSONObject findUserServiceOrderDetail(String orderId) throws ServiceException {
		UserTradeOrderDetailVo orders = tradeOrderMapper.selectUserOrderServiceDetail(orderId);
		List<TradeOrderItem> tradeOrderItems = tradeOrderItemMapper.selectTradeOrderItemOrDetail(orderId);
		// 判断订单是否评价appraise大于0，已评价
		Integer appraise = tradeOrderItemMapper.selectTradeOrderItemIsAppraise(orderId);

		// 查询店铺扩展信息
		String storeId = orders.getStoreInfo().getId();
		StoreInfoExt storeInfoExt = storeInfoExtService.getByStoreId(storeId);

		orders.setItems(tradeOrderItems);
		JSONObject json = getServiceJsonObj(orders, appraise, storeInfoExt);
		return json;
	}

	// 服务订单详细组装返回数据
	private JSONObject getServiceJsonObj(UserTradeOrderDetailVo orders, Integer appraise, StoreInfoExt storeInfoExt) {
		// 订单信息
		JSONObject json = new JSONObject();
		// 订单号
		json.put("orderId", orders.getId() == null ? "" : orders.getId());
		// 订单编号
		json.put("orderNo", orders.getOrderNo() == null ? "" : orders.getOrderNo());
		json.put("tradeNum", orders.getTradeNum() == null ? "" : orders.getTradeNum());
		// 订单状态
		json.put("orderStatus", OrderAppStatusAdaptor.convertAppOrderStatus(orders.getStatus()));
		// 订单支付倒计时计算
		/*
		 * Integer remainingTime = orders.getRemainingTime(); if (remainingTime
		 * != null) { remainingTime = remainingTime + 1800;
		 * json.put("remainingTime", remainingTime <= 0 ? "0" : remainingTime);
		 * } else { json.put("remainingTime", "0"); }
		 */
		OrderStatusEnum orderStatus = orders.getStatus();
		if (orderStatus != null && orderStatus.ordinal() == Constant.ZERO) {
			// 状态为未付款
			// 订单创建时间
			Date createTime = orders.getCreateTime();
			Date currentDate = new Date();
			// 支付到期毫秒
			long endTimes = createTime.getTime() + (Constant.THIRTH * 60 * 1000);
			long remainingTime = (endTimes - currentDate.getTime()) / 1000;
			// 支付剩余时间（精确）
			if (remainingTime > 0) {
				json.put("remainingTime", remainingTime);
			} else {
				json.put("remainingTime", 0);
			}
		} else {
			// 状态为非未付款
			json.put("remainingTime", 0);
		}

		// 订单描述
		json.put("remark", orders.getRemark() == null ? "" : orders.getRemark());
		// 订单总金额
		json.put("orderAmount", orders.getTotalAmount() == null ? "0" : orders.getTotalAmount());
		// 订单实付金额
		json.put("actualAmount", orders.getActualAmount() == null ? "0" : orders.getActualAmount());
		// 服务取消原因
		json.put("cancelReason", orders.getReason() == null ? "" : orders.getReason());
		int orderStatus1 = json.getInt("orderStatus");
		if (orderStatus1 == OrderStatusEnum.CANCELED.ordinal()) {
			// 服务取消时间
			String cancelTime = DateUtils.formatDate(orders.getUpdateTime(), "yyyy-MM-dd HH:mm:ss");
			json.put("cancelTime", cancelTime == null ? "" : cancelTime);
		}
		OrderStatusEnum status = orders.getStatus();
		// 是否是拒绝服务 0:否，1：是
		if (status.ordinal() == 4) {
			json.put("isRefund", "1");
		} else {
			json.put("isRefund", "0");
		}
		// 下单时间
		json.put("orderSubmitOrderTime",
				orders.getCreateTime() != null ? DateUtils.formatDate(orders.getCreateTime(), "yyyy-MM-dd HH:mm:ss")
						: "");
		// 出发时间--对应实物订单发货时间
		json.put(
				"orderDeliveryTime",
				orders.getDeliveryTime() != null ? DateUtils.formatDate(orders.getDeliveryTime(), "yyyy-MM-dd HH:mm:ss")
						: "");
		// 服务完成时间--对应实物订单收货时间
		json.put(
				"orderConfirmGoodTime",
				orders.getReceivedTime() != null ? DateUtils.formatDate(orders.getReceivedTime(), "yyyy-MM-dd HH:mm:ss")
						: "");
		// 优惠金额
		json.put("preferentialPrice", orders.getPreferentialPrice() == null ? "" : orders.getPreferentialPrice());
		// 订单评价类型0：未评价，1：已评价 1已投诉
		json.put("orderIsComment", appraise > 0 ? Constant.ONE : Constant.ZERO);
		// 订单投诉状态 0未投诉
		json.put("compainStatus", orders.getCompainStatus() == null ? "" : orders.getCompainStatus().ordinal());
		json.put("payWay", orders.getPayWay().ordinal() + "");
		// 4 支付类型
		TradeOrderPay tradeOrderPay = orders.getTradeOrderPay();
		if (tradeOrderPay != null) {
			json.put("payType", tradeOrderPay.getPayType().ordinal());
			json.put("payAmount", tradeOrderPay.getPayAmount());
			json.put(
					"payTime",
					tradeOrderPay.getPayTime() == null ? "" : DateUtils.formatDate(tradeOrderPay.getPayTime(),
							"yyyy-MM-dd HH:mm:ss"));
		} else {
			// 支付方式
			json.put("payType", "");
			// 支付金额
			json.put("payAmount", "");
			// 支付时间
			json.put("payTime", "");
		}

		TradeOrderInvoice tradeOrderInvoice = orders.getTradeOrderInvoice();
		String head = "";
		String context = "";
		if (tradeOrderInvoice != null) {
			head = tradeOrderInvoice.getHead();
			context = tradeOrderInvoice.getContext();
		}
		// 发票抬头信息
		json.put("orderInvoiceTitle", head);
		// 发票内容
		json.put("orderInvoiceContent", context);

		// 店铺信息
		StoreInfo storeInfo = orders.getStoreInfo();
		String storeId = "";
		String storeName = "";
		String storeMobile = "";
		if (storeInfo != null) {
			storeId = storeInfo.getId();
			storeName = storeInfo.getStoreName();
			// Begin 客服电话优先取store_info_ext表中的service_phone，如为空再选店铺号码 add by
			// zengj
			storeMobile = storeInfo.getStoreInfoExt() == null
					|| StringUtils.isBlank(storeInfo.getStoreInfoExt().getServicePhone()) ? storeInfo.getMobile()
					: storeInfo.getStoreInfoExt().getServicePhone();
			// End 客服电话优先取store_info_ext表中的service_phone，如为空再选店铺号码 add by zengj
		}
		// 店铺名称
		json.put("orderShopName", storeName);
		// 店铺的服务电话
		json.put("orderShopMobile", storeMobile);
		// 3 物流信息
		TradeOrderLogistics tradeOrderLogistics = orders.getTradeOrderLogistics();
		String consigneeName = "";
		String mobile = "";
		String allAddress = "";
		if (tradeOrderLogistics != null) {
			consigneeName = tradeOrderLogistics.getConsigneeName();
			mobile = tradeOrderLogistics.getMobile();
			String area = tradeOrderLogistics.getArea() == null ? "" : tradeOrderLogistics.getArea();
			String address = tradeOrderLogistics.getAddress() == null ? "" : tradeOrderLogistics.getAddress();
			allAddress = area + address;
		}
		// 联系人姓名
		json.put("orderConsignee", consigneeName);
		// 联系人电话
		json.put("orderConsigneeMobile", mobile);
		// 服务地址
		json.put("orderConsigneeAddress", allAddress);
		// 预约服务时间--对应实物订单的选择的送货时间
		String strPickUpTime = orders.getPickUpTime();
		json.put("pickUpTime", strPickUpTime == null ? "" : strPickUpTime);

		// 商品信息
		List<TradeOrderItem> items = orders.getItems();
		Map<String, Object> item = new HashMap<String, Object>();
		if (items != null && items.size() > 0) {
			for (TradeOrderItem tradeOrderItem : items) {
				// 商品名称
				item.put("skuName", tradeOrderItem.getSkuName() == null ? "" : tradeOrderItem.getSkuName());
				// 商品id
				item.put("productId", tradeOrderItem.getStoreSkuId() == null ? "" : tradeOrderItem.getStoreSkuId());
				// 商品图片
				item.put("mainPicPrl", tradeOrderItem.getMainPicPrl() == null ? "" : tradeOrderItem.getMainPicPrl());
				// 商品单价
				item.put("unitPrice", tradeOrderItem.getUnitPrice() == null ? "0" : tradeOrderItem.getUnitPrice());
				// 购买商品的数量
				item.put("quantity", tradeOrderItem.getQuantity());
			}
		}
		// 商品信息
		json.put("orderItems", item);
		json.put("height", 126);
		json.put("width", 126);
		return json;
	}

	/**
	 * 
	 * @desc 查询买家实物订单各状态订单数量
	 * @author zengj
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public List<TradeOrderStatusVo> selectBuyerPhysicalOrderCount(String userId) {
		return tradeOrderMapper.selectBuyerPhysicalOrderCount(userId);
	}

	// begin add by wushp 20160823
	/**
	 * 
	 * @desc 微信查询买家实物订单各状态订单数量
	 * @author wushp
	 * @param userI 用户ID
	 * @return list
	 */
	public List<TradeOrderStatusVo> selectWxBuyerPhysicalOrderCount(String userId) {
		return tradeOrderMapper.selectWxBuyerPhysicalOrderCount(userId);
	}

	// end add by wushp 20160823

	/**
	 * 
	 * @desc 查询买家服务订单各状态订单数量
	 * @author zengj
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public List<TradeOrderStatusVo> selectBuyerServiceOrderCount(String userId) {
		return tradeOrderMapper.selectBuyerServiceOrderCount(userId);
	}

	/**
	 * DESC: 首页交易订单统计
	 * 
	 * @author LIU.W
	 * @param storeId
	 * @return
	 */
	public List<TradeOrderStatisticsVo> findTradeOrderStatistics(String storeId) throws ServiceException {

		try {
			return tradeOrderMapper.selectTradeOrderStatistics(storeId);
		} catch (Exception e) {
			throw new ServiceException("成交统计", e);
		}
	}

	/**
	 * DESC: 右下角弹窗交易订单统计
	 * 
	 * @author LIU.W
	 * @param storeId
	 *            店铺ID
	 * @param type
	 *            类型 1= 订单 2=售后单3=纠纷单
	 * @return
	 */
	public Map<String, Object> findWindowTipOrderCounts(String storeId, String type) throws ServiceException {

		try {
			return tradeOrderMapper.selectWindowTipOrderCounts(storeId, type);
		} catch (Exception e) {
			throw new ServiceException("成交统计", e);
		}

	}

	@Override
	public PageUtils<ERPTradeOrderVo> erpSelectByParams(Map<String, Object> params, int pageSize, int pageNum)
			throws ServiceException {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize, true);
		List<ERPTradeOrderVo> result = tradeOrderMapper.ERPSelectByParams(params);
		return new PageUtils<ERPTradeOrderVo>(result);
	}

	@Override
	public List<ERPTradeOrderVo> erpSelectByParam(Map<String, Object> params) throws ServiceException {
		// TODO Auto-generated method stub
		List<ERPTradeOrderVo> result = tradeOrderMapper.ERPSelectByParams(params);
		return result;
	}

	@Override
	public TradeOrder erpSelectByOrderId(String orderId) throws ServiceException {
		// TODO Auto-generated method stub
		// 订单
		TradeOrder tradeOrder = tradeOrderMapper.selectByOrderId(orderId);
		// Begin 12051 add by wusw 20160811
		// 设置活动名称
		tradeOrder.setActivityName(this.getOrderActivityName(tradeOrder.getActivityId(), tradeOrder.getActivityType()));
		// End 12051 add by wusw 20160811
		// 交易项信息（包括商品基本信息）
		List<TradeOrderItem> tradeOrderItemList = tradeOrderItemMapper.selectTradeOrderItem(orderId);
		tradeOrder.setTradeOrderItem(tradeOrderItemList);

		// 支付信息
		TradeOrderPay tradeOrderPay = tradeOrderPayMapper.selectByOrderId(orderId);
		tradeOrder.setTradeOrderPay(tradeOrderPay);

		// 发票信息
		TradeOrderInvoice tradeOrderInvoice = tradeOrderInvoiceMapper.selectByOrderId(orderId);
		tradeOrder.setTradeOrderInvoice(tradeOrderInvoice);

		// 收货信息
		TradeOrderLogistics tradeOrderLogistics = tradeOrderLogisticsMapper.selectByOrderId(orderId);
		tradeOrder.setTradeOrderLogistics(tradeOrderLogistics);

		return tradeOrder;
	}

	@Override
	public TradeOrder erpSelectByServiceOrderId(String orderId) throws ServiceException {
		// TODO Auto-generated method stub
		// 订单
		TradeOrder tradeOrder = tradeOrderMapper.selectByOrderId(orderId);
		// Begin 12051 add by wusw 20160811
		// 设置活动名称
		tradeOrder.setActivityName(this.getOrderActivityName(tradeOrder.getActivityId(), tradeOrder.getActivityType()));
		// End 12051 add by wusw 20160811

		// 交易项信息（包括商品基本信息）
		List<TradeOrderItem> tradeOrderItemList = tradeOrderItemMapper.selectTradeOrderItem(orderId);
		tradeOrder.setTradeOrderItem(tradeOrderItemList);

		// 支付信息
		TradeOrderPay tradeOrderPay = tradeOrderPayMapper.selectByOrderId(orderId);
		tradeOrder.setTradeOrderPay(tradeOrderPay);

		// 交易订单项消费详细表(仅服务型商品有)
		List<TradeOrderItemDetail> tradeOrderItemDetail = tradeOrderItemDetailMapper.selectByOrderItemId(orderId);
		tradeOrder.setTradeOrderItemDetail(tradeOrderItemDetail);

		return tradeOrder;
	}

	@Override
	public TradeOrder selectOrderByInfo(Map<String, Object> map) throws Exception {

		return tradeOrderMapper.selectOrderByInfo(map);
	}

	/**
	 * 根据条件查询订单交易号
	 * </p>
	 * 
	 * @author yangq
	 * @param params
	 * @return
	 */
	@Override
	public TradeOrder getByTradeNum(String tradeNum) throws Exception {
		TradeOrder tradeOrder = tradeOrderMapper.selectByParamsTrade(tradeNum);
		return tradeOrder;
	}

	// Begin modified by maojj 2016-07-26 添加分布式事务处理机制
	/**
	 * 
	 * @desc 消费码验证
	 * @author zengj
	 * @param userId
	 *            当前登录ID
	 * @param storeId
	 *            店铺ID
	 * @param consumeCodes
	 *            消费码集合，多个验证码一起验证
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, String> updateServiceOrderConsume(String userId, String storeId, List<String> consumeCodes)
			throws Exception {
		// 返回值Map
		Map<String, String> resultMap = new HashMap<String, String>();

		List<String> rpcIdList = new ArrayList<String>();
		List<StockAdjustVo> stockAdjustList = new ArrayList<StockAdjustVo>();
		try {
			// 查询消费码参数
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("storeId", storeId);

			// 验证成功的消费码
			StringBuffer successResult = new StringBuffer();
			// 验证失败的消费码
			StringBuffer failResult = new StringBuffer();
			// 当前时间
			Calendar calendar = Calendar.getInstance();
			for (String consumeCode : consumeCodes) {
				params.put("consumeCode", consumeCode);
				// 根据消费码查询订单信息
				Map<String, Object> data = this.selectOrderDetailByConsumeCode(params);
				// 没有找到订单信息
				if (data == null || data.get("id") == null || data.get("orderItemId") == null
						|| data.get("detailId") == null) {
					failResult.append(consumeCode + "|抱歉,没有对应的订单信息,请核实消费码的准确性;");
					continue;
				}
				// 消费码有效开始时间
				Date startTime = data.get("startTime") == null ? null : (Date) data.get("startTime");
				// 消费码有效结束时间
				Date endTime = data.get("endTime") == null ? null : (Date) data.get("endTime");
				if (ConsumeStatusEnum.noConsume.ordinal() != ((Long) data.get("status"))) {
					// 消费码状态不为未消费
					failResult.append(consumeCode + "|抱歉,消费码失效,请输入有效的消费码;");
					continue;
				} else if (startTime != null && calendar.getTime().getTime() < startTime.getTime()) {
					// 如果当前时间小于消费码有效开始时间，说明该消费码暂时还不能消费
					failResult.append(consumeCode + "|抱歉,消费码还未到使用日期;");
					continue;
				} else if (endTime != null && calendar.getTime().getTime() > endTime.getTime()) {
					// 如果当前时间大于消费码有效结束时间，说明该消费码已过期
					failResult.append(consumeCode + "|抱歉,消费码已过期;");
					continue;
				} else {
					TradeOrder tradeOrder = new TradeOrder();
					// 订单编号
					String orderNo = data.get("orderNo").toString();
					// 订单交易号
					String tradeNum = data.get("tradeNum").toString();
					// 店主ID
					String storeUserId = data.get("storeUserId") == null ? null : data.get("storeUserId").toString();;
					// 当前登录用户ID
					tradeOrder.setUserId(userId);
					// 店铺ID
					tradeOrder.setStoreId(storeId);
					// 订单ID
					tradeOrder.setId(data.get("id").toString());
					// 查询消费码对应的订单项信息
					TradeOrderItem tradeOrderItem = tradeOrderItemMapper.selectOrderItemById(data.get("orderItemId")
							.toString());
					List<TradeOrderItem> tradeOrderItemList = new ArrayList<TradeOrderItem>();
					tradeOrderItemList.add(tradeOrderItem);
					tradeOrder.setTradeOrderItem(tradeOrderItemList);
					// 消费后调整库存
					StockAdjustVo stockAdjustVo = buildServiceOrderStock(tradeOrder,
							StockOperateEnum.ACTIVITY_SEND_OUT_GOODS, 1, rpcIdList);
					stockAdjustList.add(stockAdjustVo);

					// 验证通过，将该消费码改为已消费
					tradeOrderItemDetailMapper.updateStatusWithConsumed(data.get("detailId").toString());
					// 单价
					BigDecimal unitPrice = (data.get("unitPrice") == null ? BigDecimal.ZERO : new BigDecimal(data.get(
							"unitPrice").toString()));
					BigDecimal totalAmount = (data.get("totalAmount") == null ? BigDecimal.ZERO : new BigDecimal(data
							.get("totalAmount").toString()));

					BalancePayTradeVo payTradeVo = new BalancePayTradeVo();
					payTradeVo.setAmount(unitPrice); // 交易金额
					payTradeVo.setCheckAmount(totalAmount);// 订单总金额
					payTradeVo.setPayUserId(userId);// 用户id
					payTradeVo.setTradeNum(tradeNum);// 交易号
					payTradeVo.setTitle("订单收入");// 标题
					payTradeVo.setBusinessType(BusinessTypeEnum.SPEND_SERVICE_ORDER);// 业务类型
					payTradeVo.setServiceFkId(data.get("id").toString());// 服务单id
					payTradeVo.setServiceNo(orderNo);// 服务单号，例如订单号、退单号
					payTradeVo.setRemark("订单[" + orderNo + "]");// 备注信息
					payTradeVo.setIncomeUserId(storeUserId);// 收款人，根据业务不同设置不同的id

					// 消费完，增加积分
					pointsBuriedService.doConsumePoints(data.get("userId").toString(), unitPrice);

					stockManagerService.updateStock(stockAdjustVo);
					// 调用dubbo接口
					BaseResultDto result = payTradeServiceApi.balanceTrade(payTradeVo);
					if (result == null || !TradeErrorEnum.SUCCESS.getName().equals(result.getCode())) {
						throw new ServiceException("调用云钱包dubbo失败==code==" + (result == null ? null : result.getCode())
								+ "==message==" + (result == null ? null : result.getMsg()));
					}

					// 自动评价计时消息
					tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_finish_evaluate_timeout,
							tradeOrder.getId());
				}
			}
			// Begin added by maojj 给ERP发消息去生成出入库单据
			// stockMQProducer.sendMessage(stockAdjustList);
			// End added by maojj

			// 这里返回的key与BaseController中一样，就没有重新定义，先写死
			// 验证成功的消费码信息,多条以逗号隔开。信息与消费码以|隔开
			resultMap.put("success", successResult.toString());
			// 验证失败的消费码信息,多条以逗号隔开。信息与消费码以|隔开
			resultMap.put("failure", failResult.toString());
		} catch (Exception e) {
			// added by maojj 通知回滚库存修改
			rollbackMQProducer.sendStockRollbackMsg(rpcIdList);
			throw e;
		}

		return resultMap;
	}

	/**
	 * 生成账号规则：固定字符串+length长度的随机数字
	 *
	 * @param str
	 *            固定字符串
	 * @param length
	 *            随机数字长度
	 * @return String 账号或者密码
	 */
	public static String generateAccount(String str, int length) {
		Random random = new Random();
		String retStr = "";
		for (int i = 0; i < length; i++) {
			retStr = retStr + random.nextInt(Constant.TEN);
		}
		retStr = str + retStr;
		return retStr;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateTradeOrderByTradeNum(TradeOrder tradeOrder) throws Exception {
		TradeOrder orders = tradeOrderMapper.selectByParamsTrade(tradeOrder.getTradeNum());
		tradeOrder.setId(orders.getId());
		tradeOrderMapper.updateTradeOrderByTradeNum(tradeOrder);
		tradeMessageService.sendSmsByCreateOrder(orders);
		tradeMessageService.sendPosMessage(new SendMsgParamVo(tradeOrder), SendMsgType.createOrder);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateRechargeOrderByTradeNum(TradeOrder tradeOrder) throws Exception {
		TradeOrder orders = tradeOrderMapper.selectByParamsTrade(tradeOrder.getTradeNum());
		tradeOrder.setId(orders.getId());
		tradeOrderMapper.updateTradeOrderByTradeNum(tradeOrder);
	}

	@Override
	public int selectTradeOrderInfo(Map<String, Object> map) throws Exception {
		int result = tradeOrderMapper.selectTradeOrderInfo(map);
		return result;
	}

	@Override
	public int selectOrderStatusByTradeNum(String tradeNum) throws Exception {
		int result = tradeOrderMapper.selectOrderStatusByTradeNum(tradeNum);
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateOrderDelivery(TradeOrder tradeOrder) throws Exception {
		String rpcId = null;
		StockAdjustVo stockAdjustVo = null;
		if (tradeOrder.getStatus() == OrderStatusEnum.TO_BE_SIGNED) {// 发货
			this.updateOrderStatus(tradeOrder);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("orderId", tradeOrder.getId());
			List<TradeOrderItem> tradeOrderItem = this.findTradeOrderItems(map);
			tradeOrder.setTradeOrderItem(tradeOrderItem);
			// 锁定库存
			try {
				rpcId = UuidUtils.getUuid();
				stockAdjustVo = buildDeliveryStock(tradeOrder, rpcId);

				// 发送计时消息
				if (ActivityTypeEnum.GROUP_ACTIVITY == tradeOrder.getActivityType()) {
					tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_confirm_group_timeout, tradeOrder.getId());
				} else {
					tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_confirm_timeout, tradeOrder.getId());
				}

				// Begin 1.0.Z 增加订单操作记录 add by zengj
				tradeOrderLogService.insertSelective(new TradeOrderLog(tradeOrder.getId(),
						tradeOrder.getUpdateUserId(), tradeOrder.getStatus().getName(), tradeOrder.getStatus()
								.getValue()));
				// End 1.0.Z 增加订单操作记录 add by zengj

				// 发送短信
				tradeMessageService.sendSmsByShipments(tradeOrder);
				// added by maojj 给ERP发消息去生成出入库单据
				// 库存调整-放到最后处理
				stockManagerService.updateStock(stockAdjustVo);
				// stockMQProducer.sendMessage(stockAdjustVo);
			} catch (Exception e) {
				logger.info("pos 发货锁定库存发生异常", e);
				// added by maojj
				rollbackMQProducer.sendStockRollbackMsg(rpcId);
				throw e;
			}

		} else { // 收货
			try {
				this.updateWithConfirm(tradeOrder);
			} catch (Exception e) {
				logger.info("pos 发货发生异常", e);
				throw new ServiceException("发货失败", e);
			}
		}

	}

	/**
	 * 修改发货库存
	 * 
	 * @param tradeOrder
	 *            订单
	 * @return StockAdjustVo
	 */
	private StockAdjustVo buildDeliveryStock(TradeOrder tradeOrder, String rpcId) {
		StockAdjustVo stockAdjustVo = new StockAdjustVo();
		// Begin added by maojj 2016-07-26
		stockAdjustVo.setRpcId(rpcId);
		// End added by maojj 2016-07-26

		stockAdjustVo.setOrderId(tradeOrder.getId());
		stockAdjustVo.setOrderNo(tradeOrder.getOrderNo());
		stockAdjustVo.setOrderResource(tradeOrder.getOrderResource());
		stockAdjustVo.setOrderType(tradeOrder.getType());
		stockAdjustVo.setStoreId(tradeOrder.getStoreId());
		// 发货
		if (tradeOrder.getStatus() == OrderStatusEnum.TO_BE_SIGNED) {
			// 活动订单发货
			if (tradeOrder.getActivityType() == ActivityTypeEnum.SALE_ACTIVITIES) {
				stockAdjustVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_SEND_OUT_GOODS);
			} else {
				stockAdjustVo.setStockOperateEnum(StockOperateEnum.SEND_OUT_GOODS);
			}
		} else if (tradeOrder.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED) {
			// 订单完成
			if (tradeOrder.getActivityType() == ActivityTypeEnum.SALE_ACTIVITIES) {
				stockAdjustVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_ORDER_COMPLETE);
			} else {
				stockAdjustVo.setStockOperateEnum(StockOperateEnum.PLACE_ORDER_COMPLETE);
			}
		}
		stockAdjustVo.setUserId(tradeOrder.getUserId());

		List<AdjustDetailVo> adjustDetailList = Lists.newArrayList();
		List<TradeOrderItem> orderItemList = tradeOrder.getTradeOrderItem();
		if (CollectionUtils.isEmpty(orderItemList)) {
			orderItemList = tradeOrderItemMapper.selectOrderItemListById(tradeOrder.getId());
		}
		for (TradeOrderItem item : orderItemList) {
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
			adjustDetailList.add(detail);
		}
		stockAdjustVo.setAdjustDetailList(adjustDetailList);
		return stockAdjustVo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertPosTradeOrder(Object entity) throws Exception {
		if (entity instanceof TradeOrder) {
			TradeOrder tradeOrder = (TradeOrder) entity;
			tradeOrderMapper.insertSelective(tradeOrder);
			TradeOrderPay tradeOrderPay = tradeOrder.getTradeOrderPay();
			TradeOrderLogistics orderLogistics = tradeOrder.getTradeOrderLogistics();
			TradeOrderInvoice invoice = tradeOrder.getTradeOrderInvoice();
			if (tradeOrderPay != null) {
				tradeOrderPayMapper.insertSelective(tradeOrderPay);
			}
			if (orderLogistics != null) {
				tradeOrderLogisticsMapper.insertSelective(tradeOrder.getTradeOrderLogistics());
			}
			if (invoice != null) {
				tradeOrderInvoiceMapper.insertSelective(invoice);
			}

			List<TradeOrderItem> itemList = tradeOrder.getTradeOrderItem();

			// for (TradeOrderItem item : itemList) {
			// tradeOrderItemMapper.insertSelective(item);
			// }
			tradeOrderItemMapper.insertBatch(itemList);

			// Begin 1.0.Z 增加订单操作记录 add by zengj
			tradeOrderLogService.insertSelective(new TradeOrderLog(tradeOrder.getId(), tradeOrder.getUserId(),
					tradeOrder.getStatus().getName(), tradeOrder.getStatus().getValue()));
			// End 1.0.Z 增加订单操作记录 add by zengj
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateActivityCouponsStatus(Map<String, Object> map) throws Exception {
		activityCouponsRecordService.updateActivityCouponsStatus(map);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMyOrderStatus(TradeOrder tradeOrder) throws Exception {

		this.update(tradeOrder);

		tradeMessageService.saveSysMsg(tradeOrder, SendMsgType.createOrder);

		TradeOrder order = tradeOrderMapper.selectByPrimaryKey(tradeOrder.getId());
		tradeOrderMapper.updateTradeOrderByTradeNum(tradeOrder);

		// Begin added by maojj 2016-08-24 提货码支付成功后才生成
		if (order.getPickUpType() == PickUpTypeEnum.TO_STORE_PICKUP) {
			order.setPickUpCode(tradeOrder.getPickUpCode());
		}
		// End added by maojj 2016-08-24

		// Begin 1.0.Z 增加订单支付操作记录 add by zengj
		tradeOrderLogService.insertSelective(new TradeOrderLog(tradeOrder.getId(), tradeOrder.getUserId(), tradeOrder
				.getStatus().getName(), tradeOrder.getStatus().getValue()));
		// End 1.0.Z 增加订单操作记录 add by zengj

		tradeMessageService.sendSmsByCreateOrder(order);

		SendMsgParamVo sendMsgParamVo = new SendMsgParamVo(tradeOrder);
		tradeMessageService.sendPosMessage(sendMsgParamVo, SendMsgType.createOrder);
		tradeMessageService.sendSellerAppMessage(sendMsgParamVo, SendMsgType.createOrder);
	}

	@Override
	public JSONObject orderCountDown(JSONObject jsonData) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateTradeOrderByTradeNumIsOder(TradeOrder tradeOrder) throws Exception {
		tradeOrderMapper.updateTradeOrderByTradeNumIsOder(tradeOrder);
	}

	/**
	 * @desc TODO Add a description 
	 *
	 * @param map
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public PageUtils<TradeOrder> selectPosOrderListByParams(Map<String, Object> map, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrder> result = tradeOrderMapper.selectPosOrderListForSeller(map);
		if (result == null) {
			result = new ArrayList<TradeOrder>();
		}
		return new PageUtils<TradeOrder>(result);
	}

	/**
	 * 更新订单信息
	 * @param tradeOrder
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateByPrimaryKeySelective(TradeOrder tradeOrder) {
		tradeOrderMapper.updateByPrimaryKeySelective(tradeOrder);
	}

	/**
	 * 货币格式化,保留两位小数
	 * @param b
	 */
	private String moneyFormat(BigDecimal b) {
		if (b != null) {
			return String.format("%.2f", b);
		}
		return "";
	}

	/**
	* 查询导出POS销售单列表
	* @desc TODO Add a description 
	* @author zengj
	* @param params
	* @return
	*/
	public List<Map<String, Object>> selectPosOrderExportList(Map<String, Object> params) {
		return tradeOrderMapper.selectPosOrderExportList(params);
	}

	// Begin 重构4.1 add by wusw
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderServiceApi#selectServiceStoreOrderByParams(java.util.Map)
	 */
	@Override
	public PageUtils<TradeOrder> findServiceStoreOrderByParams(Map<String, Object> params, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrder> result = tradeOrderMapper.selectServiceStoreOrderList(params);
		/*
		 * if (result == null) { result = new ArrayList<TradeOrder>(); } else {
		 * for (TradeOrder order : result) { List<TradeOrderItem> orderItem =
		 * tradeOrderItemMapper.selectOrderItemListById(order.getId());
		 * order.setTradeOrderItem(orderItem); } }
		 */
		return new PageUtils<TradeOrder>(result);
	}

	// End 重构4.1 add by wusw

	// Begin 重构4.1 add by wusw
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderServiceApi#findServiceStoreOrderForExport(java.util.Map)
	 */
	@Override
	public PageUtils<TradeOrderExportVo> findServiceStoreOrderForExport(Map<String, Object> params, int pageNumber,
			int pageSize) throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradeOrder> list = tradeOrderMapper.selectServiceStoreOrderList(params);
		List<TradeOrderExportVo> result = new ArrayList<TradeOrderExportVo>();
		if (list != null) {
			for (TradeOrder order : list) {
				Map<String, String> orderStatusMap = OrderStatusEnum.convertViewStatus(order.getType());
				List<TradeOrderItem> itemList = order.getTradeOrderItem();
				if (itemList != null) {
					TradeOrderItem item = itemList.get(0);
					TradeOrderExportVo vo = new TradeOrderExportVo();
					vo.setOrderNo(order.getOrderNo());
					vo.setCreateTime(DateUtils.formatDate(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
					vo.setUserPhone(order.getUserPhone());
					vo.setSkuName(item.getSkuName());
					vo.setUnitPrice(item.getUnitPrice());
					vo.setQuantity(item.getQuantity() == null ? "" : item.getQuantity().toString());
					vo.setActualAmount(item.getActualAmount());
					vo.setStatus(orderStatusMap.get(order.getStatus().getName()));
					// Begin 重构4.1 add by wusw 20160727
					if (order.getPayWay() == PayWayEnum.OFFLINE_CONFIRM_AND_PAY) {
						vo.setPayType("线下确认价格并当面支付");
					} else {
						vo.setPayType(order.getPayWay().getValue());
					}
					// End 重构4.1 add by wusw 20160727
					BigDecimal quantity = new BigDecimal(item.getQuantity());
					vo.setTotalAmount(item.getUnitPrice().multiply(quantity));

					result.add(vo);
				}
			}
		}
		return new PageUtils<TradeOrderExportVo>(result);
	}

	// End 重构4.1 add by wusw

	// Begin 重构4.1 add by wusw
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderServiceApi#findServiceStoreOrderDetail(java.lang.String)
	 */
	@Override
	public TradeOrderVo findServiceStoreOrderDetail(String orderId) throws ServiceException {
		TradeOrderVo vo = tradeOrderMapper.selectServiceStoreOrderDetail(orderId);
		if (vo != null) {
			// 获取订单活动信息
			Map<String, Object> activityMap = getActivity(vo.getActivityType(), vo.getActivityId());
			String activityName = activityMap.get("activityName") == null ? null : activityMap.get("activityName")
					.toString();
			ActivitySourceEnum activitySource = activityMap.get("activitySource") == null ? null
					: (ActivitySourceEnum) activityMap.get("activitySource");
			vo.setActivityName(activityName);
			vo.setActivitySource(activitySource);
		}
		return vo;
	}

	// End 重构4.1 add by wusw

	// Begin 重构4.1 add by zhulq
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderServiceApi#findRechargeOrder
	 */
	@Transactional(readOnly = true)
	@Override
	public PageUtils<TradeOrderRechargeVo> findRechargeOrder(TradeOrderRechargeVo vo, int pageNumber, int pageSize)
			throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		if (vo.getIds() != null && vo.getIds().length <= 0) {
			vo.setIds(null);
		}
		List<TradeOrderRechargeVo> result = tradeOrderMapper.selectRechargeOrder(vo);
		return new PageUtils<TradeOrderRechargeVo>(result);
	}

	// End 重构4.1 add by zhulq

	// Begin 重构4.1 add by wusw
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderServiceApi#findServiceStoreOrderForOperateByParams(java.util.Map, int, int)
	 */
	@Override
	public PageUtils<PhysicsOrderVo> findServiceStoreOrderForOperateByParams(Map<String, Object> params,
			int pageNumber, int pageSize) throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<PhysicsOrderVo> result = tradeOrderMapper.selectServiceStoreListForOperate(params);
		if (result == null) {
			result = new ArrayList<PhysicsOrderVo>();
		} else {
			for (PhysicsOrderVo vo : result) {
				this.convertOrderStatus(vo);
			}
		}
		return new PageUtils<PhysicsOrderVo>(result);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderServiceApi#findServiceStoreOrderForOperateExport(java.util.Map)
	 */
	@Override
	public List<PhysicsOrderVo> findServiceStoreOrderForOperateExport(Map<String, Object> params)
			throws ServiceException {
		List<PhysicsOrderVo> result = tradeOrderMapper.selectServiceStoreListForOperate(params);
		if (result != null) {
			for (PhysicsOrderVo vo : result) {
				this.convertOrderStatus(vo);
			}
		}
		return result;
	}

	/**
	 * 
	 * @Description: 服务店订单状态和支付方式转换
	 * @param vo   
	 * @return void  
	 * @author wusw
	 * @date 2016年7月17日
	 */
	private void convertOrderStatus(PhysicsOrderVo vo) {
		switch (vo.getStatus()) {
			case DROPSHIPPING:
				vo.setOrderStatusName("待派单");
				break;
			case TO_BE_SIGNED:
				vo.setOrderStatusName("已派单");
				break;
			case HAS_BEEN_SIGNED:
				vo.setOrderStatusName("服务完成");
				break;
			case CANCELED:
			case CANCELING:
			case TRADE_CLOSED:
				vo.setOrderStatusName("交易关闭");
				break;
			case REFUSED:
			case REFUSING:
				vo.setOrderStatusName("订单取消");
				break;
			default:
				vo.setOrderStatusName(vo.getStatus().getValue());
				break;
		}
		if (vo.getPayWay() == PayWayEnum.PAY_ONLINE) {
			if (vo.getPayType() != null) {
				vo.setOrderPayTypeName(vo.getPayType().getValue());
			}
			// Begin 重构4.1 add by wusw 20160720
		} else if (vo.getPayWay() == PayWayEnum.OFFLINE_CONFIRM_AND_PAY) {
			vo.setOrderPayTypeName("线下确认价格并当面支付");
		}
		// End 重构4.1 add by wusw 20160720
	}

	// End 重构4.1 add by wusw

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderServiceApi#findRechargeOrderByParams(java.util.Map)
	 */
	@Override
	public List<TradeOrderRechargeVo> findRechargeOrderByParams(Map<String, Object> params) throws ServiceException {
		return tradeOrderMapper.selectRechargeOrderExport(params);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderServiceApi#findRechargeOrderDetail(java.lang.String)
	 */
	@Transactional(readOnly = true)
	@Override
	public List<TradeOrderRechargeVo> findRechargeOrderDetail(TradeOrderRechargeVo vo) throws ServiceException {
		return tradeOrderMapper.selectRechargeOrderDetail(vo);
	}

	// Begin 重构4.1 add by wusw 20160719
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderService#findOrderByParams(java.util.Map, int, int)
	 */
	@Override
	public PageUtils<ERPTradeOrderVo> findOrderForFinanceByParams(Map<String, Object> params, int pageNumber,
			int pageSize) throws ServiceException {
		PageHelper.startPage(pageNumber, pageSize);
		// 参数转换处理（例如订单状态）
		this.convertParams(params);
		List<ERPTradeOrderVo> result = tradeOrderMapper.findOrderForFinanceByParams(params);
		if (result == null) {
			result = new ArrayList<ERPTradeOrderVo>();
		}
		return new PageUtils<ERPTradeOrderVo>(result);
	}

	/**
	 * 
	 * @Description: 财务系统订单 接口参数处理
	 * @param params   
	 * @return void  
	 * @author wusw
	 * @date 2016年7月19日
	 */
	private void convertParams(Map<String, Object> params) {
		if (params == null) {
			params = new HashMap<String, Object>();
		} else {
			// 订单类型转换，注意充值订单包括话费充值和流量充值
			if (params.get("type") == null || StringUtils.isBlank(params.get("type").toString())) {
				params.remove("type");
			} else {
				List<OrderTypeEnum> typeList = new ArrayList<OrderTypeEnum>();
				switch (params.get("type").toString()) {
					case "0":
						typeList.add(OrderTypeEnum.PHYSICAL_ORDER);
						break;
					case "1":
						typeList.add(OrderTypeEnum.SERVICE_STORE_ORDER);
						break;
					case "2":
						typeList.add(OrderTypeEnum.PHONE_PAY_ORDER);
						typeList.add(OrderTypeEnum.TRAFFIC_PAY_ORDER);
						break;
					default:
						break;
				}
				params.put("type", typeList);

			}
			// 由于不同的订单状态描述针对不同的类型订单，所以针对订单状态转换时需要带上相应的订单类型参数
			if (params.get("status") == null || StringUtils.isBlank(params.get("status").toString())) {
				params.remove("status");
			} else {
				List<OrderStatusEnum> statusList = new ArrayList<OrderStatusEnum>();
				List<OrderTypeEnum> orderTypeList = new ArrayList<OrderTypeEnum>();
				switch (params.get("status").toString()) {
					case "0":
						statusList.add(OrderStatusEnum.BUYER_PAYING);
						orderTypeList.add(OrderTypeEnum.PHYSICAL_ORDER);
						orderTypeList.add(OrderTypeEnum.SERVICE_STORE_ORDER);
						break;
					case "1":
						statusList.add(OrderStatusEnum.DROPSHIPPING);
						orderTypeList.add(OrderTypeEnum.PHYSICAL_ORDER);
						break;
					case "2":
						statusList.add(OrderStatusEnum.TO_BE_SIGNED);
						orderTypeList.add(OrderTypeEnum.PHYSICAL_ORDER);
						break;
					case "3":
						statusList.add(OrderStatusEnum.HAS_BEEN_SIGNED);
						orderTypeList.add(OrderTypeEnum.PHYSICAL_ORDER);
						break;
					case "4":
						statusList.add(OrderStatusEnum.UNPAID);
						orderTypeList.add(OrderTypeEnum.PHYSICAL_ORDER);
						orderTypeList.add(OrderTypeEnum.SERVICE_STORE_ORDER);
						break;
					case "5":
						statusList.add(OrderStatusEnum.REFUSED);
						statusList.add(OrderStatusEnum.REFUSING);
						orderTypeList.add(OrderTypeEnum.PHYSICAL_ORDER);
						break;
					case "6":
						statusList.add(OrderStatusEnum.TRADE_CLOSED);
						statusList.add(OrderStatusEnum.CANCELED);
						statusList.add(OrderStatusEnum.CANCELING);
						orderTypeList.add(OrderTypeEnum.PHYSICAL_ORDER);
						orderTypeList.add(OrderTypeEnum.SERVICE_STORE_ORDER);
						break;
					case "7":
						statusList.add(OrderStatusEnum.DROPSHIPPING);
						orderTypeList.add(OrderTypeEnum.PHONE_PAY_ORDER);
						orderTypeList.add(OrderTypeEnum.TRAFFIC_PAY_ORDER);
						break;
					case "8":
						statusList.add(OrderStatusEnum.HAS_BEEN_SIGNED);
						orderTypeList.add(OrderTypeEnum.PHONE_PAY_ORDER);
						orderTypeList.add(OrderTypeEnum.TRAFFIC_PAY_ORDER);
						break;
					case "9":
						statusList.add(OrderStatusEnum.TRADE_CLOSED);
						orderTypeList.add(OrderTypeEnum.PHONE_PAY_ORDER);
						orderTypeList.add(OrderTypeEnum.TRAFFIC_PAY_ORDER);
						break;
					case "10":
						statusList.add(OrderStatusEnum.DROPSHIPPING);
						orderTypeList.add(OrderTypeEnum.SERVICE_STORE_ORDER);
						break;
					case "11":
						statusList.add(OrderStatusEnum.TO_BE_SIGNED);
						orderTypeList.add(OrderTypeEnum.SERVICE_STORE_ORDER);
						break;
					case "12":
						statusList.add(OrderStatusEnum.HAS_BEEN_SIGNED);
						orderTypeList.add(OrderTypeEnum.SERVICE_STORE_ORDER);
						break;
					case "13":
						statusList.add(OrderStatusEnum.REFUSED);
						statusList.add(OrderStatusEnum.REFUSING);
						orderTypeList.add(OrderTypeEnum.SERVICE_STORE_ORDER);
						break;
					default:
						break;
				}
				params.put("status", statusList);
				if (params.get("type") == null || StringUtils.isBlank(params.get("type").toString())) {
					params.put("type", orderTypeList);
				}
			}
			// 订单来源转换，注意线上订单包括云上城app和微信
			if (params.get("orderResource") == null || StringUtils.isBlank(params.get("orderResource").toString())) {
				params.remove("orderResource");
			} else {
				List<OrderResourceEnum> orderResourceList = new ArrayList<OrderResourceEnum>();
				switch (params.get("orderResource").toString()) {
					case "0":
						orderResourceList.add(OrderResourceEnum.WECHAT);
						orderResourceList.add(OrderResourceEnum.YSCAPP);
						break;
					case "1":
						orderResourceList.add(OrderResourceEnum.POS);
						break;
					default:
						break;
				}
				params.put("orderResource", orderResourceList);
			}
			if (params.get("payType") == null || StringUtils.isBlank(params.get("payType").toString())) {
				params.remove("payType");
			} else {
				// 现金支付包含实物订单的货到付款的支付方式和服务店订单的线下确定价格并当面支付的支付方式
				if ("4".equals(params.get("payType").toString())) {
					params.put("payWayServer", PayWayEnum.OFFLINE_CONFIRM_AND_PAY.ordinal());
					// Begin 12170 add by wusw 20160809
					params.put("payWayPhysical", PayWayEnum.CASH_DELIERY.ordinal());
					// End 12170 add by wusw 20160809
					// params.put("payType", PayTypeEnum.CASH.ordinal());
				}
			}
			if (params.get("startTime") == null || StringUtils.isBlank(params.get("startTime").toString())) {
				params.remove("startTime");
			}
			if (params.get("endTime") == null || StringUtils.isBlank(params.get("endTime").toString())) {
				params.remove("endTime");
			}
		}
		// Begin 重构4.1 add by wusw 20160727
		// 订单默认参数，以便综合用户已支付，却被POS机取消情况的订单
		params.put("unDisabled", Disabled.valid);
		params.put("disabled", Disabled.invalid);
		params.put("completeStatus", OrderStatusEnum.HAS_BEEN_SIGNED);
		// End 重构4.1 add by wusw 20160727
	}

	// End 重构4.1 add by wusw 20160719

	// Begin 重构4.1 add by wusw 20160723
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderService#findOrderListForFinanceByParams(java.util.Map)
	 */
	@Override
	public List<ERPTradeOrderVo> findOrderListForFinanceByParams(Map<String, Object> params) throws ServiceException {
		// 参数转换处理（例如订单状态）
		this.convertParams(params);
		return tradeOrderMapper.findOrderForFinanceByParams(params);
	}

	// End 重构4.1 add by wusw 20160723

	// Begin 重构4.1 add by zhaoqc 20160722
	@Override
	public void updataRechargeOrderStatus(TradeOrder tradeOrder, String sporderId) throws ServiceException {
		// 修改订单状态
		this.updateOrderStatus(tradeOrder);
		// 记录充值订单信息和第三方平台关系
		TradeOrderThirdRelation relation = new TradeOrderThirdRelation();
		relation.setId(UuidUtils.getUuid());
		relation.setOrderId(tradeOrder.getId());
		relation.setThirdOrderNo(sporderId);
		this.tradeOrderThirdRelationMapper.insertSelective(relation);
	}

	// End 重构4.1 add by zhaoqc 20160722

	// Begin 重构4.1 add by wusw 20160729
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderServiceApi#findServiceStoreOrderDetailForOperate(java.lang.String)
	 */
	@Override
	public TradeOrderVo findServiceStoreOrderDetailForOperate(String orderId) {
		return tradeOrderMapper.selectServiceStoreDetailForOperate(orderId);
	}

	// End 重构4.1 add by wusw 20160729

	@Override
	public Map<String, List<TradeOrder>> findRechargeOrdersByStatus(OrderStatusEnum status) throws ServiceException {
		Map<String, List<TradeOrder>> orderMap = new HashMap<String, List<TradeOrder>>();
		List<TradeOrder> orders = tradeOrderMapper.findRechargeOrdersByStatus(status.ordinal());
		List<TradeOrder> rechargeList = new ArrayList<TradeOrder>();
		List<TradeOrder> dataplanList = new ArrayList<TradeOrder>();
		for (TradeOrder order : orders) {
			if (order.getType() == OrderTypeEnum.PHONE_PAY_ORDER) {
				rechargeList.add(order);
			} else if (order.getType() == OrderTypeEnum.TRAFFIC_PAY_ORDER) {
				dataplanList.add(order);
			}
		}
		orderMap.put("rechargeList", rechargeList);
		orderMap.put("dataplanList", dataplanList);
		return orderMap;
	}

	// begin 重构4.1 add by wushp 20160803
	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderServiceApi#findStoreDetailByOrder(java.lang.String)
	 */
	@Override
	public JSONObject findStoreDetailByOrder(String orderId) throws ServiceException {
		// 获取订单信息
		TradeOrder tradeOrder = tradeOrderMapper.selectByPrimaryKey(orderId);
		if (tradeOrder == null) {
			return null;
		}
		String storeId = tradeOrder.getStoreId();
		// 获取店铺信息
		StoreInfo storeInfo = storeInfoService.getStoreBaseInfoById(storeId);
		if (storeInfo == null) {
			return null;
		}
		StoreTypeEnum storeType = storeInfo.getType();
		JSONObject jsonObject = new JSONObject();
		// 店铺id
		jsonObject.put("storeId", storeId);
		// 店铺类型(0:是云周边,1:快送,2:便利店,3:团购,4:服务店)
		jsonObject.put("storeType", storeType.ordinal());
		// 店铺是否关闭(0:关闭,1:开启)
		jsonObject.put("isClosed", storeInfo.getStoreInfoExt().getIsClosed().ordinal());
		if (storeType.ordinal() == 4) {
			// 服务店，获取服务栏目id
			ServerColumnStore serverColumnStore = serverColumnService.findServerStoreByStoreId(storeId);
			if (serverColumnStore != null) {
				// 服务栏目id
				jsonObject.put("columnServerId", serverColumnStore.getColumnServerId());
				ServerColumn serverColumn = serverColumnService.findById(serverColumnStore.getColumnServerId());
				jsonObject.put("columnServerName", serverColumn.getServerName());
			} else {
				jsonObject.put("columnServerId", "");
				jsonObject.put("columnServerName", "");
			}
		} else {
			jsonObject.put("columnServerId", "");
			jsonObject.put("columnServerName", "");
		}
		return jsonObject;
	}

	// end 重构4.1 add by wushp 20160803

	// begin 充值订单导出转换订单状态名 add by zhulq 2016-8-8
	/**
	 * @Description: 充值订单导出转换订单状态名
	 * @param vo   实体类
	 * @author zhulq
	 * @date 2016年8月8日
	 */
	private void convertRechargeOrderStatus(TradeOrderRechargeVo vo) {
		switch (vo.getStatus()) {
			case UNPAID:
				vo.setOrderStatusName("待付款");
				break;
			case BUYER_PAYING:
				vo.setOrderStatusName("付款确认中");
				break;
			case DROPSHIPPING:
				vo.setOrderStatusName("充值中");
				break;
			case HAS_BEEN_SIGNED:
				vo.setOrderStatusName("充值成功");
				break;
			case TRADE_CLOSED:
				vo.setOrderStatusName("充值失败");
				break;
			default:
				vo.setOrderStatusName(vo.getStatus().getValue());
				break;
		}
	}

	// End 充值订单导出转换订单状态名 add by zhulq 2016-8-8

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderServiceApi#findRechargeOrderForExport()
	 */
	@Transactional(readOnly = true)
	@Override
	public List<TradeOrderRechargeVo> findRechargeOrderForExport(TradeOrderRechargeVo vo) throws ServiceException {
		if (vo.getIds() != null && vo.getIds().length <= 0) {
			vo.setIds(null);
		}
		List<TradeOrderRechargeVo> result = tradeOrderMapper.selectRechargeOrder(vo);
		if (CollectionUtils.isNotEmpty(result)) {
			for (TradeOrderRechargeVo rechargeVo : result) {
				this.convertRechargeOrderStatus(rechargeVo);
			}
		}
		return result;
	}

	// Begin 12051 add by wusw 20160811
	/**
	 * 
	 * @Description: 根据id，获取活动名称
	 * @param activityId 活动id
	 * @param activityType 活动类型
	 * @return 活动名称
	 * @author wusw
	 * @date 2016年8月11日
	 */
	private String getOrderActivityName(String activityId, ActivityTypeEnum activityType) {
		if (activityType == ActivityTypeEnum.VONCHER) {
			// 代金券活动
			ActivityCollectCoupons activityCollectCoupons = activityCollectCouponsService.get(activityId);
			return activityCollectCoupons.getName();
		} else if (activityType == ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES
				|| activityType == ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES) {
			// 满减或满折活动
			ActivityDiscount activityDiscount = activityDiscountMapper.selectByPrimaryKey(activityId);
			return activityDiscount.getName();
		} else if (activityType == ActivityTypeEnum.SECKILL_ACTIVITY) {
			// 秒杀活动
			ActivitySeckill activitySeckill = activitySeckillMapper.findByPrimaryKey(activityId);
			return activitySeckill.getSeckillName();
		} else if (activityType == ActivityTypeEnum.GROUP_ACTIVITY) {
			// 团购活动
			ActivityGroup activityGroup = activityGroupService.selectByPrimaryKey(activityId);
			return activityGroup.getName();
		} else {
			// 无活动
			return null;
		}
	}

	// End 12051 add by wusw 20160811

	/**
	 * 
	 * @Description: 查询POS确认收货订单列表
	 * @param storeId 店铺ID
	 * @param pageNumber 当前页
	 * @param pageSize 页大小
	 * @return List 确认收货订单列表  
	 * @author zengj
	 * @date 2016年9月13日
	 */
	public PageUtils<Map<String, Object>> findConfirmDeliveryOrderListByPos(String storeId, int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		return new PageUtils<Map<String, Object>>(tradeOrderMapper.findConfirmDeliveryOrderListByPos(storeId));
	}
	
	
	// Begin v1.1.0 add by zengjz 20160912
	@Override
	public Map<String, Object> statisOrderForFinanceByParams(Map<String, Object> params) {
		// 参数转换处理（例如订单状态）
		this.convertParams(params);
		Map<String, Object> result = tradeOrderMapper.statisOrderForFinanceByParams(params);
		return result;
	}
	
	/**
	 * @Description: 财务系统退款订单参数转换
	 * @param map   查询参数
	 * @author zengjizu
	 * @date 2016年9月17日
	 */
	private void convertParamsForFinance(Map<String, Object> map) {
		// 退款状态参数： 0:退款中 1:退款完成 默认为空，全部
		String status = (String) map.get("status");
		List<String> statusList = Lists.newArrayList();

		List<OrderStatusEnum> orderStatusList = new ArrayList<OrderStatusEnum>();
		/*
		 * 2:已取消 4:已拒收 7:取消中,8:拒收中
		 */
		if (status != null && "0".equals(status)) {
			orderStatusList.add(OrderStatusEnum.CANCELING);
			orderStatusList.add(OrderStatusEnum.REFUSING);
		} else if (status != null && "1".equals(status)) {
			orderStatusList.add(OrderStatusEnum.CANCELED);
			orderStatusList.add(OrderStatusEnum.REFUSED);
		} else {
			orderStatusList.add(OrderStatusEnum.CANCELING);
			orderStatusList.add(OrderStatusEnum.REFUSING);
			orderStatusList.add(OrderStatusEnum.CANCELED);
			orderStatusList.add(OrderStatusEnum.REFUSED);
		}
		map.put("status", statusList);
	}

	@Override
	public Map<String, Object> statisOrderCannelRefundByParams(Map<String, Object> params) {
		convertParamsForFinance(params);
		return tradeOrderMapper.statisOrderCannelRefundByParams(params);
	}
	// End v1.1.0 add by zengjz 20160912
}