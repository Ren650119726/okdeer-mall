
package com.okdeer.mall.activity.wxchat.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.wechat.dto.ActivityPosterDrawRecordParamDto;
import com.okdeer.mall.activity.wxchat.entity.ActivityPosterDrawRecord;
import com.okdeer.mall.activity.wxchat.mapper.ActivityPosterDrawRecordMapper;
import com.okdeer.mall.activity.wxchat.service.ActivityPosterDrawRecordService;

@Service
public class ActivityPosterDrawRecordServiceImpl extends BaseServiceImpl implements ActivityPosterDrawRecordService {

	@Autowired
	private ActivityPosterDrawRecordMapper activityPosterDrawRecordMapper;

	@Override
	public IBaseMapper getBaseMapper() {

		return activityPosterDrawRecordMapper;
	}

	@Override
	public long findCountByParams(ActivityPosterDrawRecordParamDto activityPosterDrawRecordParamDto) {
		PageHelper.startPage(1, -1, true);
		List<ActivityPosterDrawRecord> list = activityPosterDrawRecordMapper
				.findByParams(activityPosterDrawRecordParamDto);
		Page<ActivityPosterDrawRecord> page = (Page<ActivityPosterDrawRecord>) list;
		return page.getTotal();
	}

	@Override
	public List<ActivityPosterDrawRecord> findByParams(
			ActivityPosterDrawRecordParamDto activityPosterDrawRecordParamDto) {
		return activityPosterDrawRecordMapper.findByParams(activityPosterDrawRecordParamDto);
	}

	@Override
	public PageUtils<ActivityPosterDrawRecord> findByParams(
			ActivityPosterDrawRecordParamDto activityPosterDrawRecordParamDto, int pageNum, int pageSize) {
		List<ActivityPosterDrawRecord> list = activityPosterDrawRecordMapper
				.findByParams(activityPosterDrawRecordParamDto);
		return new PageUtils<>(list);
	}

	@Override
	public int updateTakeInfo(ActivityPosterDrawRecord activityPosterDrawRecord) {
		return activityPosterDrawRecordMapper.updateTakeInfo(activityPosterDrawRecord);
	}

}
