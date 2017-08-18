/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年8月10日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.mapper.JsonMapper;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.order.bo.TradePinMoneyUseBo;
import com.okdeer.mall.order.constant.PinMoneyStatusConstant;
import com.okdeer.mall.order.dto.TradePinMoneyQueryDto;
import com.okdeer.mall.order.entity.TradePinMoneyObtain;
import com.okdeer.mall.order.entity.TradePinMoneyUse;
import com.okdeer.mall.order.mapper.TradePinMoneyObtainMapper;
import com.okdeer.mall.order.mapper.TradePinMoneyUseMapper;
import com.okdeer.mall.order.service.TradePinMoneyUseService;

/**
 * ClassName: TradePinMoneyObtainServiceImpl 
 * @Description: 零花钱使用服务实现
 * @author guocp
 * @date 2017年8月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class TradePinMoneyUseServiceImpl extends BaseServiceImpl implements TradePinMoneyUseService {

	@Autowired
	private TradePinMoneyUseMapper tradePinMoneyUseMapper;
	
	@Autowired
	private TradePinMoneyObtainMapper tradePinMoneyObtainMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return tradePinMoneyUseMapper;
	}

	@Override
	public PageUtils<TradePinMoneyUse> findPage(String userId, int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true);
		List<TradePinMoneyUse> list = tradePinMoneyUseMapper.findList(userId);
		return new PageUtils<TradePinMoneyUse>(list);
	}

	@Override
	public PageUtils<TradePinMoneyUseBo> findUsePageList(TradePinMoneyQueryDto paramDto, int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradePinMoneyUseBo> list = tradePinMoneyUseMapper.fingUsePageList(paramDto);
		return new PageUtils<TradePinMoneyUseBo>(list);
	}

	@Override
	public Integer findUseListCount(TradePinMoneyQueryDto paramDto) {
		return tradePinMoneyUseMapper.findUseListCount(paramDto);
	}

	/**
	 * 释放订单占用零花钱
	 */
	@Override
	public void releaseOrderOccupy(String orderId) {
		TradePinMoneyUse pinMoneyUse = tradePinMoneyUseMapper.findByOrderId(orderId);
		//设置失效
		pinMoneyUse.setDisabled(Disabled.invalid);
		tradePinMoneyUseMapper.update(pinMoneyUse);
		
		//释放零花钱领取占用
		Map<String, BigDecimal> sourceMap = JsonMapper.nonDefaultMapper().fromJson(pinMoneyUse.getSourceId(),
				new TypeReference<HashMap<String, BigDecimal>>() {
				});
		List<String> ids = Lists.newArrayList();
		sourceMap.keySet().forEach(k -> ids.add(k));
		List<TradePinMoneyObtain> pinMoneyObtains = tradePinMoneyObtainMapper.findByIds(ids);
		for (TradePinMoneyObtain pinMoneyObtain : pinMoneyObtains) {
			pinMoneyObtain.setStatus(PinMoneyStatusConstant.UNUSED);
			pinMoneyObtain.setRemainAmount(pinMoneyObtain.getRemainAmount().add(sourceMap.get(pinMoneyObtain.getId())));
			pinMoneyObtain.setUpdateTime(new Date());
			tradePinMoneyObtainMapper.update(pinMoneyObtain);
		}
	}

}