/** 
 *@Project: okdeer-mall-api 
 *@Author: guocp
 *@Date: 2017年10月12日 
 *@Copyright: ©2014-2020 www.okdeer.com Inc. All rights reserved. 
 */    
package com.okdeer.mall.order.service;

import com.okdeer.base.common.model.RequestParams;
import com.okdeer.mall.order.dto.ScanOrderDto;

/**
 * ClassName: ScanOrderFavourApi 
 * @Description: 扫码购优惠服务接口
 * @author guocp
 * @date 2017年10月12日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface ScanOrderFavourService {

	/**
	 * @Description: 扫码购订单补充优惠信息
	 * @param orderDetail
	 * @param requestParams
	 * @return   
	 * @author guocp
	 * @throws Exception 
	 * @date 2017年10月12日
	 */
	void appendFavour(ScanOrderDto orderDetail, RequestParams requestParams) throws Exception;
}
