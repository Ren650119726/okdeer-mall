package com.okdeer.mall.order.api;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.order.bo.UserOrderParamBo;
import com.okdeer.mall.order.dto.AppUserOrderDto;
import com.okdeer.mall.order.dto.UserOrderParamDto;
import com.okdeer.mall.order.service.UserOrderApi;
import com.okdeer.mall.order.service.UserOrderService;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.order.service.UserOrderApi")
public class UserOrderApiImpl implements UserOrderApi {

	
	@Resource
	private UserOrderService userOrderService;
	
	@Override
	public AppUserOrderDto findUserOrders(UserOrderParamDto paramDto) throws Exception {
		UserOrderParamBo paramBo = BeanMapper.map(paramDto, UserOrderParamBo.class);
		paramBo.setCreateTime(null);
		return userOrderService.findUserOrders(paramBo);
	}

	@Override
	public Map<String, Object> countUserOrders(String userId) throws Exception {
		return userOrderService.countUserOrders(userId);
	}
}
