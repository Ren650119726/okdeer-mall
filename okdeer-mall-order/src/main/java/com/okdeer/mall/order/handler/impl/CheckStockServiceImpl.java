package com.okdeer.mall.order.handler.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

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
			if (isOutOfLimitKind(parserBo)) {
				resp.setResult(ResultCodeEnum.KIND_IS_OUT);
				return;
			}
			// 检查是否超出限购
			if (isOutOfLimitBuy(parserBo, paramDto.getSkuList(),resp)) {
				resp.setResult(ResultCodeEnum.BUY_IS_OUT);
				return;
			}
		}
		
		if (isOutOfStock(parserBo,resp)) {
			resp.setResult(ResultCodeEnum.GOODS_STOCK_NOT_ENOUGH);
		}else{
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
	public boolean isOutOfLimitKind(StoreSkuParserBo parserBo) {
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
					// 购买限款数大于特惠活动限款数量
					isOutOfLimitKind = true;
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
	private boolean isOutOfLimitBuy(StoreSkuParserBo parserBo, List<PlaceOrderItemDto> skuList,Response<PlaceOrderDto> resp)
			throws ServiceException {
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
			// 限购 判定该用户购买的该店铺的该商品是否超过限定数量
			if (tradeMax > 0) {
				// 活动商品已购数量
				int boughtNum = parserBo.getBoughtSkuNum(activityGoods.getStoreSkuId());
				// 活动商品信息
				skuBo = parserBo.getCurrentStoreSkuBo(activityGoods.getStoreSkuId());
				if (skuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()) {
					int skuActQuantity = skuBo.getSkuActQuantity();
					// 低价商品，超过限购数量的，则按照原价购买
					if(skuActQuantity > tradeMax - boughtNum){
						// 可参与活动的数量
						skuBo.setSkuActQuantity(tradeMax - boughtNum);
						resp.setMessage(ResultCodeEnum.LOW_BUY_IS_OUT.getDesc());
					}
				}else if (skuBo.getActivityType() == ActivityTypeEnum.SALE_ACTIVITIES.ordinal()){
					// 特惠商品超出限款不能进行购买
					if ( skuBo.getQuantity() > tradeMax - boughtNum) {
						isOutOfLimit = true;
						break; 
					}
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
		for (CurrentStoreSkuBo storeSkuBo : parserBo.getCurrentSkuMap().values()) {
			if(storeSkuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
				if(storeSkuBo.getSkuActQuantity() > 0 && storeSkuBo.getSkuActQuantity() > storeSkuBo.getLocked()){
					// 重新设置低价商品可参与活动的数量
					storeSkuBo.setSkuActQuantity(storeSkuBo.getLocked());
					resp.setMessage(ResultCodeEnum.LOW_BUY_IS_OUT.getDesc());
				}
				
				if(storeSkuBo.getQuantity() - storeSkuBo.getSkuActQuantity() > storeSkuBo.getSellable()){
					return true;
				}
			}else if(storeSkuBo.getQuantity() > storeSkuBo.getSellable()){
				return true;
			}
		}
		return false;
	}

}
