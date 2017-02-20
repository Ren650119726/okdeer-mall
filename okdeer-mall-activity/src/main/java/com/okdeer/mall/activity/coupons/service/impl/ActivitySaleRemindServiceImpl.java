package com.okdeer.mall.activity.coupons.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.coupons.bo.ActivitySaleRemindBo;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleRemind;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleRemindMapper;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRemindService;

/**
 * 
 * ClassName: ActivitySaleRemindServiceImpl 
 * @Description: 活动安全库存联系人关联
 * @author tangy
 * @date 2017年2月20日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *     2.0.0          2017年2月20日                               tangy
 */
@Service
public class ActivitySaleRemindServiceImpl extends BaseServiceImpl implements ActivitySaleRemindService {

	@Autowired
	private ActivitySaleRemindMapper activitySaleRemindMapper;
	
	@Override
	public List<ActivitySaleRemindBo> findActivitySaleRemindBySaleId(String saleId) {
		return activitySaleRemindMapper.findActivitySaleRemindBySaleId(saleId);
	}
	
	@Override
	public int insertSelectiveBatch(List<ActivitySaleRemind> list) {
		return activitySaleRemindMapper.insertSelectiveBatch(list);
	}
	
	@Override
	public int deleteBySaleId(String saleId) {
		return activitySaleRemindMapper.deleteBySaleId(saleId);
	}

	@Override
	public IBaseMapper getBaseMapper() {
		return activitySaleRemindMapper;
	}

}
