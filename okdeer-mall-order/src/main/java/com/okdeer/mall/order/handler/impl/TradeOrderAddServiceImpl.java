
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.base.enums.GoodsTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerJxcServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.entity.StoreBranches;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoExt;
import com.okdeer.archive.store.service.StoreBranchesServiceApi;
import com.okdeer.archive.store.service.StoreInfoServiceApi;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.common.consts.LogConstants;
import com.okdeer.mall.activity.coupons.entity.ActivityCoupons;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleRecord;
import com.okdeer.mall.activity.coupons.entity.CouponsFindVo;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleRecordMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountRecord;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountType;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountRecordMapper;
import com.okdeer.mall.common.utils.DateUtils;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.order.constant.text.OrderTipMsgConstant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderLog;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.enums.AppraiseEnum;
import com.okdeer.mall.order.enums.CompainStatusEnum;
import com.okdeer.mall.order.enums.OrderIsShowEnum;
import com.okdeer.mall.order.enums.OrderItemStatusEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PaymentStatusEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.enums.WithInvoiceEnum;
import com.okdeer.mall.order.handler.TradeOrderAddService;
import com.okdeer.mall.order.service.GenerateNumericalService;
import com.okdeer.mall.order.service.OrderReturnCouponsService;
import com.okdeer.mall.order.service.TradeOrderLogService;
import com.okdeer.mall.order.service.TradeOrderPayServiceApi;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.order.utils.CodeStatistical;
import com.okdeer.mall.order.utils.OrderNoUtils;
import com.okdeer.mall.order.vo.TradeOrderContext;
import com.okdeer.mall.order.vo.TradeOrderGoodsItem;
import com.okdeer.mall.order.vo.TradeOrderReq;
import com.okdeer.mall.order.vo.TradeOrderReqDto;
import com.okdeer.mall.order.vo.TradeOrderResp;
import com.okdeer.mall.order.vo.TradeOrderRespDto;
import com.okdeer.mall.system.mapper.SysBuyerUserMapper;
import com.okdeer.mall.system.mq.RollbackMQProducer;

import net.sf.json.JSONObject;

/**
 * ClassName: TradeOrderAddServiceImpl 
 * @Description: 生成订单流程
 * @author maojj
 * @date 2016年7月14日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构V4.1			2016-07-14			maojj			生成订单流程
 *		重构V4.1			2016-07-30			maojj			修改获取折扣金额的查询语句
 *		Bug:12710		2016-08-15			maojj			修改地址信息
 *		1.0.Z			2016-09-05			zengj			增加订单操作记录
  *     1.0.Z	        2016年9月07日                    	zengj           库存管理修改，采用商业管理系统校验
  *     Bug:13906		2016-10-10			maojj			添加订单实付金额为0的处理
  *     V1.1.0			2016-10-18			maojj			添加邀请送代金券返券流程（针对货到付款的订单）
 */
@Service
public class TradeOrderAddServiceImpl implements TradeOrderAddService {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderAddServiceImpl.class);

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
	 * 会员收货地址信息Mapper
	 */
	@Resource
	private MemberConsigneeAddressMapper memberConsigneeAddressMapper;

	/**
	 * 店铺信息Dubbo接口
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreInfoServiceApi storeInfoServiceApi;

	/**
	 * 买家用户Mapper
	 */
	@Resource
	private SysBuyerUserMapper sysBuyerUserMapper;

	/**
	 * 生成编号的service
	 */
	@Resource
	private GenerateNumericalService generateNumericalService;

	/**
	 * 交易订单service
	 */
	@Resource
	private TradeOrderService tradeOrderSerive;

	/**
	 * 订单超时计时器
	 */
	@Resource
	private TradeOrderTimer tradeOrderTimer;

	// /**
	// * 库存管理Dubbo接口
	// */
	// @Reference(version = "1.0.0", check = false)
	// private StockManagerServiceApi stockManagerServiceApi;

	// Begin 1.0.Z add by zengj
	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerJxcServiceApi stockManagerServiceApi;

	/**
	 * 机构Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StoreBranchesServiceApi storeBranchesService;
	// End 1.0.Z add by zengj

	/**
	 * 代金券Mapper
	 */
	@Resource
	private ActivityCouponsMapper activityCouponsMapper;

	/**
	 * 特惠活动记录Mapper
	 */
	@Resource
	private ActivitySaleRecordMapper activitySaleRecordMapper;

	/**
	 * 折扣活动记录Mapper
	 */
	@Resource
	private ActivityDiscountRecordMapper activityDiscountRecordMapper;

	// Begin 1.0.Z 增加订单操作记录Service add by zengj
	/**
	 * 订单操作记录Service
	 */
	@Resource
	private TradeOrderLogService tradeOrderLogService;
	// End 1.0.Z 增加订单操作记录Service add by zengj

	/**
	 * 消息生产者
	 */
	@Resource
	private RollbackMQProducer rollbackMQProducer;
	
	// Begin Bug:13906 added by maojj 2016-10-10
	@Reference(version = "1.0.0", check = false)
	private TradeOrderPayServiceApi tradeOrderPayService;
	// End Bug:13906 added by maojj 2016-10-10
	
	// Begin added by maojj 2016-10-18 
	@Resource
	private OrderReturnCouponsService orderReturnCouponsService;
	// End added by maojj 2016-10-18

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void process(TradeOrderReqDto reqDto, TradeOrderRespDto respDto) throws Exception {
		List<String> rpcIdList = new ArrayList<String>();
		try {
			TradeOrderReq req = reqDto.getData();
			TradeOrderResp resp = respDto.getResp();
			// 根据请求构建订单
			TradeOrder tradeOrder = buildTradeOrder(reqDto);
			// 更新代金券
			updateActivityCoupons(tradeOrder, req);
			// 保存特惠商品记录
			saveActivitySaleRecord(tradeOrder.getId(), reqDto);
			// 保存满减、满折记录
			saveActivityDiscountRecord(tradeOrder.getId(), req);
			// 插入订单
			tradeOrderSerive.insertTradeOrder(tradeOrder);
			// 发送消息
			sendTimerMessage(tradeOrder.getId(), req.getPayType());

			// Begin 1.0.Z 增加订单操作记录 add by zengj
			tradeOrderLogService.insertSelective(new TradeOrderLog(tradeOrder.getId(), tradeOrder.getUserId(),
					tradeOrder.getStatus().getName(), tradeOrder.getStatus().getValue()));
			// End 1.0.Z 增加订单操作记录 add by zengj
			// 更新库存 -- 放到最后执行
			toUpdateStock(tradeOrder, reqDto, rpcIdList);
			// Begin Bug:13906 added by maojj 2016-10-10
			// 如果订单实付金额为0，调用余额支付进行支付。
			if(tradeOrder.getActualAmount().compareTo(BigDecimal.valueOf(0.0)) == 0){
				tradeOrderPayService.wlletPay(String.valueOf(tradeOrder.getActualAmount()), tradeOrder);
			}
			// End Bug:13906 added by maojj 2016-10-10
			
			// Begin V1.1.0 added by maojj 2016-10-18
			// 支付方式：0：在线支付、1:货到付款、6：微信支付
			if(req.getPayType() == 1){
				// 如果支付方式为货到付款，则下单时，给邀请人返回邀请注册活动代金券。 
				orderReturnCouponsService.firstOrderReturnCoupons(tradeOrder);
			}
			// End V1.1.0 added by maojj 2016-10-18
			
			resp.setOrderId(tradeOrder.getId());
			resp.setOrderNo(tradeOrder.getOrderNo());
			resp.setOrderPrice(tradeOrder.getActualAmount());
			resp.setTradeNum(tradeOrder.getTradeNum());
			// 订单倒计时
			resp.setLimitTime(60 * 30);
			resp.setIsOrder(1);
			respDto.setMessage(OrderTipMsgConstant.ORDER_SUCESS);
		} catch (Exception e) {
			rollbackMQProducer.sendStockRollbackMsg(rpcIdList);
			throw e;
		}
	}

	/**
	 * @Description: 构建订单对象
	 * @param reqDto 订单请求对象
	 * @return TradeOrder  交易订单对象
	 * @throws ServiceException 自定义服务异常  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private TradeOrder buildTradeOrder(TradeOrderReqDto reqDto) throws ServiceException {
		TradeOrder tradeOrder = new TradeOrder();

		TradeOrderReq req = reqDto.getData();

		tradeOrder.setId(UuidUtils.getUuid());
		tradeOrder.setUserId(req.getUserId());
		tradeOrder.setUserPhone(req.getUserPhone());
		tradeOrder.setStoreId(req.getStoreId());
		tradeOrder.setStoreName(reqDto.getContext().getStoreInfo().getStoreName());
		tradeOrder.setSellerId(req.getStoreId());
		tradeOrder.setRemark(req.getRemark());
		tradeOrder.setType(req.getType());
		tradeOrder.setPid("0");
		tradeOrder.setActivityType(req.getActivityType());
		tradeOrder.setActivityId(req.getActivityId());
		tradeOrder.setActivityItemId(req.getActivityItemId());
		tradeOrder.setOrderResource(req.getOrderResource());
		tradeOrder.setIsShow(OrderIsShowEnum.yes);
		tradeOrder.setPaymentStatus(PaymentStatusEnum.STAY_BACK);
		tradeOrder.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
		// tradeOrder.setPickUpCode(RandomStringUtil.getRandomInt(6));
		tradeOrder.setTradeNum(TradeNumUtil.getTradeNum());
		tradeOrder.setDisabled(Disabled.valid);
		tradeOrder.setCreateTime(new Date());
		tradeOrder.setUpdateTime(new Date());
		// 设置订单编号
		setOrderNo(tradeOrder);
		// 设置订单总金额
		tradeOrder.setTotalAmount(calculateAmount(req.getList()));
		// 解析支付方式
		parsePayType(tradeOrder, req.getPayType());
		// 解析优惠活动
		parseFavour(tradeOrder, req);
		// 解析提货类型
		parsePickType(tradeOrder, reqDto);
		// 设置发票
		setTradeOrderInvoice(tradeOrder, req);
		// tradeOrder.setPospay();
		// 根据请求构建订单项列表
		List<TradeOrderItem> orderItemList = buildOrderItemList(tradeOrder, reqDto);
		// 设置订单项
		tradeOrder.setTradeOrderItem(orderItemList);
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
		// String orderNo =
		// generateNumericalService.generateNumberAndSave("XS");
		// if (orderNo == null) {
		// throw new ServiceException("订单编号生成失败");
		// }
		// Begin 1.0.Z add by zengj
		// 查询店铺机构信息
		StoreBranches storeBranches = storeBranchesService.findBranches(tradeOrder.getStoreId());
		if (storeBranches == null || StringUtils.isEmpty(storeBranches.getBranchCode())) {
			throw new ServiceException(LogConstants.STORE_BRANCHE_NOT_EXISTS);
		}
		String orderNo = generateNumericalService.generateOrderNo(OrderNoUtils.PHYSICAL_ORDER_PREFIX,
				storeBranches.getBranchCode(), OrderNoUtils.ONLINE_POS_ID);
		logger.info("生成订单编号：{}", orderNo);
		tradeOrder.setOrderNo(orderNo);
		// End 1.0.Z add by zengj
	}

	/**
	 * @Description: 计算订单总金额
	 * @param goodsItemList 订单请求商品列表
	 * @return BigDecimal  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private BigDecimal calculateAmount(List<TradeOrderGoodsItem> goodsItemList) {
		BigDecimal totalAmount = new BigDecimal(0.00);
		for (TradeOrderGoodsItem goodsItem : goodsItemList) {
			totalAmount = totalAmount.add(goodsItem.getTotalAmount());
		}
		return totalAmount;
	}

	/**
	 * @Description: 解析支付方式
	 * @param tradeOrder 交易订单
	 * @param payType 支付方式：0：在线支付、1:货到付款、6：微信支付
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void parsePayType(TradeOrder tradeOrder, int payType) {
		// 支付方式：0：在线支付、1:货到付款、6：微信支付
		switch (payType) {
			case 0:
			case 6:
				tradeOrder.setStatus(OrderStatusEnum.UNPAID);
				tradeOrder.setPayWay(PayWayEnum.PAY_ONLINE);
				break;
			case 1:
				tradeOrder.setStatus(OrderStatusEnum.DROPSHIPPING);
				tradeOrder.setPayWay(PayWayEnum.CASH_DELIERY);
				break;
			default:
				break;
		}
	}

	/**
	 * @Description: 解析提货类型
	 * @param tradeOrder 交易订单
	 * @param reqDto 订单请求对象
	 * @return void  
	 * @throws ServiceException 异常  
	 * @author maojj
	 * @date 2016年7月19日
	 */
	private void parsePickType(TradeOrder tradeOrder, TradeOrderReqDto reqDto) throws ServiceException {
		PickUpTypeEnum pickType = reqDto.getData().getPickType();

		tradeOrder.setPickUpType(pickType);

		switch (pickType) {
			case DELIVERY_DOOR:
				processDelivery(tradeOrder, reqDto);
				break;
			case TO_STORE_PICKUP:
				processPickUp(tradeOrder, reqDto);
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
	private void processDelivery(TradeOrder tradeOrder, TradeOrderReqDto reqDto) {
		TradeOrderReq req = reqDto.getData();
		StoreInfoExt storeInfoExt = reqDto.getContext().getStoreInfo().getStoreInfoExt();

		// 店铺起送价格
		BigDecimal startPrice = storeInfoExt.getStartPrice();
		// 店铺运费
		BigDecimal freight = storeInfoExt.getFreight() == null ? new BigDecimal(0.0) : storeInfoExt.getFreight();

		if (startPrice != null) {
			// 判断商品总金额是否达到起送金额 后台判断
			// 如果商品总金额没有达到起送金额,则订单总金额=订单总金额+运费
			if (tradeOrder.getTotalAmount().compareTo(startPrice) == -1) {
				// 运费
				tradeOrder.setFare(freight);
				tradeOrder.setTotalAmount(tradeOrder.getTotalAmount().add(freight));
				tradeOrder.setActualAmount(tradeOrder.getActualAmount().add(freight));
				tradeOrder.setIncome(tradeOrder.getIncome().add(freight));
			} else {
				// 运费
				tradeOrder.setFare(new BigDecimal(0.0));
			}
		}
		// 设置TradeOrderLogistics
		setTradeOrderLogistics(tradeOrder, req.getAddressId());
		// 设置提货时间
		setPickUpTime(tradeOrder, req);
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

		// Begin modified by maojj 2016-08-15
		StringBuilder area = new StringBuilder();
		area.append(clean(address.getProvinceName())).append(clean(address.getCityName()))
				.append(clean(address.getAreaName())).append(clean(address.getAreaExt()));
		orderLogistics.setArea(area.toString());
		// End modified by maojj 2016-08-15

		orderLogistics.setOrderId(tradeOrder.getId());
		orderLogistics.setAreaId(address.getAreaId());
		orderLogistics.setProvinceId(address.getProvinceId());
		orderLogistics.setCityId(address.getCityId());
		orderLogistics.setZipCode(address.getZipCode());

		tradeOrder.setTradeOrderLogistics(orderLogistics);
	}

	// Begin added by maojj 2016-08-15
	/**
	 * @Description: 处理字符串
	 * @param str 源字符串对象
	 * @return String
	 * @author maojj
	 * @date 2016年8月15日
	 */
	private String clean(String str) {
		return str == null ? "" : str;
	}
	// Begin added by maojj 2016-08-15

	/**
	 * @Description: 设置提货时间
	 * @param tradeOrder 交易订单
	 * @param req 订单请求对象 
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setPickUpTime(TradeOrder tradeOrder, TradeOrderReq req) {
		String pickTime = req.getPickTime();
		// 支付方式：1:货到付款、0：在线支付
		int payType = req.getPayType();

		if (payType == 1 || payType == 0 || payType == 6) {
			if (!"".equals(pickTime)) {
				tradeOrder.setPickUpTime(pickTime);
			} else {
				tradeOrder.setPickUpTime("立即配送");
			}
		}
	}

	/**
	 * @Description: 处理到店自提
	 * @param tradeOrder 交易订单
	 * @param reqDto 订单请求对象
	 * @return void  
	 * @throws ServiceException 自定义异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void processPickUp(TradeOrder tradeOrder, TradeOrderReqDto reqDto) throws ServiceException {
		setPickUpIdAndTime(tradeOrder, reqDto);
		tradeOrder.setFare(new BigDecimal("0.00"));
	}

	/**
	 * @Description: 设置提货时间
	 * @param tradeOrder 交易订单
	 * @param reqDto 订单请求对象
	 * @return void  
	 * @throws ServiceException 自定义异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setPickUpIdAndTime(TradeOrder tradeOrder, TradeOrderReqDto reqDto) throws ServiceException {
		TradeOrderReq req = reqDto.getData();
		String receiveTime = req.getReceiveTime();

		StoreInfoExt storeInfoExt = reqDto.getContext().getStoreInfo().getStoreInfoExt();

		StoreInfo storeInfo = storeInfoServiceApi.selectDefaultAddressById(req.getStoreId());
		// 获取默认地址
		String defaultAddressId = storeInfo.getMemberConsignee().getId();
		tradeOrder.setPickUpId(defaultAddressId);
		if (!StringUtils.isEmpty(receiveTime)) {
			tradeOrder.setPickUpTime(receiveTime);
		} else {
			String attrTime = DateUtils.getDate();
			String pcTime = attrTime + " " + storeInfoExt.getServiceStartTime() + "-"
					+ storeInfoExt.getServiceEndTime();
			tradeOrder.setPickUpTime(pcTime);
		}
	}

	/**
	 * @Description: 设置发票
	 * @param tradeOrder 交易订单
	 * @param req 订单请求对象
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setTradeOrderInvoice(TradeOrder tradeOrder, TradeOrderReq req) {
		TradeOrderInvoice orderInvoice = new TradeOrderInvoice();
		// 是否有发票标识(0:无,1:有)
		WithInvoiceEnum invoice = req.getIsInvoice();
		tradeOrder.setInvoice(invoice);
		if (invoice == WithInvoiceEnum.HAS) {
			// 有发票
			orderInvoice.setId(UuidUtils.getUuid());
			orderInvoice.setOrderId(tradeOrder.getId());
			orderInvoice.setHead(req.getInvoiceHead());
			orderInvoice.setContext(req.getInvoiceContent());
			tradeOrder.setTradeOrderInvoice(orderInvoice);
		}
	}

	/**
	 * @Description: 解析优惠
	 * @param tradeOrder 交易订单
	 * @param req 订单请求对象
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void parseFavour(TradeOrder tradeOrder, TradeOrderReq req) throws ServiceException {
		// 优惠金额
		BigDecimal favourAmount = getFavourAmount(req, tradeOrder.getTotalAmount());
		// 设置订单优惠金额
		tradeOrder.setPreferentialPrice(favourAmount);
		// 设置订单实付金额
		setActualAmount(tradeOrder);
		// 设置店铺总收入
		setIncome(tradeOrder);
	}

	/**
	 * @Description: 获取订单的优惠金额
	 * @param req 订单请求对象
	 * @param totalAmount 订单总金额
	 * @return BigDecimal  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private BigDecimal getFavourAmount(TradeOrderReq req, BigDecimal totalAmount) throws ServiceException {
		BigDecimal favourAmount = new BigDecimal(0.0);
		// 活动类型(0:没参加活动,1:代金券,2:满减活动,3:满折活动,4:团购活动)
		ActivityTypeEnum activityType = req.getActivityType();
		switch (activityType) {
			case VONCHER:
				favourAmount = getCouponsFaceValue(req);
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
	 * @Description: 获取代金券面额
	 * @param req 订单请求对象
	 * @return BigDecimal  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private BigDecimal getCouponsFaceValue(TradeOrderReq req) {
		// 查询代金券
		ActivityCoupons activityCoupons = activityCouponsRecordMapper.selectCouponsItem(buildCouponsFindVo(req));
		return BigDecimal.valueOf(activityCoupons.getFaceValue());
	}

	/**
	 * @Description: 获取满减优惠金额
	 * @param req 订单请求对象
	 * @param totalAmount 订单总金额
	 * @return BigDecimal  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private BigDecimal getDiscountFavour(String activityItemId, BigDecimal totalAmount) {
		BigDecimal discountVal = getDiscountValue(activityItemId);
		BigDecimal actualAmount = totalAmount.multiply(discountVal).divide(BigDecimal.valueOf(10)).setScale(2,
				BigDecimal.ROUND_DOWN);
		return totalAmount.subtract(actualAmount);
	}

	/**
	 * @Description: 获取满减满折优惠金额
	 * @param req 订单请求对象
	 * @param type 折扣类型：满减或满折
	 * @return BigDecimal  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private BigDecimal getDiscountValue(String activityItemId) {
		return activityDiscountMapper.getDiscountValue(activityItemId);
	}

	/**
	 * @Description: 构建代金券查询条件
	 * @param req 订单请求对象
	 * @return CouponsFindVo  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private CouponsFindVo buildCouponsFindVo(TradeOrderReq req) {
		CouponsFindVo findCondition = new CouponsFindVo();
		findCondition.setActivityId(req.getActivityId());
		findCondition.setActivityItemId(req.getActivityItemId());
		findCondition.setConponsType(req.getCouponsType());
		return findCondition;
	}

	/**
	 * @Description: 计算订单实付金额
	 * @param tradeOrder 交易订单
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void setActualAmount(TradeOrder tradeOrder) {
		BigDecimal totalAmount = tradeOrder.getTotalAmount();
		BigDecimal favourAmount = tradeOrder.getPreferentialPrice();
		// 如果总金额<优惠金额，则实付为0，优惠为订单总金额，否则实付金额为总金额-优惠金额，优惠为优惠金额
		if (totalAmount.compareTo(favourAmount) == -1) {
			tradeOrder.setActualAmount(BigDecimal.valueOf(0.0));
			tradeOrder.setPreferentialPrice(totalAmount);
		} else {
			tradeOrder.setActualAmount(totalAmount.subtract(favourAmount));
		}
	}

	/**
	 * @Description:	设置订单总收入
	 * 	订单未参与活动、使用代金券，店铺总收入为：订单总金额
	 * 	订单参与运营商发起的满减活动，店铺总收入为：订单总金额
	 * 	订单参与店铺发起的满减满折活动，店铺总收入为：订单总金额-优惠金额（即订单实付金额）
	 * @param tradeOrder 交易订单  
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
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
	 * @author maojj
	 * @date 2016年7月14日
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
	 * @Description: 构建订单项列表
	 * @param tradeOrder 交易订单
	 * @param reqDto 请求对象
	 * @throws ServiceException 自定义异常  
	 * @return List
	 * @author maojj
	 * @date 2016年7月14日
	 */
	@SuppressWarnings("unchecked")
	private List<TradeOrderItem> buildOrderItemList(TradeOrder tradeOrder, TradeOrderReqDto reqDto)
			throws ServiceException {
		List<TradeOrderItem> orderItemList = new ArrayList<TradeOrderItem>();

		TradeOrderReq req = reqDto.getData();
		List<GoodsStoreSku> currentStoreSkuList = reqDto.getContext().getCurrentStoreSkuList();

		List<String> mainPicList = new ArrayList<String>();

		String orderId = tradeOrder.getId();
		// 订单项总金额
		BigDecimal totalAmount = calculateAmount(req.getList());
		BigDecimal totalFavour = tradeOrder.getPreferentialPrice();
		BigDecimal favourSum = new BigDecimal("0.00");
		int index = 0;
		int itemSize = req.getList().size();
		TradeOrderItem tradeOrderItem = null;
		GoodsStoreSku storeSku = null;

		List<TradeOrderGoodsItem> goodsItemList = req.getList();
		ComparatorChain chain = new ComparatorChain();
		chain.addComparator(new BeanComparator("skuPrice"), false);
		Collections.sort(goodsItemList, chain);

		for (TradeOrderGoodsItem goodsItem : goodsItemList) {
			tradeOrderItem = new TradeOrderItem();
			storeSku = findFromStoreSkuList(currentStoreSkuList, goodsItem.getSkuId());

			tradeOrderItem.setId(UuidUtils.getUuid());
			tradeOrderItem.setOrderId(orderId);
			tradeOrderItem.setStoreSpuId(storeSku.getStoreSpuId());
			tradeOrderItem.setStoreSkuId(storeSku.getId());
			tradeOrderItem.setSkuName(storeSku.getName());
			setPropertiesIndb(tradeOrderItem, storeSku.getPropertiesIndb());
			tradeOrderItem.setMainPicPrl(storeSku.getGoodsStoreSkuPicture().getUrl());
			tradeOrderItem.setSpuType(GoodsTypeEnum.SINGLE_GOODS);
			tradeOrderItem.setUnitPrice(goodsItem.getSkuPrice());
			tradeOrderItem.setQuantity(goodsItem.getSkuNum());
			tradeOrderItem.setStatus(OrderItemStatusEnum.NO_REFUND);
			tradeOrderItem.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
			tradeOrderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);
			tradeOrderItem.setCreateTime(new Date());
			tradeOrderItem.setServiceAssurance(
					StringUtils.isEmpty(storeSku.getGuaranteed()) ? 0 : Integer.valueOf(storeSku.getGuaranteed()));
			tradeOrderItem.setActivityType(String.valueOf(req.getActivityType().ordinal()));
			tradeOrderItem.setActivityId(req.getActivityId());
			tradeOrderItem.setBarCode(storeSku.getBarCode());
			tradeOrderItem.setStyleCode(storeSku.getStyleCode());

			// 订单项总金额
			BigDecimal totalAmountOfItem = goodsItem.getTotalAmount();
			tradeOrderItem.setTotalAmount(totalAmountOfItem);
			// 计算订单项优惠金额
			BigDecimal favourItem = new BigDecimal(0.0);
			if (req.getActivityType() != ActivityTypeEnum.NO_ACTIVITY) {
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
			// 设置优惠金额
			tradeOrderItem.setPreferentialPrice(favourItem);
			// 设置实付金额
			tradeOrderItem.setActualAmount(totalAmountOfItem.subtract(favourItem));
			// 设置订单项收入
			setOrderItemIncome(tradeOrderItem, tradeOrder);
			// weight未设置 tradeOrderItem.setWeight(weight);
			orderItemList.add(tradeOrderItem);

			mainPicList.add(storeSku.getGoodsStoreSkuPicture().getUrl());
		}

		// reqDto.getContext().setPicUrlList(mainPicList);
		return orderItemList;
	}

	/**
	 * @Description: 根据店铺商品ID查询店铺商品信息
	 * @param storeSkuList 店铺商品列表
	 * @param skuId 店铺商品ID
	 * @return GoodsStoreSku 店铺商品信息
	 * @throws ServiceException 自定义异常
	 * @author maojj
	 * @date 2016年7月19日
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
			logger.error("根据ID查询店铺商品详细信息为空-------->{}", skuId, CodeStatistical.getLineInfo());
			throw new ServiceException("查询店铺商品详细信息异常：storeSku为空-------->" + CodeStatistical.getLineInfo());
		}
		return findResult;
	}

	/**
	 * @Description: 设置商品属性
	 * @param tradeOrderItem 交易订单项
	 * @param propertiesIndb 商品属性
	 * @return void  
	 * @author maojj
	 * @date 2016年7月19日
	 */
	private void setPropertiesIndb(TradeOrderItem tradeOrderItem, String propertiesIndb) {
		if (!StringUtils.isEmpty(propertiesIndb)) {
			JSONObject propertiesJson = JSONObject.fromObject(propertiesIndb);
			String skuProperties = propertiesJson.get("skuName").toString();
			tradeOrderItem.setPropertiesIndb(skuProperties);
		} else {
			tradeOrderItem.setPropertiesIndb("");
		}
	}

	/**
	 * @Description: 设置订单项收入
	 * @param tradeOrderItem 交易订单项
	 * @param tradeOrder 交易订单
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
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
	 * @Description: 更新库存
	 * @param order 订单对象
	 * @param reqDto 请求对象
	 * @return void  
	 * @throws Exception 异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void toUpdateStock(TradeOrder order, TradeOrderReqDto reqDto, List<String> rpcIdList) throws Exception {
		StockAdjustVo stockAdjustVo = null;
		if (!CollectionUtils.isEmpty(reqDto.getContext().getNomalSkuList())) {
			stockAdjustVo = buildStockAdjustVo(order, reqDto, false);
			rpcIdList.add(stockAdjustVo.getRpcId());
			// 正常商品下单，更新库存
			stockManagerServiceApi.updateStock(stockAdjustVo);
		}

		if (!CollectionUtils.isEmpty(reqDto.getContext().getActivitySkuList())) {
			stockAdjustVo = buildStockAdjustVo(order, reqDto, true);
			rpcIdList.add(stockAdjustVo.getRpcId());
			// 特惠商品下单，更新库存
			stockManagerServiceApi.updateStock(stockAdjustVo);
		}
	}

	/**
	 * @Description: 构建库存更新对象
	 * @param order 订单对象
	 * @param reqDto 请求对象
	 * @param isPrivilege 是否为特惠商品
	 * @return StockAdjustVo  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private StockAdjustVo buildStockAdjustVo(TradeOrder order, TradeOrderReqDto reqDto, boolean isPrivilege) {
		TradeOrderReq req = reqDto.getData();
		TradeOrderContext context = reqDto.getContext();

		List<GoodsStoreSku> storeSkuList = null;
		if (isPrivilege) {
			storeSkuList = context.getActivitySkuList();
		} else {
			storeSkuList = context.getNomalSkuList();
		}

		StockAdjustVo stockAjustVo = new StockAdjustVo();

		stockAjustVo.setRpcId(UuidUtils.getUuid());
		stockAjustVo.setOrderId(order.getId());
		stockAjustVo.setOrderNo(order.getOrderNo());
		stockAjustVo.setOrderResource(order.getOrderResource());
		stockAjustVo.setOrderType(order.getType());

		stockAjustVo.setStoreId(req.getStoreId());
		stockAjustVo.setUserId(req.getUserId());
		stockAjustVo.setMethodName(this.getClass().getName() + ".process");

		AdjustDetailVo adjustDetailVo = null;
		TradeOrderGoodsItem orderItem = null;
		List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();

		for (GoodsStoreSku storeSku : storeSkuList) {
			adjustDetailVo = new AdjustDetailVo();
			orderItem = req.findOrderItem(storeSku.getId());

			adjustDetailVo.setBarCode(storeSku.getBarCode());
			adjustDetailVo.setGoodsName(storeSku.getName());
			adjustDetailVo.setGoodsSkuId(storeSku.getSkuId());
			adjustDetailVo.setMultipleSkuId(storeSku.getMultipleSkuId());
			adjustDetailVo.setNum(orderItem.getSkuNum());
			adjustDetailVo.setPrice(orderItem.getSkuPrice());
			adjustDetailVo.setPropertiesIndb(storeSku.getPropertiesIndb());
			adjustDetailVo.setStoreSkuId(storeSku.getId());
			adjustDetailVo.setGoodsSkuId(storeSku.getSkuId());
			adjustDetailList.add(adjustDetailVo);
		}

		stockAjustVo.setAdjustDetailList(adjustDetailList);

		if (isPrivilege) {
			stockAjustVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_PLACE_ORDER);
		} else {
			stockAjustVo.setStockOperateEnum(StockOperateEnum.PLACE_ORDER);
		}
		return stockAjustVo;
	}

	/**
	 * @Description: 发送消息
	 * @param orderId 订单id
	 * @param payType 支付类型
	 * @return void  
	 * @throws Exception 异常
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void sendTimerMessage(String orderId, int payType) throws Exception {
		// 1:货到付款、0：在线支付
		if (payType == 1) {
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_timeout, orderId);
		} else {
			// 发送消息
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_pay_timeout, orderId);
		}
	}

	/**
	 * @Description: 更新代金券
	 * @param tradeOrder 交易订单
	 * @param req 请求对象
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void updateActivityCoupons(TradeOrder tradeOrder, TradeOrderReq req) throws Exception{
		ActivityTypeEnum activityType = req.getActivityType();
		int couponsType = req.getCouponsType();

		if (activityType == ActivityTypeEnum.VONCHER ) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderId", tradeOrder.getId());
			params.put("id", req.getRecordId());
			params.put("collectUserId", tradeOrder.getUserPhone());
			params.put("couponsId", req.getActivityItemId());
			params.put("collectType", couponsType);
			// 更新代金券状态
			int updateResult = activityCouponsRecordMapper.updateActivityCouponsStatus(params);
			if(updateResult == 0){
				throw new Exception("代金券已使用或者已过期");
			}
			// 修改代金券使用数量
			activityCouponsMapper.updateActivityCouponsUsedNum(req.getActivityItemId());
		}
	}

	/**
	 * @Description: 保存特惠商品记录
	 * @param orderId 订单id
	 * @param reqDto 请求对象
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void saveActivitySaleRecord(String orderId, TradeOrderReqDto reqDto) {
		TradeOrderReq req = reqDto.getData();
		String activityId = reqDto.getContext().getActivityId();
		if (activityId == null || "0".equals(activityId)) {
			return;
		}

		List<ActivitySaleRecord> recordList = new ArrayList<ActivitySaleRecord>();
		ActivitySaleRecord record = null;
		for (TradeOrderGoodsItem goodsItem : req.getList()) {
			if (goodsItem.isPrivilege()) {
				record = new ActivitySaleRecord();

				record.setId(UuidUtils.getUuid());
				record.setStroeId(req.getStoreId());
				record.setSaleGoodsId(goodsItem.getSkuId());
				record.setSaleGoodsNum(goodsItem.getSkuNum());
				record.setUserId(req.getUserId());
				record.setSaleId(activityId);
				record.setOrderId(orderId);
				record.setOrderDisabled(Disabled.valid);

				recordList.add(record);
			}
		}

		if (!CollectionUtils.isEmpty(recordList)) {
			activitySaleRecordMapper.batchInsert(recordList);
		}
	}

	/**
	 * @Description: 保存满减、满折记录
	 * @param orderId 订单id
	 * @param req 请求对象
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void saveActivityDiscountRecord(String orderId, TradeOrderReq req) {
		ActivityTypeEnum activityType = req.getActivityType();
		if (activityType != ActivityTypeEnum.FULL_DISCOUNT_ACTIVITIES
				&& activityType != ActivityTypeEnum.FULL_REDUCTION_ACTIVITIES) {
			return;
		}

		ActivityDiscountRecord discountRecord = new ActivityDiscountRecord();

		discountRecord.setId(UuidUtils.getUuid());
		discountRecord.setDiscountId(req.getActivityId());
		discountRecord.setDiscountConditionsId(req.getActivityItemId());
		discountRecord.setUserId(req.getUserId());
		discountRecord.setStoreId(req.getStoreId());
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
