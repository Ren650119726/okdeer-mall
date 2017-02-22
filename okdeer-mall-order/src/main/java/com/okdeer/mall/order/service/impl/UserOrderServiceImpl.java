package com.okdeer.mall.order.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.order.bo.UserOrderDtoLoader;
import com.okdeer.mall.order.bo.UserOrderParamBo;
import com.okdeer.mall.order.dto.AppUserOrderDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.mapper.TradeOrderLogisticsMapper;
import com.okdeer.mall.order.service.TradeOrderItemService;
import com.okdeer.mall.order.service.TradeOrderService;
import com.okdeer.mall.order.service.UserOrderService;
import com.okdeer.third.order.dto.ThirdOrderParamDto;
import com.okdeer.third.train.dto.ThirdTrainOrderDto;
import com.okdeer.third.train.service.ThirdTrainOrderApi;

@Service
public class UserOrderServiceImpl implements UserOrderService {
	
	@Resource
	private TradeOrderService tradeOrderService;
	
	@Resource
	private TradeOrderItemService tradeOrderItemService;
	
	@Resource
	private TradeOrderLogisticsMapper tradeOrderLogisticsMapper;
	
	/**
     * 第三方火车票订单API
     */
    @Reference(version = "1.0.0", check = false)
    private ThirdTrainOrderApi thirdTrainOrderApi;

	@Override
	public AppUserOrderDto findUserOrders(UserOrderParamBo paramBo) throws Exception {
		UserOrderDtoLoader loader = new UserOrderDtoLoader(paramBo.getPageSize());
	    // 查询便利店、服务店、充值订单
		PageUtils<TradeOrder> orderList = tradeOrderService.findUserOrders(paramBo);
		// 查询火车票订单
		PageUtils<ThirdTrainOrderDto> trainOrderList = findTrainOrderList(paramBo);
		// 装载便利店、服务店、充值订单列表
		loader.loadOrderList(orderList);
		// 装载火车票订单列表
		loader.loadTrainOrderList(trainOrderList);
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
		return loader.retrieveResult();
	}

	private PageUtils<ThirdTrainOrderDto> findTrainOrderList(UserOrderParamBo paramBo) throws Exception{
		PageUtils<ThirdTrainOrderDto> trainOrderList = new PageUtils<ThirdTrainOrderDto>(new ArrayList<ThirdTrainOrderDto>()); 
		// 获取请求参数的关键字
		String keyword = paramBo.getKeyword();
		if(StringUtils.isNotEmpty(keyword)){
			// 如果按照关键字搜索，则不查询火车票列表记录
			return trainOrderList;
		}
		if("3".equals(paramBo.getStatus())){
			// 火车票没有待评价订单，如果请求时待评价订单，则不查询火车票列表记录
			return trainOrderList;
		}
		ThirdOrderParamDto thirdParamDto = BeanMapper.map(paramBo, ThirdOrderParamDto.class);
		trainOrderList = thirdTrainOrderApi.findTrainOrderList(thirdParamDto);
		return trainOrderList;
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
		PageHelper.startPage(1, -1);
		PageUtils<TradeOrder> unPayOrderList = tradeOrderService.findUserOrders(paramBo);
		if (null != unPayOrderList) {
			unPaySum += unPayOrderList.getTotal();
		}
		// 查询火车票订单
		PageHelper.startPage(1, -1);
		PageUtils<ThirdTrainOrderDto> unPayTrainOrderList = findTrainOrderList(paramBo);
		if (null != unPayTrainOrderList) {
			unPaySum += unPayTrainOrderList.getTotal();
		}
		
		// 统计待使用
		paramBo.setStatus("2");
		// 查询便利店、服务店、充值订单
		PageHelper.startPage(1, -1);
		PageUtils<TradeOrder> unReceiptOrderList = tradeOrderService.findUserOrders(paramBo);
		if (null != unReceiptOrderList) {
			unReceiptSum += unReceiptOrderList.getTotal();
		}
		// 查询火车票订单
		PageHelper.startPage(1, -1);
		PageUtils<ThirdTrainOrderDto> unReceiptTrainOrderList = findTrainOrderList(paramBo);
		if (null != unReceiptTrainOrderList) {
			unReceiptSum += unReceiptTrainOrderList.getTotal();
		}
		
		// 统计待评价
		paramBo.setStatus("3");
		// 查询便利店、服务店、充值订单
		PageHelper.startPage(1, -1);
		PageUtils<TradeOrder> unAppraiseOrderList = tradeOrderService.findUserOrders(paramBo);
		if (null != unAppraiseOrderList) {
			unAppraiseSum += unAppraiseOrderList.getTotal();
		}
		// 查询火车票订单
		PageHelper.startPage(1, -1);
		PageUtils<ThirdTrainOrderDto> unAppraiseTrainOrderList = findTrainOrderList(paramBo);
		if (null != unAppraiseTrainOrderList) {
			unAppraiseSum += unAppraiseTrainOrderList.getTotal();
		}
		
		resultMap.put("unPay", String.valueOf(unPaySum));
		resultMap.put("unReceipt", String.valueOf(unReceiptSum));
		resultMap.put("unAppraise", String.valueOf(unAppraiseSum));
		return resultMap;
	}
}
