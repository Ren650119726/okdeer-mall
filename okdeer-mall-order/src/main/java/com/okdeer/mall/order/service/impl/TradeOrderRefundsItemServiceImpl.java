package com.okdeer.mall.order.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderRefundsItem;
import com.okdeer.mall.order.service.TradeOrderRefundsItemServiceApi;
import com.okdeer.mall.order.mapper.TradeOrderRefundsItemMapper;
import com.okdeer.mall.order.service.TradeOrderRefundsItemService;

/**
 * 
 * 
 * @pr mall
 * @desc 售后订单项service
 * @author chenwj
 * @date 2016年4月1日 下午4:05:21
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderRefundsItemServiceApi")
public class TradeOrderRefundsItemServiceImpl implements TradeOrderRefundsItemService, TradeOrderRefundsItemServiceApi {

	private static final Logger logger = LoggerFactory.getLogger(TradeOrderRefundsItemServiceImpl.class);

	@Resource
	TradeOrderRefundsItemMapper tradeOrderRefundsItemMapper;

	@Override
	public List<TradeOrderRefundsItem> getTradeOrderRefundsItemByRefundsId(String refundsId) {
		return tradeOrderRefundsItemMapper.getTradeOrderRefundsItemByRefundsId(refundsId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int insert(List<TradeOrderRefundsItem> refundItems) {
		return tradeOrderRefundsItemMapper.insert(refundItems);
	}

}