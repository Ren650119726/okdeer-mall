package com.okdeer.mall.activity.lowprice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.lowprice.entity.ActivityLowPriceGoods;
import com.okdeer.mall.activity.lowprice.mapper.ActivityLowPriceGoodsMapper;
import com.okdeer.mall.activity.lowprice.service.ActivityLowPriceGoodsService;

/**
 * 
 * ClassName: ActivityLowPriceGoodsServiceImpl 
 * @Description: 低价商品
 * @author tangy
 * @date 2016年12月29日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2016年12月29日                               tangy
 */
@Service
public class ActivityLowPriceGoodsServiceImpl extends BaseServiceImpl implements ActivityLowPriceGoodsService {
	/**
	 * 低价商品
	 */
	@Autowired
	private ActivityLowPriceGoodsMapper activityLowPriceGoodsMapper;
	
	@Override
	public PageUtils<ActivityLowPriceGoods> findByActivityLowPriceId(String activityLowPriceId)
			throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBaseMapper getBaseMapper() {
		return activityLowPriceGoodsMapper;
	}

}
