package com.okdeer.mall.order.service;

import com.okdeer.mall.order.entity.TradeOrderComplainImage;
import com.yschome.base.common.exception.ServiceException;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderComplainImageService {

	/***
	 * 插入投诉单图片
	 * @param tradeOrderComplainImage
	 * @throws ServiceException
	 */
	void update(TradeOrderComplainImage tradeOrderComplainImage) throws ServiceException;
}