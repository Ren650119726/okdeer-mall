/** 
 *@Project: yschome-mall-order 
 *@Author: zengj
 *@Date: 2016年7月21日 
 *@Copyright: ©2014-2020 www.yschome.com Inc. All rights reserved. 
 */

package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.goods.base.enums.GoodsTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuPicture;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.goods.store.enums.BSSC;
import com.okdeer.archive.goods.store.enums.GoodsStoreSkuPayTypeEnum;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuPictureServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuStockServiceApi;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.enums.PublicResultCodeEnum;
import com.okdeer.archive.store.enums.StoreStatusEnum;
import com.okdeer.archive.store.service.IStoreServerAreaServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountCondition;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountRecord;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountStatus;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountType;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckillRange;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckillRecord;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.common.enums.RangeTypeEnum;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.operate.entity.ServerColumn;
import com.okdeer.mall.operate.enums.ServerStatus;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.enums.AppraiseEnum;
import com.okdeer.mall.order.enums.CompainStatusEnum;
import com.okdeer.mall.order.enums.OrderComplete;
import com.okdeer.mall.order.enums.OrderIsShowEnum;
import com.okdeer.mall.order.enums.OrderItemStatusEnum;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PaymentStatusEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.enums.WithInvoiceEnum;
import com.okdeer.mall.order.exception.OrderException;
import com.okdeer.mall.order.service.ServiceOrderProcessServiceApi;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountConditionMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountRecordMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountService;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillRangeService;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillRecordService;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillService;
import com.okdeer.mall.member.service.MemberConsigneeAddressService;
import com.okdeer.mall.operate.column.service.ServerColumnService;
import com.okdeer.mall.order.constant.ExceptionConstant;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.service.TradeOrderLogisticsService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.timer.TradeOrderTimer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ClassName: ServiceOrderProcessServiceImpl 
 * @Description: 服务订单下单处理类
 * @author zengj
 * @date 2016年7月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     重构4.1          2016年7月21日                               zengj				新建类
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.ServiceOrderProcessServiceApi")
public class ServiceOrderProcessServiceImpl implements ServiceOrderProcessServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(ServiceOrderProcessServiceImpl.class);

	/**
	 * 商品图片信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuPictureServiceApi goodsStoreSkuPictureService;

	/**
	 * 服务栏目Service
	 */
	@Autowired
	private ServerColumnService serverColumnService;

	/**
	 * 秒杀活动Service
	 */
	@Autowired
	private ActivitySeckillService activitySeckillService;

	/**
	 * 秒杀活动记录Service
	 */
	@Autowired
	private ActivitySeckillRecordService activitySeckillRecordService;

	/**
	 * 秒杀活动范围Service
	 */
	@Autowired
	private ActivitySeckillRangeService activitySeckillRangeService;

	/**
	 * 编码生成
	 */
	@Autowired
	private GenerateNumericalService generateNumericalOrderService;

	/**
	 * 满减满折活动Mapper
	 */
	@Autowired
	private ActivityDiscountConditionMapper activityDiscountConditionMapper;

	/**
	 * 折扣活动记录Mapper
	 */
	@Resource
	private ActivityDiscountRecordMapper activityDiscountRecordMapper;

	/**
	 * 订单服务Service
	 */
	@Resource
	private TradeOrderService tradeOrderService;

	/**
	 * 订单超时计时器
	 */
	@Autowired
	private TradeOrderTimer tradeOrderTimer;

	/**
	 * 订单物流信息 
	 */
	@Autowired
	private TradeOrderLogisticsService tradeOrderLogisticsService;

	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerServiceApi stockManagerService;

	/**
	 * 店铺信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	/**
	 * 商品信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuService;

	/**
	 * 店铺商品库存Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuStockServiceApi goodsStoreSkuStockService;

	/**
	 * 满减满折活动Service
	 */
	@Resource
	private ActivityDiscountService activityDiscountService;

	/**
	 * 会员地址管理Service
	 */
	@Resource
	private MemberConsigneeAddressService memberConsigneeAddressService;

	/**
	 * 服务店铺服务范围Service
	 */
	@Reference(version = "1.0.0", check = false)
	private IStoreServerAreaServiceApi storeServerAreaService;

	/**
	 * @Description: 服务订单确认订单
	 * @return JSONObject
	 * @throws Exception 秒杀活动查询异常信息
	 * @throws OrderException 订单校验异常
	 * @author zengj
	 * @date 2016年7月13日
	 */
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public JSONObject confirmServiceOrder(ServiceOrderReq orderReq) throws OrderException, Exception {
		DecimalFormat df = new DecimalFormat("0.00");
		// 将校验通过的数据返回，需要用到的时候就可以直接取，不需要再查数据库了
		Map<String, Object> result = new HashMap<String, Object>();
		// 校验通过返回值
		JSONObject resultJson = new JSONObject();
		try {
			// 数据校验
			checkConfirmOrderInputParam(orderReq, result, resultJson);
		} catch (OrderException e) {
			resultJson.put("code", PublicResultCodeEnum.BUSINESS_FAILURE);
			resultJson.put("message", e.getMessage());
			return resultJson;
		}

		// 从校验结果集里取出校验通过后的信息
		GoodsStoreSku goodsStoreSku = (GoodsStoreSku) result.get("goodsStoreSku");
		ActivitySeckill activitySeckill = (ActivitySeckill) result.get("activitySeckill");
		StoreInfo storeInfo = (StoreInfo) result.get("storeInfo");

		// 查询商品主图信息
		GoodsStoreSkuPicture goodsStoreSkuPicture = goodsStoreSkuPictureService
				.findMainPicByStoreSkuId(orderReq.getSkuId());

		// 商品ID
		resultJson.put("skuId", goodsStoreSku.getId());
		// 商品名称
		resultJson.put("skuName", goodsStoreSku.getName());
		// 商品主图
		resultJson.put("skuIcon", goodsStoreSkuPicture.getUrl());

		// 如果是秒杀商品，单价就是秒杀价格
		if (activitySeckill != null) {
			resultJson.put("seckillPrice", df.format(
					activitySeckill.getSeckillPrice() == null ? BigDecimal.ZERO : activitySeckill.getSeckillPrice()));
			resultJson.put("seckillId", activitySeckill.getId());
		}
		// 正常商品，单价就是商品单价
		resultJson.put("unitPrice",
				goodsStoreSku.getOnlinePrice() == null ? "0.00" : df.format(goodsStoreSku.getOnlinePrice()));
		// 限购数量
		resultJson.put("limitNum", goodsStoreSku.getTradeMax());
		resultJson.put("skuNum", orderReq.getSkuNum());
		// 系统时间
		resultJson.put("sysTime", new Date().getTime());
		// 返回的店铺信息JSON
		JSONObject storeJson = new JSONObject();
		storeJson.put("storeId", storeInfo.getId());
		// 店铺预约期限天数
		storeJson.put("deadline", storeInfo.getStoreInfoExt().getSubscribeTime());
		// 是否提前预约（0：否 1：是）
		if (storeInfo.getStoreInfoExt().getIsAdvanceType() != null
				&& storeInfo.getStoreInfoExt().getIsAdvanceType() == 1) {
			// 预约类型（0：提前多少小时下单 1：只能下当前日期多少天后的订单）
			if (storeInfo.getStoreInfoExt().getAdvanceType() != null
					&& storeInfo.getStoreInfoExt().getAdvanceType() == 0) {
				// 提前天数预约
				storeJson.put("aheadTimeDay", "");
				// 提前小时数预约
				storeJson.put("aheadTimeHours", storeInfo.getStoreInfoExt().getAdvanceTime());
			} else {
				// 提前天数预约
				storeJson.put("aheadTimeDay", storeInfo.getStoreInfoExt().getAdvanceTime());
				// 提前小时数预约
				storeJson.put("aheadTimeHours", "");
			}
		} else {
			// 提前天数预约
			storeJson.put("aheadTimeDay", "");
			// 提前小时数预约
			storeJson.put("aheadTimeHours", "");
		}
		// 店铺服务时间
		storeJson.put("startTime", storeInfo.getStoreInfoExt().getServiceStartTime());
		storeJson.put("endTime", storeInfo.getStoreInfoExt().getServiceEndTime());
		// 是否有发票
		storeJson.put("isInvoice", storeInfo.getStoreInfoExt().getIsInvoice() == null ? 0
				: storeInfo.getStoreInfoExt().getIsInvoice().ordinal());
		resultJson.put("storeInfo", storeJson);

		// 支持的支付方式（0：在线支付 1：线下确认价格并当面支付）,这里的线下支付对应订单的 当面确认并当面支付
		int paymentMode = 0;
		switch (goodsStoreSku.getPayType()) {
			case onlinePay:
				paymentMode = PayWayEnum.PAY_ONLINE.ordinal();
				break;
			case offlinePay:
				paymentMode = PayWayEnum.OFFLINE_CONFIRM_AND_PAY.ordinal();
				break;
			default:
				paymentMode = PayWayEnum.PAY_ONLINE.ordinal();
				break;
		}

		resultJson.put("paymentMode", paymentMode);
		// 订单总金额
		BigDecimal totalAmount = BigDecimal.ZERO;
		// 在线支付才有订单金额，线下支付，订单金额是0
		if (goodsStoreSku.getPayType() == null || goodsStoreSku.getPayType() == GoodsStoreSkuPayTypeEnum.onlinePay) {
			totalAmount = goodsStoreSku.getOnlinePrice().multiply(new BigDecimal(orderReq.getSkuNum()));
		}
		// 查询满减满折活动信息
		findActivityDiscount(storeInfo.getId(), totalAmount, orderReq.getSeckillId(), resultJson);

		// 查询秒杀范围
		findActivitySeckillRangeList(activitySeckill, resultJson);

		// 查询用户默认收货地址
		resultJson.put("defaultAddress", findUserDefaultAddress(orderReq));
		return resultJson;
	}

	/**
	 * @Description: 服务店订单下单
	 * @param orderReq 下单请求参数
	 * @return JSONObject 返回值JSON对象
	 * @throws OrderException,Exception
	 * @author zengj
	 * @date 2016年7月13日
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public JSONObject addServiceOrder(ServiceOrderReq orderReq) throws OrderException, Exception {
		// 返回的JSON对象
		JSONObject resultJson = new JSONObject();
		// 将校验通过的数据返回，需要用到的时候就可以直接取，不需要再查数据库了
		Map<String, Object> checkResult = new HashMap<String, Object>();
		try {
			// 数据校验
			checkAddOrderInputParam(orderReq, checkResult, resultJson);
		} catch (OrderException e) {
			resultJson.put("code", PublicResultCodeEnum.BUSINESS_FAILURE);
			resultJson.put("message", e.getMessage());
			return resultJson;
		}

		// 计算订单金额
		calcOrderAmount(orderReq, checkResult);
		// 构建订单信息
		TradeOrder tradeOrder = buildTradeOrder(checkResult, orderReq);

		// 构建订单项信息
		TradeOrderItem tradeOrderItem = buildTradeOrderItem(checkResult, orderReq);
		tradeOrderItem.setOrderId(tradeOrder.getId());
		// 如果是秒杀商品，需要保存秒杀记录
		if (StringUtils.isNotBlank(orderReq.getSeckillId())) {
			activitySeckillRecordService.add(buildActivitySeckillRecord(orderReq, tradeOrder));
		}
		// 保存满减满折记录
		if (StringUtils.isNotBlank(orderReq.getActivityId()) && StringUtils.isNotBlank(orderReq.getActivityItemId())) {
			saveActivityDiscountRecord(tradeOrder.getId(), orderReq);
		}
		// 如果需要开发票，构建发票信息
		if (tradeOrder.getInvoice() == WithInvoiceEnum.HAS) {
			tradeOrder.setTradeOrderInvoice(buildTradeOrderInvoice(orderReq, tradeOrder));
		}
		// 服务店订单一单只能下一款商品
		List<TradeOrderItem> tradeOrderItemList = new ArrayList<TradeOrderItem>();
		tradeOrderItemList.add(tradeOrderItem);
		tradeOrder.setTradeOrderItem(tradeOrderItemList);
		// 用户收货地址
		MemberConsigneeAddress memberConsigneeAddress = (MemberConsigneeAddress) checkResult
				.get("memberConsigneeAddress");

		// 保存服务地址信息
		TradeOrderLogistics logistics = buildTradeOrderLogistics(tradeOrder, memberConsigneeAddress);
		tradeOrder.setPickUpId(logistics.getId());
		// 保存订单和订单项信息，并发送消息
		tradeOrderService.insertTradeOrder(tradeOrder);
		// 保存订单物流信息
		tradeOrderLogisticsService.addTradeOrderLogistics(logistics);

		// 下单后，更新用户的地址使用时间，以便用来默认带出到确认订单页面
		memberConsigneeAddress.setUseTime(DateUtils.getSysDate());
		// 更新用户使用时间
		memberConsigneeAddressService.updateByPrimaryKeySelective(memberConsigneeAddress);

		Date serviceTime = DateUtils.parseDate(tradeOrder.getPickUpTime(), "yyyy-MM-dd HH:mm");

		// 锁定库存-- 是dubbo调用，不在一个事务，所以放在最后，若锁定库存失败，回滚事务
		lockStock(tradeOrder, checkResult, orderReq);
		// 线下确认价格并支付
		if (tradeOrder.getPayWay() == PayWayEnum.OFFLINE_CONFIRM_AND_PAY) {
			// 预约服务时间过后2小时未派单的自动取消订单
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_server_timeout, tradeOrder.getId(),
					(DateUtils.addHours(serviceTime, 2).getTime() - DateUtils.getSysDate().getTime()) / 1000);

			// Begin 12002 add by zengj
			// 线下确认价格的，下单完成，销量增加
			GoodsStoreSku goodsStoreSku = (GoodsStoreSku) checkResult.get("goodsStoreSku");
			goodsStoreSku.setSaleNum(
					(goodsStoreSku.getSaleNum() == null ? 0 : goodsStoreSku.getSaleNum()) + orderReq.getSkuNum());

			this.goodsStoreSkuService.updateByPrimaryKeySelective(goodsStoreSku);
			// End 12002 add by zengj
		} else {
			// 超时未支付的，取消订单
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_pay_timeout, tradeOrder.getId());
		}
		resultJson.put("orderId", tradeOrder.getId());
		resultJson.put("orderNo", tradeOrder.getOrderNo());
		resultJson.put("orderPrice", String.format("%.2f", tradeOrder.getActualAmount()));
		resultJson.put("tradeNum", tradeOrder.getTradeNum());
		resultJson.put("limitTime", 1800);
		return resultJson;
	}

	/**
	 * @Description: 保存满减、满折记录
	 * @param orderId 订单id
	 * @param orderReq 请求对象
	 * @return void  
	 * @author zengj
	 * @date 2016年7月20日
	 */
	private void saveActivityDiscountRecord(String orderId, ServiceOrderReq orderReq) {
		ActivityDiscountRecord discountRecord = new ActivityDiscountRecord();

		discountRecord.setId(UuidUtils.getUuid());
		discountRecord.setDiscountId(orderReq.getActivityId());
		discountRecord.setDiscountConditionsId(orderReq.getActivityItemId());
		discountRecord.setUserId(orderReq.getUserId());
		discountRecord.setStoreId(orderReq.getStoreId());
		discountRecord.setOrderId(orderId);
		discountRecord.setOrderTime(new Date());

		if (orderReq.getActivityType() == ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES) {
			// 满减活动
			discountRecord.setDiscountType(ActivityDiscountType.mlj);
		} else {
			// 满折活动
			discountRecord.setDiscountType(ActivityDiscountType.discount);
		}

		activityDiscountRecordMapper.insertRecord(discountRecord);
	}

	/**
	 * @Description: 锁定库存
	 * @param tradeOrder 构建的订单对象
	 * @param checkResult 订单校验通过后的对象信息
	 * @param orderReq   订单请求参数
	 * @return void  
	 * @throws Exception 库存校验异常
	 * @author zengj
	 * @date 2016年7月14日
	 */
	private void lockStock(TradeOrder tradeOrder, Map<String, Object> checkResult, ServiceOrderReq orderReq)
			throws Exception {
		GoodsStoreSku goodsStoreSku = (GoodsStoreSku) checkResult.get("goodsStoreSku");
		List<AdjustDetailVo> saleGoodsList = new ArrayList<AdjustDetailVo>();
		AdjustDetailVo saleDetail = new AdjustDetailVo();
		saleDetail.setBarCode(goodsStoreSku.getBarCode());
		saleDetail.setGoodsName(goodsStoreSku.getName());
		saleDetail.setGoodsSkuId(goodsStoreSku.getSkuId());
		saleDetail.setMultipleSkuId(goodsStoreSku.getMultipleSkuId());
		saleDetail.setNum(Integer.valueOf(orderReq.getSkuNum()));
		saleDetail.setPrice(orderReq.getUnitPrice());
		saleDetail.setPropertiesIndb(goodsStoreSku.getPropertiesIndb());
		saleDetail.setStoreSkuId(goodsStoreSku.getId());
		saleGoodsList.add(saleDetail);

		StockAdjustVo stockVo = new StockAdjustVo();
		stockVo.setOrderId(tradeOrder.getId());
		stockVo.setStoreId(orderReq.getStoreId());
		stockVo.setUserId(orderReq.getUserId());
		stockVo.setAdjustDetailList(saleGoodsList);
		// 非活动下单，占用普通库存
		if (StringUtils.isBlank(orderReq.getSeckillId())) {
			stockVo.setStockOperateEnum(StockOperateEnum.PLACE_ORDER);
		} else {
			// 活动下单，占用活动库存
			stockVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_PLACE_ORDER);
		}
		stockManagerService.updateStock(stockVo);
	}

	/**
	 * @Description: 构建秒杀活动记录实体
	 * @param orderReq 订单请求参数
	 * @param order 构建后的订单信息
	 * @return ActivitySeckillRecord 秒杀活动记录实体  
	 * @author zengj
	 * @date 2016年7月15日
	 */
	private ActivitySeckillRecord buildActivitySeckillRecord(ServiceOrderReq orderReq, TradeOrder order) {
		ActivitySeckillRecord activitySeckillRecord = new ActivitySeckillRecord();
		activitySeckillRecord.setId(UuidUtils.getUuid());
		// 秒杀活动ID
		activitySeckillRecord.setActivitySeckillId(orderReq.getSeckillId());
		// 买家ID
		activitySeckillRecord.setBuyerUserId(orderReq.getUserId());
		activitySeckillRecord.setStoreId(orderReq.getStoreId());
		activitySeckillRecord.setOrderId(order.getId());
		// 活动商品ID
		activitySeckillRecord.setGoodsStoreSkuId(orderReq.getSkuId());
		activitySeckillRecord.setOrderNo(order.getOrderNo());
		activitySeckillRecord.setOrderDisabled("0");
		return activitySeckillRecord;
	}

	/**
	 * @Description: 构建发票实体对象
	 * @param orderReq 订单请求参数
	 * @param order 构建后的订单信息
	 * @return TradeOrderInvoice 发票实体对象  
	 * @author zengj
	 * @date 2016年7月15日
	 */
	private TradeOrderInvoice buildTradeOrderInvoice(ServiceOrderReq orderReq, TradeOrder order) {
		TradeOrderInvoice tradeOrderInvoice = new TradeOrderInvoice();
		tradeOrderInvoice.setId(UuidUtils.getUuid());
		tradeOrderInvoice.setOrderId(order.getId());
		tradeOrderInvoice.setHead(orderReq.getInvoiceHead());
		return tradeOrderInvoice;
	}

	/**
	 * @Description: 构建订单信息
	 * @param checkResult 校验通过后结果信息，保存了校验通过的信息
	 * @param orderReq 订单请求Vo
	 * @return TradeOrder  订单实体
	 * @author zengj
	 * @throws OrderException 生成订单编号异常
	 * @date 2016年7月13日
	 */
	private TradeOrder buildTradeOrder(Map<String, Object> checkResult, ServiceOrderReq orderReq)
			throws OrderException {
		// 店铺信息
		StoreInfo storeInfo = (StoreInfo) checkResult.get("storeInfo");
		// 当前时间
		final Date now = new Date();
		// 校验通过后生成订单
		TradeOrder tradeOrder = new TradeOrder();
		tradeOrder.setId(UuidUtils.getUuid());
		// 如果是线下确认并当面支付，这时候订单状态是待派单
		if (orderReq.getPayWay().equals(PayWayEnum.OFFLINE_CONFIRM_AND_PAY)) {
			tradeOrder.setStatus(OrderStatusEnum.DROPSHIPPING);
			tradeOrder.setPayWay(PayWayEnum.OFFLINE_CONFIRM_AND_PAY);
		} else {
			// 否则是等待买家付款
			tradeOrder.setStatus(OrderStatusEnum.UNPAID);
			tradeOrder.setPayWay(PayWayEnum.PAY_ONLINE);
		}

		// 订单总金额
		tradeOrder.setTotalAmount(orderReq.getTotalAmount());
		tradeOrder.setActualAmount(orderReq.getActualAmount());
		tradeOrder.setPreferentialPrice(orderReq.getPreferentialPrice());
		tradeOrder.setFare(BigDecimal.ZERO);
		tradeOrder.setUserPhone(orderReq.getUserPhone());
		tradeOrder.setUserId(orderReq.getUserId());
		tradeOrder.setStoreName(storeInfo.getStoreName());
		tradeOrder.setSellerId(storeInfo.getId());
		tradeOrder.setStoreId(storeInfo.getId());
		tradeOrder.setPid("0");
		String orderNo = generateNumericalOrderService.generateNumberAndSave("FW");
		logger.info("生成的订单编号=[" + orderNo + "]");
		// 订单编号生成失败
		if (StringUtils.isBlank(orderNo)) {
			logger.warn("订单编号=[" + orderNo + "]");
			throw new OrderException(ExceptionConstant.ORDER_ADD_FAIL);
		}
		tradeOrder.setOrderNo(orderNo);
		tradeOrder.setType(OrderTypeEnum.SERVICE_STORE_ORDER);
		tradeOrder.setPickUpType(PickUpTypeEnum.DELIVERY_DOOR);
		tradeOrder.setPickUpCode(null);
		// 预约时间
		tradeOrder.setPickUpTime(orderReq.getServiceTime());
		// 有秒杀活动，活动类型设置为秒杀
		if (StringUtils.isNotBlank(orderReq.getSeckillId())) {
			tradeOrder.setActivityType(ActivityTypeEnum.SECKILL_ACTIVITY);
			tradeOrder.setActivityId(orderReq.getSeckillId());
		} else {
			// 满减活动
			if (orderReq.getActivityType() == ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES) {
				tradeOrder.setActivityType(ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES);
			} else if (orderReq.getActivityType() == ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES) {
				// 满折活动
				tradeOrder.setActivityType(ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES);
			} else {
				tradeOrder.setActivityType(null);
			}
			tradeOrder.setActivityId(orderReq.getActivityId());
		}
		tradeOrder.setRemark(orderReq.getRemark());
		// 如果有发票
		if (orderReq.getIsInvoice() == WithInvoiceEnum.HAS && StringUtils.isNotBlank(orderReq.getInvoiceHead())) {
			tradeOrder.setInvoice(WithInvoiceEnum.HAS);
		} else {
			tradeOrder.setInvoice(WithInvoiceEnum.NONE);
		}
		tradeOrder.setDisabled(Disabled.valid);
		tradeOrder.setCreateTime(now);
		tradeOrder.setUpdateTime(now);
		tradeOrder.setPaymentStatus(PaymentStatusEnum.STAY_BACK);
		tradeOrder.setOrderResource(OrderResourceEnum.YSCAPP);
		tradeOrder.setReceivedTime(null);
		tradeOrder.setDeliveryTime(null);
		tradeOrder.setPickUpId(orderReq.getAddressId());
		tradeOrder.setUpdateUserId(orderReq.getUserId());
		// 该字段已废弃
		tradeOrder.setPropertiesIndb(null);
		tradeOrder.setReason(null);
		tradeOrder.setPaymentTime(null);
		tradeOrder.setIncome(orderReq.getActualAmount());
		tradeOrder.setIsShow(OrderIsShowEnum.yes);
		tradeOrder.setPospay(null);
		tradeOrder.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
		tradeOrder.setPickUpItem(null);
		tradeOrder.setTradeNum(TradeNumUtil.getTradeNum());
		tradeOrder.setActivityItemId(orderReq.getActivityItemId());
		tradeOrder.setIsComplete(OrderComplete.NO);
		return tradeOrder;
	}

	/**
	 * 
	 * @Description: 构建服务地址信息
	 * @param order 订单信息
	 * @param memberConsigneeAddress 收货地址信息
	 * @return   订单物流信息
	 * @author zengj
	 * @date 2016年7月29日
	 */
	private TradeOrderLogistics buildTradeOrderLogistics(TradeOrder order,
			MemberConsigneeAddress memberConsigneeAddress) {
		TradeOrderLogistics logistics = new TradeOrderLogistics();
		logistics.setId(UuidUtils.getUuid());
		logistics.setAddress(memberConsigneeAddress.getAddress());
		logistics.setArea((StringUtils.isBlank(memberConsigneeAddress.getProvinceName()) ? ""
				: memberConsigneeAddress.getProvinceName())
				+ (StringUtils.isBlank(memberConsigneeAddress.getCityName()) ? ""
						: memberConsigneeAddress.getCityName())
				+ (StringUtils.isBlank(memberConsigneeAddress.getAreaName()) ? ""
						: memberConsigneeAddress.getAreaName())
				+ (StringUtils.isBlank(memberConsigneeAddress.getAreaExt()) ? ""
						: memberConsigneeAddress.getAreaExt()));

		logistics.setAreaId(memberConsigneeAddress.getAreaId());
		logistics.setCityId(memberConsigneeAddress.getCityId());
		logistics.setConsigneeName(memberConsigneeAddress.getConsigneeName());
		// logistics.setLogisticsCompanyName(memberConsigneeAddress.getCompanyName());
		// logistics.setLogisticsNo(memberConsigneeAddress.getlo);
		logistics.setMobile(memberConsigneeAddress.getMobile());
		logistics.setOrderId(order.getId());
		logistics.setProvinceId(memberConsigneeAddress.getProvinceId());
		logistics.setTelephone(memberConsigneeAddress.getTelephone());
		// logistics.setType(LogisticsType.NONE);
		logistics.setZipCode(memberConsigneeAddress.getZipCode());
		return logistics;
	}

	/**
	 * 
	 * @Description: 构建订单项信息
	 * @param checkResult 校验通过后结果信息，保存了校验通过的信息
	 * @param orderReq 订单请求Vo
	 * @return TradeOrderItem  订单项实体
	 * @author zengj
	 * @date 2016年7月13日
	 */
	private TradeOrderItem buildTradeOrderItem(Map<String, Object> checkResult, ServiceOrderReq orderReq) {
		// 商品信息
		GoodsStoreSku goodsStoreSku = (GoodsStoreSku) checkResult.get("goodsStoreSku");
		String propertiesIndb = "";

		// 解析商品中的properties
		String properties = goodsStoreSku.getPropertiesIndb();
		if (StringUtils.isNotBlank(properties)) {
			JSONObject jb = JSONObject.fromObject(goodsStoreSku.getPropertiesIndb());
			String skuPrperties = jb.get("skuName").toString();
			propertiesIndb = skuPrperties;
		}
		// 查询商品主图信息
		GoodsStoreSkuPicture goodsStoreSkuPicture = goodsStoreSkuPictureService
				.findMainPicByStoreSkuId(orderReq.getSkuId());
		TradeOrderItem tradeOrderItem = new TradeOrderItem();

		tradeOrderItem.setId(UuidUtils.getUuid());
		// 当前时间
		Date now = new Date();
		tradeOrderItem.setStoreSpuId(goodsStoreSku.getStoreSpuId());
		tradeOrderItem.setStoreSkuId(goodsStoreSku.getId());
		tradeOrderItem.setSkuName(goodsStoreSku.getName());
		tradeOrderItem.setPropertiesIndb(propertiesIndb);
		tradeOrderItem.setMainPicPrl(goodsStoreSkuPicture.getUrl());
		tradeOrderItem.setSpuType(GoodsTypeEnum.SERVICE_GOODS);
		tradeOrderItem.setUnitPrice(orderReq.getUnitPrice());
		tradeOrderItem.setQuantity(orderReq.getSkuNum());
		// 商品总额=商品单价*数量
		tradeOrderItem.setTotalAmount(orderReq.getTotalAmount());
		tradeOrderItem.setActualAmount(orderReq.getActualAmount());
		if (orderReq.getPreferentialPrice() != null) {
			tradeOrderItem.setPreferentialPrice(orderReq.getPreferentialPrice());
		}
		// 该字段废弃
		tradeOrderItem.setStatus(OrderItemStatusEnum.NO_REFUND);
		tradeOrderItem.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
		tradeOrderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);
		tradeOrderItem.setCreateTime(now);
		// 服务保障，来自数据字典，0:无、1:7天退、2:15天退、...
		tradeOrderItem.setServiceAssurance(0);
		tradeOrderItem.setBarCode(goodsStoreSku.getBarCode());
		tradeOrderItem.setStyleCode(goodsStoreSku.getStyleCode());
		tradeOrderItem.setIncome(orderReq.getActualAmount());
		return tradeOrderItem;
	}

	/**
	 * @Description: 非空校验
	 * @param orderReq   订单请求参数
	 * @return void  
	 * @author zengj
	 * @throws OrderException 订单校验异常信息
	 * @date 2016年7月15日
	 */
	private void checkNotNull(ServiceOrderReq orderReq) throws OrderException {
		// 服务栏目不能为空
		if (StringUtils.isNullOrEmpty(orderReq.getColumnServerId())) {
			throw new OrderException(ExceptionConstant.SERVER_COLUMN_IS_NULL);
		}
		// 服务店铺不能为空
		if (StringUtils.isNullOrEmpty(orderReq.getStoreId())) {
			throw new OrderException(ExceptionConstant.SERVER_STORE_IS_NULL);
		}
		// 商品不能为空
		if (StringUtils.isNullOrEmpty(orderReq.getSkuId())) {
			throw new OrderException(ExceptionConstant.GOODS_IS_NULL);
		}
		// 购买商品数量不能为空
		if (orderReq.getSkuNum() == null || orderReq.getSkuNum() == 0) {
			throw new OrderException(ExceptionConstant.GOODS_BUY_NUM_IS_NULL);
		}
		// 商品更新时间不能为空
		if (StringUtils.isNullOrEmpty(orderReq.getGoodsUpdateTime())) {
			throw new OrderException(ExceptionConstant.GOODS_UPDATE_TIME_IS_NULL);
		}
		// 商品更新时间格式错误
		if (DateUtils.parseDate(orderReq.getGoodsUpdateTime()) == null) {
			throw new OrderException(ExceptionConstant.GOODS_UPDATE_TIME_FORMAT_ERROR);
		}
	}

	/**
	 * @Description: 校验服务订单确认订单参数
	 * @param orderReq 订单请求参数
	 * @param result 校验通过的结果
	 * @param resultJson 返回前端信息
	 * @throws OrderException  订单验证异常 
	 * @throws Exception   其他异常
	 * @author zengj
	 * @date 2016年7月13日
	 */
	private void checkConfirmOrderInputParam(ServiceOrderReq orderReq, Map<String, Object> result,
			JSONObject resultJson) throws OrderException, Exception {
		// 1、先做非空校验
		checkNotNull(orderReq);
		// 2、再做业务逻辑校验
		ServerColumn serverColumn = serverColumnService.findById(orderReq.getColumnServerId());
		// 服务栏目不存在
		if (serverColumn == null || serverColumn.getDisabled() == Disabled.invalid) {
			resultJson.put("serverColumnStatus", ExceptionConstant.ZERO);
			throw new OrderException(ExceptionConstant.SERVER_COLUMN_NOT_EXISTS);
		}
		// 服务栏目已关闭
		if (serverColumn.getServerStatus() == ServerStatus.close) {
			resultJson.put("serverColumnStatus", ExceptionConstant.ONE);
			throw new OrderException(ExceptionConstant.SERVER_COLUMN_IS_CLOSED);
		}
		// 服务店铺不存在
		StoreInfo storeInfo = storeInfoService.getStoreInfoById(orderReq.getStoreId());
		if (storeInfo == null || storeInfo.getStoreInfoExt() == null) {
			resultJson.put("storeStatus", ExceptionConstant.ZERO);
			throw new OrderException(ExceptionConstant.SERVER_STORE_NOT_EXISTS);
		}
		// 店铺已关闭
		if (StoreStatusEnum.OPENING != storeInfo.getStoreInfoExt().getIsClosed()) {
			resultJson.put("storeStatus", ExceptionConstant.ONE);
			throw new OrderException(ExceptionConstant.SERVER_STORE_IS_CLOSED);
		}
		// 判断商品信息是否有更新
		GoodsStoreSku goodsStoreSku = goodsStoreSkuService.getById(orderReq.getSkuId());
		if (goodsStoreSku == null || goodsStoreSku.getOnline() != BSSC.PUTAWAY) {
			resultJson.put("skuStatus", ExceptionConstant.ZERO);
			resultJson.put("skuInfo", findGoodsStoreSkuInfo(orderReq.getSkuId()));
			throw new OrderException(ExceptionConstant.GOODS_NOT_EXSITS);
		}
		// 查询参数
		Map<String, Object> params = new HashMap<String, Object>();
		// 存在秒杀活动，库存为秒杀活动库存
		if (StringUtils.isNotEmpty(orderReq.getSeckillId())) {
			ActivitySeckill activitySeckill = activitySeckillService.findSeckillById(orderReq.getSeckillId());
			// 活动不存在
			if (activitySeckill == null) {
				throw new OrderException(ExceptionConstant.ACTIVITY_NOT_EXISTS);
			}
			resultJson.put("seckillStatus", activitySeckill.getSeckillStatus().ordinal());
			// 活动已关闭
			if (activitySeckill.getSeckillStatus() == SeckillStatusEnum.closed) {
				resultJson.put("goodsIsUpdate", ExceptionConstant.ONE);
				resultJson.put("skuInfo", findGoodsStoreSkuInfo(orderReq.getSkuId()));
				throw new OrderException(ExceptionConstant.ACTIVITY_IS_CLOSED);
			}
			// 活动已结束
			if (activitySeckill.getSeckillStatus() == SeckillStatusEnum.end) {
				resultJson.put("goodsIsUpdate", ExceptionConstant.ONE);
				resultJson.put("skuInfo", findGoodsStoreSkuInfo(orderReq.getSkuId()));
				throw new OrderException(ExceptionConstant.ACTIVITY_IS_END);
			}
			// 秒杀活动限购一件
			if (orderReq.getSkuNum() > Integer.parseInt(ExceptionConstant.ONE)) {
				throw new OrderException(ExceptionConstant.ACTIVITY_LIMIT_NUM);
			}
			// 活动商品和欲购买商品不一致
			if (!orderReq.getSkuId().equals(activitySeckill.getStoreSkuId())) {
				throw new OrderException(ExceptionConstant.ACTIVITY_GOODS_NOT_SUPPORT);
			}
			params.clear();
			// 统计该用户是否参与过该秒杀活动
			params.put("activitySeckillId", orderReq.getSeckillId());
			params.put("buyerUserId", orderReq.getUserId());
			// 查询用户参与该秒杀活动的次数
			int userBuyNum = activitySeckillRecordService.findSeckillCount(params);
			// 判断该用户是否参与过该秒杀活动，如果参与过，不能再次参与
			if (userBuyNum > 0) {
				resultJson.put("isBuy", "1");
				resultJson.put("skuInfo", findGoodsStoreSkuInfo(orderReq.getSkuId()));
				throw new OrderException(ExceptionConstant.ACTIVITY_NOT_REPEAT_PARTICIPATION);
			}
			// 秒杀数量
			int seckillNum = activitySeckill.getSeckillNum() == null ? 0 : activitySeckill.getSeckillNum();
			params.clear();
			// 统计秒杀活动已售出的数量
			params.put("activitySeckillId", orderReq.getSeckillId());
			int buySeckillNum = activitySeckillRecordService.findSeckillCount(params);
			// 如果秒杀数量小于已售数量+本次欲售数量，说明库存不足
			if (seckillNum < (buySeckillNum + orderReq.getSkuNum())) {
				resultJson.put("goodsIsUpdate", ExceptionConstant.ONE);
				resultJson.put("skuInfo", findGoodsStoreSkuInfo(orderReq.getSkuId()));
				throw new OrderException(ExceptionConstant.GOODS_STOCK_INSUFICIENTE_BY_ACTIVITY);
			}
			// 将验证通过的信息返回
			result.put("activitySeckill", activitySeckill);
		} else {
			// 如果有限购
			if (goodsStoreSku.getTradeMax() != null && goodsStoreSku.getTradeMax() > 0) {
				if (goodsStoreSku.getTradeMax() < orderReq.getSkuNum()) {
					throw new OrderException(
							String.format(ExceptionConstant.GOODS_LIMIT_NUM, goodsStoreSku.getTradeMax()));
				}
			}
			// 不是秒杀活动，校验正常商品库存
			GoodsStoreSkuStock goodsStoreSkuStock = goodsStoreSkuStockService.getBySkuId(orderReq.getSkuId());
			if (goodsStoreSkuStock.getSellable() < orderReq.getSkuNum()) {
				resultJson.put("stockStatus", ExceptionConstant.ONE);
				resultJson.put("skuInfo", findGoodsStoreSkuInfo(orderReq.getSkuId()));
				if (goodsStoreSkuStock.getSellable() == 0) {
					throw new OrderException(ExceptionConstant.GOODS_STOCK_INSUFICIENTE_BY_ZERO);
				}
				throw new OrderException(
						String.format(ExceptionConstant.GOODS_STOCK_INSUFICIENTE, goodsStoreSkuStock.getSellable()));
			}
		}
		Date goodsUpdateTime = DateUtils.parseDate(orderReq.getGoodsUpdateTime());
		// 商品信息有更新
		if (goodsStoreSku.getUpdateTime().getTime() != goodsUpdateTime.getTime()) {
			resultJson.put("goodsIsUpdate", ExceptionConstant.ONE);
			resultJson.put("skuInfo", findGoodsStoreSkuInfo(orderReq.getSkuId()));
			throw new OrderException(ExceptionConstant.GOODS_IS_UPDATE);
		}
		// 将验证通过的信息返回
		result.put("storeInfo", storeInfo);
		result.put("goodsStoreSku", goodsStoreSku);
	}

	/**
	 * 校验下单参数校验
	 * @param orderReq 订单请求参数
	 * @param result 校验通过的结果
	 * @param resultJson 返回前端JSON
	 * @throws OrderException  订单验证异常 
	 * @throws Exception   其他异常  
	 * @author zengj
	 * @date 2016年7月14日
	 */
	private void checkAddOrderInputParam(ServiceOrderReq orderReq, Map<String, Object> result, JSONObject resultJson)
			throws OrderException, Exception {
		// 先校验确认订单时的校验，如非空，店铺，库存等
		checkConfirmOrderInputParam(orderReq, result, resultJson);

		// 服务地址为空
		if (StringUtils.isNullOrEmpty(orderReq.getAddressId())) {
			throw new OrderException(ExceptionConstant.ADDRESS_IS_NULL);
		}
		// 服务时间为空
		if (StringUtils.isNullOrEmpty(orderReq.getServiceTime())) {
			throw new OrderException(ExceptionConstant.SERVER_TIME_IS_NULL);
		}
		try {
			DateUtils.parseDate(orderReq.getServiceTime(), "yyyy-MM-dd HH:mm");
		} catch (Exception e) {
			throw new OrderException(ExceptionConstant.SERVER_TIME_FORMAT_ERROR);
		}
		// 支付方式为空
		if (orderReq.getPayWay() == null) {
			throw new OrderException(ExceptionConstant.PAY_TYPE_IS_NULL);
		}

		// 如果存在活动，判断活动
		if (StringUtils.isNotEmpty(orderReq.getActivityId()) && StringUtils.isNotEmpty(orderReq.getActivityItemId())) {
			// 服务订单只有店铺满减满折活动，都是在同一个表中
			ActivityDiscount activityDiscount = activityDiscountService.getById(orderReq.getActivityId());
			// 活动不存在
			if (activityDiscount == null) {
				throw new OrderException(ExceptionConstant.ACTIVITY_NOT_EXISTS);
			}
			// 活动已关闭
			if (activityDiscount.getStatus() == ActivityDiscountStatus.closed) {
				throw new OrderException(ExceptionConstant.ACTIVITY_DISCOUNT_IS_CLOSED);
			}
			// 活动已结束
			if (activityDiscount.getStatus() == ActivityDiscountStatus.end) {
				throw new OrderException(ExceptionConstant.ACTIVITY_DISCOUNT_IS_END);
			}
			// 查询活动项信息
			ActivityDiscountCondition activityDiscountCondition = activityDiscountConditionMapper
					.findByPrimaryKey(orderReq.getActivityItemId());
			// 活动项不存在
			if (activityDiscountCondition == null) {
				throw new OrderException(ExceptionConstant.ACTIVITY_ITEM_NOT_EXISTS);
			}

			// 将验证通过的信息返回
			result.put("activityDiscount", activityDiscount);
			result.put("activityDiscountCondition", activityDiscountCondition);
		}

		// 判断服务地址是否存在
		MemberConsigneeAddress memberConsigneeAddress = memberConsigneeAddressService
				.getConsigneeAddress(orderReq.getAddressId());
		// 服务地址不存在
		if (memberConsigneeAddress == null) {
			throw new OrderException(ExceptionConstant.ADDRESS_NOT_EXSITS);
		}

		// 将验证通过的信息返回
		result.put("memberConsigneeAddress", memberConsigneeAddress);
	}

	/**
	 * 
	 * @Description: 计算订单金额
	 * @param orderReq 订单请求参数
	 * @param result   校验通过的结果信息
	 * @return void  
	 * @author zengj
	 * @throws Exception 抛出异常
	 * @date 2016年7月18日
	 */
	private void calcOrderAmount(ServiceOrderReq orderReq, Map<String, Object> result) throws Exception {
		ActivityDiscountCondition activityDiscountCondition = (ActivityDiscountCondition) result
				.get("activityDiscountCondition");
		ActivityDiscount activityDiscount = (ActivityDiscount) result.get("activityDiscount");
		GoodsStoreSku goodsStoreSku = (GoodsStoreSku) result.get("goodsStoreSku");
		MemberConsigneeAddress memberConsigneeAddress = (MemberConsigneeAddress) result.get("memberConsigneeAddress");

		// 是否已秒杀价格购买
		boolean isSeckillBuy = false;
		// 如果是秒杀活动且服务地址是秒杀活动范围，给秒杀价，不在范围内，给正常商品价格
		ActivitySeckill activitySeckill = (ActivitySeckill) result.get("activitySeckill");

		// 秒杀活动不为空就是秒杀价格
		if (activitySeckill != null) {
			// 查询秒杀活动范围
			List<ActivitySeckillRange> list = activitySeckillRangeService
					.findSeckillRangeAllBySeckillId(activitySeckill.getId());

			if (!CollectionUtils.isEmpty(list)) {
				// 循环秒杀活动范围
				for (ActivitySeckillRange range : list) {
					// 如果用户选择的地址在秒杀活动范围内，说明是秒杀价格购买
					if (StringUtils.isNotBlank(range.getProvinceName()) && StringUtils.isNotBlank(range.getCityName())
							&& range.getProvinceName().equals(memberConsigneeAddress.getProvinceName())
							&& range.getCityName().equals(memberConsigneeAddress.getCityName())) {
						isSeckillBuy = true;
						break;
					}
				}
			}
		}
		// 单价
		if (isSeckillBuy) {
			orderReq.setUnitPrice(activitySeckill.getSeckillPrice());
		} else {
			// 不在秒杀范围内，以原价购买,将秒杀ID清除掉
			orderReq.setSeckillId(null);
			orderReq.setUnitPrice(goodsStoreSku.getOnlinePrice());
		}
		// 如果是线下确认价格并当面支付的，订单金额是0
		if (orderReq.getPayWay() == PayWayEnum.OFFLINE_CONFIRM_AND_PAY) {
			orderReq.setTotalAmount(BigDecimal.ZERO);
			orderReq.setActualAmount(BigDecimal.ZERO);
			orderReq.setPreferentialPrice(BigDecimal.ZERO);
		} else {
			// 计算订单总额,单价*数量
			orderReq.setTotalAmount(orderReq.getUnitPrice().multiply(new BigDecimal(orderReq.getSkuNum())));
			// 如果存在活动, 且不是秒杀活动
			if (activityDiscount != null && !isSeckillBuy) {
				// 计算优惠活动折扣
				if (activityDiscount.getType() == ActivityDiscountType.discount) {
					// 满折需要计算对应的优惠金额,公式为：总价*折扣
					orderReq.setPreferentialPrice(orderReq.getTotalAmount().subtract(orderReq.getTotalAmount()
							.multiply(activityDiscountCondition.getDiscount().divide(new BigDecimal(10)))));
				} else {
					// 满减直接就是对应的减值
					orderReq.setPreferentialPrice(activityDiscountCondition.getDiscount());
				}
			}
			// 计算实付款=订单总价-优惠金额
			orderReq.setActualAmount(orderReq.getTotalAmount().subtract(
					orderReq.getPreferentialPrice() == null ? BigDecimal.ZERO : orderReq.getPreferentialPrice()));
		}
	}

	/**
	 * 
	 * @Description: 查询满减满折活动信息
	 * @param storeId 店铺ID
	 * @param totalAmount 订单金额
	 * @param seckillId 秒杀活动ID
	 * @param resultJson   返回JSON接口
	 * @author zengj
	 * @date 2016年7月22日
	 */
	private void findActivityDiscount(String storeId, BigDecimal totalAmount, String seckillId, JSONObject resultJson) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("storeId", storeId);
		params.put("totalAmount", totalAmount);
		JSONArray discountList = new JSONArray();
		JSONArray fullSubtractList = new JSONArray();
		// 查询活动列表
		List<Map<String, Object>> activityList = activityDiscountService.findActivityDiscountByStoreId(params);

		if (!CollectionUtils.isEmpty(activityList)) {
			DecimalFormat df = new DecimalFormat("#.##");
			for (Map<String, Object> map : activityList) {
				// 如果是满折活动
				if (map.get("activityType").toString().equals("0")) {
					JSONObject discount = new JSONObject();
					// 折扣标题
					discount.put("discountTitle",
							"满" + df.format(map.get("arrive")) + "享" + df.format(map.get("discount")) + "折");
					// 是否支持货到付款,0否，1是
					discount.put("usableRange", map.get("isCashDelivery"));
					// 有效期
					discount.put("indate", DateUtils.dateFormat((Date) map.get("endTime")));
					// 活动ID
					discount.put("id", map.get("activityId"));
					// 满值
					discount.put("arrive", map.get("arrive"));
					// 折扣值
					discount.put("discountPrice", map.get("discount"));
					// 商家活动还是平台活动，0：平台活动，1：商家活动，这里不会有平台活动，写死
					discount.put("type", "1");
					// 活动项ID
					discount.put("activityItemId", map.get("activityItemId"));

					discountList.add(discount);
				} else {
					// 满减活动

					JSONObject fullSubtract = new JSONObject();
					// 满减标题
					fullSubtract.put("fullSubtractTitle",
							df.format(map.get("arrive")) + "减" + df.format(map.get("discount")) + "元");
					// 是否支持货到付款,0否，1是
					fullSubtract.put("usableRange", map.get("isCashDelivery"));
					// 有效期
					fullSubtract.put("indate", DateUtils.dateFormat((Date) map.get("endTime")));
					// 活动ID
					fullSubtract.put("id", map.get("activityId"));
					// 满值
					fullSubtract.put("arrive", map.get("arrive"));
					// 满减金额
					fullSubtract.put("fullSubtractPrice", map.get("discount"));
					// 商家活动还是平台活动，0：平台活动，1：商家活动，这里不会有平台活动，写死
					fullSubtract.put("type", "1");
					// 活动项ID
					fullSubtract.put("activityItemId", map.get("activityItemId"));

					fullSubtractList.add(fullSubtract);
				}
			}
		}
		resultJson.put("discountList", discountList);
		resultJson.put("fullSubtractList", fullSubtractList);
	}

	/**
	 * 
	 * @Description: 查询秒杀活动范围
	 * @param seckill 秒杀活动实体
	 * @param resultJson 返回Json
	 * @throws Exception   抛出异常
	 * @author zengj
	 * @date 2016年7月27日
	 */
	private void findActivitySeckillRangeList(ActivitySeckill seckill, JSONObject resultJson) throws Exception {
		if (seckill == null) {
			return;
		}
		resultJson.put("seckillRangeType", seckill.getSeckillRangeType().ordinal());
		// 秒杀活动范围集合
		JSONArray seckillRangeList = new JSONArray();
		// 如果秒杀活动范围是区域
		if (seckill.getSeckillRangeType() == RangeTypeEnum.area) {
			// 查询秒杀活动范围
			List<ActivitySeckillRange> list = activitySeckillRangeService
					.findSeckillRangeAllBySeckillId(seckill.getId());
			if (!CollectionUtils.isEmpty(list)) {
				for (ActivitySeckillRange range : list) {
					JSONObject jsonRange = new JSONObject();
					jsonRange.put("provinceName", range.getProvinceName());
					jsonRange.put("cityName", range.getCityName());

					seckillRangeList.add(jsonRange);
				}
			}
		}
		resultJson.put("seckillRangeList", seckillRangeList);
	}

	/**
	 * 
	 * @Description: 查询店铺商品信息
	 * @param skuId 商品ID
	 * @return   商品信息
	 * @author zengj
	 * @date 2016年8月8日
	 */
	private Map<String, Object> findGoodsStoreSkuInfo(String skuId) {
		// 组装请求参数
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("skuId", skuId);
		// 查询商品详情信息
		Map<String, Object> result = goodsStoreSkuService.findGoodsDetailByMap(params);
		return result;
	}

	/**
	 * 
	 * @Description: 查询用户默认收货地址
	 * @param orderReq 订单参数
	 * @return   用户默认地址
	 * @author zengj
	 * @date 2016年8月11日
	 */
	private Map<String, Object> findUserDefaultAddress(ServiceOrderReq orderReq) {
		// 构建查询参数
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", orderReq.getUserId());
		params.put("storeId", orderReq.getStoreId());
		// 查询用户默认的地址信息
		Map<String, Object> result = null;
		// 存在秒杀ID，先去查符合秒杀活动且符合服务范围的默认地址
		if (StringUtils.isNotBlank(orderReq.getSeckillId())) {
			params.put("seckillId", orderReq.getSeckillId());
			result = memberConsigneeAddressService.findUserDefilatSeckillAddress(params);
		}
		// 没有符合秒杀活动地址且符合店铺服务范围的地址，查询不符合的秒杀但符合服务范围的默认地址
		if (result == null || result.isEmpty()) {
			result = memberConsigneeAddressService.findUserDefaultAddress(params);
		}
		// 防止空指针问题，new一个实例
		if (result == null) {
			result = new HashMap<String, Object>();
		}
		return result;
	}
}
