
package com.okdeer.mall.order.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.okdeer.archive.store.enums.ResultCodeEnum;
import com.okdeer.mall.common.dto.Response;
import com.okdeer.mall.order.bo.TradeOrderRefundContextBo;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundParamDto;
import com.okdeer.mall.order.dto.TradeOrderApplyRefundResultDto;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.enums.ConsumeStatusEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemDetailMapper;

@Service("storeConsumeOrderRefundProcessService")
public class StoreConsumeOrderRefundProcessService extends AbstractTradeOrderRefundProcessService {

	@Autowired
	private TradeOrderItemDetailMapper tradeOrderItemDetailMapper;
	
	
	@Override
	public boolean checkApplyRefund(TradeOrderApplyRefundParamDto tradeOrderApplyRefundParamDto,
			Response<TradeOrderApplyRefundResultDto> response, TradeOrderRefundContextBo tradeOrderRefundContext) {
		boolean result = super.checkApplyRefund(tradeOrderApplyRefundParamDto, response, tradeOrderRefundContext);
		if (!result) {
			return false;
		}
		List<String> consumerIds = tradeOrderApplyRefundParamDto.getConsumerIds();
		Assert.notEmpty(consumerIds);
		
		List<TradeOrderItemDetail> waitRefundDetailList = Lists.newArrayList();
		int invalidCount = 0;
		// 已消费的消费码id集合
		for (String detailId : consumerIds) {
			TradeOrderItemDetail tradeOrderItemDetail = tradeOrderItemDetailMapper.selectByPrimaryKey(detailId);
			if (!tradeOrderItemDetail.getOrderItemId().equals(tradeOrderApplyRefundParamDto.getOrderItemId())) {
				// 不是该订单项的id则不处理
				response.setResult(ResultCodeEnum.ILLEGAL_PARAM);
				return false;
			}
			if (tradeOrderItemDetail.getStatus() != ConsumeStatusEnum.noConsume) {
				// 如果状态不是未消费，invalidCount＋1；
				invalidCount++;
			}
			waitRefundDetailList.add(tradeOrderItemDetail);
		}
		
		tradeOrderRefundContext.setTradeOrderItemDetail(waitRefundDetailList);
		if (invalidCount > 0) {
			// 判断失效数量
			if (invalidCount == consumerIds.size()) {
				// 如果消费码全部失效，返回特殊状态,方便app端做判断跳转页面
				response.setResult(ResultCodeEnum.CONSUME_CODE_INVALID);
				return false;
			} else {
				// 部分失效
				response.setResult(ResultCodeEnum.CONSUME_CODE_ANY_INVALID);
				return false;
			}
		}
		return true;
	}

}
