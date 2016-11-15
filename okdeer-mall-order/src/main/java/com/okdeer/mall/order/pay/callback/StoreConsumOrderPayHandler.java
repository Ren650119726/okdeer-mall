package com.okdeer.mall.order.pay.callback;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.ConsumerCodeStatusEnum;
import com.okdeer.mall.order.service.TradeOrderService;

@Service("storeConsumOrderPayHandler")
public class StoreConsumOrderPayHandler extends AbstractPayResultHandler{
	
	@Resource
	private TradeOrderService tradeOrderService;
	
	@Override
	public void preProcessOrder(TradeOrder tradeOrder) throws Exception{
		tradeOrder.setUpdateTime(new Date());
		tradeOrder.setConsumerCodeStatus(ConsumerCodeStatusEnum.WAIT_CONSUME);
		// 增加回款时间
		tradeOrder.setPaymentTime(new Date());
	}
	
	@Override
	public void processOrderItem(TradeOrder tradeOrder) throws Exception{
		tradeOrderService.dealWithStoreConsumeOrder(tradeOrder);
	}
	
}
