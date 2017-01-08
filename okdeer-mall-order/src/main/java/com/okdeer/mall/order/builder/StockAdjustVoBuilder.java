package com.okdeer.mall.order.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.okdeer.archive.goods.assemble.GoodsStoreSkuAssembleApi;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreAssembleDto;
import com.okdeer.archive.goods.assemble.dto.GoodsStoreSkuAssembleDto;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.stock.enums.StockOperateEnum;
import com.okdeer.archive.stock.vo.AdjustDetailVo;
import com.okdeer.archive.stock.vo.StockAdjustVo;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.service.TradeOrderItemService;

import net.sf.json.JSONObject;

@Component
public class StockAdjustVoBuilder {
	
	@Resource
	private TradeOrderItemService tradeOrderItemService;
	
	@Reference(version = "1.0.0", check = false)
	private GoodsStoreSkuAssembleApi goodsStoreSkuAssembleApi;

	/**
	 * @Description: 构建组合商品变更库存
	 * @param tradeOrder
	 * @param rpcId
	 * @return   
	 * @author maojj
	 * @throws ServiceException 
	 * @date 2017年1月8日
	 */
	public StockAdjustVo buildComboStock(TradeOrder tradeOrder,String rpcId,StockOperateEnum operateType) throws ServiceException{
		List<AdjustDetailVo> adjustDetailList = Lists.newArrayList();
		List<TradeOrderItem> orderItemList = tradeOrder.getTradeOrderItem();
		if (CollectionUtils.isEmpty(orderItemList)) {
			orderItemList = tradeOrderItemService.selectOrderItemByOrderId(tradeOrder.getId());
		}
		AdjustDetailVo detail = null;
		for (TradeOrderItem item : orderItemList) {
			if(item.getSpuType() == SpuTypeEnum.assembleSpu){
				detail = new AdjustDetailVo();
				detail.setStoreSkuId(item.getStoreSkuId());
				detail.setGoodsName(item.getSkuName());
				detail.setNum(item.getQuantity());
				adjustDetailList.add(detail);
			}
		}
		if(CollectionUtils.isEmpty(adjustDetailList)){
			return null;
		}
		StockAdjustVo stockAdjustVo = new StockAdjustVo();
		stockAdjustVo.setRpcId(rpcId);
		stockAdjustVo.setStockOperateEnum(StockOperateEnum.ACTIVITY_ORDER_COMPLETE);
		loadTradeOrder(stockAdjustVo,tradeOrder);
		stockAdjustVo.setAdjustDetailList(adjustDetailList);
		return stockAdjustVo;
	}
	
	/**
	 * @Description: 加载订单信息
	 * @param stockAdjustVo
	 * @param tradeOrder   
	 * @author maojj
	 * @date 2017年1月8日
	 */
	private void loadTradeOrder(StockAdjustVo stockAdjustVo,TradeOrder tradeOrder){
		stockAdjustVo.setOrderId(tradeOrder.getId());
		stockAdjustVo.setOrderNo(tradeOrder.getOrderNo());
		stockAdjustVo.setOrderResource(tradeOrder.getOrderResource());
		stockAdjustVo.setOrderType(tradeOrder.getType());
		stockAdjustVo.setStoreId(tradeOrder.getStoreId());
		stockAdjustVo.setUserId(tradeOrder.getUserId());
	}
	
	/**
	 * @Description: 构建进销存库存
	 * @param tradeOrder
	 * @param rpcId
	 * @return   
	 * @author maojj
	 * @throws ServiceException 
	 * @date 2017年1月8日
	 */
	public StockAdjustVo buildJxcStock(TradeOrder tradeOrder, String rpcId) throws Exception {
		StockAdjustVo stockAdjustVo = new StockAdjustVo();
		stockAdjustVo.setRpcId(rpcId);
		loadTradeOrder(stockAdjustVo,tradeOrder);
		stockAdjustVo.setStockOperateEnum(getStockOperate(tradeOrder));
		List<AdjustDetailVo> adjustDetailList = Lists.newArrayList();
		List<TradeOrderItem> orderItemList = tradeOrder.getTradeOrderItem();
		if (CollectionUtils.isEmpty(orderItemList)) {
			orderItemList = tradeOrderItemService.selectOrderItemByOrderId(tradeOrder.getId());
			
		}
		// 提取组合商品列表
		Map<String,List<GoodsStoreSkuAssembleDto>> comboSkuMap = parseComboSku(orderItemList);
		AdjustDetailVo detail = null;
		for (TradeOrderItem item : orderItemList) {
			if(item.getSpuType() == SpuTypeEnum.assembleSpu){
				List<GoodsStoreSkuAssembleDto> comboDetailList = comboSkuMap.get(item.getStoreSkuId());
				for (GoodsStoreSkuAssembleDto comboDetail : comboDetailList) {
					int buyNum = comboDetail.getQuantity() * item.getQuantity();
					detail = buildDetailVo(comboDetail, buyNum);
					adjustDetailList.add(detail);
				}
			}else{
				detail = buildStockDetail(item,tradeOrder);
				adjustDetailList.add(detail);
			}
		}
		stockAdjustVo.setAdjustDetailList(adjustDetailList);
		return stockAdjustVo;
	}
	
	private StockOperateEnum getStockOperate(TradeOrder tradeOrder){
		StockOperateEnum stockOperate = null;
		// 发货
		if (tradeOrder.getStatus() == OrderStatusEnum.TO_BE_SIGNED) {
			// 活动订单发货
			if (tradeOrder.getActivityType() == ActivityTypeEnum.SALE_ACTIVITIES) {
				stockOperate = StockOperateEnum.ACTIVITY_SEND_OUT_GOODS;
			} else {
				stockOperate = StockOperateEnum.SEND_OUT_GOODS;
			}
		} else if (tradeOrder.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED) {
			// 订单完成
			if (tradeOrder.getActivityType() == ActivityTypeEnum.SALE_ACTIVITIES) {
				stockOperate = StockOperateEnum.ACTIVITY_ORDER_COMPLETE;
			} else {
				stockOperate = StockOperateEnum.PLACE_ORDER_COMPLETE;
			}
		}
		return stockOperate;
	}
	
	public Map<String,List<GoodsStoreSkuAssembleDto>> parseComboSku(List<TradeOrderItem> itemList) throws Exception {
		List<String> comboIdList = new ArrayList<String>();
		 for(TradeOrderItem item : itemList){
			 if(item.getSpuType() == SpuTypeEnum.assembleSpu){
				 // 如果是组合商品
				 comboIdList.add(item.getStoreSkuId());
			 }
		 }
		if(CollectionUtils.isEmpty(comboIdList)){
			return null;
		}
		Map<String,List<GoodsStoreSkuAssembleDto>> comboSkuMap = new HashMap<String, List<GoodsStoreSkuAssembleDto>>();
		List<GoodsStoreAssembleDto> comboDtoList = goodsStoreSkuAssembleApi
				.findByAssembleSkuIds(comboIdList);
		for (GoodsStoreAssembleDto dto : comboDtoList) {
			for (GoodsStoreSkuAssembleDto detail : dto.getGoodsStoreSkuAssembleDtos()) {
				if (!comboSkuMap.containsKey(detail.getAssembleSkuId())) {
					comboSkuMap.put(detail.getAssembleSkuId(), new ArrayList<GoodsStoreSkuAssembleDto>());
				}
				comboSkuMap.get(detail.getAssembleSkuId()).add(detail);
			}
		}
		return comboSkuMap;
	}
	
	public Map<String,List<GoodsStoreSkuAssembleDto>> parseComboSkuForRefund(List<TradeOrderRefundsItem> itemList) throws Exception {
		List<String> comboIdList = new ArrayList<String>();
		 for(TradeOrderRefundsItem item : itemList){
			 if(item.getSpuType() == SpuTypeEnum.assembleSpu){
				 // 如果是组合商品
				 comboIdList.add(item.getStoreSkuId());
			 }
		 }
		if(CollectionUtils.isEmpty(comboIdList)){
			return null;
		}
		Map<String,List<GoodsStoreSkuAssembleDto>> comboSkuMap = new HashMap<String, List<GoodsStoreSkuAssembleDto>>();
		List<GoodsStoreAssembleDto> comboDtoList = goodsStoreSkuAssembleApi
				.findByAssembleSkuIds(comboIdList);
		for (GoodsStoreAssembleDto dto : comboDtoList) {
			for (GoodsStoreSkuAssembleDto detail : dto.getGoodsStoreSkuAssembleDtos()) {
				if (!comboSkuMap.containsKey(detail.getAssembleSkuId())) {
					comboSkuMap.put(detail.getAssembleSkuId(), new ArrayList<GoodsStoreSkuAssembleDto>());
				}
				comboSkuMap.get(detail.getAssembleSkuId()).add(detail);
			}
		}
		return comboSkuMap;
	}
	
	private AdjustDetailVo buildStockDetail(TradeOrderItem item,TradeOrder order){
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
		if (order.getStatus() == OrderStatusEnum.HAS_BEEN_SIGNED
				&& order.getType() == OrderTypeEnum.PHYSICAL_ORDER) {
			if (item.getPreferentialPrice() != null
					&& item.getPreferentialPrice().compareTo(BigDecimal.ZERO) == 1) {
				boolean isWeigh = false;
				if (item.getWeight() != null) {
					isWeigh = true;
				}
				BigDecimal number = convertScaleToKg(item.getQuantity(), isWeigh);
				// 优惠单价
				BigDecimal price = item.getIncome().divide(number, 4, BigDecimal.ROUND_HALF_UP);
				detail.setPrice(price);
			}
		}
		return detail;
	}
	
	/**
	 * @Description: 根据组合商品明细构建调整单明细
	 * @param storeSku
	 * @param isLow
	 * @return   
	 * @author maojj
	 * @date 2017年1月5日
	 */
	private AdjustDetailVo buildDetailVo(GoodsStoreSkuAssembleDto comboDetailDto,int quantity) {
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
		adjustDetailVo.setGroup(true);
		return adjustDetailVo;
	}
	
	/**
	 * 如果是称重会转换成千克
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
