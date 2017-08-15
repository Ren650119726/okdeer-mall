
package com.okdeer.mall.activity.wxchat.service;

import java.util.List;

import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.service.IBaseService;
import com.okdeer.mall.activity.wechat.dto.ActivityPosterDrawRecordParamDto;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterDrawRecord;

public interface ActivityPosterDrawRecordService extends IBaseService {
	
	long findCountByParams(ActivityPosterDrawRecordParamDto activityPosterDrawRecordParamDto);

	List<ActivityPosterDrawRecord> findByParams(ActivityPosterDrawRecordParamDto activityPosterDrawRecordParamDto);

	PageUtils<ActivityPosterDrawRecord> findByParams(ActivityPosterDrawRecordParamDto activityPosterDrawRecordParamDto, int pageNum, int pageSize);

	int updateTakeInfo(ActivityPosterDrawRecord activityPosterDrawRecord);

}
