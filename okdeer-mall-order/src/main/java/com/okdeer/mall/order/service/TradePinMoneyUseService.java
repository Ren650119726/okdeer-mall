/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年8月10日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.service;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.operate.bo.TradePinMoneyUseBo;
import com.okdeer.mall.operate.dto.TradePinMoneyQueryDto;

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
	 * @Description: 零花钱使用列表查询
	 * @param paramDto
	 * @param pageNumber
	 * @param pageSize
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月12日
	 */
	PageUtils<TradePinMoneyUseBo> fingPageList(TradePinMoneyQueryDto paramDto, int pageNumber, int pageSize);

	/**
	 * @Description: 条件获取零花钱使用记录数
	 * @param paramDto
	 * @return   
	 * @author xuzq01
	 * @date 2017年8月12日
	 */
	Integer findUseListCount(TradePinMoneyQueryDto paramDto);
}
