package com.okdeer.mall.order.service.impl;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderDispute;
import com.okdeer.mall.order.service.TradeOrderDisputeServiceApi;
import com.yschome.base.common.exception.ServiceException;
import com.okdeer.mall.order.mapper.TradeOrderDisputeLogMapper;
import com.okdeer.mall.order.mapper.TradeOrderDisputeMapper;
import com.okdeer.mall.order.mapper.TradeOrderRefundsMapper;
import com.okdeer.mall.order.service.TradeOrderDisputeService;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName="com.okdeer.mall.order.service.TradeOrderDisputeServiceApi")
class TradeOrderDisputeServiceImpl implements TradeOrderDisputeService, TradeOrderDisputeServiceApi {

	@Resource
	private TradeOrderDisputeMapper tradeOrderDisputeMapper;

	@Resource
	private TradeOrderDisputeLogMapper tradeOrderDisputeLogMapper;

	@Resource
	private TradeOrderRefundsMapper tradeOrderRefundsMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.okdeer.mall.trade.order.serivce.TradeOrderDisputeService#
	 * updateApplyDispute(com.okdeer.mall.trade.order.entity.TradeOrderDispute)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateApplyDispute(TradeOrderDispute tradeOrderDispute) throws ServiceException {
		tradeOrderDisputeLogMapper.insertSelective(tradeOrderDispute.getTradeOrderDisputeLog());
		tradeOrderDisputeMapper.insertDispute(tradeOrderDispute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.okdeer.mall.trade.order.serivce.TradeOrderDisputeService#
	 * updateCancelDispute
	 * (com.okdeer.mall.trade.order.entity.TradeOrderDispute)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateCancelDispute(TradeOrderDispute tradeOrderDispute) throws ServiceException {
		tradeOrderDisputeLogMapper.insertSelective(tradeOrderDispute.getTradeOrderDisputeLog());
		tradeOrderDisputeMapper.updateDispute(tradeOrderDispute);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.okdeer.mall.trade.order.serivce.TradeOrderDisputeService#
	 * updateForceRefund(com.okdeer.mall.trade.order.entity.TradeOrderDispute)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateForceRefund(TradeOrderDispute tradeOrderDispute) throws ServiceException {

		tradeOrderDisputeLogMapper.insertSelective(tradeOrderDispute.getTradeOrderDisputeLog());

		/**
		 * to do
		 * 1、查询该纠纷单所关联的退款单的信息
		 * 2、查询所关联的买家与卖家支付交易条件
		 * 3、原路返回
		 */
		boolean flag = true;
		if (flag) { // 如果金额原路返回成功
			tradeOrderDisputeMapper.updateDispute(tradeOrderDispute);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.okdeer.mall.trade.order.serivce.TradeOrderDisputeService#updateYscRefund
	 * (com.okdeer.mall.trade.order.entity.TradeOrderDispute)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateYscRefund(TradeOrderDispute tradeOrderDispute) throws ServiceException {
		tradeOrderDisputeLogMapper.insertSelective(tradeOrderDispute.getTradeOrderDisputeLog());

		/**
		 * to do
		 * 1、查询该纠纷单关联的退款单信息
		 * 2、查询需要产生交易的条件
		 * 3、原路返回
		 */
		boolean flag = true;
		if (flag) { // 如果金额返回成功
			tradeOrderDisputeMapper.updateDispute(tradeOrderDispute);
		}

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateByApplyDispute(TradeOrderDispute tradeOrderDispute) throws ServiceException {
		// TODO Auto-generated method stub
		tradeOrderDisputeMapper.insertDispute(tradeOrderDispute);
	}

	@Override
	public TradeOrderDispute findById(String id) throws ServiceException {
		// TODO Auto-generated method stub
		return tradeOrderDisputeMapper.selectByPrimaryKey(id);
	}
}