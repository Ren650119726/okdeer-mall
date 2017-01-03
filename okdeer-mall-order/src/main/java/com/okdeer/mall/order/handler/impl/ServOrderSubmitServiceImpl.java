
package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.base.enums.GoodsTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuService;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceServiceApi;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoServiceExt;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.CouponsFindVo;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountRecord;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountType;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountRecordMapper;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderLog;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.enums.AppraiseEnum;
import com.okdeer.mall.order.enums.CompainStatusEnum;
import com.okdeer.mall.order.enums.OrderIsShowEnum;
import com.okdeer.mall.order.enums.OrderItemStatusEnum;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PaymentStatusEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.enums.WithInvoiceEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.service.OrderReturnCouponsService;
import com.okdeer.mall.order.service.TradeOrderLogService;
import com.okdeer.mall.order.service.TradeOrderPayServiceApi;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.service.TradeOrderServiceApi;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.order.utils.OrderNoUtils;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;
import com.okdeer.mall.order.vo.TradeOrderGoodsItem;
import com.okdeer.mall.order.vo.TradeOrderServiceGoodsItem;
import com.okdeer.mall.system.mq.RollbackMQProducer;
import com.okdeer.mall.system.utils.ConvertUtil;

import net.sf.json.JSONObject;

/**
 * ClassName: ServOrderAddServiceImpl 
 * @Description: 服务订单下单(上门服务，到店消费)
 * @author wushp
 * @date 2016年9月28日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.1.0			2016年9月28日				wushp		服务订单下单(上门服务，到店消费)
 *		V1.2			2016年11月9日				maojj		服务订单下单增加违约金判断
 */
@Service("servOrderSubmitService")
public class ServOrderSubmitServiceImpl implements RequestHandler<ServiceOrderReq, ServiceOrderResp> {

	/**
	 * LOG
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServOrderSubmitServiceImpl.class);

	/**
	 * 生成编号的service
	 */
	@Resource
	private GenerateNumericalService generateNumericalService;

	/**
	 * 地址mapper
	 */
	@Resource
	private MemberConsigneeAddressMapper memberConsigneeAddressMapper;

	/**
	 * 订单服务Service
	 */
	@Resource
	private TradeOrderService tradeOrderService;

	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerServiceApi stockManagerServiceApi;

	/**
	 * 订单超时计时器
	 */
	@Autowired
	private TradeOrderTimer tradeOrderTimer;

	/**
	 * 消息生产者
	 */
	@Resource
	private RollbackMQProducer rollbackMQProducer;

	/**
	 * 代金券记录Mapper
	 */
	@Resource
	private ActivityCouponsRecordMapper activityCouponsRecordMapper;

	/**
	 * 折扣活动表（折扣、满减）Mapper
	 */
	@Resource
	private ActivityDiscountMapper activityDiscountMapper;

	/**
	 * 店铺信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoService;

	/**
	 * 代金券Mapper
	 */
	@Resource
	private ActivityCouponsMapper activityCouponsMapper;

	/**
	 * 折扣活动记录Mapper
	 */
	@Resource
	private ActivityDiscountRecordMapper activityDiscountRecordMapper;

	/**
	 * 订单操作记录Service
	 */
	@Resource
	private TradeOrderLogService tradeOrderLogService;

	/**
	 * 商品信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuService;
	
	/**
	 * TradeOrderServiceApi
	 */
	@Reference(version = "1.0.0", check = false)
	private TradeOrderServiceApi tradeOrderServiceApi;
	
	/**
	 * 服务商品信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceServiceApi goodsStoreSkuServiceServiceApi;
	
	@Reference(version = "1.0.0", check = false)
	private TradeOrderPayServiceApi tradeOrderPayService;
	
	// Begin added by maojj 2016-10-18 
	@Resource
	private OrderReturnCouponsService orderReturnCouponsService;
	// End added by maojj 2016-10-18

	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		List<String> rpcIdList = new ArrayList<String>();
		try {
			ServiceOrderResp respData = resp.getData();
			ServiceOrderReq reqData = req.getData();
			// 判断服务地址是否存在
			MemberConsigneeAddress address = memberConsigneeAddressMapper
					.selectByPrimaryKey(req.getData().getAddressId());
			// 如果是到店消费订单，不需要判断服务地址
			if (reqData.getOrderType() != OrderTypeEnum.STORE_CONSUME_ORDER) {
				// 服务地址不存在
				if (address == null) {
					resp.setResult(ResultCodeEnum.ADDRESS_NOT_EXSITS);
					req.setComplete(true);
					return;
				}
			}
			// 根据请求构建订单
			TradeOrder tradeOrder = buildTradeOrder(req, address, resp);
			// 更新代金券
			updateActivityCoupons(tradeOrder, reqData);
			// 保存满减、满折记录
			saveActivityDiscountRecord(tradeOrder.getId(), reqData);
			// 保存订单和订单项信息，并发送消息
			tradeOrderService.insertTradeOrder(tradeOrder);
			tradeOrderLogService.insertSelective(new TradeOrderLog(tradeOrder.getId(), tradeOrder.getUserId(),
					tradeOrder.getStatus().getName(), tradeOrder.getStatus().getValue()));

			// 更新库存 -- 放到最后执行
			toUpdateStock(tradeOrder, req, rpcIdList, resp);
			// 更新地址信息,更新用户使用时间
			if (address != null) {
				address.setUseTime(DateUtils.getSysDate());
				memberConsigneeAddressMapper.updateByPrimaryKeySelective(address);
			}
			// 线下确认价格并支付
			if (tradeOrder.getPayWay() == PayWayEnum.OFFLINE_CONFIRM_AND_PAY) {
				Date serviceTime = DateUtils.parseDate(tradeOrder.getPickUpTime().substring(0,16), "yyyy-MM-dd HH:mm");
				
				// Begin V1.1.0 added by maojj 2016-10-18
				// 如果线下确认价格并支付，则下单时，给邀请人返回邀请注册活动代金券。 
				orderReturnCouponsService.firstOrderReturnCoupons(tradeOrder);
				// End V1.1.0 added by maojj 2016-10-18
				
				// 预约服务时间过后2小时未派单的自动取消订单
				tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_server_timeout, tradeOrder.getId(),
						(DateUtils.addHours(serviceTime, 2).getTime() - DateUtils.getSysDate().getTime()) / 1000);

				List<GoodsStoreSku> goodsStoreSkuList = (List<GoodsStoreSku>) req.getContext().get("storeSkuList");
				List<TradeOrderGoodsItem> list = req.getData().getList();
				for (GoodsStoreSku goodsStoreSku : goodsStoreSkuList) {
					for (TradeOrderGoodsItem goodsItem : list) {
						if (goodsStoreSku.getId().equals(goodsItem.getSkuId())) {
							int skuNum = goodsItem.getSkuNum();
							goodsStoreSku.setSaleNum(
									(goodsStoreSku.getSaleNum() == null ? 0 : goodsStoreSku.getSaleNum()) + skuNum);

							this.goodsStoreSkuService.updateByPrimaryKeySelective(goodsStoreSku);
						}
					}

				}
			} else {
				
				if (tradeOrder.getActualAmount().compareTo(BigDecimal.ZERO) == 0) {
					//余额支付
					tradeOrderPayService.wlletPay("0",tradeOrder);
				}else{
					// 超时未支付的，取消订单
					tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_pay_timeout, tradeOrder.getId());
				}
			}

			// resp.setCode(PublicResultCodeEnum.SUCCESS);
			respData.setOrderId(tradeOrder.getId());
			respData.setOrderNo(tradeOrder.getOrderNo());
			respData.setOrderPrice(ConvertUtil.format(tradeOrder.getActualAmount()));
			respData.setTradeNum(tradeOrder.getTradeNum());
			respData.setLimitTime(1800);

		} catch (Exception e) {
			if (rpcIdList != null) {
				rollbackMQProducer.sendStockRollbackMsg(rpcIdList);
			}
			throw e;
		} finally {
			req.setComplete(true);
		}
	}

	/**
	 * @Description: 构建订单
	 * @return  TradeOrder
	 * @author wushp
	 * @date 2016年9月23日
	 */
	public TradeOrder buildTradeOrder(Request<ServiceOrderReq> req, MemberConsigneeAddress address,
			Response<ServiceOrderResp> resp) throws Exception {
		TradeOrder tradeOrder = new TradeOrder();

		ServiceOrderReq reqData = req.getData();
		StoreInfoServiceExt servExt = resp.getData().getStoreInfoServiceExt();

		tradeOrder.setId(UuidUtils.getUuid());
		tradeOrder.setUserId(reqData.getUserId());
		tradeOrder.setUserPhone(reqData.getUserPhone());
		tradeOrder.setStoreId(reqData.getStoreId());
		tradeOrder.setStoreName((String) req.getContext().get("storeName"));
		tradeOrder.setSellerId(reqData.getStoreId());
		tradeOrder.setRemark(reqData.getRemark());
		tradeOrder.setType(reqData.getOrderType());
		tradeOrder.setPid("0");
		tradeOrder.setActivityType(reqData.getActivityType());
		tradeOrder.setActivityId(reqData.getActivityId());
		tradeOrder.setActivityItemId(reqData.getActivityItemId());
		tradeOrder.setOrderResource(OrderResourceEnum.YSCAPP);
		tradeOrder.setIsShow(OrderIsShowEnum.yes);
		tradeOrder.setPaymentStatus(PaymentStatusEnum.STAY_BACK);
		tradeOrder.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
		tradeOrder.setTradeNum(TradeNumUtil.getTradeNum());
		tradeOrder.setDisabled(Disabled.valid);
		tradeOrder.setCreateTime(new Date());
		tradeOrder.setUpdateTime(new Date());
		// 设置订单编号
		setOrderNo(tradeOrder);
		// 设置订单总金额
		//List<TradeOrderGoodsItem> list = reqData.getList();
		List<TradeOrderServiceGoodsItem> list = resp.getData().getList();
		tradeOrder.setTotalAmount(calculateAmount(list));
		// 设置支付方式
		// 如果是线下确认并当面支付，这时候订单状态是待接单
		if (reqData.getPayWay().equals(PayWayEnum.OFFLINE_CONFIRM_AND_PAY)) {
			tradeOrder.setStatus(OrderStatusEnum.WAIT_RECEIVE_ORDER);
			tradeOrder.setPayWay(PayWayEnum.OFFLINE_CONFIRM_AND_PAY);
			//线下确认并当面支付的订单实付金额、收入均为0
			tradeOrder.setActualAmount(BigDecimal.valueOf(0));
			tradeOrder.setIncome(BigDecimal.valueOf(0));
			tradeOrder.setPreferentialPrice(BigDecimal.valueOf(0));
		} else {
			// 否则是等待买家付款
			tradeOrder.setStatus(OrderStatusEnum.UNPAID);
			tradeOrder.setPayWay(PayWayEnum.PAY_ONLINE);
			// 解析优惠活动(优惠金额,实付金额,店铺总收入)
			parseFavour(tradeOrder, reqData);
		}

		OrderTypeEnum orderType = reqData.getOrderType();
		switch (orderType) {
			case SERVICE_STORE_ORDER:
				tradeOrder.setPickUpType(PickUpTypeEnum.DELIVERY_DOOR);
				break;
			case STORE_CONSUME_ORDER:
				tradeOrder.setPickUpType(PickUpTypeEnum.TO_STORE_PICKUP);
				break;
			default:
				break;
		}

		// 解析提货类型
		parsePickType(tradeOrder, reqData, resp);
		tradeOrder.setPickUpCode(null);
		// 设置发票
		setTradeOrderInvoice(tradeOrder, reqData);
		// 根据请求构建订单项列表
		List<TradeOrderItem> orderItemList = buildOrderItemList(tradeOrder, req, resp);
		// 设置订单项
		tradeOrder.setTradeOrderItem(orderItemList);
		// 如果地址不为空，说明是到店消费订单。不保存地址信息
		if (address != null) {
			tradeOrder.setTradeOrderLogistics(buildTradeOrderLogistics(tradeOrder.getId(), address));
		}
		// Begin V1.2 added by maojj 2016-11-08
		// 订单默认未违约
		tradeOrder.setIsBreach(WhetherEnum.not);
		if(servExt != null){
			// 店铺设置是否有违约金
			tradeOrder.setIsBreachMoney(WhetherEnum.enumOrdinalOf(servExt.getIsBreachMoney()));
			// 店铺设置收取违约金的时间限制
			tradeOrder.setBreachTime(servExt.getBreachTime());
			// 店铺设置违约金的百分比
			tradeOrder.setBreachPercent(servExt.getBreachPercent());
		}
		if(tradeOrder.getIsBreachMoney() == WhetherEnum.whether){
			// 如果店铺设置了违约金，计算订单应该收取的违约金存入订单记录中
			tradeOrder.setBreachMoney(tradeOrder.getActualAmount().multiply(BigDecimal.valueOf(tradeOrder.getBreachPercent())).divide(BigDecimal.valueOf(100),2, BigDecimal.ROUND_UP));
		}
		// End V1.2 added by maojj 2016-11-08
		
		return tradeOrder;
	}

	/**
	 * @Description: 设置订单编号
	 * @param tradeOrder 交易订单
	 * @return void  无
	 * @throws ServiceException 自定义异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setOrderNo(TradeOrder tradeOrder) throws ServiceException {
		String orderNo = generateNumericalService.generateOrderNo(OrderNoUtils.SERV_ORDER_PREFIXE, "",
				OrderNoUtils.ONLINE_POS_ID);
		//String orderNo = generateNumericalService.generateNumberAndSave(OrderNoUtils.SERV_ORDER_PREFIXE);
		tradeOrder.setOrderNo(orderNo);
	}

	/**
	 * @Description: 设置发票
	 * @param tradeOrder 交易订单
	 * @param req 订单请求对象
	 * @return void  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private void setTradeOrderInvoice(TradeOrder tradeOrder, ServiceOrderReq reqData) {
		TradeOrderInvoice orderInvoice = new TradeOrderInvoice();
		// 是否有发票标识(0:无,1:有)
		WithInvoiceEnum invoice = reqData.getIsInvoice();
		tradeOrder.setInvoice(invoice);
		if (invoice == WithInvoiceEnum.HAS) {
			// 有发票
			orderInvoice.setId(UuidUtils.getUuid());
			orderInvoice.setOrderId(tradeOrder.getId());
			orderInvoice.setHead(reqData.getInvoiceHead());
			tradeOrder.setTradeOrderInvoice(orderInvoice);
		}
	}

	/**
	 * @Description: 构建订单项列表
	 * @param tradeOrder 交易订单
	 * @param reqDto 请求对象
	 * @throws ServiceException 自定义异常  
	 * @return List
	 * @author wushp
	 * @date 2016年9月28日
	 */
	@SuppressWarnings("unchecked")
	private List<TradeOrderItem> buildOrderItemList(TradeOrder tradeOrder, Request<ServiceOrderReq> req,
			Response<ServiceOrderResp> resp) throws Exception {
		List<TradeOrderItem> orderItemList = new ArrayList<TradeOrderItem>();
		// 数据库中对应的商品信息list
		List<GoodsStoreSku> goodsStoreSkuList = (List<GoodsStoreSku>) req.getContext().get("storeSkuList");
		// List<TradeOrderServiceGoodsItem> goodsSkuItemList =
		// resp.getData().getList();
		// List<TradeOrderGoodsItem> goodsSkuItemList = req.getData().getList();
		List<TradeOrderServiceGoodsItem> goodsSkuItemList = resp.getData().getList();
		// 订单项总金额
		BigDecimal totalAmount = tradeOrder.getTotalAmount();
		BigDecimal totalFavour = tradeOrder.getPreferentialPrice();
		BigDecimal favourSum = new BigDecimal("0.00");
		GoodsStoreSku storeSku = null;
		int index = 0;
		int itemSize = goodsSkuItemList.size();
		TradeOrderItem tradeOrderItem = null;
		ComparatorChain chain = new ComparatorChain();
		chain.addComparator(new BeanComparator("unitPrice"), false);
		Collections.sort(goodsSkuItemList, chain);
		// 订单类型
		OrderTypeEnum orderType = req.getData().getOrderType();
		// for (TradeOrderServiceGoodsItem goodsItem : goodsSkuItemList) {
		for (TradeOrderServiceGoodsItem goodsItem : goodsSkuItemList) {
			storeSku = findFromStoreSkuList(goodsStoreSkuList, goodsItem.getSkuId());
			tradeOrderItem = new TradeOrderItem();
			tradeOrderItem.setId(UuidUtils.getUuid());
			tradeOrderItem.setOrderId(tradeOrder.getId());
			tradeOrderItem.setStoreSpuId(storeSku.getStoreSpuId());
			tradeOrderItem.setStoreSkuId(storeSku.getId());
			tradeOrderItem.setSkuName(storeSku.getName());
			tradeOrderItem.setPropertiesIndb(parseProperties(storeSku.getPropertiesIndb()));
			tradeOrderItem.setMainPicPrl(goodsItem.getSkuIcon());
			tradeOrderItem.setSpuType(GoodsTypeEnum.SERVICE_GOODS);
			tradeOrderItem.setUnitPrice(storeSku.getOnlinePrice());
			tradeOrderItem.setQuantity(goodsItem.getSkuNum());
			tradeOrderItem.setStatus(OrderItemStatusEnum.NO_REFUND);
			tradeOrderItem.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
			tradeOrderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);
			tradeOrderItem.setCreateTime(new Date());
			// 服务保障
			if (req.getData().getOrderType().ordinal() == OrderTypeEnum.STORE_CONSUME_ORDER.ordinal()) {
				// 到店消费，取goods_store_sku_service表的is_unsubscribe是否支持退订，0：不支持，1：支持
				GoodsStoreSkuService storeSkuService = goodsStoreSkuServiceServiceApi
						.findGoodsStoreSkuServiceBySkuId(goodsItem.getSkuId());
				if (storeSkuService != null && storeSkuService.getIsUnsubscribe() != null) {
					tradeOrderItem.setServiceAssurance(storeSkuService.getIsUnsubscribe().ordinal());
				} else {
					tradeOrderItem.setServiceAssurance(0);
				}
			} else {
				// 上门服务商品
				tradeOrderItem.setServiceAssurance(ConvertUtil.parseInt(storeSku.getGuaranteed()));
			}
			
			tradeOrderItem.setBarCode(storeSku.getBarCode());
			tradeOrderItem.setStyleCode(storeSku.getStyleCode());

			// 订单项总金额
			BigDecimal totalAmountOfItem = goodsItem.getTotalAmount();
			tradeOrderItem.setTotalAmount(goodsItem.getTotalAmount());
			// 设置优惠金额
			// 计算订单项优惠金额
			BigDecimal favourItem = new BigDecimal(0.0);
			if (req.getData().getActivityType() != ActivityTypeEnum.NO_ACTIVITY) {
				if (index++ < itemSize - 1) {
					favourItem = totalAmountOfItem.multiply(totalFavour).divide(totalAmount, 2, BigDecimal.ROUND_FLOOR);
					if (favourItem.compareTo(totalAmountOfItem) == 1) {
						favourItem = totalAmountOfItem;
					}
					favourSum = favourSum.add(favourItem);
				} else {
					favourItem = totalFavour.subtract(favourSum);
					if (favourItem.compareTo(totalAmountOfItem) == 1) {
						favourItem = totalAmountOfItem;
					}
				}
			}
			tradeOrderItem.setPreferentialPrice(favourItem);
			// 设置实付金额
			if (req.getData().getPayWay() == PayWayEnum.OFFLINE_CONFIRM_AND_PAY) {
				// 线下支付的实付金额为0
				tradeOrderItem.setActualAmount(BigDecimal.valueOf(0));
				tradeOrderItem.setIncome(BigDecimal.valueOf(0));
			} else {
				tradeOrderItem.setActualAmount(totalAmountOfItem.subtract(favourItem));
				// 设置订单项收入
				setOrderItemIncome(tradeOrderItem, tradeOrder);
			}
			if (tradeOrderItem.getActualAmount().compareTo(BigDecimal.ZERO) == 0
					&& orderType == OrderTypeEnum.STORE_CONSUME_ORDER ) {
				// 实付金额为0的到店消费订单，设置服务保障为无
				tradeOrderItem.setServiceAssurance(0);
			}
			orderItemList.add(tradeOrderItem);
		}
		return orderItemList;
	}

	/**
	 * @Description: 设置商品属性
	 * @param tradeOrderItem 交易订单项
	 * @param propertiesIndb 商品属性
	 * @return void  
	 * @author maojj
	 * @date 2016年7月19日
	 */
	private String parseProperties(String propertiesIndb) {
		String skuProperties = "";
		if (!StringUtils.isEmpty(propertiesIndb)) {
			JSONObject propertiesJson = JSONObject.fromObject(propertiesIndb);
			skuProperties = propertiesJson.get("skuName").toString();
		}
		return skuProperties;
	}

	/**
	 * @Description: 设置TradeOrderLogistics
	 * @param tradeOrder 交易订单 
	 * @param addressId 店铺地址  
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private TradeOrderLogistics buildTradeOrderLogistics(String orderId, MemberConsigneeAddress address) {
		// 获取买家收货地址
		TradeOrderLogistics orderLogistics = new TradeOrderLogistics();
		orderLogistics.setId(UuidUtils.getUuid());
		orderLogistics.setConsigneeName(address.getConsigneeName());
		orderLogistics.setMobile(address.getMobile());
		orderLogistics.setAddress(address.getAddress());

		StringBuilder area = new StringBuilder();
		area.append(ConvertUtil.format(address.getProvinceName())).append(ConvertUtil.format(address.getCityName()))
				.append(ConvertUtil.format(address.getAreaName())).append(ConvertUtil.format(address.getAreaExt()));
		orderLogistics.setArea(area.toString());

		orderLogistics.setOrderId(orderId);
		orderLogistics.setAreaId(address.getAreaId());
		orderLogistics.setProvinceId(address.getProvinceId());
		orderLogistics.setCityId(address.getCityId());
		orderLogistics.setZipCode(address.getZipCode());
		return orderLogistics;
	}

	/**
	 * @Description: 更新库存
	 * @param order 订单对象
	 * @param reqDto 请求对象
	 * @return void  
	 * @throws Exception 异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void toUpdateStock(TradeOrder order, Request<ServiceOrderReq> req, List<String> rpcIdList,
			Response<ServiceOrderResp> resp) throws Exception {

		StockAdjustVo stockAdjustVo = null;
		stockAdjustVo = buildStockAdjustVo(order, req, resp);
		rpcIdList.add(stockAdjustVo.getRpcId());
		// 正常商品下单，更新库存
		stockManagerServiceApi.updateStock(stockAdjustVo);
	}

	/**
	 * @Description: 构建库存更新对象
	 * @param order 订单对象
	 * @param reqDto 请求对象
	 * @return StockAdjustVo  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	@SuppressWarnings("unchecked")
	private StockAdjustVo buildStockAdjustVo(TradeOrder order, Request<ServiceOrderReq> req,
			Response<ServiceOrderResp> resp) {
		Map<String, Object> context = req.getContext();
		ServiceOrderReq reqData = req.getData();
		List<GoodsStoreSku> storeSkuList = (List<GoodsStoreSku>) context.get("storeSkuList");

		StockAdjustVo stockAjustVo = new StockAdjustVo();

		stockAjustVo.setRpcId(UuidUtils.getUuid());
		stockAjustVo.setOrderId(order.getId());
		stockAjustVo.setOrderNo(order.getOrderNo());
		stockAjustVo.setOrderResource(order.getOrderResource());
		stockAjustVo.setOrderType(order.getType());

		stockAjustVo.setStoreId(reqData.getStoreId());
		stockAjustVo.setUserId(reqData.getUserId());
		stockAjustVo.setMethodName(this.getClass().getName() + ".process");

		AdjustDetailVo adjustDetailVo = null;
		TradeOrderGoodsItem orderItem = null;
		List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();

		for (GoodsStoreSku storeSku : storeSkuList) {
			adjustDetailVo = new AdjustDetailVo();
			orderItem = req.getData().findOrderItem(storeSku.getId());

			adjustDetailVo.setBarCode(storeSku.getBarCode());
			adjustDetailVo.setGoodsName(storeSku.getName());
			adjustDetailVo.setGoodsSkuId(storeSku.getSkuId());
			adjustDetailVo.setMultipleSkuId(storeSku.getMultipleSkuId());
			adjustDetailVo.setNum(orderItem.getSkuNum());
			adjustDetailVo.setPrice(storeSku.getOnlinePrice());
			adjustDetailVo.setPropertiesIndb(storeSku.getPropertiesIndb());
			adjustDetailVo.setStoreSkuId(storeSku.getId());
			adjustDetailVo.setGoodsSkuId(storeSku.getSkuId());
			adjustDetailList.add(adjustDetailVo);
		}

		stockAjustVo.setAdjustDetailList(adjustDetailList);
		stockAjustVo.setStockOperateEnum(StockOperateEnum.PLACE_ORDER);
		return stockAjustVo;
	}

	/**
	 * @Description: 计算订单总金额
	 * @param goodsItemList 订单请求商品列表
	 * @return BigDecimal  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private BigDecimal calculateAmount(List<TradeOrderServiceGoodsItem> goodsItemList) {
		BigDecimal totalAmount = new BigDecimal(0.00);
		for (TradeOrderServiceGoodsItem goodsItem : goodsItemList) {
			totalAmount = totalAmount.add(goodsItem.getTotalAmount());
		}
		return totalAmount;
	}

	/**
	 * @Description: 解析优惠
	 * @param tradeOrder 交易订单
	 * @param reqData 订单请求对象
	 * @return void  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private void parseFavour(TradeOrder tradeOrder, ServiceOrderReq reqData) throws ServiceException {
		// 优惠金额
		BigDecimal favourAmount = getFavourAmount(reqData, tradeOrder.getTotalAmount());
		// 设置订单优惠金额
		tradeOrder.setPreferentialPrice(favourAmount);
		// 设置订单实付金额
		BigDecimal totalAmount = tradeOrder.getTotalAmount();
		// 如果总金额<优惠金额，则实付为0，否则实付金额为总金额-优惠金额
		if (totalAmount.compareTo(favourAmount) == -1 || totalAmount.compareTo(favourAmount) == 0) {
			tradeOrder.setActualAmount(new BigDecimal(0.0));
			// 实付金额为0时，订单状态为未支付
			tradeOrder.setStatus(OrderStatusEnum.UNPAID);
			OrderTypeEnum orderType = reqData.getOrderType();
			// 到店消费订单，实付金额为0时，订单状态为5（交易完成）
			if (orderType == OrderTypeEnum.STORE_CONSUME_ORDER) {
				tradeOrder.setStatus(OrderStatusEnum.HAS_BEEN_SIGNED);
			}
		} else {
			tradeOrder.setActualAmount(totalAmount.subtract(favourAmount));
		}
		// 设置店铺总收入
		setIncome(tradeOrder);
	}

	/**
	 * @Description: 获取订单的优惠金额
	 * @param req 订单请求对象
	 * @param totalAmount 订单总金额
	 * @return BigDecimal  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private BigDecimal getFavourAmount(ServiceOrderReq req, BigDecimal totalAmount) throws ServiceException {
		BigDecimal favourAmount = new BigDecimal(0.0);
		// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		ActivityTypeEnum activityType = req.getActivityType();
		switch (activityType) {
			case VONCHER:
				favourAmount = getCouponsFaceValue(req, totalAmount);
				break;
			case FULL_REDUCTION_ACTIVITIES:
				favourAmount = getDiscountValue(req.getActivityItemId());
				break;
			case FULL_DISCOUNT_ACTIVITIES:
				favourAmount = getDiscountFavour(req.getActivityItemId(), totalAmount);
				break;
			default:
				break;
		}
		if (favourAmount == null) {
			throw new ServiceException("未查到优惠金额");
		}
		return favourAmount;
	}

	/**
	 * @Description: 获取优惠金额
	 * @param req 订单请求对象
	 * @param totalAmount 商品总金额
	 * @return BigDecimal  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private BigDecimal getCouponsFaceValue(ServiceOrderReq req, BigDecimal totalAmount) {
		// 优惠金额
		BigDecimal favourAmount = BigDecimal.ZERO;
		CouponsFindVo findCondition = new CouponsFindVo();
		findCondition.setActivityId(req.getActivityId());
		findCondition.setActivityItemId(req.getActivityItemId());
		findCondition.setConponsType(req.getCouponsType());
		// 查询代金券
		ActivityCoupons activityCoupons = activityCouponsRecordMapper.selectCouponsItem(findCondition);
		// 代金券面额
		favourAmount = BigDecimal.valueOf(activityCoupons.getFaceValue());
		if (totalAmount.compareTo(favourAmount) == -1) {
			// 商品总金额小于代金券面值，优惠金额设为商品总金额
			favourAmount = totalAmount;
		}
		return favourAmount;
	}

	/**
	 * @Description: 获取满减满折优惠金额
	 * @param req 订单请求对象
	 * @param type 折扣类型：满减或满折
	 * @return BigDecimal  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private BigDecimal getDiscountValue(String activityItemId) {
		return activityDiscountMapper.getDiscountValue(activityItemId);
	}

	/**
	 * @Description: 获取满减优惠金额
	 * @param req 订单请求对象
	 * @param totalAmount 订单总金额
	 * @return BigDecimal  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private BigDecimal getDiscountFavour(String activityItemId, BigDecimal totalAmount) {
		BigDecimal discountVal = getDiscountValue(activityItemId);
		BigDecimal actualAmount = totalAmount.multiply(discountVal).divide(BigDecimal.valueOf(10)).setScale(2,
				BigDecimal.ROUND_DOWN);
		return totalAmount.subtract(actualAmount);
	}

	/**
	 * @Description:	设置订单总收入
	 * 	订单未参与活动、使用代金券，店铺总收入为：订单总金额
	 * 	订单参与运营商发起的满减活动，店铺总收入为：订单总金额
	 * 	订单参与店铺发起的满减满折活动，店铺总收入为：订单总金额-优惠金额（即订单实付金额）
	 * @param tradeOrder 交易订单  
	 * @return void  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private void setIncome(TradeOrder tradeOrder) {

		ActivityTypeEnum activityType = tradeOrder.getActivityType();

		switch (activityType) {
			case NO_ACTIVITY:
			case VONCHER:
				tradeOrder.setIncome(tradeOrder.getTotalAmount());
				break;
			case FULL_REDUCTION_ACTIVITIES:
				if (isPublishByStore(tradeOrder.getActivityId())) {
					// 活动由店铺发起，则收入==实付金额
					tradeOrder.setIncome(tradeOrder.getActualAmount());
				} else {
					tradeOrder.setIncome(tradeOrder.getTotalAmount());
				}
				break;
			case FULL_DISCOUNT_ACTIVITIES:
				tradeOrder.setIncome(tradeOrder.getActualAmount());
				break;
			default:
				break;
		}
	}

	/**
	 * @Description: 判断活动是否由店铺发起
	 * @param activityId 活动ID
	 * @return boolean  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private boolean isPublishByStore(String activityId) {
		boolean isPublishByStore = true;
		ActivityDiscount discount = activityDiscountMapper.selectByPrimaryKey(activityId);
		String storeId = discount.getStoreId();
		if ("0".equals(storeId)) {
			isPublishByStore = false;
		}
		return isPublishByStore;
	}

	/**
	 * @Description: 解析提货类型
	 * @param tradeOrder 交易订单
	 * @param reqDto 订单请求对象
	 * @return void  
	 * @throws ServiceException 异常  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private void parsePickType(TradeOrder tradeOrder, ServiceOrderReq reqDto, Response<ServiceOrderResp> resp)
			throws ServiceException {
		PickUpTypeEnum pickType = tradeOrder.getPickUpType();
		switch (pickType) {
			case DELIVERY_DOOR:
				processDelivery(tradeOrder, reqDto, resp);
				break;
			case TO_STORE_PICKUP:
				processPickUp(tradeOrder, reqDto, resp);
				break;
			default:
				break;
		}
	}

	/**
	 * @Description: 送货上门处理
	 * @param tradeOrder 交易订单
	 * @param reqDto 订单请求对象  
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void processDelivery(TradeOrder tradeOrder, ServiceOrderReq reqDto, Response<ServiceOrderResp> resp) {
		// 运费
		tradeOrder.setFare(BigDecimal.ZERO);
		// 服务店扩展信息
		StoreInfoServiceExt serviceExt = resp.getData().getStoreInfoServiceExt();
		// 线下支付确认的没有运费
		if (reqDto.getPayWay() != PayWayEnum.OFFLINE_CONFIRM_AND_PAY && serviceExt != null && serviceExt.getIsShoppingCart() == 1 && serviceExt.getIsDistributionFee() == 1) {
			// 配送费
			Double distributionFee = serviceExt.getDistributionFee();
			// 已满起送价是否收取配送费，0：否，1：是
			Integer isCollect = serviceExt.getIsCollect();
			// 是否有起送价，0：否，1：是
			Integer isStartingPrice = serviceExt.getIsStartingPrice();
			if (isStartingPrice == 1) {
				if (serviceExt.getIsCollect() == 1) {
					// 已满起送价收取配送费
					tradeOrder.setFare(BigDecimal.valueOf(distributionFee));
					tradeOrder.setTotalAmount(tradeOrder.getTotalAmount().add(tradeOrder.getFare()));
					tradeOrder.setActualAmount(tradeOrder.getActualAmount().add(tradeOrder.getFare()));
					tradeOrder.setIncome(tradeOrder.getIncome().add(tradeOrder.getFare()));
				} else {
					// 已满起送价不收取配送费
					BigDecimal startingPrice = serviceExt.getStartingPrice();
					if (tradeOrder.getTotalAmount().compareTo(startingPrice) == -1) {
						// 设置运费
						tradeOrder.setFare(BigDecimal.valueOf(distributionFee));
						tradeOrder.setTotalAmount(tradeOrder.getTotalAmount().add(tradeOrder.getFare()));
						tradeOrder.setActualAmount(tradeOrder.getActualAmount().add(tradeOrder.getFare()));
						tradeOrder.setIncome(tradeOrder.getIncome().add(tradeOrder.getFare()));
					}
				}
				
			} else {
				// 无起送价
				if (isCollect == 1) {
					// 已满起送价收取配送费
					tradeOrder.setFare(BigDecimal.valueOf(distributionFee));
					tradeOrder.setTotalAmount(tradeOrder.getTotalAmount().add(tradeOrder.getFare()));
					tradeOrder.setActualAmount(tradeOrder.getActualAmount().add(tradeOrder.getFare()));
					tradeOrder.setIncome(tradeOrder.getIncome().add(tradeOrder.getFare()));
				}
			}
		}

		// 设置TradeOrderLogistics
		setTradeOrderLogistics(tradeOrder, reqDto.getAddressId());

		tradeOrder.setPickUpTime(reqDto.getServiceTime());
	}

	/**
	 * @Description: 处理到店自提
	 * @param tradeOrder 交易订单
	 * @param reqDto 订单请求对象
	 * @return void  
	 * @throws ServiceException 自定义异常
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private void processPickUp(TradeOrder tradeOrder, ServiceOrderReq reqDto, Response<ServiceOrderResp> resp)
			throws ServiceException {
		// 服务时间
		String serviceTime = reqDto.getServiceTime();

		StoreInfo storeInfo = storeInfoService.selectDefaultAddressById(reqDto.getStoreId());
		// 获取默认地址
		String defaultAddressId = storeInfo.getMemberConsignee().getId();
		tradeOrder.setPickUpId(defaultAddressId);
		if (reqDto.getOrderType() != OrderTypeEnum.STORE_CONSUME_ORDER) {
			if (!StringUtils.isEmpty(serviceTime)) {
				tradeOrder.setPickUpTime(serviceTime);
			} else {
				String attrTime = DateUtils.getDate();
				String pcTime = attrTime + " " + resp.getData().getStoreInfo().getStartTime() + "-"
						+ resp.getData().getStoreInfo().getEndTime();
				tradeOrder.setPickUpTime(pcTime);
			}
		}
		tradeOrder.setFare(new BigDecimal("0.00"));
	}

	/**
	 * @Description: 设置TradeOrderLogistics
	 * @param tradeOrder 交易订单 
	 * @param addressId 店铺地址  
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setTradeOrderLogistics(TradeOrder tradeOrder, String addressId) {
		// 获取买家收货地址
		MemberConsigneeAddress address = memberConsigneeAddressMapper.selectAddressById(addressId);

		TradeOrderLogistics orderLogistics = new TradeOrderLogistics();
		orderLogistics.setId(UuidUtils.getUuid());
		orderLogistics.setConsigneeName(address.getConsigneeName());
		orderLogistics.setMobile(address.getMobile());
		orderLogistics.setAddress(address.getAddress());

		StringBuilder area = new StringBuilder();
		area.append(clean(address.getProvinceName())).append(clean(address.getCityName()))
				.append(clean(address.getAreaName())).append(clean(address.getAreaExt()));
		orderLogistics.setArea(area.toString());

		orderLogistics.setOrderId(tradeOrder.getId());
		orderLogistics.setAreaId(address.getAreaId());
		orderLogistics.setProvinceId(address.getProvinceId());
		orderLogistics.setCityId(address.getCityId());
		orderLogistics.setZipCode(address.getZipCode());

		tradeOrder.setTradeOrderLogistics(orderLogistics);
	}

	/**
	 * @Description: 处理字符串
	 * @param str 源字符串对象
	 * @return String
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private String clean(String str) {
		return str == null ? "" : str;
	}

	/**
	 * @Description: 根据店铺商品ID查询店铺商品信息
	 * @param storeSkuList 店铺商品列表
	 * @param skuId 店铺商品ID
	 * @return GoodsStoreSku 店铺商品信息
	 * @throws ServiceException 自定义异常
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private GoodsStoreSku findFromStoreSkuList(List<GoodsStoreSku> storeSkuList, String skuId) throws ServiceException {
		GoodsStoreSku findResult = null;
		for (GoodsStoreSku storeSku : storeSkuList) {
			if (skuId.equals(storeSku.getId())) {
				findResult = storeSku;
				break;
			}
		}
		if (findResult == null) {
			LOGGER.error("根据ID查询店铺商品详细信息为空-------->{}", skuId);
			throw new ServiceException("查询店铺商品详细信息异常：storeSku为空");
		}
		return findResult;
	}

	/**
	 * @Description: 设置订单项收入
	 * @param tradeOrderItem 交易订单项
	 * @param tradeOrder 交易订单
	 * @return void  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private void setOrderItemIncome(TradeOrderItem tradeOrderItem, TradeOrder tradeOrder) {
		ActivityTypeEnum activityType = tradeOrder.getActivityType();

		switch (activityType) {
			case NO_ACTIVITY:
			case VONCHER:
				tradeOrderItem.setIncome(tradeOrderItem.getTotalAmount());
				break;
			case FULL_REDUCTION_ACTIVITIES:
				if (isPublishByStore(tradeOrder.getActivityId())) {
					// 活动由店铺发起，则收入==实付金额
					tradeOrderItem.setIncome(tradeOrderItem.getActualAmount());
				} else {
					tradeOrderItem.setIncome(tradeOrderItem.getTotalAmount());
				}
				break;
			case FULL_DISCOUNT_ACTIVITIES:
				tradeOrderItem.setIncome(tradeOrderItem.getActualAmount());
				break;
			default:
				break;
		}
	}

	/**
	 * @Description: 更新代金券
	 * @param tradeOrder 交易订单
	 * @param req 请求对象
	 * @return void  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private void updateActivityCoupons(TradeOrder tradeOrder, ServiceOrderReq reqData) {
		ActivityTypeEnum activityType = reqData.getActivityType();
		int couponsType = reqData.getCouponsType();

		if (activityType != ActivityTypeEnum.NO_ACTIVITY && !"".equals(couponsType)) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderId", tradeOrder.getId());
			params.put("id", reqData.getRecordId());
			params.put("collectUserId", tradeOrder.getUserPhone());
			params.put("couponsId", reqData.getActivityItemId());
			params.put("collectType", couponsType);
			// 更新代金券状态
			activityCouponsRecordMapper.updateActivityCouponsStatus(params);
			// 修改代金券使用数量
			activityCouponsMapper.updateActivityCouponsUsedNum(reqData.getActivityItemId());
		}
	}

	/**
	 * @Description: 保存满减、满折记录
	 * @param orderId 订单id
	 * @param req 请求对象
	 * @return void  
	 * @author wushp
	 * @date 2016年9月28日
	 */
	private void saveActivityDiscountRecord(String orderId, ServiceOrderReq reqData) {
		ActivityTypeEnum activityType = reqData.getActivityType();
		if (activityType != ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES
				&& activityType != ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES) {
			return;
		}

		ActivityDiscountRecord discountRecord = new ActivityDiscountRecord();

		discountRecord.setId(UuidUtils.getUuid());
		discountRecord.setDiscountId(reqData.getActivityId());
		discountRecord.setDiscountConditionsId(reqData.getActivityItemId());
		discountRecord.setUserId(reqData.getUserId());
		discountRecord.setStoreId(reqData.getStoreId());
		discountRecord.setOrderId(orderId);
		discountRecord.setOrderTime(new Date());

		if (activityType == ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES) {
			// 满减活动
			discountRecord.setDiscountType(ActivityDiscountType.mlj);
		} else {
			// 满折活动
			discountRecord.setDiscountType(ActivityDiscountType.discount);
		}

		activityDiscountRecordMapper.insertRecord(discountRecord);
	}
}
