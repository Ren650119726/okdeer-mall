package com.okdeer.mall.order.pay.callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.mall.order.enums.OrderTypeEnum;

@Service
public class PayResultHandlerFactory {

	@Autowired
	private AbstractPayResultHandler physicalOrderPayHandler;
	
	@Autowired
	private AbstractPayResultHandler serviceOrderPayHandler;
	
	@Autowired
	private AbstractPayResultHandler storeConsumOrderPayHandler;
	
	@Autowired
	private AbstractPayResultHandler phoneOrderPayHandler;
	
	@Autowired
	private AbstractPayResultHandler trafficOrderPayHandler;
	
	public AbstractPayResultHandler getByOrderType(OrderTypeEnum orderType){
		AbstractPayResultHandler handler = null;
		switch (orderType) {
			case PHYSICAL_ORDER:
				handler = physicalOrderPayHandler;
				break;
			case SERVICE_STORE_ORDER:
				handler = serviceOrderPayHandler;			
				break;
			case PHONE_PAY_ORDER:
				handler = phoneOrderPayHandler;
				break;
			case TRAFFIC_PAY_ORDER:
				handler = trafficOrderPayHandler;	
				break;
			case STORE_CONSUME_ORDER:
				handler = storeConsumOrderPayHandler;
				break;
			default:
				break;
		}
		return handler;
	}
}
