package com.okdeer.mall.order.handler.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.okdeer.archive.goods.dto.StoreSkuComponentDto;
import com.okdeer.archive.goods.spu.enums.SkuBindType;
import com.okdeer.archive.goods.spu.enums.SpuTypeEnum;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.activity.coupons.entity.ActivitySale;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleGoods;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleRecord;
import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleRecordMapper;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderItemDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.handler.RequestHandler;

/**
 * ClassName: CheckStockServiceImpl 
 * @Description: 检查库存
 * @author maojj
 * @date 2017年1月5日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.0 			2017年1月5日				maojj
 */
@Service("checkStockService")
public class CheckStockServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {

	/**
	 * 活动记录Mapper
	 */
	@Resource
	private ActivitySaleRecordMapper activitySaleRecordMapper;

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		StoreSkuParserBo parserBo = (StoreSkuParserBo) paramDto.get("parserBo");
		Map<String, List<String>> activitySkuMap = parserBo.getActivitySkuMap();
		if (activitySkuMap != null && CollectionUtils.isNotEmpty(activitySkuMap.keySet())) {
			// 查询用户的活动购买记录
			List<ActivitySaleRecord> buyRecordList = activitySaleRecordMapper.findSaleRecord(paramDto.getUserId(),
					activitySkuMap.keySet());
			parserBo.loadBuyRecordList(buyRecordList);
			// 检查是否超出限款
			if (isOutOfLimitKind(parserBo, resp)) {
				return;
			}
			// 检查是否超出限购
			if (isOutOfLimitBuy(parserBo, paramDto.getSkuList(), resp)) {
				return;
			}
		}

		if (!isOutOfStock(parserBo, resp)) {
			// 库存检查通过，则解析低价优惠信息
			parserBo.parseLowFavour();
		}
	}

	/**
	 * @Description: 是否超出限款
	 * @param parserBo
	 * @return   
	 * @author maojj
	 * @date 2017年1月4日
	 */
	public boolean isOutOfLimitKind(StoreSkuParserBo parserBo, Response<PlaceOrderDto> resp) {
		boolean isOutOfLimitKind = false;
		// 活动商品映射信息
		List<ActivitySale> activityList = parserBo.getActivityList();
		if (CollectionUtils.isEmpty(activityList)) {
			return isOutOfLimitKind;
		}
		for (ActivitySale sale : activityList) {
			int limit = sale.getLimit().intValue();
			if (limit > 0) {
				// 活动有限款限制
				int totalAdd = parserBo.countAddKindNum(sale.getId());
				if (totalAdd > limit - parserBo.getBuyKind(sale.getId())) {
					if (sale.getType() == ActivityTypeEnum.LOW_PRICE) {
						// 购买限款数大于特惠活动限款数量
						resp.setCode(ResultCodeEnum.LOW_KIND_IS_OUT.getCode());
						resp.setMessage(String.format(ResultCodeEnum.LOW_KIND_IS_OUT.getDesc(), limit));
					} else {
						// 购买限款数大于特惠活动限款数量
						resp.setCode(ResultCodeEnum.KIND_IS_OUT.getCode());
						resp.setMessage(String.format(ResultCodeEnum.KIND_IS_OUT.getDesc(), limit));
					}
					isOutOfLimitKind = true;
					break;
				}
			}
		}

		return isOutOfLimitKind;
	}

	/**
	 * @Description: 是否超出限购
	 * @param parserBo
	 * @param skuList
	 * @return
	 * @throws ServiceException   
	 * @author maojj
	 * @date 2017年1月4日
	 */
	private boolean isOutOfLimitBuy(StoreSkuParserBo parserBo, List<PlaceOrderItemDto> skuList,
			Response<PlaceOrderDto> resp) throws ServiceException {
		boolean isOutOfLimit = false;
		Map<String, ActivitySaleGoods> currentActivitySkuMap = parserBo.getCurrentActivitySkuMap();
		if (currentActivitySkuMap == null || CollectionUtils.isEmpty(currentActivitySkuMap.keySet())) {
			return isOutOfLimit;
		}
		CurrentStoreSkuBo skuBo = null;
		for (Map.Entry<String, ActivitySaleGoods> entry : currentActivitySkuMap.entrySet()) {
			ActivitySaleGoods activityGoods = entry.getValue();
			// 限购数量
			int tradeMax = activityGoods.getTradeMax();
			// 活动商品信息
			skuBo = parserBo.getCurrentStoreSkuBo(activityGoods.getStoreSkuId());
			// 限购 判定该用户购买的该店铺的该商品是否超过限定数量
			if (tradeMax > 0) {
				// 活动商品已购数量
				int boughtNum = parserBo.getBoughtSkuNum(activityGoods.getStoreSkuId());
				if (skuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()) {
					// 低价商品，可购买的低价数量
					int buyNumEnabled = tradeMax > boughtNum ? tradeMax - boughtNum : 0;
					int skuActQuantity = skuBo.getQuantity() > buyNumEnabled ? buyNumEnabled : skuBo.getQuantity();
					skuBo.setSkuActQuantity(skuActQuantity);
				} else if (skuBo.getActivityType() == ActivityTypeEnum.SALE_ACTIVITIES.ordinal()) {
					// 特惠商品超出限款不能进行购买
					if (skuBo.getQuantity() > tradeMax - boughtNum) {
						resp.setCode(ResultCodeEnum.BUY_IS_OUT.getCode());
						resp.setMessage(String.format(ResultCodeEnum.BUY_IS_OUT.getDesc(), tradeMax));
						isOutOfLimit = true;
						break;
					}
				}
			} else {
				if (skuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()) {
					// 低价不限购
					skuBo.setSkuActQuantity(skuBo.getQuantity());
				}
			}
		}
		return isOutOfLimit;
	}

	/**
	 * @Description: 库存是否不足
	 * @param parserBo
	 * @param req
	 * @return   
	 * @author maojj
	 * @date 2017年1月4日
	 */
	private boolean isOutOfStock(StoreSkuParserBo parserBo,Response<PlaceOrderDto> resp) {
		// 当前商品映射信息
		Map<String, CurrentStoreSkuBo> currentSkuMap = parserBo.getCurrentSkuMap();
		int kindSize = currentSkuMap.values().size();
		// 特价商品购买数量映射Map
		Map<String,Integer> skuActNumMap = parserBo.getSkuActNumMap();
		// 商品购买数量映射Map
		Map<String,Integer> skuNumMap = Maps.newHashMap();
		for (CurrentStoreSkuBo storeSkuBo : currentSkuMap.values()) {
			if(storeSkuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
				if(storeSkuBo.getSkuActQuantity() > 0 && storeSkuBo.getSkuActQuantity() > storeSkuBo.getLocked()){
					// 重新设置低价商品可参与活动的数量
					storeSkuBo.setSkuActQuantity(storeSkuBo.getLocked());
				}
				if(storeSkuBo.getSkuActQuantity() > 0 && storeSkuBo.getActPrice().compareTo(storeSkuBo.getAppActPrice()) > 0){
					// 如果后台分配用户有购买低价商品，且当前的低价价格>app请求的低价价格，则提示用户信息发生变化。让用户重新确认购买。
					if(kindSize > 1){
						resp.setResult(ResultCodeEnum.PART_GOODS_IS_CHANGE);
					}else{
						resp.setResult(ResultCodeEnum.GOODS_IS_CHANGE);
					}
					return true;
				}
				// 购买原价商品数量
				int buyPrimeNum = storeSkuBo.getQuantity() - storeSkuBo.getSkuActQuantity();
				if (storeSkuBo.getTradeMax() != null && storeSkuBo.getTradeMax().intValue() > 0
						&& buyPrimeNum > storeSkuBo.getTradeMax().intValue()) {
					// 如果原价商品限购，检查原价商品购买数量是否超过限购。
					resp.setCode(ResultCodeEnum.LOW_STOCK_NOT_ENOUGH.getCode());
					resp.setMessage(String.format(ResultCodeEnum.LOW_STOCK_NOT_ENOUGH.getDesc(), storeSkuBo.getName()));
					return true;
				}
				if(storeSkuBo.getSpuType() == SpuTypeEnum.assembleSpu && buyPrimeNum > 0){
					// 组合商品只能加入活动才能售卖。组合商品不能按原价商品进行售卖。所以，如果组合商品参与了低价活动，后台判定有部分数据将被转换成原价时，则不能进行购买。
					resp.setCode(ResultCodeEnum.LOW_STOCK_NOT_ENOUGH.getCode());
					resp.setMessage(String.format(ResultCodeEnum.LOW_STOCK_NOT_ENOUGH.getDesc(), storeSkuBo.getName()));
					return true;
				}
				
				// 检查总共购买的是否超过可售数量
				if( storeSkuBo.getQuantity() > storeSkuBo.getSellable()){
					resp.setResult(kindSize > 1 ? ResultCodeEnum.PART_GOODS_STOCK_NOT_ENOUGH : ResultCodeEnum.STOCK_NOT_ENOUGH);
					return true;
				}
				// 如果重新分配的低价活动数量<请求的低价活动数量，则提示：部分商品超过活动限制
				if(storeSkuBo.getSkuActQuantity() < skuActNumMap.get(storeSkuBo.getId())){
					resp.setMessage(ResultCodeEnum.LOW_BUY_IS_OUT.getDesc());
				}
			}else if(storeSkuBo.getActivityType() == ActivityTypeEnum.SALE_ACTIVITIES.ordinal()){
				// 特惠商品，判断商品购买数量和活动商品数量
				if(storeSkuBo.getQuantity() > storeSkuBo.getSellable() || storeSkuBo.getQuantity() > storeSkuBo.getLocked()){
					resp.setResult(
							kindSize > 1 ? ResultCodeEnum.PART_GOODS_STOCK_NOT_ENOUGH : ResultCodeEnum.STOCK_NOT_ENOUGH);
					return true;
				}
			} else if(storeSkuBo.getQuantity() > storeSkuBo.getSellable()){
				resp.setResult(
						kindSize > 1 ? ResultCodeEnum.PART_GOODS_STOCK_NOT_ENOUGH : ResultCodeEnum.STOCK_NOT_ENOUGH);
				return true;
			} else if (storeSkuBo.getTradeMax() != null && storeSkuBo.getTradeMax().intValue() > 0
					&& storeSkuBo.getQuantity() > storeSkuBo.getTradeMax().intValue()) {
				resp.setResult(ResultCodeEnum.TRADE_LIMIT_OVERFLOW);
				return true;
			}
			
			//捆绑商品库存计算
			if (storeSkuBo.getBindType() == SkuBindType.bind) {
				List<StoreSkuComponentDto> skuComponent = parserBo.getComponentSkuMap().get(storeSkuBo.getId());
				for (StoreSkuComponentDto comDto : skuComponent) {
					if(skuNumMap.containsKey(comDto.getComponentStoreSkuId())){
						skuNumMap.put(comDto.getComponentStoreSkuId(),skuNumMap.get(comDto.getComponentStoreSkuId())
										+ (storeSkuBo.getQuantity() * comDto.getComponentNum().intValue()));
					}else{
						skuNumMap.put(comDto.getComponentStoreSkuId(),
								storeSkuBo.getQuantity() * comDto.getComponentNum().intValue());
					}
				}
			} else {
				if(skuNumMap.containsKey(storeSkuBo.getId())){
					skuNumMap.put(storeSkuBo.getId(), skuNumMap.get(storeSkuBo.getId()) + storeSkuBo.getQuantity());
				}else{
					skuNumMap.put(storeSkuBo.getId(), storeSkuBo.getQuantity());
				}
			}
		}
		// Begin V2.6.1 Modified by maojj 2017-08-31 
		//验证SKU库存(包含校验捆绑商品成分)
		// 库存检查值--当前商品的可售库存
		Integer stockCheckNum = null; 
		// 捆绑商品明细库存映射
		Map<String, GoodsStoreSkuStock> bindStockMap = parserBo.getBindStockMap();
		for(Map.Entry<String, Integer> skuNum : skuNumMap.entrySet()){
			// 如果当前商品中不存在商品Id，则表示该商品为捆绑商品成分，需要从捆绑商品库存映射中查找
			if (currentSkuMap.containsKey(skuNum.getKey())) {
				stockCheckNum = currentSkuMap.get(skuNum.getKey()).getSellable();
			} else if (bindStockMap.containsKey(skuNum.getKey())) {
				stockCheckNum = bindStockMap.get(skuNum.getKey()).getSellable();
			}
			if(skuNum.getValue().compareTo(stockCheckNum) > 0){
				resp.setResult(kindSize > 1 ? ResultCodeEnum.PART_GOODS_STOCK_NOT_ENOUGH:ResultCodeEnum.STOCK_NOT_ENOUGH);
				return true;
			}
		}
		// End V2.6.1 Modified by maojj 2017-08-31 
		return false;
	}

}
