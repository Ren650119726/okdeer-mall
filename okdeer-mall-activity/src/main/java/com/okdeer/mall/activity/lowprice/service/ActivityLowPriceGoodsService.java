package com.okdeer.mall.activity.lowprice.service;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.lowprice.entity.ActivityLowPriceGoods;

/**
 * 
 * ClassName: ActivityLowPriceGoodsService 
 * @Description: 低价商品
 * @author tangy
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2016年12月29日                               tangy
 */
public interface ActivityLowPriceGoodsService extends IBaseService {
	
	/**
	 * 
	 * @Description: 根据低价活动id查询低价商品列表
	 * @param activityLowPriceId   低价活动id
	 * @throws ServiceException    
	 * @return List<ActivityLowPriceGoods>  
	 * @author tangy
	 * @date 2016年12月29日
	 */
	PageUtils<ActivityLowPriceGoods> findByActivityLowPriceId(String activityLowPriceId) throws ServiceException;

}
