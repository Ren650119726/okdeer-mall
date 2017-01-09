package com.okdeer.mall.order.handler.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.assemble.GoodsStoreSkuAssembleApi;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreAssembleDto;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreSkuAssembleDto;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSku;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuServiceApi;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerJxcServiceApi;
import com.okdeer.archive.stock.service.StockManagerServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivityCouponsRecordMapper;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleRecordMapper;
import com.okdeer.mall.activity.discount.entity.ActivityDiscountRecord;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountType;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountMapper;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountRecordMapper;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.member.mapper.MemberConsigneeAddressMapper;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.builder.TradeOrderBuilder;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderLog;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.enums.PlaceOrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;
import com.okdeer.mall.order.service.OrderReturnCouponsService;
import com.okdeer.mall.order.service.TradeOrderLogService;
import com.okdeer.mall.order.service.TradeOrderPayServiceApi;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.timer.TradeOrderTimer;

import net.sf.json.JSONObject;

/**
 * ClassName: PlaceOrderServiceImpl 
 * @Description: TODO
 * @author maojj
 * @date 2017年1月5日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.0 			2017年1月5日				maojj
 */
@Service("placeOrderService")
public class PlaceOrderServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {

	@Autowired
	private TradeOrderBuilder tradeOrderBoBuilder;

	/**
	 * 地址mapper
	 */
	@Resource
	private MemberConsigneeAddressMapper memberConsigneeAddressMapper;

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
	 * 折扣活动记录Mapper
	 */
	@Resource
	private ActivityDiscountRecordMapper activityDiscountRecordMapper;

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

	/**
	 * 订单操作记录Service
	 */
	@Resource
	private TradeOrderLogService tradeOrderLogService;

	@Reference(version = "1.0.0", check = false)
	private TradeOrderPayServiceApi tradeOrderPayService;

	@Resource
	private OrderReturnCouponsService orderReturnCouponsService;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuAssembleApi goodsStoreSkuAssembleApi;

	/**
	 * 商业系统库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerJxcServiceApi stockManagerJxcServiceApi;

	/**
	 * 商城库存管理Dubbo接口
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerServiceApi stockManagerServiceApi;
	
	/**
	 * 商品信息Service
	 */
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuServiceApi goodsStoreSkuServiceApi;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		// 根据请求构建订单
		TradeOrder tradeOrder = tradeOrderBoBuilder.build(paramDto);
		MemberConsigneeAddress userUseAddr = (MemberConsigneeAddress) paramDto.get("userUseAddr");
		if (paramDto.getPickType() == PickUpTypeEnum.DELIVERY_DOOR && userUseAddr == null) {
			resp.setResult(ResultCodeEnum.ADDRESS_NOT_EXSITS);
			return;
		}
		// 更新代金券
		updateActivityCoupons(tradeOrder, paramDto);
		// 保存特惠商品记录
		saveActivitySaleRecord(tradeOrder.getId(), paramDto);
		// 保存满减、满折记录
		saveActivityDiscountRecord(tradeOrder.getId(), paramDto);
		// 更新用户最后使用的地址
		updateLastUseAddr(userUseAddr);
		// 插入订单
		tradeOrderSerive.insertTradeOrder(tradeOrder);
		// 发送消息
		sendTimerMessage(tradeOrder, paramDto);

		tradeOrderLogService.insertSelective(new TradeOrderLog(tradeOrder.getId(), tradeOrder.getUserId(),
				tradeOrder.getStatus().getName(), tradeOrder.getStatus().getValue()));
		// TODO 更新库存TODO
		updateStock(tradeOrder, paramDto);
		// 如果订单实付金额为0，调用余额支付进行支付。
		if (tradeOrder.getActualAmount().compareTo(BigDecimal.valueOf(0.0)) == 0
				&& (paramDto.getPayType() == 0 || paramDto.getPayType() == 6)) {
			tradeOrderPayService.wlletPay(String.valueOf(tradeOrder.getActualAmount()), tradeOrder);
		}
		// 到店消费订单增加商品销量
		addSkuSaleNum(paramDto);
		// 支付方式：0：在线支付、1:货到付款、6：微信支付 4 线下确认并当面支付
		if (paramDto.getPayType() == 1 || paramDto.getPayType() == 4) {
			// 如果支付方式为货到付款，则下单时，给邀请人返回邀请注册活动代金券。
			orderReturnCouponsService.firstOrderReturnCoupons(tradeOrder);
		}
		resp.getData().setOrderId(tradeOrder.getId());
		resp.getData().setOrderNo(tradeOrder.getOrderNo());
		resp.getData().setOrderPrice(tradeOrder.getActualAmount());
		resp.getData().setTradeNum(tradeOrder.getTradeNum());
		// 订单倒计时
		resp.getData().setLimitTime(60 * 30);
	}

	/**
	 * @Description: 更新代金券
	 * @param tradeOrder 交易订单
	 * @param req 请求对象
	 * @return void  
	 * @author maojj
	 * @date 2016年7月14日
	 */
	private void updateActivityCoupons(TradeOrder tradeOrder, PlaceOrderParamDto req) throws Exception {
		ActivityTypeEnum activityType = req.getActivityType();

		if (activityType == ActivityTypeEnum.VONCHER) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orderId", tradeOrder.getId());
			params.put("id", req.getActivityId());
			// 更新代金券状态
			int updateResult = activityCouponsRecordMapper.updateActivityCouponsStatus(params);
			if (updateResult == 0) {
				throw new Exception("代金券已使用或者已过期");
			}
			// 修改代金券使用数量
			activityCouponsMapper.updateActivityCouponsUsedNum(req.getActivityItemId());
		}
	}

	/**
	 * @Description: 保存活动商品购买记录
	 * @param orderId
	 * @param req   
	 * @author maojj
	 * @date 2017年1月4日
	 */
	private void saveActivitySaleRecord(String orderId, PlaceOrderParamDto req) {
		StoreSkuParserBo parserBo = (StoreSkuParserBo) req.get("parserBo");
		Map<String, List<String>> activitySkuMap = parserBo.getActivitySkuMap();
		if (activitySkuMap == null || CollectionUtils.isEmpty(activitySkuMap.keySet())) {
			return;
		}
		List<ActivitySaleRecord> recordList = new ArrayList<ActivitySaleRecord>();
		ActivitySaleRecord record = null;
		CurrentStoreSkuBo storeSkuBo = null;
		for (Map.Entry<String, List<String>> entry : activitySkuMap.entrySet()) {
			for (String storeSkuId : entry.getValue()) {
				storeSkuBo = parserBo.getCurrentStoreSkuBo(storeSkuId);

				record = new ActivitySaleRecord();
				record.setId(UuidUtils.getUuid());
				record.setStroeId(req.getStoreId());
				record.setSaleGoodsId(storeSkuId);
				record.setSaleGoodsNum(storeSkuBo.getQuantity());
				record.setUserId(req.getUserId());
				record.setSaleId(entry.getKey());
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
	 * @Description: 保存折扣购买记录
	 * @param orderId
	 * @param req   
	 * @author maojj
	 * @date 2017年1月4日
	 */
	private void saveActivityDiscountRecord(String orderId, PlaceOrderParamDto req) {
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

	public void updateLastUseAddr(MemberConsigneeAddress userUseAddr) {
		if(userUseAddr == null){
			return;
		}
		userUseAddr.setUseTime(DateUtils.getSysDate());
		memberConsigneeAddressMapper.updateByPrimaryKeySelective(userUseAddr);
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
	private void sendTimerMessage(TradeOrder tradeOrder, PlaceOrderParamDto paramDto) throws Exception {
		// 1:货到付款、0：在线支付 4 :线下确认价格并当面支付
		int payType = paramDto.getPayType();
		if (payType == 1) {
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_timeout, tradeOrder.getId());
		} else if (payType == 4) {
			Date serviceTime = DateUtils.parseDate(tradeOrder.getPickUpTime().substring(0, 16), "yyyy-MM-dd HH:mm");
			// 预约服务时间过后2小时未派单的自动取消订单
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_delivery_server_timeout, tradeOrder.getId(),
					(DateUtils.addHours(serviceTime, 2).getTime() - DateUtils.getSysDate().getTime()) / 1000);
		} else {
			// 超时未支付的，取消订单
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_pay_timeout, tradeOrder.getId());
		}
	}

	private void updateStock(TradeOrder tradeOrder, PlaceOrderParamDto paramDto) throws Exception {
		StoreSkuParserBo parserBo = (StoreSkuParserBo) paramDto.get("parserBo");
		if (paramDto.getOrderType() == PlaceOrderTypeEnum.CVS_ORDER) {
			StockAdjustVo erpAdjustVo = buildErpStockAjustVo(tradeOrder, parserBo);
			stockManagerJxcServiceApi.updateStock(erpAdjustVo);
			if (CollectionUtils.isNotEmpty(parserBo.getComboSkuIdList())) {
				StockAdjustVo mallAdjustVo = buildMallStockAdjustVo(tradeOrder, parserBo);
				stockManagerServiceApi.updateStock(mallAdjustVo);
			}
		} else {
			// 服务订单
			StockAdjustVo servAdjustVo = buildMallServStockAdjustVo(tradeOrder, parserBo);
			stockManagerServiceApi.updateStock(servAdjustVo);
		}

	}

	/**
	 * @Description: 构建调整单对象
	 * @param order
	 * @param paramDto
	 * @return
	 * @throws Exception   
	 * @author maojj
	 * @date 2017年1月5日
	 */
	private StockAdjustVo buildErpStockAjustVo(TradeOrder order, StoreSkuParserBo parserBo) throws Exception {
		if (CollectionUtils.isNotEmpty(parserBo.getComboSkuIdList())) {
			List<GoodsStoreAssembleDto> comboDtoList = goodsStoreSkuAssembleApi
					.findByAssembleSkuIds(parserBo.getComboSkuIdList());
			parserBo.loadComboSkuList(comboDtoList);
		}

		StockAdjustVo stockAjustVo = new StockAdjustVo();

		stockAjustVo.setRpcId(UuidUtils.getUuid());
		stockAjustVo.setOrderId(order.getId());
		stockAjustVo.setOrderNo(order.getOrderNo());
		stockAjustVo.setOrderResource(order.getOrderResource());
		stockAjustVo.setOrderType(order.getType());
		stockAjustVo.setStoreId(order.getStoreId());
		stockAjustVo.setUserId(order.getUserId());
		stockAjustVo.setMethodName(this.getClass().getName() + ".process");

		AdjustDetailVo adjustDetailVo = null;
		List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();

		for (CurrentStoreSkuBo storeSku : parserBo.getCurrentSkuMap().values()) {
			if(storeSku.getSpuType() == SpuTypeEnum.assembleSpu){
				// 如果是组合商品，对商品进行拆分.组合商品只能加入活动才能售卖
				List<GoodsStoreSkuAssembleDto> comboDetailList = parserBo.getComboSkuMap().get(storeSku.getId());
				for (GoodsStoreSkuAssembleDto comboDetail : comboDetailList) {
					int buyNum = comboDetail.getQuantity() * (storeSku.getQuantity() + storeSku.getSkuActQuantity());
					adjustDetailVo = buildDetailVo(comboDetail, true, buyNum);
					adjustDetailList.add(adjustDetailVo);
				}
			}else if(storeSku.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
				// 如果是低价，需要将订单商品拆分为两条记录去发起库存变更请求
				if (storeSku.getSkuActQuantity() > 0) {
					adjustDetailVo = buildDetailVo(storeSku, true, true);
					adjustDetailList.add(adjustDetailVo);
				}
				if (storeSku.getQuantity() > 0) {
					adjustDetailVo = buildDetailVo(storeSku, false, false);
					adjustDetailList.add(adjustDetailVo);
				}
			}else if (storeSku.getActivityType() == ActivityTypeEnum.SALE_ACTIVITIES.ordinal()) {
				adjustDetailVo = buildDetailVo(storeSku, false, true);
				adjustDetailList.add(adjustDetailVo);
			}else {
				adjustDetailVo = buildDetailVo(storeSku, false, false);
				adjustDetailList.add(adjustDetailVo);
			}
		}

		stockAjustVo.setAdjustDetailList(adjustDetailList);
		stockAjustVo.setStockOperateEnum(StockOperateEnum.PLACE_ORDER);
		return stockAjustVo;
	}

	/**
	 * @Description: 构建调整单明细
	 * @param storeSku
	 * @param isLow
	 * @return   
	 * @author maojj
	 * @date 2017年1月5日
	 */
	private AdjustDetailVo buildDetailVo(CurrentStoreSkuBo storeSku, boolean isLow, boolean isActivity) {
		AdjustDetailVo adjustDetailVo = new AdjustDetailVo();
		adjustDetailVo.setBarCode(storeSku.getBarCode());
		adjustDetailVo.setGoodsName(storeSku.getName());
		adjustDetailVo.setGoodsSkuId(storeSku.getSkuId());
		adjustDetailVo.setMultipleSkuId(storeSku.getMultipleSkuId());
		adjustDetailVo.setPropertiesIndb(storeSku.getPropertiesIndb());
		adjustDetailVo.setStoreSkuId(storeSku.getId());
		if (isLow) {
			adjustDetailVo.setNum(storeSku.getSkuActQuantity());
			adjustDetailVo.setPrice(storeSku.getActPrice());
		} else {
			adjustDetailVo.setNum(storeSku.getQuantity());
			adjustDetailVo.setPrice(storeSku.getOnlinePrice());
		}
		adjustDetailVo.setIsEvent(isActivity);
		return adjustDetailVo;
	}

	/**
	 * @Description: 根据组合商品明细构建调整单明细
	 * @param storeSku
	 * @param isLow
	 * @return   
	 * @author maojj
	 * @date 2017年1月5日
	 */
	private AdjustDetailVo buildDetailVo(GoodsStoreSkuAssembleDto comboDetailDto, boolean isActivity, int quantity) {
		AdjustDetailVo adjustDetailVo = new AdjustDetailVo();
		adjustDetailVo.setBarCode(comboDetailDto.getBarCode());
		adjustDetailVo.setGoodsName(comboDetailDto.getName());
		adjustDetailVo.setGoodsSkuId(comboDetailDto.getSkuId());
		adjustDetailVo.setMultipleSkuId(comboDetailDto.getMultipleSkuId());
		if (!StringUtils.isEmpty(comboDetailDto.getPropertiesIndb())) {
			JSONObject propertiesJson = JSONObject.fromObject(comboDetailDto.getPropertiesIndb());
			String skuProperties = propertiesJson.get("skuName").toString();
			adjustDetailVo.setPropertiesIndb(skuProperties);
		} else {
			adjustDetailVo.setPropertiesIndb("");
		}
		adjustDetailVo.setPropertiesIndb(comboDetailDto.getPropertiesIndb());
		adjustDetailVo.setStoreSkuId(comboDetailDto.getId());
		adjustDetailVo.setNum(quantity);
		adjustDetailVo.setPrice(comboDetailDto.getUnitPrice());
		adjustDetailVo.setIsEvent(isActivity);
		adjustDetailVo.setGroup(true);
		return adjustDetailVo;
	}

	private StockAdjustVo buildMallStockAdjustVo(TradeOrder order, StoreSkuParserBo parserBo) {

		StockAdjustVo stockAjustVo = new StockAdjustVo();

		stockAjustVo.setRpcId(UuidUtils.getUuid());
		stockAjustVo.setOrderId(order.getId());
		stockAjustVo.setOrderNo(order.getOrderNo());
		stockAjustVo.setOrderResource(order.getOrderResource());
		stockAjustVo.setOrderType(order.getType());
		stockAjustVo.setStoreId(order.getStoreId());
		stockAjustVo.setUserId(order.getUserId());
		stockAjustVo.setMethodName(this.getClass().getName() + ".process");

		AdjustDetailVo adjustDetailVo = null;
		List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();
		for (String storeSkuId : parserBo.getComboSkuIdList()) {
			CurrentStoreSkuBo skuBo = parserBo.getCurrentStoreSkuBo(storeSkuId);
			if (skuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()) {
				// 如果是低价，需要将订单商品拆分为两条记录去发起库存变更请求
				if (skuBo.getSkuActQuantity() > 0) {
					adjustDetailVo = buildDetailVo(skuBo, true, true);
					adjustDetailList.add(adjustDetailVo);
				}
				if (skuBo.getQuantity() > 0) {
					adjustDetailVo = buildDetailVo(skuBo, false, false);
					adjustDetailList.add(adjustDetailVo);
				}
			} else if (skuBo.getActivityType() == ActivityTypeEnum.SALE_ACTIVITIES.ordinal()) {
				// 如果是特惠
				adjustDetailVo = buildDetailVo(skuBo, false, true);
				adjustDetailList.add(adjustDetailVo);
			} else {
				adjustDetailVo = buildDetailVo(skuBo, false, false);
				adjustDetailList.add(adjustDetailVo);
			}
		}

		stockAjustVo.setAdjustDetailList(adjustDetailList);
		stockAjustVo.setStockOperateEnum(StockOperateEnum.PLACE_ORDER);
		return stockAjustVo;
	}

	private StockAdjustVo buildMallServStockAdjustVo(TradeOrder order, StoreSkuParserBo parserBo) {

		StockAdjustVo stockAjustVo = new StockAdjustVo();

		stockAjustVo.setRpcId(UuidUtils.getUuid());
		stockAjustVo.setOrderId(order.getId());
		stockAjustVo.setOrderNo(order.getOrderNo());
		stockAjustVo.setOrderResource(order.getOrderResource());
		stockAjustVo.setOrderType(order.getType());
		stockAjustVo.setStoreId(order.getStoreId());
		stockAjustVo.setUserId(order.getUserId());
		stockAjustVo.setMethodName(this.getClass().getName() + ".process");

		AdjustDetailVo adjustDetailVo = null;
		List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();
		for (CurrentStoreSkuBo skuBo : parserBo.getCurrentSkuMap().values()) {
			if (skuBo.getActivityType() == ActivityTypeEnum.NO_ACTIVITY.ordinal()) {
				adjustDetailVo = buildDetailVo(skuBo, false, false);
				adjustDetailList.add(adjustDetailVo);
			} else {
				adjustDetailVo = buildDetailVo(skuBo, false, true);
				adjustDetailList.add(adjustDetailVo);
			}
		}

		stockAjustVo.setAdjustDetailList(adjustDetailList);
		stockAjustVo.setStockOperateEnum(StockOperateEnum.PLACE_ORDER);
		return stockAjustVo;
	}

	private void addSkuSaleNum(PlaceOrderParamDto paramDto) throws Exception {
		if(paramDto.getSkuType() != OrderTypeEnum.STORE_CONSUME_ORDER){
			return;
		}
		StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
		GoodsStoreSku storeSku = null;
		for (CurrentStoreSkuBo skuBo : parserBo.getCurrentSkuMap().values()) {
			storeSku = new GoodsStoreSku();
			storeSku.setId(skuBo.getId());
			storeSku.setSaleNum(skuBo.getSaleNum() + skuBo.getQuantity() + skuBo.getSkuActQuantity());
			this.goodsStoreSkuServiceApi.updateByPrimaryKeySelective(storeSku);
		}
	}
}
