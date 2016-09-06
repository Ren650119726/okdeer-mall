
package com.okdeer.mall.activity.coupons.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.mall.activity.coupons.entity.ActivitySaleRecord;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRecordServiceApi;
import com.okdeer.mall.activity.coupons.mapper.ActivitySaleRecordMapper;
import com.okdeer.mall.activity.coupons.service.ActivitySaleRecordService;

@Service(version = "1.0.0", interfaceName = "com.okdeer.mall.activity.coupons.service.ActivitySaleRecordServiceApi")
public class ActivitySaleRecordServiceImpl implements ActivitySaleRecordService, ActivitySaleRecordServiceApi {

	@Resource
	private ActivitySaleRecordMapper activitySaleRecordMapper;

	@Override
	public List<String> selectActivitySaleRecordOfFund(Map<String, Object> params) {
		return activitySaleRecordMapper.selectActivitySaleRecordOfFund(params);
	}

	@Override
	public List<String> selectActivitySaleRecordList(Map<String, Object> params) {
		return activitySaleRecordMapper.selectActivitySaleRecordList(params);
	}

	@Override
	public int selectActivitySaleRecord(Map<String, Object> params) {
		return activitySaleRecordMapper.selectActivitySaleRecord(params);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertSelective(ActivitySaleRecord activitySaleRecord) {
		activitySaleRecordMapper.insertSelective(activitySaleRecord);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateDisabledByOrderId(Map<String, Object> params) {
		activitySaleRecordMapper.updateDisabledByOrderId(params);

	}

	@Override
	public int selectOrderGoodsCount(Map<String, Object> params) {
		return activitySaleRecordMapper.selectOrderGoodsCount(params);
	}

	// begin add by wangf01 2016.08.08
	@Override
	public String selectOrderGoodsActivity(Map<String, Object> params) {
		return activitySaleRecordMapper.selectOrderGoodsActivity(params);
	}
	// end add by wangf01 2016.08.08
}
