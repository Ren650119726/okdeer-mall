package com.okdeer.mall.order.service;

import java.util.List;

import com.okdeer.mall.order.bo.UserOrderBo;
import com.okdeer.mall.order.bo.UserOrderParamBo;

public interface UserOrderService {

	List<UserOrderBo> findUserOrders(UserOrderParamBo paramBo);
}
