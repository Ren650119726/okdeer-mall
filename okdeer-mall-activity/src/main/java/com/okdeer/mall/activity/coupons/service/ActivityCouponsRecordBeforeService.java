
package com.okdeer.mall.activity.coupons.service;

import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.dto.ActivityCouponsRecordBeforeParamDto;

public interface ActivityCouponsRecordBeforeService extends IBaseService {

	int selectCountByParam(ActivityCouponsRecordBeforeParamDto activityCouponsRecordBeforeParam);

}
