
package com.okdeer.mall.activity.share.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.okdeer.base.common.utils.PageUtils;
import com.okdeer.base.dal.IBaseMapper;
import com.okdeer.base.service.BaseServiceImpl;
import com.okdeer.mall.activity.share.bo.ActivityShareRecordNumParamBo;
import com.okdeer.mall.activity.share.dto.ActivityShareRecordParamDto;
import com.okdeer.mall.activity.share.entity.ActivityShareRecord;
import com.okdeer.mall.activity.share.mapper.ActivityShareRecordMapper;
import com.okdeer.mall.activity.share.service.ActivityShareRecordService;

@Service
public class ActivityShareRecordServiceImpl extends BaseServiceImpl implements ActivityShareRecordService {

	@Autowired
	private ActivityShareRecordMapper activityShareRecordMapper;

	@Override
	public IBaseMapper getBaseMapper() {
		return activityShareRecordMapper;
	}

	@Override
	public PageUtils<ActivityShareRecord> findList(ActivityShareRecordParamDto activityShareRecordParamDto, int pageNum,
			int pageSize) {
		PageHelper.startPage(pageNum, pageSize, true);
		List<ActivityShareRecord> list = activityShareRecordMapper.findList(activityShareRecordParamDto);
		return new PageUtils<>(list);
	}

	@Override
	public List<ActivityShareRecord> findList(ActivityShareRecordParamDto activityShareRecordParamDto) {
		return activityShareRecordMapper.findList(activityShareRecordParamDto);
	}

	@Override
	public int updateNum(ActivityShareRecordNumParamBo activityShareRecordNumParamBo) {
		return activityShareRecordMapper.updateNum(activityShareRecordNumParamBo);
	}

}
