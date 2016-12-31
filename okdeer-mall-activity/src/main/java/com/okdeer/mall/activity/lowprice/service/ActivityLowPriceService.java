package com.okdeer.mall.activity.lowprice.service;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.lowprice.entity.ActivityLowPrice;

/**
 * 
 * ClassName: ActivityLowPriceService 
 * @Description: 低价活动
 * @author tangy
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2016年12月29日                               tangy
 */
public interface ActivityLowPriceService extends IBaseService {
	/**
	 * 
	 * @Description: 根据店铺查询低价活动
	 * @param storeId
	 * @throws ServiceException   
	 * @return ActivityLowPrice  
	 * @author tangy
	 * @date 2016年12月29日
	 */
	ActivityLowPrice findByStoreId(String storeId) throws ServiceException;
	
}
