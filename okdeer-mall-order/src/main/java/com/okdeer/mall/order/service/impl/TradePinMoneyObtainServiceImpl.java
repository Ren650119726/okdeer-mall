/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年8月10日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.operate.dto.TradePinMoneyQueryDto;
import com.okdeer.mall.operate.vo.TradePinMoneyObtainVo;
import com.okdeer.mall.order.entity.TradePinMoneyObtain;
import com.okdeer.mall.order.mapper.TradePinMoneyObtainMapper;
import com.okdeer.mall.order.service.TradePinMoneyObtainService;


/**
 * ClassName: TradePinMoneyObtainServiceImpl 
 * @Description: 零花钱领取服务实现
 * @author guocp
 * @date 2017年8月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service
public class TradePinMoneyObtainServiceImpl extends BaseServiceImpl implements TradePinMoneyObtainService {

	@Autowired
	private TradePinMoneyObtainMapper tradePinMoneyObtainMapper;
	
	@Override
	public IBaseMapper getBaseMapper() {
		return tradePinMoneyObtainMapper;
	}

	/**
	 * 查找我的可用零花钱
	 */
	@Override
	public BigDecimal findMyUsableTotal(String userId,Date nowDate) {
		return tradePinMoneyObtainMapper.findMyUsableTotal(userId,nowDate);
	}

	@Override
	public List<TradePinMoneyObtain> findList(String userId, Date date, int status) {
		return tradePinMoneyObtainMapper.findList(userId,date,status);
	}

	@Override
	public PageUtils<TradePinMoneyObtainVo> findPageList(TradePinMoneyQueryDto paramDto, int pageNumber, int pageSize) {
		PageHelper.startPage(pageNumber, pageSize, true, false);
		List<TradePinMoneyObtainVo> list = tradePinMoneyObtainMapper.findPageList(paramDto);
		return new PageUtils<TradePinMoneyObtainVo>(list);
	}

	@Override
	public Integer findObtainListCount(TradePinMoneyQueryDto paramDto) {
		return tradePinMoneyObtainMapper.findObtainListCount(paramDto);
	}

}
