package com.okdeer.mall.order.pay.callback;

import org.springframework.stereotype.Service;

import com.okdeer.mall.activity.coupons.enums.ActivityTypeEnum;
import com.okdeer.mall.common.utils.RandomStringUtil;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.enums.PickUpTypeEnum;
import com.okdeer.mall.order.timer.TradeOrderTimer;

@Service("physicalOrderPayHandler")
public class PhysicalOrderPayHandler extends AbstractPayResultHandler {
	
	@Override
	public void preProcessOrder(TradeOrder tradeOrder) throws Exception{
		// 实物订单，如果是到店自提，需要生成提货码
		if (tradeOrder.getPickUpType() == PickUpTypeEnum.TO_STORE_PICKUP) {
			tradeOrder.setPickUpCode(RandomStringUtil.getRandomInt(6));
		}
	}
	
	@Override
	public void sendTimerMessage(TradeOrder tradeOrder) throws Exception {
		// 发送计时消息
		if (tradeOrder.getPickUpType() == PickUpTypeEnum.TO_STORE_PICKUP){
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_take_goods_timeout, tradeOrder.getId());
		} else {
			tradeOrderTimer.sendTimerMessage(TradeOrderTimer.Tag.tag_confirm_timeout, tradeOrder.getId());
		}
		
	}

}
