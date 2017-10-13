package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.order.mapper.TradeOrderGroupMapper;
import com.okdeer.mall.order.service.TradeOrderGroupService;

@Service
public class TradeOrderGroupServiceImpl extends BaseServiceImpl implements TradeOrderGroupService {

	@Resource
	private TradeOrderGroupMapper tradeOrderGroupMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return tradeOrderGroupMapper;
	}

}
