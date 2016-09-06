
package com.okdeer.mall.activity.coupons.service;

import java.util.List;
import java.util.Map;

import com.okdeer.mall.activity.coupons.entity.ActivitySaleRecord;

public interface ActivitySaleRecordService {

	List<String> selectActivitySaleRecordOfFund(Map<String, Object> params);

	List<String> selectActivitySaleRecordList(Map<String, Object> params);

	int selectActivitySaleRecord(Map<String, Object> params);

	void insertSelective(ActivitySaleRecord activitySaleRecord);

	void updateDisabledByOrderId(Map<String, Object> params);

	int selectOrderGoodsCount(Map<String, Object> params);

	// begin add by wangf01 2016.08.08
	/**
	 * 
	 * @Description: 根据订单id跟订单向id查询特惠活动id
	 * @param params
	 * @return
	 * @author wangf01
	 * @date 2016年8月8日
	 */
	String selectOrderGoodsActivity(Map<String, Object> params);
	// end add by wangf01 2016.08.08
}
