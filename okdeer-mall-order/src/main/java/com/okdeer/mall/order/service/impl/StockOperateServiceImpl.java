
package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.archive.goods.assemble.GoodsStoreSkuAssembleApi;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreSkuAssembleDto;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.service.StockManagerJxcServiceApi;
import com.okdeer.archive.stock.service.StockManagerServiceApi;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleMapper;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRecordService;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.activity.seckill.enums.SeckillStatusEnum;
import com.okdeer.mall.activity.seckill.service.ActivitySeckillService;
import com.okdeer.mall.order.builder.StockAdjustVoBuilder;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
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
	 * 
	 */
	@Autowired
	private StockAdjustVoBuilder stockAdjustVoBuilder;

	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuAssembleApi goodsStoreSkuAssembleApi;
	
	@Autowired
	private TradeOrderItemMapper tradeOrderItemMapper;

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

		StockAdjustVo stockAdjustVo = buildStockAdjust(tradeOrder);
		rpcIdList.add(stockAdjustVo.getRpcId());

		List<AdjustDetailVo> adjustDetailList = Lists.newArrayList();

		List<AdjustDetailVo> assembleAdjustDetailList = Lists.newArrayList();

		// 获取组合商品detail
		List<AdjustDetailVo> comboSkuDetailList = buildComboSkuDetail(tradeOrderItems);
		if (CollectionUtils.isNotEmpty(comboSkuDetailList)) {
			adjustDetailList.addAll(comboSkuDetailList);
		}

		for (TradeOrderItem item : tradeOrderItems) {

			AdjustDetailVo detail = buildAdjustDetail(tradeOrder, item);
			if (item.getSpuType() == SpuTypeEnum.assembleSpu) {
				// 组合商品
				assembleAdjustDetailList.add(detail);
				continue;
			}

			if (detail.getIsEvent() && ActivityTypeEnum.LOW_PRICE == tradeOrder.getActivityType()) {
				// 如果存在活动，并且活动类型是低价限购的活动类型
				int normalQytity = item.getQuantity() - item.getActivityQuantity();
				if (normalQytity > 0) {
					AdjustDetailVo normalAdjuestDetail = new AdjustDetailVo();
					BeanMapper.copy(detail, normalAdjuestDetail);
					detail.setNum(item.getActivityQuantity());
					normalAdjuestDetail.setIsEvent(false);
					normalAdjuestDetail.setNum(normalQytity);
					adjustDetailList.add(normalAdjuestDetail);
				}
			}

			adjustDetailList.add(detail);
		}

		stockAdjustVo.setAdjustDetailList(adjustDetailList);
		stockAdjustList.add(stockAdjustVo);
		// 如果是实物订单，走进销存库存
		if (tradeOrder.getType() == OrderTypeEnum.PHYSICAL_ORDER) {
			// 便利店优惠金额单价
			stockManagerJxcService.updateStock(stockAdjustVo);
		} else {
			// 否则走商城库存
			serviceStockManagerService.updateStock(stockAdjustVo);
		}

		if (CollectionUtils.isNotEmpty(assembleAdjustDetailList)) {
			// 组合商品还需要更新商城库的库存
			// Begin Bug:17007 added by maojj 2017-01-14.便利店商品只有在确认收货之后，才会扣减订单占用。所以便利店商品拒收流程等同于取消流程。
			if (stockAdjustVo.getStockOperateEnum() == StockOperateEnum.REFUSED_SIGN
					|| stockAdjustVo.getStockOperateEnum() == StockOperateEnum.ACTIVITY_REFUSED_SIGN) {
				stockAdjustVo.setStockOperateEnum(StockOperateEnum.CANCEL_ORDER);
			}
			stockAdjustVo.setAdjustDetailList(assembleAdjustDetailList);
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
		
		//组合商品detailVo
		List<AdjustDetailVo> assembleAdjustDetailList = Lists.newArrayList();
		
		// 获取组合商品detail
		List<AdjustDetailVo> comboSkuDetailList = buildComboSkuDetailByRefund(orderRefunds.getTradeOrderRefundsItem());
		if (CollectionUtils.isNotEmpty(comboSkuDetailList)) {
			adjustDetailList.addAll(comboSkuDetailList);
		}
		
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
			} else if (tradeOrder.getType() == OrderTypeEnum.SERVICE_STORE_ORDER) {
				detail.setSpuType(SpuTypeEnum.fwdSpu);
			} else if (tradeOrder.getType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
				detail.setSpuType(SpuTypeEnum.fwdDdxfSpu);
			}
			
			boolean isEvent = hasActivity(tradeOrder, item.getStoreSkuId());
			detail.setIsEvent(isEvent);

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
				// 如果是称重,用重量,如果是计件,用数量
				if (isWeigh) {
					number = item.getWeight();
				}

				// 优惠单价.订单实际收入除以商品数量得到的价格
				BigDecimal price = item.getIncome().divide(number, 4, BigDecimal.ROUND_HALF_UP);
				detail.setPrice(price);
			}
			// 便利店优惠金额单价
			
			if (item.getSpuType() == SpuTypeEnum.assembleSpu) {
				// 组合商品
				assembleAdjustDetailList.add(detail);
				continue;
			}

			if (detail.getIsEvent() && ActivityTypeEnum.LOW_PRICE == tradeOrder.getActivityType()) {
				// 如果存在活动，并且活动类型是低价限购的活动类型
				TradeOrderItem tradeOrderItem = tradeOrderItemMapper.selectOrderItemById(item.getOrderItemId());
				int normalQytity = item.getQuantity() - tradeOrderItem.getActivityQuantity();
				if (normalQytity > 0) {
					AdjustDetailVo normalAdjuestDetail = new AdjustDetailVo();
					BeanMapper.copy(detail, normalAdjuestDetail);
					detail.setNum(tradeOrderItem.getActivityQuantity());
					normalAdjuestDetail.setIsEvent(false);
					normalAdjuestDetail.setNum(normalQytity);
					adjustDetailList.add(normalAdjuestDetail);
				}
			}
			
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
		
		if (CollectionUtils.isNotEmpty(assembleAdjustDetailList)) {
			// 组合商品还需要更新商城库的库存
			stockAdjustVo.setAdjustDetailList(assembleAdjustDetailList);
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

	/**
	 * @Description: 组合商品库存detail构建
	 * @param tradeOrderItems 订单项
	 * @return
	 * @throws Exception
	 * @author zengjizu
	 * @date 2017年1月8日
	 */
	private List<AdjustDetailVo> buildComboSkuDetail(List<TradeOrderItem> tradeOrderItems) throws Exception {

		Map<String, List<GoodsStoreSkuAssembleDto>> goodsStoreAssembleMap = stockAdjustVoBuilder
				.parseComboSku(tradeOrderItems);

		if (goodsStoreAssembleMap == null) {
			return null;
		}

		List<AdjustDetailVo> adjustDetailList = Lists.newArrayList();

		for (TradeOrderItem item : tradeOrderItems) {
			if (item.getSpuType() == SpuTypeEnum.assembleSpu) {
				// 如果是组合商品
				List<GoodsStoreSkuAssembleDto> dtoList = goodsStoreAssembleMap.get(item.getStoreSkuId());
				for (GoodsStoreSkuAssembleDto goodsStoreSkuAssembleDto : dtoList) {
					AdjustDetailVo detail = new AdjustDetailVo();
					detail.setStoreSkuId(goodsStoreSkuAssembleDto.getStoreSkuId());
					detail.setGoodsSkuId(goodsStoreSkuAssembleDto.getSkuId());
					detail.setMultipleSkuId(goodsStoreSkuAssembleDto.getMultipleSkuId());
					detail.setGoodsName(goodsStoreSkuAssembleDto.getName());
					detail.setPrice(goodsStoreSkuAssembleDto.getUnitPrice());
					detail.setPropertiesIndb(goodsStoreSkuAssembleDto.getPropertiesIndb());
					detail.setStyleCode(goodsStoreSkuAssembleDto.getStyleCode());
					detail.setBarCode(goodsStoreSkuAssembleDto.getBarCode());
					int qutity = new BigDecimal(goodsStoreSkuAssembleDto.getQuantity())
							.multiply(new BigDecimal(item.getQuantity())).intValue();
					detail.setNum(qutity);
					detail.setIsEvent(false);
					detail.setSpuType(SpuTypeEnum.assembleSpu);

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
					adjustDetailList.add(detail);
				}
			}
		}
		return adjustDetailList;
	}

	/**
	 * @Description: 构建 StockAdjustVo
	 * @param tradeOrder 订单信息
	 * @return
	 * @author zengjizu
	 * @date 2017年1月8日
	 */
	private StockAdjustVo buildStockAdjust(TradeOrder tradeOrder) {
		StockAdjustVo stockAdjustVo = new StockAdjustVo();
		String rpcId = UuidUtils.getUuid();
		stockAdjustVo.setOrderId(tradeOrder.getId());
		stockAdjustVo.setOrderNo(tradeOrder.getOrderNo());
		stockAdjustVo.setOrderResource(tradeOrder.getOrderResource());
		stockAdjustVo.setOrderType(tradeOrder.getType());
		stockAdjustVo.setStoreId(tradeOrder.getStoreId());
		if (OrderTypeEnum.STORE_CONSUME_ORDER == tradeOrder.getType()) {
			// 如果是到店消费的话，就使用退活方式还库存
			stockAdjustVo.setStockOperateEnum(StockOperateEnum.RETURN_OF_GOODS);
		} else {
			stockAdjustVo.setStockOperateEnum(getStockOperateType(tradeOrder.getStatus(), Boolean.FALSE));
		}
		stockAdjustVo.setUserId(tradeOrder.getUserId());
		stockAdjustVo.setRpcId(rpcId);
		return stockAdjustVo;
	}

	private AdjustDetailVo buildAdjustDetail(TradeOrder tradeOrder, TradeOrderItem item) throws Exception {
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

		boolean isGoodActivity = hasActivity(tradeOrder, item.getStoreSkuId());
		detail.setIsEvent(isGoodActivity);
		if (tradeOrder.getType() == OrderTypeEnum.PHYSICAL_ORDER) {
			detail.setSpuType(SpuTypeEnum.physicalSpu);
		} else if (tradeOrder.getType() == OrderTypeEnum.SERVICE_STORE_ORDER) {
			detail.setSpuType(SpuTypeEnum.fwdSpu);
		} else if (tradeOrder.getType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
			detail.setSpuType(SpuTypeEnum.fwdDdxfSpu);
		}

		if (tradeOrder.getType() == OrderTypeEnum.PHYSICAL_ORDER) {
			// 便利店优惠金额单价
			if (item.getPreferentialPrice() != null && item.getPreferentialPrice().compareTo(BigDecimal.ZERO) == 1) {
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
		return detail;
	}

	/**
	 * @Description: 判断是否有活动
	 * @param tradeOrder
	 * @param item
	 * @return
	 * @throws Exception
	 * @author zengjizu
	 * @date 2017年1月8日
	 */
	private boolean hasActivity(TradeOrder tradeOrder, String storeSkuId) throws Exception {
		// 判断是否是团购和特惠商品
		boolean isGoodActivity = ActivityTypeEnum.GROUP_ACTIVITY == tradeOrder.getActivityType()
				|| isAttendSale(tradeOrder.getId(), storeSkuId);

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
		String saleId = findSaleId(tradeOrder.getId(), storeSkuId);
		if (!StringUtils.isNullOrEmpty(saleId)) {
			isGoodActivity = true;
			ActivitySale entity = activitySaleMapper.get(saleId);
			if (entity.getStatus() != 1) {
				isGoodActivity = false;
			}
		}
		return isGoodActivity;
	}

	
	private List<AdjustDetailVo> buildComboSkuDetailByRefund(List<TradeOrderRefundsItem> orderRefundsItemList) throws Exception {
		Map<String, List<GoodsStoreSkuAssembleDto>> goodsStoreAssembleMap = stockAdjustVoBuilder
				.parseComboSkuForRefund(orderRefundsItemList);

		if (goodsStoreAssembleMap == null) {
			return null;
		}

		List<AdjustDetailVo> adjustDetailList = Lists.newArrayList();

		for (TradeOrderRefundsItem item : orderRefundsItemList) {
			if (item.getSpuType() == SpuTypeEnum.assembleSpu) {
				// 如果是组合商品
				List<GoodsStoreSkuAssembleDto> dtoList = goodsStoreAssembleMap.get(item.getStoreSkuId());
				for (GoodsStoreSkuAssembleDto goodsStoreSkuAssembleDto : dtoList) {
					AdjustDetailVo detail = new AdjustDetailVo();
					detail.setStoreSkuId(goodsStoreSkuAssembleDto.getStoreSkuId());
					detail.setGoodsSkuId(goodsStoreSkuAssembleDto.getSkuId());
					detail.setMultipleSkuId(goodsStoreSkuAssembleDto.getMultipleSkuId());
					detail.setGoodsName(goodsStoreSkuAssembleDto.getName());
					detail.setPrice(goodsStoreSkuAssembleDto.getUnitPrice());
					detail.setPropertiesIndb(goodsStoreSkuAssembleDto.getPropertiesIndb());
					detail.setStyleCode(goodsStoreSkuAssembleDto.getStyleCode());
					detail.setBarCode(goodsStoreSkuAssembleDto.getBarCode());
					int qutity = new BigDecimal(goodsStoreSkuAssembleDto.getQuantity())
							.multiply(new BigDecimal(item.getQuantity())).intValue();
					detail.setNum(qutity);
					detail.setIsEvent(false);
					detail.setSpuType(goodsStoreSkuAssembleDto.getSpuTypeEnum());

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
					adjustDetailList.add(detail);
				}
			}
		}
		return adjustDetailList;
	}
}
