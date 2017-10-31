/** 
 *@Project: okdeer-mall-order 
 *@Author: guocp
 *@Date: 2017年10月16日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.service;

import com.okdeer.base.common.model.RequestParams;
import com.okdeer.mall.order.dto.ScanOrderDto;

/**
 * ClassName: ScanOrderService 
 * @Description: 扫描购订单
 * @author guocp
 * @date 2017年10月16日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */

public interface ScanOrderService {

	/**
	 * @Description: 更新代金券状态
	 * @param orderId
	 * @param recordId
	 * @param couponsId
	 * @param deviceId
	 * @throws Exception   
	 * @author guocp
	 * @date 2017年10月16日
	 */
	void updateActivityCoupons(String orderId, String recordId, String couponsId, String deviceId) throws Exception;

	/**
	 * @Description: 保存扫码购订单
	 * @param vo
	 * @param requestParams
	 * @throws Exception   
	 * @author guocp
	 * @date 2017年10月16日
	 */
	void saveScanOrder(ScanOrderDto vo, RequestParams requestParams) throws Exception;

}
