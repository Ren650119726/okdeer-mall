
package com.okdeer.mall.order.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.service.TradeOrderRefundsListener;

/**
 * ClassName: RechargeOrderProcess 
 * @Description: 充值订单处理
 * @author zengjizu
 * @date 2017年10月15日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service("rechargeOrderProcess")
public class RechargeOrderProcess implements TradeOrderRefundsListener {

	@Override
	public void beforeTradeOrderRefundsCrteate(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		Assert.notNull(tradeOrderRefundContext.getTradeOrder());
		if (!(tradeOrderRefundContext.getTradeOrder().getType() == OrderTypeEnum.PHONE_PAY_ORDER
				|| tradeOrderRefundContext.getTradeOrder().getType() == OrderTypeEnum.TRAFFIC_PAY_ORDER)) {
			return;
		}
		if (tradeOrderRefundContext.getTradeOrderRefunds() != null) {
			TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
			tradeOrderRefunds.setRefundsStatus(RefundsStatusEnum.SELLER_REFUNDING);
		}
	}

	@Override
	public void afterTradeOrderRefundsCrteated(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		// do nothing

	}

	@Override
	public void beforeTradeOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext)
			throws MallApiException {
		// do nothing

	}

	@Override
	public void afterOrderRefundsChanged(TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		// do nothing

	}

}
