/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年8月10日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.service;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
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

}
