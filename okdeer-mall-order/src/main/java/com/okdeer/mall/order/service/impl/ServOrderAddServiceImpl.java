package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.base.enums.GoodsTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.common.consts.RedisKeyConstants;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.common.utils.TradeNumUtil;
import com.okdeer.mall.common.vo.Request;
import com.okdeer.mall.common.vo.Response;
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderInvoice;
import com.okdeer.mall.order.entity.TradeOrderItem;
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
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.order.utils.OrderNoUtils;
import com.okdeer.mall.order.vo.ServiceOrderReq;
import com.okdeer.mall.order.vo.ServiceOrderResp;
import com.okdeer.mall.system.mq.RollbackMQProducer;
import com.okdeer.mall.system.utils.ConvertUtil;

import net.sf.json.JSONObject;

/**
 * ClassName: ServOrderAddServiceImpl 
 * @Description: 服务订单下单
 * @author maojj
 * @date 2016年9月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		重构1.1			2016年9月23日				maojj		服务订单下单
 */
@Service("servOrderAddService")
public class ServOrderAddServiceImpl implements RequestHandler<ServiceOrderReq, ServiceOrderResp> {

	/**
	 * 生成编号的service
	 */
	@Resource
	private GenerateNumericalService generateNumericalService;

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
	 * redis缓存
	 */
	@Autowired
	private RedisTemplate<String, Integer> redisTemplate;
	
	/**
	 * 消息生产者
	 */
	@Resource
	private RollbackMQProducer rollbackMQProducer;

	@Transactional(rollbackFor=Exception.class)
	@Override
	public void process(Request<ServiceOrderReq> req, Response<ServiceOrderResp> resp) throws Exception {
		String rpcId = null;
		// 秒杀商品库存缓存键值
		String seckillStockKey = RedisKeyConstants.SECKILL_STOCK + req.getData().getSkuId();
		try {
			ServiceOrderResp respData = resp.getData();
			// 判断服务地址是否存在
			MemberConsigneeAddress address = memberConsigneeAddressMapper
					.selectByPrimaryKey(req.getData().getAddressId());
			// 服务地址不存在
			if (address == null) {
				resp.setResult(ResultCodeEnum.ADDRESS_NOT_EXSITS);
				req.setComplete(true);
				return;
			}
			// 根据请求构建订单
			TradeOrder tradeOrder = buildTradeOrder(req, address);
			// 保存订单和订单项信息，并发送消息
			tradeOrderService.insertTradeOrder(tradeOrder);
			// 更新库存
			rpcId = UuidUtils.getUuid();
			toUpdateStock(tradeOrder, req, rpcId);
			// 更新地址信息,更新用户使用时间
			address.setUseTime(DateUtils.getSysDate());
			memberConsigneeAddressMapper.updateByPrimaryKeySelective(address);
			// 超时未支付的，取消订单
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_pay_timeout, tradeOrder.getId());

			//resp.setCode(PublicResultCodeEnum.SUCCESS);
			respData.setOrderId(tradeOrder.getId());
			respData.setOrderNo(tradeOrder.getOrderNo());
			respData.setOrderPrice(ConvertUtil.format(tradeOrder.getActualAmount()));
			respData.setTradeNum(tradeOrder.getTradeNum());
			respData.setLimitTime(1800);
			
			//成功则减去缓存中的数据
			redisTemplate.boundValueOps(seckillStockKey).increment(-1);
		} catch (Exception e) {
			//TODO
			if(rpcId != null){
				rollbackMQProducer.sendStockRollbackMsg(rpcId);
				redisTemplate.boundValueOps(seckillStockKey).increment(1);
			}
			throw e;
		}finally{
			req.setComplete(true);
		}
	}

	/**
	 * @Description: 构建订单
	 * @return   
	 * @author maojj
	 * @date 2016年9月23日
	 */
	public TradeOrder buildTradeOrder(Request<ServiceOrderReq> req, MemberConsigneeAddress address) throws Exception {
		TradeOrder tradeOrder = new TradeOrder();

		ServiceOrderReq reqData = req.getData();

		tradeOrder.setId(UuidUtils.getUuid());
		tradeOrder.setUserId(reqData.getUserId());
		tradeOrder.setUserPhone(reqData.getUserPhone());
		tradeOrder.setStoreId(reqData.getStoreId());
		tradeOrder.setStoreName((String) req.getContext().get("storeName"));
		tradeOrder.setSellerId(reqData.getStoreId());
		tradeOrder.setRemark(reqData.getRemark());
		tradeOrder.setType(OrderTypeEnum.SERVICE_STORE_ORDER);
		tradeOrder.setPid("0");
		tradeOrder.setActivityType(ActivityTypeEnum.SECKILL_ACTIVITY);
		tradeOrder.setActivityId(reqData.getSeckillId());
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
		// 设置订单总金额 TODO
		BigDecimal skuPrice = (BigDecimal) req.getContext().get("skuPrice");
		BigDecimal seckillPrice = (BigDecimal) req.getContext().get("seckillPrice");
		BigDecimal favourPrice = skuPrice.subtract(seckillPrice);
		tradeOrder.setTotalAmount(skuPrice);
		// 设置订单优惠金额
		tradeOrder.setPreferentialPrice(favourPrice);
		// 设置订单实付金额
		tradeOrder.setActualAmount(seckillPrice);
		// 设置店铺总收入
		tradeOrder.setIncome(skuPrice);
		// 解析支付方式
		tradeOrder.setStatus(OrderStatusEnum.UNPAID);
		tradeOrder.setPayWay(PayWayEnum.PAY_ONLINE);
		// 解析提货类型
		tradeOrder.setPickUpType(PickUpTypeEnum.DELIVERY_DOOR);
		tradeOrder.setPickUpTime(reqData.getServiceTime());
		// 设置运费
		tradeOrder.setFare(BigDecimal.ZERO);
		// 设置发票
		setTradeOrderInvoice(tradeOrder, reqData);
		// tradeOrder.setPospay();
		// 根据请求构建订单项列表
		List<TradeOrderItem> orderItemList = buildOrderItemList(tradeOrder, req);
		// 设置订单项
		tradeOrder.setTradeOrderItem(orderItemList);
		tradeOrder.setTradeOrderLogistics(buildTradeOrderLogistics(tradeOrder.getId(), address));
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
		String orderNo = generateNumericalService.generateOrderNo(OrderNoUtils.SERV_ORDER_PREFIXE,
				"", OrderNoUtils.ONLINE_POS_ID);
		tradeOrder.setOrderNo(orderNo);
	}

	/**
	 * @Description: 设置发票
	 * @param tradeOrder 交易订单
	 * @param req 订单请求对象
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
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
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private List<TradeOrderItem> buildOrderItemList(TradeOrder tradeOrder, Request<ServiceOrderReq> req)
			throws ServiceException {
		ServiceOrderReq reqData = req.getData();
		GoodsStoreSku storeSku = (GoodsStoreSku) req.getContext().get("storeSku");
		BigDecimal skuPrice = (BigDecimal) req.getContext().get("skuPrice");
		BigDecimal seckillPrice = (BigDecimal) req.getContext().get("seckillPrice");
		BigDecimal favourPrice = skuPrice.subtract(seckillPrice);

		List<TradeOrderItem> orderItemList = new ArrayList<TradeOrderItem>();
		TradeOrderItem tradeOrderItem = new TradeOrderItem();
		tradeOrderItem.setId(UuidUtils.getUuid());
		tradeOrderItem.setOrderId(tradeOrder.getId());
		tradeOrderItem.setStoreSpuId(storeSku.getStoreSpuId());
		tradeOrderItem.setStoreSkuId(storeSku.getId());
		tradeOrderItem.setSkuName(storeSku.getName());
		tradeOrderItem.setPropertiesIndb(parseProperties(storeSku.getPropertiesIndb()));
		tradeOrderItem.setMainPicPrl((String)req.getContext().get("mainPic"));
		tradeOrderItem.setSpuType(GoodsTypeEnum.SERVICE_GOODS);
		tradeOrderItem.setUnitPrice(skuPrice);
		tradeOrderItem.setQuantity(reqData.getSkuNum());
		tradeOrderItem.setStatus(OrderItemStatusEnum.NO_REFUND);
		tradeOrderItem.setCompainStatus(CompainStatusEnum.NOT_COMPAIN);
		tradeOrderItem.setAppraise(AppraiseEnum.NOT_APPROPRIATE);
		tradeOrderItem.setCreateTime(new Date());
		tradeOrderItem.setServiceAssurance(ConvertUtil.parseInt(storeSku.getGuaranteed()));
		tradeOrderItem.setBarCode(storeSku.getBarCode());
		tradeOrderItem.setStyleCode(storeSku.getStyleCode());

		// 订单项总金额
		tradeOrderItem.setTotalAmount(skuPrice);
		// 设置优惠金额
		tradeOrderItem.setPreferentialPrice(favourPrice);
		// 设置实付金额
		tradeOrderItem.setActualAmount(seckillPrice);
		// 设置订单项收入
		tradeOrderItem.setIncome(skuPrice);
		orderItemList.add(tradeOrderItem);
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
	private void toUpdateStock(TradeOrder order, Request<ServiceOrderReq> req, String rpcId) throws Exception {

		GoodsStoreSku storeSku = (GoodsStoreSku) req.getContext().get("storeSku");
		ServiceOrderReq reqData = req.getData();

		StockAdjustVo stockAjustVo = new StockAdjustVo();
		stockAjustVo.setRpcId(rpcId);
		stockAjustVo.setOrderId(order.getId());
		stockAjustVo.setOrderNo(order.getOrderNo());
		stockAjustVo.setOrderResource(order.getOrderResource());
		stockAjustVo.setOrderType(order.getType());

		stockAjustVo.setStoreId(reqData.getStoreId());
		stockAjustVo.setUserId(reqData.getUserId());
		stockAjustVo.setMethodName(this.getClass().getName() + ".process");
		stockAjustVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_PLACE_ORDER);

		List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();
		AdjustDetailVo adjustDetailVo = new AdjustDetailVo();
		adjustDetailVo.setBarCode(storeSku.getBarCode());
		adjustDetailVo.setGoodsName(storeSku.getName());
		adjustDetailVo.setGoodsSkuId(storeSku.getSkuId());
		adjustDetailVo.setMultipleSkuId(storeSku.getMultipleSkuId());
		adjustDetailVo.setNum(reqData.getSkuNum());
		adjustDetailVo.setPrice(storeSku.getOnlinePrice());
		adjustDetailVo.setPropertiesIndb(storeSku.getPropertiesIndb());
		adjustDetailVo.setStoreSkuId(storeSku.getId());
		adjustDetailVo.setGoodsSkuId(storeSku.getSkuId());
		adjustDetailList.add(adjustDetailVo);

		stockAjustVo.setAdjustDetailList(adjustDetailList);
		stockManagerServiceApi.updateStock(stockAjustVo);
	}
}
