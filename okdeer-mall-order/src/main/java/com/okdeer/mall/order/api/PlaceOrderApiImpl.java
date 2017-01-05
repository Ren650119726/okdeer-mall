package com.okdeer.mall.order.api;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.bo.AppAdapter;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.handler.RequestHandlerChain;
import com.okdeer.mall.order.service.PlaceOrderApi;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.PlaceOrderApi")
public class PlaceOrderApiImpl implements PlaceOrderApi {
	
	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> confirmOrderService;
	
	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> submitOrderService;

	@Override
	public Response<PlaceOrderDto> confirmOrder(Request<PlaceOrderParamDto> req) throws Exception {
		Response<PlaceOrderDto> resp = new Response<PlaceOrderDto>();
		resp.setData(new PlaceOrderDto());
		req.getData().setOrderOptType(OrderOptTypeEnum.ORDER_SETTLEMENT);
		confirmOrderService.process(req, resp);
		if(!resp.isSuccess()){
			// 如果处理失败，将店铺信息和当前商品信息返回给App
			PlaceOrderParamDto paramDto = req.getData();
			StoreInfo storeInfo = (StoreInfo)paramDto.get("storeInfo");
			StoreSkuParserBo parserBo = (StoreSkuParserBo)paramDto.get("parserBo");
			resp.getData().setStoreInfo(AppAdapter.convert(storeInfo));
			resp.getData().setSkuList(AppAdapter.convert(parserBo));
		}
		return resp;
	}

	@Override
	public Response<PlaceOrderDto> submitOrder(Request<PlaceOrderParamDto> req) throws Exception {
		Response<PlaceOrderDto> resp = new Response<PlaceOrderDto>();
		resp.setData(new PlaceOrderDto());
		req.getData().setOrderOptType(OrderOptTypeEnum.ORDER_SUBMIT);
		submitOrderService.process(req, resp);
		return resp;
	}

}
