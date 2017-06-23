package com.okdeer.mall.order.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseCrudMapper;
import com.okdeer.mall.order.entity.TradeOrderRefundsImage;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
public interface TradeOrderRefundsImageMapper extends IBaseCrudMapper {
	
	public List<TradeOrderRefundsImage> findByRefundsId(String refundsId);
}