package com.okdeer.mall.order.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.okdeer.mall.order.entity.TradeOrder;
import com.okdeer.mall.order.entity.TradeOrderItem;

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
public class UserOrderBoLoader {

	private List<UserOrderBo> orderBoList = null;
	
	private Map<String,UserOrderBo> orderBoMap = null;
	
	private List<String> orderIds = null;
	
	public UserOrderBoLoader(){
		this.orderBoList = new ArrayList<UserOrderBo>();
		this.orderBoMap = new HashMap<String,UserOrderBo>();
		this.orderIds = new ArrayList<String>();
	}
	
	public List<UserOrderBo> retrieveResult(){
		this.orderBoList.addAll(this.orderBoMap.values());
		return this.orderBoList; 
	}
	
	/**
	 * @Description: 装载订单列表
	 * @param orderList   
	 * @author maojj
	 * @date 2017年2月18日
	 */
	public void loadOrderList(List<TradeOrder> orderList){
		UserOrderBo orderBo = null;
		for(TradeOrder order : orderList){
			orderBo = new UserOrderBo();
			orderBo.setTradeOrder(order);
			this.orderBoMap.put(order.getId(), orderBo);
			this.orderIds.add(order.getId());
		}
	}
	
	/**
	 * @Description: 装载订单项列表
	 * @param orderItemList   
	 * @author maojj
	 * @date 2017年2月18日
	 */
	public void loadOrderItemList(List<TradeOrderItem> orderItemList){
		UserOrderBo orderBo = null;
		for(TradeOrderItem orderItem : orderItemList){
			orderBo = this.orderBoMap.get(orderItem.getOrderId());
			if(orderBo.getOrderItems() == null){
				orderBo.setOrderItems(new ArrayList<TradeOrderItem>());
			}
			orderBo.getOrderItems().add(orderItem);
		}
	}
}
