
package com.okdeer.mall.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.service.TradeOrderRefundsListener;
import com.okdeer.mall.order.timer.TradeOrderTimer;
import com.okdeer.mall.order.timer.constant.TimerMessageConstant.Tag;

@Service("sendTimerMessageProcess")
public class SendTimerMessageProcess implements TradeOrderRefundsListener {

	@Autowired
	private TradeOrderTimer tradeOrderTimer;

	@Override
	public void beforeTradeOrderRefundsCrteate(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		// do nothing
	}

	@Override
	public void afterTradeOrderRefundsCrteated(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		// do nothing
		Assert.notNull(tradeOrderRefundContext.getTradeOrderRefunds());
		sendTimerMessage(TradeOrderTimer.Tag.tag_refund_agree_timeout,
				tradeOrderRefundContext.getTradeOrderRefunds().getId());
	}


	@Override
	public void beforeTradeOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		// do nothing
	}

	@Override
	public void afterOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		// do nothing
		Assert.notNull(tradeOrderRefundContext.getTradeOrderRefunds());
		switch (tradeOrderRefundContext.getTradeOrderRefunds().getRefundsStatus()) {
			case WAIT_BUYER_RETURN_GOODS:
				//等待买家退货
				sendTimerMessage(TradeOrderTimer.Tag.tag_refund_cancel_by_agree_timeout,
						tradeOrderRefundContext.getTradeOrderRefunds().getId());
				break;
			case SELLER_REJECT_APPLY:
				//商家拒绝申请
				sendTimerMessage(TradeOrderTimer.Tag.tag_refund_cancel_by_refuse_apply_timeout,
						tradeOrderRefundContext.getTradeOrderRefunds().getId());
				break;
			case SELLER_REJECT_REFUND:
				//卖家拒绝退款
				sendTimerMessage(TradeOrderTimer.Tag.tag_refund_cancel_by_refuse_timeout,
						tradeOrderRefundContext.getTradeOrderRefunds().getId());
				break;
			case WAIT_SELLER_REFUND:
				//等待买家退款
				sendTimerMessage(TradeOrderTimer.Tag.tag_refund_confirm_timeout,
						tradeOrderRefundContext.getTradeOrderRefunds().getId());
				break;	
			default:
				break;
		}
		
	}
	
	private void sendTimerMessage(Tag tag, String id) throws MallApiException {
		// 自动同意申请计时消息
		try {
			tradeOrderTimer.sendTimerMessage(tag,
					id);
		} catch (Exception e) {
			throw new MallApiException(e);
		}
	}

}
