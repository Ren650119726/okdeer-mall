
package com.okdeer.mall.order.api;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.exception.MallApiException;
import com.okdeer.mall.order.bo.PayInfo;
import com.okdeer.mall.order.dto.PayInfoDto;
import com.okdeer.mall.order.dto.PayInfoParamDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.service.TradeOrderPayService;
import com.okdeer.mall.order.service.TradeOrderPayServiceApi;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderPayServiceApi")
public class TradeOrderPayApiImpl implements TradeOrderPayServiceApi {

	@Autowired
	private TradeOrderPayService tradeOrderPayService;

	@Override
	public boolean wlletPay(String orderMoney, TradeOrder order) throws MallApiException {
		try {
			return tradeOrderPayService.wlletPay(orderMoney, order);
		} catch (Exception e) {
			throw new MallApiException(e);
		}
	}
	
	@Override
	public PayInfoDto getPayInfo(PayInfoParamDto payInfoParamDto) throws MallApiException {
		try {
			PayInfo payInfo = tradeOrderPayService.getPayInfo(payInfoParamDto);
			return BeanMapper.map(payInfo, PayInfoDto.class);
		} catch (Exception e) {
			throw new MallApiException(e);
		}
	}

}
