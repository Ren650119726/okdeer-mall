/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年8月10日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */
package com.okdeer.mall.order.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.order.bo.TradePinMoneyObtainBo;
import com.okdeer.mall.order.dto.TradePinMoneyQueryDto;
import com.okdeer.mall.order.entity.TradePinMoneyObtain;

/**
 * ClassName: TradePinMoneyObtainService 
 * @Description: 零花钱领取记录service
 * @author guocp
 * @date 2017年8月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface TradePinMoneyObtainService extends IBaseService {

	/**
	 * @Description: 查询我的可用零花钱
	 * @param userId
	 * @return   
	 * @author guocp
	 * @date 2017年8月10日
	 */
	BigDecimal findMyUsableTotal(String userId, Date nowDate);

	/**
	 * @Description: 查询用户领取记录
	 * @param userId
	 * @param date
	 * @return   
	 * @author guocp
	 * @date 2017年8月10日
	 */
	List<TradePinMoneyObtain> findList(String userId, Date date, int status);

	/**
	 * @Description: 商城后台查询列表
	 * @param paramDto   
	 * @author xuzq01
	 * @param pageSize 
	 * @param pageNumber 
	 * @date 2017年8月11日
	 */
	PageUtils<TradePinMoneyObtainBo> findPageList(TradePinMoneyQueryDto paramDto, int pageNumber, int pageSize);

	/**
	 * @Description: 获取零花钱领取记录数
	 * @param paramDto
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月12日
	 */
	Integer findObtainListCount(TradePinMoneyQueryDto paramDto);
}
