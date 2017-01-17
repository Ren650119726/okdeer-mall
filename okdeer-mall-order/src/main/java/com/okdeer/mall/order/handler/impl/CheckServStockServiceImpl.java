package com.okdeer.mall.order.handler.impl;

import org.springframework.stereotype.Service;

import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.PlaceOrderTypeEnum;
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
				if (paramDto.getOrderType() == PlaceOrderTypeEnum.SECKILL_ORDER) {
					// 如果是秒杀，提示秒杀库存不足。
					resp.setResult(ResultCodeEnum.SECKILL_STOCK_NOT_ENOUGH);
				} else {
					if(buyKindSize > 1){
						resp.setResult(ResultCodeEnum.PART_GOODS_STOCK_NOT_ENOUGH);
					}else{
						resp.setResult(ResultCodeEnum.STOCK_NOT_ENOUGH);
					}
				}
				break;
			}
		}
	}
}
