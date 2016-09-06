package com.okdeer.mall.order.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderItemDetail;
import com.okdeer.mall.order.service.TradeOrderItemDetailServiceApi;
import com.okdeer.mall.order.mapper.TradeOrderItemDetailMapper;
import com.okdeer.mall.order.service.TradeOrderItemDetailService;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderItemDetailServiceApi")
class TradeOrderItemDetailServiceImpl implements TradeOrderItemDetailService, TradeOrderItemDetailServiceApi {

	@Resource
	private TradeOrderItemDetailMapper tradeOrderItemDetailMapper;

	/**
	 * @desc 根据订单项ID更新消费明细状态为退款
	 *
	 * @param orderItemId
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateStatusWithRefund(String orderItemId) {
		return tradeOrderItemDetailMapper.updateStatusWithRefund(orderItemId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int updateStatusWithExpire(String orderItemId) {
		return tradeOrderItemDetailMapper.updateStatusWithExpire(orderItemId);
	}

	/**
	 * @desc 查询订单明显
	 */
	@Override
	public List<TradeOrderItemDetail> selectByOrderItemId(String orderItemId) {
		return tradeOrderItemDetailMapper.selectByOrderItemId(orderItemId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertSelective(TradeOrderItemDetail itemDetail) throws Exception {
		tradeOrderItemDetailMapper.insertSelective(itemDetail);
	}

	@Override
	public List<TradeOrderItemDetail> selectByOrderItemById(String orderItemId) throws Exception {
		return tradeOrderItemDetailMapper.selectByOrderItemById(orderItemId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertBatch(List<TradeOrderItemDetail> itemDetailList) throws Exception {
		tradeOrderItemDetailMapper.insertBatch(itemDetailList);

	}

	/**
	 * 查询未消费数量
	 */
	@Override
	public int selectUnConsumerCount(String orderItemId) {
		return tradeOrderItemDetailMapper.selectUnConsumerCount(orderItemId);
	}
}