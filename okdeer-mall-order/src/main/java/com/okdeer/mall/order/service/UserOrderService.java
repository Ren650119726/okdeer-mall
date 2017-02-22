package com.okdeer.mall.order.service;

import java.util.Map;

import com.okdeer.mall.order.bo.UserOrderParamBo;
import com.okdeer.mall.order.dto.AppUserOrderDto;

public interface UserOrderService {

	AppUserOrderDto findUserOrders(UserOrderParamBo paramBo) throws Exception;
	
	/**
	 * 
	 * @Description: 统计用户待付款，待使用，待评价订单数量
	 * @return Map<String,Object>  
	 * @throws 异常
	 * @author chenzc
	 * @date 2017年2月22日
	 */
	Map<String, Object> countUserOrders(String userId) throws Exception;
}
