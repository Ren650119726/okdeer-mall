/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年8月11日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.order.api;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.DateUtils;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.common.utils.UuidUtils;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.dto.ActivityPinMoneyDto;
import com.okdeer.mall.activity.dto.ActivityPinMoneyQueryDto;
import com.okdeer.mall.order.dto.TradePinMoneyObtainDto;
import com.okdeer.mall.order.dto.TradePinMoneyQueryDto;
import com.okdeer.mall.order.dto.TradePinMoneyUseDto;
import com.okdeer.mall.order.entity.TradePinMoneyObtain;
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

	@SuppressWarnings("unchecked")
	@Override
	public PageUtils<TradePinMoneyObtainDto> findObtainPageList(TradePinMoneyQueryDto paramDto, int pageNumber,
			int pageSize) {
		return tradePinMoneyObtainService.findObtainPageList(paramDto, pageNumber, pageSize).toBean(TradePinMoneyObtainDto.class);
	}

	@Override
	public Integer findObtainListCount(TradePinMoneyQueryDto paramDto) {
		return tradePinMoneyObtainService.findObtainListCount(paramDto);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PageUtils<TradePinMoneyUseDto> findUsePageList(TradePinMoneyQueryDto paramDto, int pageNumber,
			int pageSize) {
		return tradePinMoneyUseService.findUsePageList(paramDto, pageNumber, pageSize).toBean(TradePinMoneyUseDto.class);
	}

	@Override
	public Integer findUseListCount(TradePinMoneyQueryDto paramDto) {
		return tradePinMoneyUseService.findUseListCount(paramDto);
	}

	@Override
	public void addObtainRecord(ActivityPinMoneyQueryDto dto, String deviceId, ActivityPinMoneyDto moneyDto, BigDecimal pinMoney) throws Exception {
		TradePinMoneyObtain obtain = BeanMapper.map(dto, TradePinMoneyObtain.class);
		obtain.setId(UuidUtils.getUuid());
		obtain.setDeviceId(deviceId);
		obtain.setStatus(0);
		obtain.setAmount(pinMoney);
		obtain.setRemainAmount(pinMoney);
		Date date = new Date();
		obtain.setCreateTime(date);
		obtain.setUpdateTime(date);
		obtain.setActivityId(moneyDto.getId());
		//0为无过期时间 
		if(moneyDto.getValidDay()==0){
			obtain.setValidTime(DateUtils.parseDate("2050-08-24 00:00:00"));
		}else{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(calendar.DATE,moneyDto.getValidDay());
			obtain.setValidTime(calendar.getTime());
		}
		tradePinMoneyObtainService.add(obtain);
	}

	@Override
	public BigDecimal findPinMoneyObtainAmount(TradePinMoneyQueryDto queryDto) {
		return tradePinMoneyObtainService.findPinMoneyObtainAmount(queryDto);
	}

}
