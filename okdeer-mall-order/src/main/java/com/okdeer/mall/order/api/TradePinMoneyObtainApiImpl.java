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
import com.okdeer.mall.operate.service.TradePinMoneyObtainApi;
import com.okdeer.mall.operate.vo.TradePinMoneyObtainVo;
import com.okdeer.mall.order.service.TradePinMoneyObtainService;

/**
 * ClassName: TradePinMoneyObtainApiImpl 
 * @Description: 零花钱领取记录api实现
 * @author xuzq01
 * @date 2017年8月11日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
@Service(version = "1.0.0")
public class TradePinMoneyObtainApiImpl implements TradePinMoneyObtainApi {
	
	@Autowired
	private TradePinMoneyObtainService tradePinMoneyObtainService;

	@Override
	public PageUtils<TradePinMoneyObtainVo> findPageList(TradePinMoneyQueryDto paramDto, int pageNumber, int pageSize) {
		return tradePinMoneyObtainService.findPageList(paramDto,pageNumber,pageSize);
	}

	@Override
	public Integer findObtainListCount(TradePinMoneyQueryDto paramDto) {
		return tradePinMoneyObtainService.findObtainListCount(paramDto);
	}

}
