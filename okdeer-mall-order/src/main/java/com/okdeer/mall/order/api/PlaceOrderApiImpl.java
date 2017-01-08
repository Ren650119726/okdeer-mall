package com.okdeer.mall.order.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.archive.store.entity.StoreInfo;
import com.okdeer.mall.activity.seckill.entity.ActivitySeckill;
import com.okdeer.mall.common.dto.Request;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.bo.AppAdapter;
import com.okdeer.mall.order.bo.CurrentStoreSkuBo;
import com.okdeer.mall.order.bo.StoreSkuParserBo;
import com.okdeer.mall.order.dto.AppStoreDto;
import com.okdeer.mall.order.dto.PlaceOrderDto;
import com.okdeer.mall.order.dto.PlaceOrderParamDto;
import com.okdeer.mall.order.enums.OrderOptTypeEnum;
import com.okdeer.mall.order.enums.PayWayEnum;
import com.okdeer.mall.order.enums.PlaceOrderTypeEnum;
import com.okdeer.mall.order.handler.RequestHandlerChain;
import com.okdeer.mall.order.service.PlaceOrderApi;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.PlaceOrderApi")
public class PlaceOrderApiImpl implements PlaceOrderApi {

	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> confirmOrderService;

	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> confirmServOrderService;
	
	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> confirmSeckillOrderService;

	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> submitOrderService;

	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> submitServOrderService;
	

	@Resource
	private RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> submitSeckillOrderService;

	@Override
	public Response<PlaceOrderDto> confirmOrder(Request<PlaceOrderParamDto> req) throws Exception {
		Response<PlaceOrderDto> resp = new Response<PlaceOrderDto>();
		resp.setData(new PlaceOrderDto());
		req.getData().setOrderOptType(OrderOptTypeEnum.ORDER_SETTLEMENT);
		RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> handlerChain = null;
		switch (req.getData().getOrderType()) {
			case CVS_ORDER:
				handlerChain = confirmOrderService;
				break;
			case SRV_ORDER:
				handlerChain = confirmServOrderService;
				break;
			case SECKILL_ORDER:
				handlerChain = confirmSeckillOrderService;
				break;
			default:
				break;
		}
		handlerChain.process(req, resp);
		fillResponse(req, resp);
		return resp;
	}

	private void fillResponse(Request<PlaceOrderParamDto> req, Response<PlaceOrderDto> resp) {
		PlaceOrderParamDto paramDto = req.getData();
		StoreInfo storeInfo = (StoreInfo) paramDto.get("storeInfo");
		ActivitySeckill seckillInfo = (ActivitySeckill) paramDto.get("seckillInfo");
		StoreSkuParserBo parserBo = (StoreSkuParserBo) paramDto.get("parserBo");
		AppStoreDto appStoreDto = AppAdapter.convert(storeInfo);
		if (parserBo != null) {
			resp.getData().setOrderFare(parserBo.getFare());
		}
		resp.getData().setStoreInfo(appStoreDto);
		if(req.getData().getOrderType() != PlaceOrderTypeEnum.CVS_ORDER){
			resp.getData().setStoreServExt(AppAdapter.convertAppStoreServiceExtDto(storeInfo));
			resp.getData().setSkuList(AppAdapter.convert(parserBo));
			resp.getData().setSeckillInfo(AppAdapter.convert(seckillInfo));
			List<CurrentStoreSkuBo> skuList = new ArrayList<CurrentStoreSkuBo>();
			if (CollectionUtils.isNotEmpty(parserBo.getCurrentSkuMap().values())) {
				skuList.addAll(parserBo.getCurrentSkuMap().values());
				if (skuList.size() > 1) {
					resp.getData().setPaymentMode(PayWayEnum.PAY_ONLINE.ordinal());
				} else {
					resp.getData().setPaymentMode(skuList.get(0).getPaymentMode());
				}
			}
		}
		if(!resp.isSuccess() && req.getData().getOrderType() == PlaceOrderTypeEnum.CVS_ORDER){
			resp.getData().setSkuList(AppAdapter.convert(parserBo));
		}
		resp.getData().setCurrentTime(System.currentTimeMillis());

	}

	@Override
	public Response<PlaceOrderDto> submitOrder(Request<PlaceOrderParamDto> req) throws Exception {
		Response<PlaceOrderDto> resp = new Response<PlaceOrderDto>();
		resp.setData(new PlaceOrderDto());
		req.getData().setOrderOptType(OrderOptTypeEnum.ORDER_SUBMIT);
		RequestHandlerChain<PlaceOrderParamDto, PlaceOrderDto> handlerChain = null;
		switch (req.getData().getOrderType()) {
			case CVS_ORDER:
				handlerChain = submitOrderService;
				break;
			case SRV_ORDER:
				handlerChain = submitServOrderService;
				break;
			case SECKILL_ORDER:
				handlerChain = submitSeckillOrderService;
				break;
			default:
				break;
		}
		handlerChain.process(req, resp);
		resp.getData().setCurrentTime(System.currentTimeMillis());
		return resp;
	}
}
