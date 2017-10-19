package com.okdeer.mall.order.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.order.bo.UserOrderDtoLoader;
import com.okdeer.mall.order.bo.UserOrderParamBo;
import com.okdeer.mall.order.dto.AppUserOrderDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderGroupRelation;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.mapper.TradeOrderLogisticsMapper;
import com.okdeer.mall.order.service.TradeOrderGroupRelationService;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.service.UserOrderService;
import com.okdeer.mall.util.SysConfigComponent;

@Service
public class UserOrderServiceImpl implements UserOrderService {
	
	@Resource
	private TradeOrderService tradeOrderService;
	
	@Resource
	private TradeOrderItemService tradeOrderItemService;
	
	@Resource
	private TradeOrderLogisticsMapper tradeOrderLogisticsMapper;
	
	@Resource
	private SysConfigComponent sysConfigComponent;
	
	@Resource
	private TradeOrderGroupRelationService tradeOrderGroupRelationService;
	
	@Override
	public AppUserOrderDto findUserOrders(UserOrderParamBo paramBo) throws Exception {
		UserOrderDtoLoader loader = new UserOrderDtoLoader(paramBo.getPageSize());
	    // 查询便利店、服务店、充值订单
		PageUtils<TradeOrder> orderList = tradeOrderService.findUserOrders(paramBo);
		// 装载便利店、服务店、充值订单列表
		loader.loadOrderList(orderList);
		// 提取便利店、服务店、充值订单ID
		List<String> orderIds = loader.extraOrderIds();
		//　根据订单Id查询订单项明细
		List<TradeOrderItem> orderItemList = tradeOrderItemService.findOrderItems(orderIds);
		// 装载订单项列表
		loader.loadOrderItemList(orderItemList);
		// 根据订单Id查询物流信息
		if(CollectionUtils.isNotEmpty(orderIds)){
			List<TradeOrderLogistics> orderLogisticsList = tradeOrderLogisticsMapper.selectByOrderIds(orderIds);
			// 装载物流信息列表
			loader.loadOrderLogisticsList(orderLogisticsList);
		}
		// 提取参与团购未成团的订单id
		List<String> groupOrderIds = loader.extraGroupOrderIds();
		if(CollectionUtils.isNotEmpty(groupOrderIds)){
			List<TradeOrderGroupRelation> groupRelList = tradeOrderGroupRelationService.findByOrderIds(groupOrderIds);
			// 装载团购订单信息
			loader.loadGroupRelList(groupRelList, sysConfigComponent.getGroupShareLink());
		}
		return loader.retrieveResult();
	}

	@Override
	public Map<String, Object> countUserOrders(String userId) throws Exception {
		// 统计结果集合
		Map<String, Object> resultMap = new HashMap<>();
		// 待支付标识
		long unPaySum = 0;
		// 待收货标识
		long unReceiptSum = 0;
		// 待评价标识
		long unAppraiseSum = 0;
		
		// 统计待付款
		UserOrderParamBo paramBo = new UserOrderParamBo();
		paramBo.setUserId(userId);
		paramBo.setStatus("1");
		// 查询便利店、服务店、充值订单
		unPaySum += tradeOrderService.countUserOrders(paramBo);
		
		// 统计待使用
		paramBo.setStatus("2");
		// 查询便利店、服务店、充值订单
		unReceiptSum += tradeOrderService.countUserOrders(paramBo);
		
		// 统计待评价， 火车票不存在待评价状态
		paramBo.setStatus("3");
		// 查询便利店、服务店、充值订单
		unAppraiseSum += tradeOrderService.countUserOrders(paramBo);
		
		resultMap.put("unPay", String.valueOf(unPaySum));
		resultMap.put("unReceipt", String.valueOf(unReceiptSum));
		resultMap.put("unAppraise", String.valueOf(unAppraiseSum));
		return resultMap;
	}
}
