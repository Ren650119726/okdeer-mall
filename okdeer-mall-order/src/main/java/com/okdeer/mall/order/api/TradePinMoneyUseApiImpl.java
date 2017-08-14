/** 
 *@Project: okdeer-mall-operate 
 *@Author: xuzq01
 *@Date: 2017年8月11日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.api;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.mall.operate.dto.TradePinMoneyQueryDto;
import com.okdeer.mall.operate.service.TradePinMoneyUseApi;
import com.okdeer.mall.operate.vo.TradePinMoneyUseVo;
import com.okdeer.mall.order.service.TradePinMoneyUseService;


/**
 * ClassName: TradePinMoneyUseApiImpl 
 * @Description: 零花钱使用api实现
 * @author xuzq01
 * @date 2017年8月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0")
public class TradePinMoneyUseApiImpl implements TradePinMoneyUseApi {

	@Autowired
	private TradePinMoneyUseService tradePinMoneyUseService;
	
	@Override
	public PageUtils<TradePinMoneyUseVo> findPageList(TradePinMoneyQueryDto paramDto, int pageNumber, int pageSize) {
		return tradePinMoneyUseService.fingPageList(paramDto, pageNumber, pageSize);
	}

	@Override
	public Integer findUseListCount(TradePinMoneyQueryDto paramDto) {
		return tradePinMoneyUseService.findUseListCount(paramDto);
	}

}
