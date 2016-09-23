package com.okdeer.mall.order.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.okdeer.mall.common.consts.Constant;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.enums.ConsumerCodeStatusEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.mapper.TradeOrderItemMapper;
import com.okdeer.mall.order.mapper.TradeOrderMapper;
import com.okdeer.mall.order.service.StoreConsumeOrderServiceApi;
import com.yschome.base.common.utils.PageUtils;

/**
 * ClassName: ConsumerCodeOrderServiceImpl 
 * @Description: 到店消费接口实现类
 * @author zengjizu
 * @date 2016年9月20日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     v1.1.0            2016-9-20          zengjz			  增加查询消费码订单列表
 */
@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.StoreConsumeOrderServiceApi")
public class StoreConsumeOrderServiceImpl implements StoreConsumeOrderServiceApi {

	@Autowired
	private TradeOrderMapper tradeOrderMapper;

	@Autowired
	private TradeOrderItemMapper tradeOrderItemMapper;

	@Override
	public PageUtils<TradeOrder> findStoreConsumeOrderList(Map<String, Object> map, Integer pageNo, Integer pageSize) {
		PageHelper.startPage(pageNo, pageSize, true, false);
		
		String status = (String) map.get("status");
		List<TradeOrder> list = new ArrayList<TradeOrder>();

		if (status != null) {
			// 订单状态
			List<String> orderStatus = new ArrayList<String>();
			
			if (status.equals(String.valueOf(Constant.ONE))) {
				// 查询未支付的消费码订单
				orderStatus.add(String.valueOf(OrderStatusEnum.UNPAID.ordinal()));
				orderStatus.add(String.valueOf(OrderStatusEnum.BUYER_PAYING.ordinal()));
				map.put("orderStatus", orderStatus);
			} else if (status.equals(String.valueOf(Constant.TWO))) {
				// 查询已经取消的消费码订单
				orderStatus.add(String.valueOf(OrderStatusEnum.CANCELED.ordinal()));
				orderStatus.add(String.valueOf(OrderStatusEnum.CANCELING.ordinal()));
				map.put("orderStatus", orderStatus);
			} else if (status.equals(String.valueOf(Constant.THIRTH))) {
				// 查询未消费的消费码订单
				map.put("orderStatus", OrderStatusEnum.HAS_BEEN_SIGNED.ordinal());
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.WAIT_CONSUME.ordinal());
			} else if (status.equals(String.valueOf(Constant.FOUR))) {
				// 查询已过期的消费码订单
				map.put("orderStatus", OrderStatusEnum.HAS_BEEN_SIGNED.ordinal());
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.EXPIRED.ordinal());
			} else if (status.equals(String.valueOf(Constant.FIVE))) {
				// 查询已消费的消费码订单
				map.put("orderStatus", OrderStatusEnum.HAS_BEEN_SIGNED.ordinal());
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.WAIT_EVALUATE.ordinal());
			} else if (status.equals(String.valueOf(Constant.SIX))) {
				// 查询已退款的消费码订单
				map.put("orderStatus", OrderStatusEnum.HAS_BEEN_SIGNED.ordinal());
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.REFUNDED.ordinal());
			} else if (status.equals(String.valueOf(Constant.SEVEN))) {
				// 查询已完成的消费码订单
				map.put("orderStatus", OrderStatusEnum.HAS_BEEN_SIGNED.ordinal());
				map.put("consumerCodeStatus", ConsumerCodeStatusEnum.COMPLETED.ordinal());
			}
			
			list = tradeOrderMapper.selectStoreConsumeOrderList(map);
		}

		for (TradeOrder vo : list) {
			List<TradeOrderItem> items = tradeOrderItemMapper.selectTradeOrderItem(vo.getId());
			vo.setTradeOrderItem(items);
		}
		return new PageUtils<TradeOrder>(list);
	}

}
