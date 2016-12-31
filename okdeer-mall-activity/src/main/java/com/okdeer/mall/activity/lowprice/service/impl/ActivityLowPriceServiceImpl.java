package com.okdeer.mall.activity.lowprice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.lowprice.entity.ActivityLowPrice;
import com.okdeer.mall.activity.lowprice.mapper.ActivityLowPriceMapper;
import com.okdeer.mall.activity.lowprice.service.ActivityLowPriceService;

/**
 * 
 * ClassName: ActivityLowPriceServiceImpl 
 * @Description: 低价活动
 * @author tangy
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2016年12月29日                               tangy
 */
@Service
public class ActivityLowPriceServiceImpl extends BaseServiceImpl implements ActivityLowPriceService {

	/**
	 * 低价活动
	 */
	@Autowired
	private ActivityLowPriceMapper activityLowPriceMapper;
	
	@Override
	public ActivityLowPrice findByStoreId(String storeId) throws ServiceException {
		return activityLowPriceMapper.findByStoreId(storeId);
	}

	@Override
	public IBaseMapper getBaseMapper() {
		return activityLowPriceMapper;
	}

}
