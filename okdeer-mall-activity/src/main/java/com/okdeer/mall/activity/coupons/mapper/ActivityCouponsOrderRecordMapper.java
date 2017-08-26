
package com.okdeer.mall.activity.coupons.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.coupons.bo.ActivityCouponsOrderRecordParamBo;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsOrderRecord;

/**
 * ClassName: ActivityCouponsOrderRecordMapper 
 * @Description: 消费返券记录mapper
 * @author wushp
 * @date 2016年9月23日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *		V1.1.0			2016-09-23		wushp				消费返券记录mapper
 */
public interface ActivityCouponsOrderRecordMapper extends IBaseMapper {
	
	/**
	 * @Description: 根据参数查询
	 * @param activityCouponsOrderRecordParamBo
	 * @return
	 * @author zengjizu
	 * @date 2017年8月26日
	 */
	List<ActivityCouponsOrderRecord> findByParam(ActivityCouponsOrderRecordParamBo activityCouponsOrderRecordParamBo);
	/**
	 * @Description: 根据参数查询数量
	 * @param activityCouponsOrderRecordParamBo
	 * @return
	 * @author zengjizu
	 * @date 2017年8月26日
	 */
	int findCountByParam(ActivityCouponsOrderRecordParamBo activityCouponsOrderRecordParamBo);
}