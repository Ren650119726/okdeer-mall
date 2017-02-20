package com.okdeer.mall.order.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.StringUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.order.bo.UserOrderDtoLoader;
import com.okdeer.mall.order.bo.UserOrderParamBo;
import com.okdeer.mall.order.dto.AppUserOrderDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
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
	
	/**
     * 第三方火车票订单API
     */
    @Reference(version = "1.0.0", check = false)
    private ThirdTrainOrderApi thirdTrainOrderApi;
	
	/**
	 * 火车票关键字
	 */
	private static final String TRAIN_KEYWORD = "火车";

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
		return loader.retrieveResult();
	}

	private PageUtils<ThirdTrainOrderDto> findTrainOrderList(UserOrderParamBo paramBo) throws Exception{
		PageUtils<ThirdTrainOrderDto> trainOrderList = new PageUtils<ThirdTrainOrderDto>(new ArrayList<ThirdTrainOrderDto>()); 
		// 获取请求参数的关键字
		String keyword = paramBo.getKeyword();
		if(StringUtils.isNotEmpty(keyword) && !keyword.contains(TRAIN_KEYWORD)){
			// 如果按照关键字搜索，且关键字中不包含火车字样，则不查询火车票列表记录
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
}
