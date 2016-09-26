package com.okdeer.mall.order.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderComplainImage;
import com.okdeer.mall.order.service.TradeOrderComplainImageServiceApi;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.order.mapper.TradeOrderComplainImageMapper;
import com.okdeer.mall.order.service.TradeOrderComplainImageService;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderComplainImageServiceApi")
class TradeOrderComplainImageServiceImpl implements TradeOrderComplainImageService, TradeOrderComplainImageServiceApi {

	@Resource
	private TradeOrderComplainImageMapper tradeOrderComplainImageMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(TradeOrderComplainImage tradeOrderComplainImage) throws ServiceException {
		// TODO Auto-generated method stub
		tradeOrderComplainImageMapper.insertSelective(tradeOrderComplainImage);
	}

	/**
	 * (non-Javadoc)
	 * @see com.okdeer.mall.order.service.TradeOrderComplainImageServiceApi#insertByBatch(java.util.List)
	 */
	@Override
	public int insertByBatch(List<TradeOrderComplainImage> tradeOrderComplainImageList) throws ServiceException {
		return tradeOrderComplainImageMapper.insertByBatch(tradeOrderComplainImageList);
	}
}