package com.okdeer.mall.order.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.common.utils.EnumAdapter;
import com.okdeer.mall.common.enums.LogisticsType;
import com.okdeer.mall.order.dto.AppUserOrderDto;
import com.okdeer.mall.order.dto.BaseUserOrderItemDto;
import com.okdeer.mall.order.dto.UserOrderDto;
import com.okdeer.mall.order.dto.UserOrderItemDto;
import com.okdeer.mall.order.dto.UserTrainOrderItem;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.enums.AppOrderTypeEnum;
import com.okdeer.mall.order.enums.OrderAppStatusAdaptor;
import com.okdeer.mall.system.utils.ConvertUtil;
import com.okdeer.third.train.dto.ThirdTrainOrderDto;

/**
 * ClassName: UserOrderBoLoader 
 * @Description: 用户交易订单列表装载器
 * @author maojj
 * @date 2017年2月18日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.1 			2017年2月18日				maojj
 */
public class UserOrderDtoLoader {

	private List<UserOrderDto> orderDtoList = null;
	
	private Map<String,UserOrderDto> orderDtoMap = null;
	
	private List<String> orderIds = null;
	
	/**
	 * 页面大小
	 */
	private int pageSize;
	
	/**
	 * 总数量
	 */
	private int totalNum;
	
	
	public UserOrderDtoLoader(int pageSize){
		this.orderDtoList = new ArrayList<UserOrderDto>();
		this.orderDtoMap = new HashMap<String,UserOrderDto>();
		this.orderIds = new ArrayList<String>();
		this.pageSize = pageSize;
	}
	
	/**
	 * @Description: 提取结果
	 * @return   
	 * @author maojj
	 * @date 2017年2月20日
	 */
	public AppUserOrderDto retrieveResult(){
		AppUserOrderDto userOrderDto = new AppUserOrderDto();
		int totalPage = totalNum/pageSize + (totalNum%pageSize == 0 ? 0 : 1);
		this.orderDtoList = new ArrayList<UserOrderDto>();
		for(Map.Entry<String, UserOrderDto> entry : this.orderDtoMap.entrySet()){
			this.orderDtoList.add(entry.getValue());
		}
		userOrderDto.setTotalNum(totalNum);
		userOrderDto.setTotalPage(totalPage);
		userOrderDto.setOrderList(this.orderDtoList);
		return userOrderDto; 
	}
	
	
	public List<String> extraOrderIds(){
		if(CollectionUtils.isEmpty(this.orderDtoList)){
			return this.orderIds;
		}
		Collections.sort(this.orderDtoList,new Comparator<UserOrderDto>() {
			@Override
			public int compare(UserOrderDto o1, UserOrderDto o2) {
				return o1.getCreateTime().compareTo(o2.getCreateTime());
			}
		});
		if(this.orderDtoList.size() > this.pageSize){
			this.orderDtoList = this.orderDtoList.subList(0, this.pageSize);
			// 刷新订单ID和map
			this.orderIds = new ArrayList<String>();
			this.orderDtoMap = new HashMap<String,UserOrderDto>();
			for(UserOrderDto orderDto : this.orderDtoList){
				if(orderDto.getType() != AppOrderTypeEnum.TRAIN_ORDER){
					this.orderIds.add(orderDto.getOrderId());
				}
				this.orderDtoMap.put(orderDto.getOrderId(), orderDto);
			}
		}
		return this.orderIds;
	}
	
	/**
	 * @Description: 装载订单列表
	 * @param orderList   
	 * @author maojj
	 * @date 2017年2月18日
	 */ 
	public void loadOrderList(PageUtils<TradeOrder> orderList){
		List<TradeOrder> orderListTemp = orderList.getList();
		if(CollectionUtils.isEmpty(orderListTemp)){
			return;
		}
		// 总数量
		totalNum += orderList.getTotal();
		UserOrderDto orderDto = null;
		for(TradeOrder order : orderListTemp){
			orderDto = new UserOrderDto();
			orderDto.setStoreId(order.getStoreId());
			orderDto.setStoreName(order.getStoreName());
			orderDto.setOrderId(order.getId());
			orderDto.setTotalAmount(ConvertUtil.format(order.getTotalAmount()));
			orderDto.setActualAmount(ConvertUtil.format(order.getActualAmount()));
			orderDto.setFare(ConvertUtil.format(order.getFare()));
			orderDto.setType(EnumAdapter.convert(order.getType()));;
			orderDto.setCreateTime(DateUtils.formatDate(order.getCreateTime()));
			orderDto.setLogisticsFlag(String.valueOf(LogisticsType.NONE.ordinal()));
			orderDto.setLogisticsNo("");
			orderDto.setPickUpType(order.getPickUpType() == null ? "0" : String.valueOf(order.getPickUpType().ordinal()));
			
			// 设置返回给App的订单状态
			switch (order.getType()) {
				case PHYSICAL_ORDER:
				case SERVICE_ORDER:
				case SERVICE_STORE_ORDER:
					orderDto.setOrderStatus(OrderAppStatusAdaptor.convertAppOrderStatus(order.getStatus()));
					break;
				case PHONE_PAY_ORDER:
				case TRAFFIC_PAY_ORDER:
					orderDto.setOrderStatus(order.getStatus().ordinal());
				case STORE_CONSUME_ORDER:
					orderDto.setOrderStatus(OrderAppStatusAdaptor.convertAppStoreConsumeOrderStatus(order.getStatus(), order.getConsumerCodeStatus()).ordinal());
				default:
					break;
			}
			
			// 设置订单是否评论.0:未评论，1：已评论
			orderDto.setOrderIsComment(order.getTotalComment() == 0 ? "0" : "1");
			this.orderIds.add(order.getId());
			this.orderDtoList.add(orderDto);
			this.orderDtoMap.put(order.getId(),orderDto);
		}
	}
	
	/**
	 * @Description: 加载火车票订单
	 * @param trainOrderList   
	 * @author maojj
	 * @date 2017年2月18日
	 */
	public void loadTrainOrderList(PageUtils<ThirdTrainOrderDto> trainOrderList){
		List<ThirdTrainOrderDto> trainOrderListTemp = trainOrderList.getList();
		if(CollectionUtils.isEmpty(trainOrderListTemp)){
			return;
		}
		// 总数量
		totalNum += trainOrderList.getTotal();
		UserOrderDto orderDto = null;
		List<BaseUserOrderItemDto> orderItems = null;
		UserTrainOrderItem orderItem = null;
		for(ThirdTrainOrderDto order : trainOrderListTemp){
			orderDto = new UserOrderDto();
			orderDto.setOrderId(order.getId());
			orderDto.setActualAmount(ConvertUtil.format(order.getActualAmount()));
			orderDto.setOrderStatus(order.getStatus());
			orderDto.setType(AppOrderTypeEnum.TRAIN_ORDER);
			orderDto.setCreateTime(DateUtils.formatDate(order.getCreateTime()));
			orderDto.setLogisticsFlag(String.valueOf(LogisticsType.NONE.ordinal()));
			orderDto.setLogisticsNo("");
			
			orderItems = new ArrayList<BaseUserOrderItemDto>();
			orderItem = new UserTrainOrderItem();
			orderItem.setFromStation(order.getFromStation());
			orderItem.setToStation(order.getToStation());
			orderItem.setDepartureTime(order.getDepartureTime().getTime());
			orderItem.setArrivalTime(order.getArrivalTime().getTime());
			orderItem.setTrainNo(order.getTrainNo());
			orderItems.add(orderItem);
			
			orderDto.setOrderItems(orderItems);
			
			this.orderDtoList.add(orderDto);
			this.orderDtoMap.put(order.getId(),orderDto);
		}
	}
	
	/**
	 * @Description: 装载订单项列表
	 * @param orderItemList   
	 * @author maojj
	 * @date 2017年2月18日
	 */
	public void loadOrderItemList(List<TradeOrderItem> orderItemList){
		if(CollectionUtils.isEmpty(orderItemList)){
			return;
		}
		UserOrderDto orderDto = null;
		UserOrderItemDto itemDto = null;
		for(TradeOrderItem orderItem : orderItemList){
			orderDto = this.orderDtoMap.get(orderItem.getOrderId());
			if(orderDto.getOrderItems() == null){
				orderDto.setOrderItems(new ArrayList<BaseUserOrderItemDto>());
			}
			itemDto = new UserOrderItemDto();
			itemDto.setItemId(orderItem.getId());
			itemDto.setMainPicUrl(orderItem.getMainPicPrl());
			itemDto.setSkuName(orderItem.getSkuName());
			itemDto.setPropertiesIndb(orderItem.getPropertiesIndb());
			itemDto.setUnitPrice(ConvertUtil.format(orderItem.getUnitPrice()));
			itemDto.setUnit(orderItem.getUnit());
			itemDto.setRechargePhone(orderItem.getRechargeMobile());
			
			orderDto.getOrderItems().add(itemDto);
		}
	}
	
	/**
	 * @Description: 加载物流列表
	 * @param orderLogisticsList   
	 * @author maojj
	 * @date 2017年2月20日
	 */
	public void loadOrderLogisticsList(List<TradeOrderLogistics> orderLogisticsList){
		if(CollectionUtils.isEmpty(orderLogisticsList)){
			return;
		}
		UserOrderDto orderDto = null;
		LogisticsType logisticsType = null;
		for(TradeOrderLogistics orderLogistics : orderLogisticsList){
			orderDto = this.orderDtoMap.get(orderLogistics.getOrderId());
			logisticsType = orderLogistics.getType();
			if(logisticsType == LogisticsType.HAS){
				// 如果有物流，返回标识为有物流的标识
				orderDto.setLogisticsFlag(String.valueOf(LogisticsType.HAS.ordinal()));
				orderDto.setLogisticsNo(orderLogistics.getLogisticsNo());
			}
		}
	}
}
