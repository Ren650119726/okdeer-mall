/** 
 *@Project: yschome-mall-order 
 *@Author: zengj
 *@Date: 2016年9月6日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.google.common.base.Charsets;
import com.mysql.fabric.xmlrpc.base.Data;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreSkuAssembleDto;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.exception.StockException;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.framework.mq.RocketMQProducer;
import com.okdeer.common.consts.LogConstants;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.member.points.dto.PointQueryParamDto;
import com.okdeer.mall.order.builder.StockAdjustVoBuilder;
import com.okdeer.mall.order.constant.mq.OrderMessageConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderPay;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.ActivityBelongType;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.mapper.TradeOrderPayMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsMapper;
import com.okdeer.mall.order.service.TradeOrderActivityService;
import com.okdeer.mall.order.service.TradeOrderCompleteProcessService;
import com.okdeer.mall.order.service.TradeOrderCompleteProcessServiceApi;
import com.okdeer.mall.order.utils.OrderNoUtils;
import com.okdeer.mall.points.bo.PointQueryResult;
import com.okdeer.mall.points.service.PointsService;

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

	/** * pos支付方式:现金 */
	private static final String CASH = "现金";

	/** * pos支付方式:银联卡 */
	private static final String UNITCARD = "银联卡";

	/** * pos支付方式:支付宝 */
	private static final String APLIPAY = "支付宝";

	/** * pos支付方式:微信 */
	private static final String WECHATPAY = "微信";
	
	@Resource
	private StockAdjustVoBuilder stockAdjustVoBuilder;
	
	/**积分规则查询服务 */
	@Resource
	private PointsService pointsService;
	
	@Autowired
	private TradeOrderActivityService orderActivityService;

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
		// Begin V2.0 added by maojj 2017-01-08
		// 低价商品和组合商品需要进行拆分，对订单项列表进行拆分
		splitItemList(tradeOrderItemList);
		// End V2.0 added by maojj 2017-01-08

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
		} else {
			// 如果不是线上支付。新建一个支付实例
			tradeOrderPay = new TradeOrderPay();
			// 支付创建时间取下单时间
			tradeOrderPay.setCreateTime(tradeOrder.getCreateTime());
			// 支付时间取下单时间
			tradeOrderPay.setPayTime(tradeOrder.getCreateTime());
			// 支付金额取用户实付金额
			tradeOrderPay.setPayAmount(tradeOrder.getActualAmount());
			// 该字段为空就是线上订单货到付款支付，默认现金支付
			if (StringUtils.isBlank(tradeOrder.getPospay())) {
				tradeOrderPay.setPayType(PayTypeEnum.CASH);
			} else {
				switch (tradeOrder.getPospay()) {
					case CASH:
						tradeOrderPay.setPayType(PayTypeEnum.CASH);
						break;
					case UNITCARD:
						tradeOrderPay.setPayType(PayTypeEnum.ONLINE_BANK);
						break;
					case APLIPAY:
						tradeOrderPay.setPayType(PayTypeEnum.ALIPAY);
						break;
					case WECHATPAY:
						tradeOrderPay.setPayType(PayTypeEnum.WXPAY);
						break;
					default:
						tradeOrderPay.setPayType(PayTypeEnum.CASH);
						break;
				}
			}
			tradeOrderPay.setId(UuidUtils.getUuid());
			tradeOrderPay.setOrderId(tradeOrder.getId());
		}

		// 订单信息
		JSONObject orderInfo = buildOrderInfo(tradeOrder);
		// 订单项json数组
		JSONArray orderItemList = buildOrderItemList(tradeOrder, tradeOrderItemList);
		// 订单支付信息
		JSONObject orderPayInfo = buildOrderPayInfo(tradeOrder, tradeOrderPay);

		orderInfo.put("orderItemList", orderItemList);
		orderInfo.put("orderPayInfo", orderPayInfo);
        logger.info("==============================orderInfo:", orderInfo.toString());
        //根据业务订单id 获得加积分值
        Integer pointVal = getUserPointByOrder(tradeOrder.getUserId(), orderId, tradeOrder.getActualAmount(), 1);
        orderInfo.put("point", pointVal);
		// 发送消息
		this.send(OrderMessageConstant.TOPIC_ORDER_COMPLETE, OrderMessageConstant.TAG_ORDER_COMPLETE,
				orderInfo.toString(),tradeOrder.getId());

	}
	
	/**
	 * 根据业务id(订单或退款单)获得加减积分值
	 * @param userId 用户id
	 * @param id 业务id  订单或退款单 id
	 * @param amount 金额-》实付、退款金额
	 * @param type 类型加还是减
	 * Tuzhd
	 * @return
	 */
	private Integer getUserPointByOrder (String userId,String id,BigDecimal amount,Integer type){
		PointQueryParamDto pointQueryParamDto = new PointQueryParamDto();
        pointQueryParamDto.setUserId(userId);
        pointQueryParamDto.setType(type);
        pointQueryParamDto.setBusinessId(id);
        pointQueryParamDto.setAmount(amount);
        PointQueryResult pointQueryResult = pointsService.findUserPoint(pointQueryParamDto);
        //当该业务查询出的加减积分不存在返回null
        if(pointQueryResult != null){
        	return pointQueryResult.getPointVal();
        }
        return null;
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
		logger.info("退款支付同步进销存系统:" + refundsId);
		if (StringUtils.isBlank(refundsId)) {
			throw new ServiceException(LogConstants.ORDER_REFUNDS_ID_IS_NULL);
		}
		TradeOrderRefunds orderRefunds = tradeOrderRefundsMapper.selectByPrimaryKey(refundsId);
		if(orderRefunds == null){
			throw new ServiceException(LogConstants.ORDER_REFUNDS_NOT_EXISTS);
		}
		// Begin V2.3 added by maojj 2017-04-24
        // 只有便利店订单需要同步。服务店不同步
        if(orderRefunds.getType() != OrderTypeEnum.PHYSICAL_ORDER){
        	return;
        }
        // End V2.3 added by maojj 2017-04-24
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
				&& orderRefunds.getRefundsStatus() != RefundsStatusEnum.YSC_REFUND_SUCCESS
				&& orderRefunds.getRefundsStatus() != RefundsStatusEnum.WAIT_SELLER_REFUND) {
			logger.error(LogConstants.ORDER_STATUS_NO_MATCHING, refundsId, RefundsStatusEnum.REFUND_SUCCESS.getName(),
					orderRefunds.getRefundsStatus().getName());
			throw new ServiceException(LogConstants.ORDER_STATUS_NO_MATCHING);
		}
		// 订单信息
		JSONObject orderRefundsInfo = buildOrderRefundsInfo(orderRefunds);
		// 订单项json数组
		JSONArray orderRefundsItemList = buildOrderRefundsItemList(orderRefunds, tradeOrderRefundsItemList);

		orderRefundsInfo.put("orderRefundsItemList", orderRefundsItemList);
		//根据业务退款单id 获得减积分值
		Integer pointVal = getUserPointByOrder(orderRefunds.getUserId(), refundsId, orderRefunds.getTotalIncome(), 2);
		orderRefundsInfo.put("point", pointVal);
		// 发送消息
		this.send(OrderMessageConstant.TOPIC_REFUND_ORDER_COMPLETE, OrderMessageConstant.TAG_REFUND_ORDER_COMPLETE,
				orderRefundsInfo.toString(),orderRefunds.getId());
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
		//Begin 排除平台优惠 update by tangy  2016-11-4
		// 平台优惠金额
		// Begin modified by maojj 2017-01-09
		BigDecimal platDiscountAmount = BigDecimal.ZERO;
		if (order.getActualAmount().compareTo(order.getIncome()) != 0 ) {
			platDiscountAmount = order.getPreferentialPrice();
		} else {
			storePreferentialPrice = order.getPreferentialPrice();
		}
		// End modified by maojj 2017-01-09
		
		// 平台优惠金额
		orderInfo.put("platDiscountAmount", platDiscountAmount);
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
		// 收货时间
		orderInfo.put("completeTime", order.getDeliveryTime() == null ? new Data() : order.getDeliveryTime());
		// 进销存那边的优惠类型0:无活动 ;1：代金券；2：其他
		int activityType = 0;
		// 活动类型为代金券活动
		if (order.getActivityType() == ActivityTypeEnum.VONCHER) {
			activityType = 1;
		} else if (order.getActivityType() == ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES
				&& order.getIncome().compareTo(order.getActualAmount()) != 0) {
			// 活动类型为满减活动且店家收入不等于用户实付，说明里面有平台的补贴
			activityType = 2;
		}
		orderInfo.put("activityType", activityType);
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
		BigDecimal actualAmount = orderRefunds.getTotalAmount();
		refunds.put("totalAmount", orderRefunds.getTotalAmount().add(orderRefunds.getTotalPreferentialPrice()));
		// 商家实收金额
		refunds.put("amount", orderRefunds.getTotalIncome());
		// 店铺优惠金额
		BigDecimal storePreferentialPrice = BigDecimal.ZERO;
		// 店铺优惠金额
		BigDecimal platDiscountAmount = BigDecimal.ZERO;
		// 订单金额如果不等于店家收入金额，说明是店铺有优惠
		if (actualAmount.compareTo(orderRefunds.getTotalIncome()) != 0 ) {
			platDiscountAmount = orderRefunds.getTotalPreferentialPrice();
		} else {
			storePreferentialPrice = orderRefunds.getTotalPreferentialPrice();
		}
		
		// 平台优惠金额
		refunds.put("platDiscountAmount", platDiscountAmount);
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
		// 退款时间
		refunds.put("completeTime", orderRefunds.getRefundMoneyTime() == null 
				? orderRefunds.getUpdateTime() : orderRefunds.getRefundMoneyTime());
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
			// 平台优惠金额
			BigDecimal platDiscountAmount = BigDecimal.ZERO;
			// 订单金额如果不等于店家收入金额，说明是店铺有优惠
			//Begin 排除平台优惠 update by tangy  2016-10-28
			if (order.getActualAmount().compareTo(order.getIncome()) != 0 ) {
				platDiscountAmount = orderItem.getPreferentialPrice();
			} else {
				storePreferentialPrice = orderItem.getPreferentialPrice();
			}
			item.put("platDiscountAmount", platDiscountAmount);
			// 实际单价=原单价减去店铺优惠
			BigDecimal actualPrice = orderItem.getUnitPrice().subtract(storePreferentialPrice);
			if (orderItem.getQuantity() != null && orderItem.getQuantity().intValue() > 0) {
				actualPrice = orderItem.getUnitPrice().subtract(
						storePreferentialPrice.divide(new BigDecimal(orderItem.getQuantity()), 4, BigDecimal.ROUND_HALF_UP));
			} else if (orderItem.getWeight() != null 
					&& storePreferentialPrice.compareTo(BigDecimal.ZERO) == 1) {
				actualPrice = orderItem.getUnitPrice().subtract(
						storePreferentialPrice.divide(orderItem.getWeight(), 4, BigDecimal.ROUND_HALF_UP));
			} 
			//End added by tangy

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
			item.put("orderId", orderRefunds.getId());
			// 序号
			item.put("rowNo", i + 1);
			// 标准商品库ID
			item.put("skuId", goods.getSkuId());
			// 店铺优惠金额
			BigDecimal storePreferentialPrice = BigDecimal.ZERO;
			
			//Begin update by tangy  2016-11-7
			// 平台优惠金额
			BigDecimal platDiscountAmount = BigDecimal.ZERO;
			
			
			//Begin update by zengjz  2017-3-20 由于零售那边说优惠金额算错了，所以重新计算优惠的归宿
			TradeOrder tradeOrder = tradeOrderMapper.selectByPrimaryKey(orderRefunds.getOrderId());
			if (tradeOrder.getActivityType() != null && 
					tradeOrder.getActivityType() != ActivityTypeEnum.NO_ACTIVITY) {
				ActivityBelongType activityBelongType = orderActivityService.findActivityType(tradeOrder);
				if (activityBelongType == ActivityBelongType.SELLER) {
					storePreferentialPrice = orderRefundsItem.getPreferentialPrice();
				}else{
					platDiscountAmount = orderRefundsItem.getPreferentialPrice();
				}
			}
			//end update by zengjz  2017-3-20 由于零售那边说优惠金额算错了，所以重新计算优惠的归宿
			
			// 订单实付金额如果不等于店家收入金额，说明是平台有优惠
//			if (orderRefunds.getTotalAmount().compareTo(orderRefunds.getTotalIncome()) != 0) {
//				platDiscountAmount = orderRefundsItem.getPreferentialPrice();
//			} else if (orderRefundsItem.getPreferentialPrice() != null 
//					&& orderRefundsItem.getPreferentialPrice().compareTo(BigDecimal.ZERO) == 1) {
//				storePreferentialPrice = orderRefundsItem.getPreferentialPrice();
//			}
			// 实际单价=原单价减去店铺优惠
			BigDecimal actualPrice = orderRefundsItem.getUnitPrice();
			if (orderRefundsItem.getQuantity() != null && orderRefundsItem.getQuantity().intValue() > 0
					&& storePreferentialPrice.compareTo(BigDecimal.ZERO) == 1) {
				actualPrice = orderRefundsItem.getUnitPrice().subtract(
						storePreferentialPrice.divide(new BigDecimal(orderRefundsItem.getQuantity()), 4, BigDecimal.ROUND_HALF_UP));
			} else if (orderRefundsItem.getWeight() != null 
					&& storePreferentialPrice.compareTo(BigDecimal.ZERO) == 1) {
				actualPrice = orderRefundsItem.getUnitPrice().subtract(
						storePreferentialPrice.divide(orderRefundsItem.getWeight(), 4, BigDecimal.ROUND_HALF_UP));
			} 	
			//End update by tangy
			// 货号
			// item.put("skuCode", goods.geta);
			// 销售类型
			item.put("saleType", ORDER_TYPE_B);
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
			// 平台优惠金额
			item.put("platDiscountAmount", platDiscountAmount);
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
	private void send(String topic, String tag, String msg,String key) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info(LogConstants.ORDER_OR_REFUNDS_COMPLETE_MSG_PARAM, tag, msg);
		}
		Message message = new Message(topic, tag, key,msg.getBytes(Charsets.UTF_8));
		message.setKeys(key);
		SendResult sendResult = rocketMqProducer.send(message);
		if (sendResult.getSendStatus() != SendStatus.SEND_OK) {
			throw new StockException("写mq数据失败");
		}
	}
	
	// Begin V2.0 added by maojj 2017-01-08
	private void splitItemList(List<TradeOrderItem> itemList) throws Exception{
		Map<String,List<GoodsStoreSkuAssembleDto>> comboSkuMap = stockAdjustVoBuilder.parseComboSku(itemList);
		Iterator<TradeOrderItem> itemIt = itemList.iterator();
		TradeOrderItem item = null;
		List<TradeOrderItem> splitItemList = new ArrayList<TradeOrderItem>();
		TradeOrderItem splitItem = null;
		
		while(itemIt.hasNext()){
			item = itemIt.next();
			if(item.getSpuType() == SpuTypeEnum.assembleSpu){
				// 如果是组合商品，对订单项进行拆分
				List<GoodsStoreSkuAssembleDto> comboDetailList = comboSkuMap.get(item.getStoreSkuId());
				for(GoodsStoreSkuAssembleDto comboDto : comboDetailList){
					splitItem = new TradeOrderItem();
					splitItem.setId(UuidUtils.getUuid());
					splitItem.setOrderId(item.getOrderId());
					splitItem.setActivityType(item.getActivityType());
					splitItem.setPreferentialPrice(BigDecimal.valueOf(0.0));
					splitItem.setUnitPrice(comboDto.getUnitPrice());
					splitItem.setQuantity(comboDto.getQuantity()*item.getQuantity());
					splitItem.setStoreSkuId(comboDto.getStoreSkuId());
					splitItem.setCreateTime(item.getCreateTime());
					splitItemList.add(splitItem);
				}
				itemIt.remove();
			}else if(item.getActivityQuantity() != null && item.getActivityQuantity() > 0){
				// 如果是低价且购买了低价商品，对商品进行拆分
				splitItem = new TradeOrderItem();
				splitItem.setId(UuidUtils.getUuid());
				splitItem.setOrderId(item.getOrderId());
				splitItem.setActivityType(ActivityTypeEnum.LOW_PRICE.ordinal());
				splitItem.setPreferentialPrice(item.getPreferentialPrice());
				splitItem.setUnitPrice(item.getUnitPrice());
				splitItem.setQuantity(item.getActivityQuantity());
				splitItem.setCreateTime(item.getCreateTime());
				splitItem.setStoreSkuId(item.getStoreSkuId());
				splitItemList.add(splitItem);
				
				if(item.getQuantity() - item.getActivityQuantity() > 0){
					splitItem = new TradeOrderItem();
					splitItem.setId(UuidUtils.getUuid());
					splitItem.setOrderId(item.getOrderId());
					splitItem.setActivityType(ActivityTypeEnum.NO_ACTIVITY.ordinal());
					splitItem.setPreferentialPrice(BigDecimal.valueOf(0.0));
					splitItem.setUnitPrice(item.getUnitPrice());
					splitItem.setQuantity(item.getQuantity() - item.getActivityQuantity());
					splitItem.setCreateTime(item.getCreateTime());
					splitItem.setStoreSkuId(item.getStoreSkuId());
					splitItemList.add(splitItem);
				}
				itemIt.remove();
			}
		}
		itemList.addAll(splitItemList);
	}
	// End V2.0 added by maojj 2017-01-08
}
