package com.okdeer.mall.order.pay.callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.OrderResourceEnum;
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
	
	//start add by zengjz 2017-3-24 增加扫码购支付成功后处理
	@Autowired
	private ScanOrderPayHandler scanOrderPayHandler;
	//end add by zengjz 2017-3-24 增加扫码购支付成功后处理
	
	//start add by tuzhd 2017-8-8 增加会员卡支付成功后处理
	@Autowired
	private MemberCardOrderPayHandler memberCardOrderPayHandler;
	//end add by tuzhd 2017-8-8 增加会员卡支付成功后处理
	
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
	
	/**
	 * @Description: 根据订单信息获取处理类
	 * @param tradeOrder 订单信息
	 * @return
	 * @author zengjizu
	 * @date 2017年3月24日
	 */
	public AbstractPayResultHandler getByOrder(TradeOrder tradeOrder){
		AbstractPayResultHandler handler = null;
		if( OrderTypeEnum.PHYSICAL_ORDER == tradeOrder.getType() && (
				OrderResourceEnum.SWEEP == tradeOrder.getOrderResource() ||
				OrderResourceEnum.MEMCARD == tradeOrder.getOrderResource() )){
			//如果是扫码够或会员卡订单使用其对应handler tuzhd 修改 2018-08-08
			handler = getByOrderResource(tradeOrder.getOrderResource());
		}else{
			//根据订单类型来获取handler
			handler = getByOrderType(tradeOrder.getType());
		}
		return handler;
	}
	
	/**
	 * @Description: 实物订单 根据 订单来源分扫码购及会员卡支付
	 * @param orderResource 订单来源
	 * @return AbstractPayResultHandler  
	 * @author tuzhd
	 * @date 2017年8月8日
	 */
	public AbstractPayResultHandler getByOrderResource(OrderResourceEnum orderResource){
		AbstractPayResultHandler handler = null;
		switch (orderResource) {
			case SWEEP:
				handler = scanOrderPayHandler;
				break;
			case MEMCARD:
				handler = memberCardOrderPayHandler;			
				break;
			default:
				break;
		}
		return handler;
	}
}
