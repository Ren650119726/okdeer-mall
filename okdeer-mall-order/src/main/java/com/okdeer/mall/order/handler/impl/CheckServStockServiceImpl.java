package com.okdeer.mall.order.handler.impl;

import org.springframework.stereotype.Service;

import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.archive.store.entity.StoreInfoServiceExt;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandler;

@Service("checkServStockService")
public class CheckServStockServiceImpl implements RequestHandler<PlaceOrderParamDto, PlaceOrderDto> {

	@Override
	public void process(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) throws Exception {
		PlaceOrderParamDto paramDto = req.getData();
		StoreSkuParserBo parserBo = (StoreSkuParserBo) paramDto.get("parserBo");
		int buyKindSize = req.getData().getSkuList().size();
		for (CurrentStoreSkuBo storeSkuBo : parserBo.getCurrentSkuMap().values()) {
			if (storeSkuBo.getQuantity() > storeSkuBo.getSellable()) {
				// 库存不足
				if (paramDto.getSkuType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
					// 到店消费
					if(buyKindSize > 1){
						resp.setResult(ResultCodeEnum.PART_GOODS_STOCK_NOT_ENOUGH);
					}else{
						resp.setResult(ResultCodeEnum.STOCK_NOT_ENOUGH);
					}
					
				} else {
					// 上门服务订单
					StoreInfoServiceExt storeserviceExt = ((StoreInfo) paramDto.get("storeInfo"))
							.getStoreInfoServiceExt();
					if (storeserviceExt != null && storeserviceExt.getIsShoppingCart() == 0) {
						// 不支持购物车
						if(buyKindSize > 1){
							resp.setResult(ResultCodeEnum.PART_GOODS_STOCK_NOT_ENOUGH);
						}else{
							resp.setResult(ResultCodeEnum.STOCK_NOT_ENOUGH);
						}
					} else {
						// 支持购物车，与便利店提示一致
						if(buyKindSize > 1){
							resp.setResult(ResultCodeEnum.PART_GOODS_STOCK_NOT_ENOUGH);
						}else{
							resp.setResult(ResultCodeEnum.STOCK_NOT_ENOUGH);
						}
					}
				}
				break;
			}
		}
	}

}
