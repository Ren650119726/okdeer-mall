package com.okdeer.mall.order.service;

import java.util.List;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.order.dto.GroupJoinUserDto;


public interface TradeOrderGroupService extends IBaseService {
	
	/**
	 * @Description: 根据团购订单查询参与用户列表
	 * @param groupOrderId 团购订单id
	 * @param screen 屏幕分辨率。用于处理用户头像图片
	 * @return   
	 * @author maojj
	 * @date 2017年10月16日
	 */
	List<GroupJoinUserDto> findGroupJoinUserList(String groupOrderId,String screen) throws ServiceException;

}
