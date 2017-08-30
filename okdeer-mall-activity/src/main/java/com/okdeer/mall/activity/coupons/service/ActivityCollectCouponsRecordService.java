package com.okdeer.mall.activity.coupons.service;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.coupons.bo.ActivityCollectCouponsRecordParamBo;

/**
 * ClassName: ActivityCollectCouponsRecordService 
 * @Description: 代金劵活动领取记录
 * @author zengjizu
 * @date 2017年8月30日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public interface ActivityCollectCouponsRecordService extends IBaseService{

	
	int findCountByParams(ActivityCollectCouponsRecordParamBo activityCollectCouponsRecordParamBo);
}
