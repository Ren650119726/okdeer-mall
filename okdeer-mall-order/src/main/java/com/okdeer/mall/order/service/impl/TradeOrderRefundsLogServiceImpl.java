
package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderRefundsLog;
import com.okdeer.mall.order.mapper.TradeOrderRefundsLogMapper;
import com.okdeer.mall.order.service.TradeOrderRefundsLogServiceApi;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderRefundsLogServiceApi")
class TradeOrderRefundsLogServiceImpl implements TradeOrderRefundsLogServiceApi {

	/** * 退款单操作记录Mapper */
	@Resource
	private TradeOrderRefundsLogMapper tradeOrderRefundsLogMapper;

	/**
	 * 
	 * @Description: 保存退款单操作记录
	 * @param tradeOrderRefundsLog 退款单操作记录
	 * @author zengj
	 * @date 2016年9月5日
	 */
	public void insertSelective(TradeOrderRefundsLog tradeOrderRefundsLog) {
		tradeOrderRefundsLogMapper.insertSelective(tradeOrderRefundsLog);
	}

}