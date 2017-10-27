/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年8月10日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.okdeer.base.common.enums.Disabled;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
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
		if (pinMoneyUse == null) {
			return;
		}
		// 设置失效
		pinMoneyUse.setDisabled(Disabled.invalid);
		tradePinMoneyUseMapper.update(pinMoneyUse);

		// 释放零花钱领取占用
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
	
	/**
	 * 订单占用零花钱
	 */
	@Override
	public void orderOccupy(String userId,String orderId,BigDecimal orderTotalAmount,BigDecimal usePinMoney) throws Exception{
		// 查询我的零花钱记录
		//V2.6.3 xuzq 20171027 修改接口调用为获取用户可用零花钱列表
		List<TradePinMoneyObtain> pinMoneyObtains = tradePinMoneyObtainMapper.findUsableList(userId,
				new Date(), PinMoneyStatusConstant.UNUSED);
		//倒序
		Collections.reverse(pinMoneyObtains);
		// 需扣减金额
		BigDecimal deduction = new BigDecimal("0.00");
		Map<String,BigDecimal> records = Maps.newHashMap();
		List<TradePinMoneyObtain> updateRecord = Lists.newArrayList();
		for (TradePinMoneyObtain pinMoney : pinMoneyObtains) {
			if (deduction.compareTo(usePinMoney) >= 0) {
				break;
			}
			// 扣减差额
			BigDecimal difference = usePinMoney.subtract(deduction);
			records.put(pinMoney.getId(),
					difference.compareTo(pinMoney.getRemainAmount()) >= 0 ? pinMoney.getRemainAmount() : difference);
			if (difference.compareTo(pinMoney.getRemainAmount()) >= 0) {
				deduction = deduction.add(pinMoney.getRemainAmount());
				pinMoney.setRemainAmount(new BigDecimal("0.00"));
				pinMoney.setStatus(PinMoneyStatusConstant.USED);
			} else {
				deduction = deduction.add(difference);
				pinMoney.setRemainAmount(pinMoney.getRemainAmount().subtract(difference));
			}
			updateRecord.add(pinMoney);
		}
		if (deduction.compareTo(usePinMoney) != 0) {
			throw new Exception("零花钱扣减异常");
		}
		// 更新领取记录  updateRecord
		for(TradePinMoneyObtain entity : updateRecord){
			entity.setUpdateTime(new Date());
			tradePinMoneyObtainMapper.update(entity);
		}
		
		// 更新使用记录
		String sources = JsonMapper.nonDefaultMapper().toJson(records);
		TradePinMoneyUse tradePinMoneyUse = new TradePinMoneyUse();
		tradePinMoneyUse.setId(UuidUtils.getUuid());
		tradePinMoneyUse.setOrderId(orderId);
		tradePinMoneyUse.setSourceId(sources);
		tradePinMoneyUse.setUseAmount(usePinMoney);
		tradePinMoneyUse.setOrderAmount(orderTotalAmount);
		tradePinMoneyUse.setUserId(userId);
		tradePinMoneyUse.setCreateTime(new Date());
		this.add(tradePinMoneyUse);
	}

}