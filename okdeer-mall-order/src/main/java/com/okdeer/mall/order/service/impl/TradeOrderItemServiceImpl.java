package com.okdeer.mall.order.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
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
	
	/**
	 * @Description: 根据日期查询便利店线上完成订单商品项数据 
	 * @param startDate 起始时间 
	 * @param endDate 结束时间
	 * @return List<TradeOrderItem>  
	 * @author tuzhd
	 * @date 2017年5月17日
	 */
	public List<TradeOrderItem> findOrderItemByDaild(String startDate,String endDate){
		Map<String, Object> map = new HashMap<String,Object>();
		//完成订单状态
		map.put("orderStatus", OrderStatusEnum.HAS_BEEN_SIGNED);
		List<OrderResourceEnum> list =  new ArrayList<OrderResourceEnum>();
		list.add(OrderResourceEnum.YSCAPP); 
		list.add(OrderResourceEnum.WECHAT);
		list.add(OrderResourceEnum.CVSAPP);
		//线上订单来源
		map.put("orderResource", list); 
		//实物订单
		map.put("orderType", OrderTypeEnum.PHYSICAL_ORDER); 
		map.put("orderStartDate", startDate);
		map.put("orderEndDate", endDate);
		return tradeOrderItemMapper.findCompletedOrderItem(map);
	}

}