
package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerJxcServiceApi;
import com.okdeer.archive.stock.service.StockManagerServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleMapper;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRecordService;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillService;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.service.StockOperateService;

/**
 * ClassName: StockOperateServiceImpl 
 * @Description: 库存操作service实现类
 * @author zengjizu
 * @date 2016年11月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class StockOperateServiceImpl implements StockOperateService {

	@Reference(version = "1.0.0", check = false)
	private StockManagerServiceApi serviceStockManagerService;

	/**
	 * 库存管理Service
	 */
	@Reference(version = "1.0.0", check = false)
	private StockManagerJxcServiceApi stockManagerJxcService;

	/**
	 * 秒杀活动service
	 */
	@Autowired
	private ActivitySeckillService sctivitySeckillService;

	/**
	 * 特惠Dao
	 */
	@Autowired
	private ActivitySaleMapper activitySaleMapper;

	/**
	 * 特惠活动记录信息mapper
	 */
	@Autowired
	private ActivitySaleRecordService activitySaleRecordService;

	/**
	 * @Description: 根据订单回收库存
	 * @param tradeOrder
	 * @param rpcIdList
	 * @return
	 * @author zengjizu
	 * @date 2016年11月11日
	 */
	@Override
	public List<StockAdjustVo> recycleStockByOrder(TradeOrder tradeOrder, List<String> rpcIdList) throws Exception {
		List<StockAdjustVo> stockAdjustList = new ArrayList<StockAdjustVo>();

		List<TradeOrderItem> tradeOrderItems = tradeOrder.getTradeOrderItem();

		StockAdjustVo stockAdjustVo = new StockAdjustVo();
		String rpcId = UuidUtils.getUuid();
		rpcIdList.add(rpcId);
		stockAdjustVo.setOrderId(tradeOrder.getId());
		stockAdjustVo.setOrderNo(tradeOrder.getOrderNo());
		stockAdjustVo.setOrderResource(tradeOrder.getOrderResource());
		stockAdjustVo.setOrderType(tradeOrder.getType());
		stockAdjustVo.setStoreId(tradeOrder.getStoreId());

		stockAdjustVo.setStockOperateEnum(getStockOperateType(tradeOrder.getStatus(), Boolean.FALSE));
		stockAdjustVo.setUserId(tradeOrder.getUserId());
		for (TradeOrderItem item : tradeOrderItems) {
			// 判断是否是团购和特惠商品
			boolean isGoodActivity = ActivityTypeEnum.GROUP_ACTIVITY == tradeOrder.getActivityType()
					|| isAttendSale(tradeOrder.getId(), item.getStoreSkuId());

			// 如果秒杀活动已经结束，则当成普通商品
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
			if (tradeOrder.getType() == OrderTypeEnum.PHYSICAL_ORDER) {
				// 便利店优惠金额单价
				if (item.getPreferentialPrice() != null
						&& item.getPreferentialPrice().compareTo(BigDecimal.ZERO) == 1) {
					boolean isWeigh = false;
					if (item.getWeight() != null) {
						isWeigh = true;
					}
					BigDecimal number = convertScaleToKg(item.getQuantity(), isWeigh);
					// 优惠单价.订单实际收入除以商品数量得到的价格
					BigDecimal price = item.getIncome().divide(number, 4, BigDecimal.ROUND_HALF_UP);
					detail.setPrice(price);
				}
			}

			adjustDetailList.add(detail);
			stockAdjustVo.setAdjustDetailList(adjustDetailList);
			stockAdjustVo.setRpcId(rpcId);
			stockAdjustList.add(stockAdjustVo);
		}

		// 如果是实物订单，走进销存库存
		if (tradeOrder.getType() == OrderTypeEnum.PHYSICAL_ORDER) {
			// 便利店优惠金额单价
			stockManagerJxcService.updateStock(stockAdjustVo);
		} else {
			// 否则走商城库存
			serviceStockManagerService.updateStock(stockAdjustVo);
		}

		return stockAdjustList;
	}

	@Override
	public List<StockAdjustVo> recycleStockByRefund(TradeOrder tradeOrder, TradeOrderRefunds orderRefunds,
			List<String> rpcIdList) throws Exception {
		List<StockAdjustVo> stockAdjustList = new ArrayList<StockAdjustVo>();
		List<TradeOrderRefundsItem> tradeOrderRefundsItems = orderRefunds.getTradeOrderRefundsItem();

		// 一个订单只调一次库存
		StockAdjustVo stockAdjustVo = new StockAdjustVo();
		String rpcId = UuidUtils.getUuid();
		rpcIdList.add(rpcId);
		stockAdjustVo.setRpcId(rpcId);
		stockAdjustVo.setOrderId(orderRefunds.getOrderId());
		stockAdjustVo.setOrderNo(orderRefunds.getOrderNo());
		stockAdjustVo.setOrderResource(orderRefunds.getOrderResource());
		stockAdjustVo.setOrderType(orderRefunds.getType());
		stockAdjustVo.setStoreId(orderRefunds.getStoreId());
		// 操作类型都默认是普通商品的，然后如果里面有活动商品，在商品详情中有isEvent判断
		stockAdjustVo.setStockOperateEnum(StockOperateEnum.RETURN_OF_GOODS);
		stockAdjustVo.setUserId(orderRefunds.getUserId());
		List<AdjustDetailVo> adjustDetailList = new ArrayList<AdjustDetailVo>();

		AdjustDetailVo detail = null;
		for (TradeOrderRefundsItem item : tradeOrderRefundsItems) {

			detail = new AdjustDetailVo();
			detail.setStoreSkuId(item.getStoreSkuId());
			detail.setGoodsSkuId("");
			detail.setMultipleSkuId("");
			detail.setGoodsName(item.getSkuName());
			detail.setPrice(item.getUnitPrice());
			detail.setPropertiesIndb(item.getPropertiesIndb());
			detail.setStyleCode(item.getStyleCode());
			detail.setBarCode(item.getBarCode());

			if (tradeOrder.getType() == OrderTypeEnum.PHYSICAL_ORDER) {
				detail.setSpuType(SpuTypeEnum.physicalSpu);
			}else if(tradeOrder.getType() == OrderTypeEnum.SERVICE_STORE_ORDER){
				detail.setSpuType(SpuTypeEnum.fwdSpu);
			}else if(tradeOrder.getType() == OrderTypeEnum.STORE_CONSUME_ORDER){
				detail.setSpuType(SpuTypeEnum.fwdDdxfSpu);
			}
			detail.setIsEvent(tradeOrder.getActivityType() == ActivityTypeEnum.GROUP_ACTIVITY
					|| isAttendSale(orderRefunds.getOrderId(), item.getStoreSkuId()));
			
			Integer quantity = item.getQuantity();
			if (quantity == null) {
				if (item.getWeight() != null) {
					quantity = item.getWeight().multiply(new BigDecimal(1000)).intValue();
					detail.setIsWeightSku("Y");
				}
			}
			detail.setNum(quantity);

			// add by 便利店优惠金额单价 lijun 20161110 begin
			if (orderRefunds.getType() == OrderTypeEnum.PHYSICAL_ORDER) {
				boolean isWeigh = false;
				if (item.getWeight() != null) {
					isWeigh = true;
				}
				BigDecimal number = convertScaleToKg(item.getQuantity(), isWeigh);
				// 优惠单价.订单实际收入除以商品数量得到的价格
				BigDecimal price = item.getIncome().divide(number, 4, BigDecimal.ROUND_HALF_UP);
				detail.setPrice(price);
			}
			// 便利店优惠金额单价

			adjustDetailList.add(detail);
		}
		stockAdjustVo.setAdjustDetailList(adjustDetailList);
		stockAdjustList.add(stockAdjustVo);
		// 实物订单走进销存库存
		if (orderRefunds.getType() == OrderTypeEnum.PHYSICAL_ORDER) {
			stockManagerJxcService.updateStock(stockAdjustVo);
		} else {
			// 否则走商城库存
			serviceStockManagerService.updateStock(stockAdjustVo);
		}
		return stockAdjustList;
	}

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

	/**
	 * @Description: 订单商品是否参与特惠活动
	 * @param orderId 订单id
	 * @param storeGoodSkuId 店铺商品skuid
	 * @return 是否参与特惠活动
	 * @author zengjizu
	 * @date 2016年11月11日
	 */
	public boolean isAttendSale(String orderId, String storeGoodSkuId) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("orderId", orderId);
		map.put("saleGoodsId", storeGoodSkuId);
		int count = activitySaleRecordService.selectOrderGoodsCount(map);
		return count > 0;
	}

	/**
	 * @Description: 订单商品是否参与特惠活动
	 * @param orderId 订单id
	 * @param storeGoodSkuId 店铺商品skuid
	 * @return 活动id
	 * @author zengjizu
	 * @date 2016年11月11日
	 */
	public String findSaleId(String orderId, String storeGoodSkuId) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("orderId", orderId);
		map.put("saleGoodsId", storeGoodSkuId);
		String saleId = activitySaleRecordService.selectOrderGoodsActivity(map);
		return saleId;
	}

	/**
	 * @Description: 如果是称重会转换成千克
	 * @param value 值
	 * @param isWeigh 是否称重
	 * @return
	 * @author zengjizu
	 * @date 2016年11月11日
	 */
	private BigDecimal convertScaleToKg(Integer value, boolean isWeigh) {
		if (value == null) {
			return null;
		}
		if (isWeigh) {
			return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(1000)).setScale(4, BigDecimal.ROUND_FLOOR);
		} else {
			return BigDecimal.valueOf(value);
		}
	}

}
