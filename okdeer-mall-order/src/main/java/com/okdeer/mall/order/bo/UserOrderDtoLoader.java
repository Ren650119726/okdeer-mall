package com.okdeer.mall.order.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.common.utils.EnumAdapter;
import com.okdeer.common.utils.ImageCutUtils;
import com.okdeer.common.utils.ImageTypeContants;
import com.okdeer.mall.common.enums.LogisticsType;
import com.okdeer.mall.order.dto.AppUserOrderDto;
import com.okdeer.mall.order.dto.BaseUserOrderItemDto;
import com.okdeer.mall.order.dto.UserOrderDto;
import com.okdeer.mall.order.dto.UserOrderItemDto;
import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderGroupRelation;
import com.okdeer.mall.order.entity.TradeOrderItem;
import com.okdeer.mall.order.entity.TradeOrderLogistics;
import com.okdeer.mall.order.enums.AppOrderTypeEnum;
import com.okdeer.mall.order.enums.OrderAppStatusAdaptor;
import com.okdeer.mall.order.enums.OrderResourceEnum;
import com.okdeer.mall.order.enums.OrderStatusEnum;
import com.okdeer.mall.order.enums.OrderTypeEnum;
import com.okdeer.mall.system.utils.ConvertUtil;

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
	 * 团购订单Id列表（只包括已经支付未成团的订单）
	 */
	private List<String> groupOrderIds = null;
		
	/**
	 * 页面大小
	 */
	private int pageSize;
	
	/**
	 * 总数量
	 */
	private int totalNum;
	
	
	public UserOrderDtoLoader(int pageSize){
		this.orderDtoList = Lists.newArrayList();
		this.orderDtoMap = Maps.newHashMap();
		this.orderIds = Lists.newArrayList();
		this.groupOrderIds = Lists.newArrayList();
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
		this.orderDtoList.addAll(this.orderDtoMap.values());
		Collections.sort(this.orderDtoList,new Comparator<UserOrderDto>() {
			@Override
			public int compare(UserOrderDto o1, UserOrderDto o2) {
				return o2.getCreateTime().compareTo(o1.getCreateTime());
			}
		});
		userOrderDto.setTotalNum(totalNum);
		userOrderDto.setTotalPage(totalPage);
		userOrderDto.setOrderList(this.orderDtoList);
		return userOrderDto; 
	}
	
	
	public List<String> extraOrderIds(){
		if(CollectionUtils.isEmpty(this.orderDtoList)){
			return null;
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
			//设置订单ID 
			orderDto.setOrderId(order.getId());
			orderDto.setStoreId(order.getStoreId());
			orderDto.setStoreName(order.getStoreName());
			orderDto.setTradeNum(order.getTradeNum());
			orderDto.setActivityType(String.valueOf(order.getActivityType().ordinal()));
			orderDto.setActivityId(order.getActivityId());
			orderDto.setActivityItemId(order.getActivityItemId());
			orderDto.setTotalAmount(ConvertUtil.format(order.getTotalAmount()));
			orderDto.setActualAmount(ConvertUtil.format(order.getActualAmount()));
			orderDto.setFare(ConvertUtil.format(order.getFare()));
			if(order.getOrderResource() == OrderResourceEnum.SWEEP){
				orderDto.setType(AppOrderTypeEnum.SWEEP_ORDER);
			
			//add by tuzhd start 2017-08-08 添加会员卡订单类型
			}else if(order.getOrderResource() == OrderResourceEnum.MEMCARD){
				orderDto.setType(AppOrderTypeEnum.MEMCARD_ORDER);
			//add by tuzhd end 2017-08-08 添加会员卡订单类型	
				
			}else{
				orderDto.setType(EnumAdapter.convert(order.getType()));
			}
			orderDto.setOrderResource(order.getOrderResource() !=null ? order.getOrderResource().ordinal():null);
			orderDto.setCreateTime(DateUtils.formatDate(order.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
			orderDto.setLogisticsFlag(String.valueOf(LogisticsType.NONE.ordinal()));
			orderDto.setLogisticsNo("");
			orderDto.setPickUpType(order.getPickUpType() == null ? "0" : String.valueOf(order.getPickUpType().ordinal()));
			
			// 设置返回给App的订单状态
			switch (order.getType()) {
				case PHYSICAL_ORDER:
				case SERVICE_ORDER:
				case SERVICE_STORE_ORDER:
				case SERVICE_EXPRESS_ORDER:
					orderDto.setOrderStatus(OrderAppStatusAdaptor.convertAppOrderStatus(order.getStatus()));
					break;
				case PHONE_PAY_ORDER:
				case TRAFFIC_PAY_ORDER:
					orderDto.setOrderStatus(order.getStatus().ordinal());
					break;
				case STORE_CONSUME_ORDER:
					orderDto.setOrderStatus(OrderAppStatusAdaptor
							.convertAppStoreConsumeOrderStatus(order.getStatus(), order.getConsumerCodeStatus())
							.ordinal());
					break;
				case GROUP_ORDER:
					// 如果是团购订单.已付款待发货状态，显示为：已付款
					if(order.getStatus() == OrderStatusEnum.DROPSHIPPING){
						orderDto.setOrderStatus(OrderStatusEnum.PAY_COMPLETE.ordinal());
					}else{
						orderDto.setOrderStatus(OrderAppStatusAdaptor.convertAppOrderStatus(order.getStatus()));
					}
					break;
				default:
					break;
			}
			
			// 设置订单是否评论.0:未评论，1：已评论
			orderDto.setOrderIsComment(order.getTotalComment() == 0 ? "0" : "1");
			this.orderIds.add(order.getId());
			this.orderDtoList.add(orderDto);
			this.orderDtoMap.put(order.getId(), orderDto);
			if(order.getType() == OrderTypeEnum.GROUP_ORDER && order.getStatus() == OrderStatusEnum.DROPSHIPPING){
				this.groupOrderIds.add(order.getId());
			}
		}
	}
	
	/**
	 * @Description: 装载订单项列表
	 * @param orderItemList   
	 * @author maojj
	 * @date 2017年2月18日
	 */
	public void loadOrderItemList(List<TradeOrderItem> orderItemList,String orderImagePrefix,String screen){
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
			if(StringUtils.isEmpty(orderItem.getMainPicPrl())){
				itemDto.setMainPicUrl("");
			}else{
				itemDto.setMainPicUrl(ImageCutUtils.changeType(ImageTypeContants.BLDDPLBDPTP, String.format("%s%s", orderImagePrefix,orderItem.getMainPicPrl()), screen));
			}
			itemDto.setStoreSkuId(orderItem.getStoreSkuId());
			itemDto.setSkuName(orderItem.getSkuName());
			itemDto.setPropertiesIndb(ConvertUtil.format(orderItem.getPropertiesIndb()));
			itemDto.setQuantity(orderItem.getQuantity()==null ? 0 : orderItem.getQuantity().intValue());
			itemDto.setQuantityStr(orderItem.getQuantity()==null ? 
					ConvertUtil.format(orderItem.getWeight()) : String.valueOf(orderItem.getQuantity()));
			if(orderDto.getType() == AppOrderTypeEnum.GROUP_ORDER){
				// 如果团购订单类型，商品价格显示为实际支付的价格。团购只能单件购买
				itemDto.setUnitPrice(ConvertUtil.format(orderItem.getActualAmount()));
			}else{
				itemDto.setUnitPrice(ConvertUtil.format(orderItem.getUnitPrice()));
			}
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

	// Begin V2.6.3 added by maojj 2017-10-19
	public void loadGroupRelList(List<TradeOrderGroupRelation> groupRelList ,String groupShareLink){
		if(CollectionUtils.isEmpty(groupRelList)){
			return;
		}
		groupRelList.forEach(groupRel -> {
			UserOrderDto orderDto = this.orderDtoMap.get(groupRel.getOrderId());
			orderDto.setGroupShareUrl(String.format("%s%s?uid=", groupShareLink,groupRel.getGroupOrderId()));
			orderDto.setGroupOrderId(groupRel.getGroupOrderId());
		});
	}
	// End V2.6.3 added by maojj 2017-10-19
	
	public List<String> extraGroupOrderIds() {
		return this.groupOrderIds;
	}
	
}
