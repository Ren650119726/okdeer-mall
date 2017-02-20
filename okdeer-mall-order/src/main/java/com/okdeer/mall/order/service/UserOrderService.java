package com.okdeer.mall.order.service;

import com.okdeer.mall.order.bo.UserOrderParamBo;
import com.okdeer.mall.order.dto.AppUserOrderDto;

public interface UserOrderService {

	AppUserOrderDto findUserOrders(UserOrderParamBo paramBo) throws Exception;
}
