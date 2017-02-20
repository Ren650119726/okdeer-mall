package com.okdeer.mall.order.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.service.TradeOrderItemServiceApi;
import com.okdeer.mall.order.vo.TradeOrderItemDetailVo;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.service.TradeOrderItemService;

/**
 * @DESC: 
 * @author YSCGD
 * @date  2016-02-05 15:22:58
 * @version 1.0.0
 * @copyright ©2005-2020 yschome.com Inc. All rights reserved
 * 
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.TradeOrderItemServiceApi")
class TradeOrderItemServiceImpl implements TradeOrderItemService, TradeOrderItemServiceApi {

	@Resource
	private TradeOrderItemMapper tradeOrderItemMapper;

	@Override
	public void insertSelective(TradeOrderItem tradeOrderItem) throws ServiceException {
		tradeOrderItemMapper.insertSelective(tradeOrderItem);
	}

	@Override
	public List<TradeOrderItem> selectOrderItemByOrderId(String orderId) throws ServiceException {
		return tradeOrderItemMapper.selectTradeOrderItem(orderId);
	}

	/**
	 * @desc 根据订单id，查询订单项和订单项消费信息 
	 *
	 * @param orderId 订单id
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public List<TradeOrderItemDetailVo> getItemDetailByOrderId(String orderId) throws ServiceException {
		return tradeOrderItemMapper.selectItemDetailByOrderId(orderId);
	}

	@Override
	public int selectCountByOrderIdDetailStatus(Map<String, Object> params) throws ServiceException {

		return tradeOrderItemMapper.selectCountByOrderIdDetailStatus(params);
	}

	@Override
	public TradeOrderItem selectByPrimaryKey(String id) {
		return tradeOrderItemMapper.selectOrderItemById(id);
	}

	@Override
	public Integer findTradeOrderItemIsAppraise(String orderId) throws ServiceException {
		return tradeOrderItemMapper.selectTradeOrderItemIsAppraise(orderId);
	}

	@Override
	public void updateWithComplete(List<String> ids) {
		tradeOrderItemMapper.updateCompleteById(ids);
	}

	@Override
	public List<TradeOrderItem> findOrderItems(List<String> orderIds) {
		if(CollectionUtils.isEmpty(orderIds)){
			return null;
		}
		return tradeOrderItemMapper.findOrderItems(orderIds);
	}

}