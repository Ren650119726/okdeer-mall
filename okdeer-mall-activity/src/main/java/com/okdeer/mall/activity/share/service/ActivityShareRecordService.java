
package com.okdeer.mall.activity.share.service;


import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.share.dto.ActivityShareRecordParamDto;
import com.okdeer.mall.activity.share.entity.ActivityShareRecord;

public interface ActivityShareRecordService extends IBaseService {

	PageUtils<ActivityShareRecord> findList(ActivityShareRecordParamDto activityShareRecordParamDto, int pageNum,
			int pageSize);

}
