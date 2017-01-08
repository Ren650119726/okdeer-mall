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
		for (CurrentStoreSkuBo storeSkuBo : parserBo.getCurrentSkuMap().values()) {
			if (storeSkuBo.getQuantity() > storeSkuBo.getSellable()) {
				// 库存不足
				if (paramDto.getSkuType() == OrderTypeEnum.STORE_CONSUME_ORDER) {
					// 到店消费
					if (storeSkuBo.getSellable() == 0) {
						resp.setResult(ResultCodeEnum.SERV_GOODS_SOLD_OUT);
					} else {
						resp.setCode(ResultCodeEnum.SERV_GOODS_NOT_ENOUGH.getCode());
						resp.setMessage(String.format(ResultCodeEnum.SERV_GOODS_NOT_ENOUGH.getDesc(),
								storeSkuBo.getSellable()));
					}
				} else {
					// 上门服务订单
					StoreInfoServiceExt storeserviceExt = ((StoreInfo) paramDto.get("storeInfo"))
							.getStoreInfoServiceExt();
					if (storeserviceExt != null && storeserviceExt.getIsShoppingCart() == 0) {
						// 不支持购物车
						if (storeSkuBo.getSellable() == 0) {
							resp.setResult(ResultCodeEnum.SERV_GOODS_SOLD_OUT_FW);
						} else {
							resp.setCode(ResultCodeEnum.SERV_GOODS_NOT_ENOUGH_FW.getCode());
							resp.setMessage(String.format(ResultCodeEnum.SERV_GOODS_NOT_ENOUGH_FW.getDesc(),
									storeSkuBo.getSellable()));
						}
					} else {
						// 支持购物车，与便利店提示一致
						resp.setResult(ResultCodeEnum.GOODS_STOCK_NOT_ENOUGH);
					}
				}
				break;
			}
		}
	}

}
