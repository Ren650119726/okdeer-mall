package com.okdeer.mall.order.handler.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.archive.goods.store.entity.GoodsStoreSkuStock;
import com.okdeer.archive.goods.store.service.GoodsStoreSkuStockServiceApi;
import com.okdeer.archive.stock.service.StockManagerJxcServiceApi;
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
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
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
			if (isOutOfLimitBuy(parserBo, paramDto.getSkuList())) {
				resp.setResult(ResultCodeEnum.KIND_IS_OUT);
				return;
			}
		}
		
		if (isOutOfStock(parserBo)) {
			if (req.getData().getOrderOptType() == OrderOptTypeEnum.ORDER_SETTLEMENT) {
				resp.setResult(ResultCodeEnum.STOCK_NOT_ENOUGH_SETTLEMENT);
			} else {
				resp.setResult(ResultCodeEnum.STOCK_NOT_ENOUGH);
			}
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
	private boolean isOutOfLimitBuy(StoreSkuParserBo parserBo, List<PlaceOrderItemDto> skuList)
			throws ServiceException {
		boolean isOutOfLimit = false;
		Map<String, ActivitySaleGoods> currentActivitySkuMap = parserBo.getCurrentActivitySkuMap();
		if (currentActivitySkuMap == null || CollectionUtils.isEmpty(currentActivitySkuMap.keySet())) {
			return isOutOfLimit;
		}
		for (Map.Entry<String, ActivitySaleGoods> entry : currentActivitySkuMap.entrySet()) {
			ActivitySaleGoods activityGoods = entry.getValue();
			// 限购数量
			int tradeMax = activityGoods.getTradeMax();
			// 限购 判定该用户购买的该店铺的该商品是否超过限定数量
			if (tradeMax > 0) {
				int boughtNum = parserBo.getBoughtSkuNum(activityGoods.getStoreSkuId());
				int buyNum = parserBo.getBuyNum(activityGoods.getStoreSkuId());
				if (buyNum > tradeMax - boughtNum) {
					isOutOfLimit = true;
					break;
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
	private boolean isOutOfStock(StoreSkuParserBo parserBo) {
		for (CurrentStoreSkuBo storeSkuBo : parserBo.getCurrentSkuMap().values()) {
			if(storeSkuBo.getActivityType() == ActivityTypeEnum.LOW_PRICE.ordinal()){
				// 如果是低价商品，需要分别判断活动库存和普通商品库存
				if(storeSkuBo.getQuantity() > 0 &&  storeSkuBo.getQuantity() > storeSkuBo.getSellable()){
					return true;
				}else if (storeSkuBo.getSkuActQuantity() > 0 && storeSkuBo.getSkuActQuantity() > storeSkuBo.getLocked()){
					return true;
				}
			}else if(storeSkuBo.getQuantity() > storeSkuBo.getSellable()){
				return true;
			}
		}
		return false;
	}

}
