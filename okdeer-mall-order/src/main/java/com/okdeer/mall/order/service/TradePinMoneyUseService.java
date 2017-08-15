/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年8月10日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.service;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.order.bo.TradePinMoneyUseBo;
import com.okdeer.mall.order.dto.TradePinMoneyQueryDto;
import com.okdeer.mall.order.entity.TradePinMoneyUse;

/**
 * ClassName: TradePinMoneyObtainService 
 * @Description: 零花钱使用记录服务接口
 * @author guocp
 * @date 2017年8月10日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface TradePinMoneyUseService extends IBaseService{

	/**
	 * @Description: 查询零花钱
	 * @param userId
	 * @param pageNumber
	 * @param pageSize
	 * @return   
	 * @author guocp
	 * @date 2017年8月14日
	 */
	PageUtils<TradePinMoneyUse> findPage(String userId, int pageNumber, int pageSize);

	/**
	 * @Description: 条件获取零花钱使用记录数
	 * @param paramDto
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月12日
	 */
	Integer findUseListCount(TradePinMoneyQueryDto paramDto);


	/**
	 * @Description: 零花钱使用列表查询
	 * @param paramDto
	 * @param pageNumber
	 * @param pageSize
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月12日
	 */
	PageUtils<TradePinMoneyUseBo> findUsePageList(TradePinMoneyQueryDto paramDto, int pageNumber, int pageSize);

	/**
	 * @Description: 释放订单占用零花钱
	 * @param id   
	 * @author guocp
	 * @date 2017年8月14日
	 */
	void releaseOrderOccupy(String orderId);
}
