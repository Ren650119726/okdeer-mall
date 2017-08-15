/*
 * Copyright(C) 2016-2021 okdeer.com Inc. All rights reserved
 * ActivityPosterDrawRecordMapper.java
 * @Date 2017-08-07 Created
 * 注意：本内容仅限于友门鹿公司内部传阅，禁止外泄以及用于其他的商业目的
 */
package com.okdeer.mall.activity.wxchat.mapper;

import java.util.List;

import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.mall.activity.wechat.dto.ActivityPosterDrawRecordParamDto;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterDrawRecord;

public interface ActivityPosterDrawRecordMapper extends IBaseMapper {

	List<ActivityPosterDrawRecord> findByParams(ActivityPosterDrawRecordParamDto activityPosterDrawRecordParamDto);

	int updateTakeInfo(ActivityPosterDrawRecord activityPosterDrawRecord);

}