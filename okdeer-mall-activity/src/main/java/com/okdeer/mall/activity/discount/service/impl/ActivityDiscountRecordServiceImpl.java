package com.okdeer.mall.activity.discount.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.coupons.bo.ActivityRecordParamBo;
import com.okdeer.mall.activity.discount.mapper.ActivityDiscountRecordMapper;
import com.okdeer.mall.activity.discount.service.ActivityDiscountRecordService;

/**
 * ClassName: ActivityDiscountRecordServiceImpl 
 * @Description: 活动记录服务
 * @author maojj
 * @date 2017年4月21日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		友门鹿2.3 		2017年4月21日				maojj
 */
@Service
class ActivityDiscountRecordServiceImpl extends BaseServiceImpl implements ActivityDiscountRecordService {

	@Resource
	private ActivityDiscountRecordMapper activityDiscountRecordMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return activityDiscountRecordMapper;
	}

	@Override
	public int countTotalFreq(ActivityRecordParamBo paramBo) {
		return activityDiscountRecordMapper.countTotalFreq(paramBo);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void deleteByOrderId(String orderId) {
		activityDiscountRecordMapper.deleteByOrderId(orderId);
	}

}