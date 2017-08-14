/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年8月11日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.order.api;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.order.dto.TradePinMoneyObtainDto;
import com.okdeer.mall.order.dto.TradePinMoneyUseDto;
import com.okdeer.mall.order.service.TradePinMoneyApi;
import com.okdeer.mall.order.service.TradePinMoneyObtainService;
import com.okdeer.mall.order.service.TradePinMoneyUseService;

/**
 * ClassName: TradePinMoneyApiImpl 
 * @Description: 零花钱服务API
 * @author guocp
 * @date 2017年8月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version="1.0.0")
public class TradePinMoneyApiImpl implements TradePinMoneyApi {

	@Autowired
	private TradePinMoneyObtainService tradePinMoneyObtainService;

	@Autowired
	private TradePinMoneyUseService tradePinMoneyUseService;

	/**
	 * 查询我的零花钱余额
	 */
	@Override
	public BigDecimal findMyUsableTotal(String userId, Date nowDate) {
		return tradePinMoneyObtainService.findMyUsableTotal(userId, nowDate);
	}

	/**
	 * @Description: 查询用户领取记录
	 * @param userId
	 * @param date
	 * @return   
	 * @author guocp
	 * @date 2017年8月10日
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PageUtils<TradePinMoneyObtainDto> findObtainList(String userId, int pageNumber, int pageSize) {
		return tradePinMoneyObtainService.findPage(userId, pageNumber, pageSize).toBean(TradePinMoneyObtainDto.class);

	}

	/**
	 * @Description: 查询用户领取记录
	 * @param userId
	 * @param date
	 * @return   
	 * @author guocp
	 * @date 2017年8月10日
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PageUtils<TradePinMoneyUseDto> findUseList(String userId, int pageNumber, int pageSize) {
		return tradePinMoneyUseService.findPage(userId, pageNumber, pageSize).toBean(TradePinMoneyUseDto.class);
	}

}
