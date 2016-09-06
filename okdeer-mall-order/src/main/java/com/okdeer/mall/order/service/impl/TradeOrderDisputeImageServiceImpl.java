package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderDisputeImage;
import com.okdeer.mall.order.service.TradeOrderDisputeImageServiceApi;
import com.yschome.base.common.exception.ServiceException;
import com.okdeer.mall.order.mapper.TradeOrderDisputeImageMapper;
import com.okdeer.mall.order.service.TradeOrderDisputeImageService;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderDisputeImageServiceApi")
class TradeOrderDisputeImageServiceImpl implements TradeOrderDisputeImageService, TradeOrderDisputeImageServiceApi {

	@Resource
	private TradeOrderDisputeImageMapper tradeOrderDisputeImageMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(TradeOrderDisputeImage tradeOrderDisputeImage) throws ServiceException {
		// TODO Auto-generated method stub
		tradeOrderDisputeImageMapper.insertSelective(tradeOrderDisputeImage);
	}
}