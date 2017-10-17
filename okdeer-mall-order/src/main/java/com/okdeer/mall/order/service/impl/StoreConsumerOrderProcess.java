
package com.okdeer.mall.order.service.impl;


import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.entity.TradeOrderRefunds;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.entity.TradeOrderRefundsItemDetail;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.order.enums.RefundsStatusEnum;
import com.okdeer.mall.order.mapper.TradeOrderRefundsItemDetailMapper;
import com.okdeer.mall.order.service.TradeOrderRefundsListener;

@Service("storeConsumerOrderProcess")
public class StoreConsumerOrderProcess implements TradeOrderRefundsListener {

	@Autowired
	private TradeOrderRefundsItemDetailMapper tradeOrderRefundsItemDetailMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void beforeTradeOrderRefundsCrteate(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			TradeOrderRefundContextBo tradeOrderRefundContext) throws MallApiException {
		Assert.notNull(tradeOrderRefundContext.getTradeOrder());
		if (tradeOrderRefundContext.getTradeOrder().getType() != OrderTypeEnum.STORE_CONSUME_ORDER) {
			return;
		}
		if (tradeOrderRefundContext.getTradeOrderRefunds() != null) {
			TradeOrderRefunds tradeOrderRefunds = tradeOrderRefundContext.getTradeOrderRefunds();
			tradeOrderRefunds.setRefundsStatus(RefundsStatusEnum.SELLER_REFUNDING);
//			tradeOrderRefunds.setRefundsReason("消费码未消费退款");
//			tradeOrderRefunds.setMemo("消费码未消费退款");
		}
		//添加退款单明细
		addTradeOrderRefundsItemDetail(tradeOrderRefundContext);
	}

	private void addTradeOrderRefundsItemDetail(TradeOrderRefundContextBo tradeOrderRefundContext) {
		if (CollectionUtils.isNotEmpty(tradeOrderRefundContext.getTradeOrderRefundsItemList())
				&& CollectionUtils.isNotEmpty(tradeOrderRefundContext.getTradeOrderItemDetail())) {
			for (TradeOrderRefundsItem tradeOrderRefundsItem : tradeOrderRefundContext.getTradeOrderRefundsItemList()) {

				for (TradeOrderItemDetail tradeOrderItemDetail : tradeOrderRefundContext.getTradeOrderItemDetail()) {
					if (!tradeOrderItemDetail.getOrderItemId().equals(tradeOrderRefundsItem.getOrderItemId())) {
						break;
					}
					TradeOrderRefundsItemDetail tradeOrderRefundsItemDetail = new TradeOrderRefundsItemDetail();
					tradeOrderRefundsItemDetail.setId(UuidUtils.getUuid());
					tradeOrderRefundsItemDetail.setRefundItemId(tradeOrderRefundsItem.getId());
					tradeOrderRefundsItemDetail.setOrderItemDetailId(tradeOrderItemDetail.getId());
					tradeOrderRefundsItemDetailMapper.add(tradeOrderRefundsItemDetail);
				}
			}
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
