
package com.okdeer.mall.activity.share.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.share.bo.ActivityShareOrderRecordParamBo;
import com.okdeer.mall.activity.share.dto.ActivityShareOrderRecordDto;
import com.okdeer.mall.activity.share.dto.ActivityShareOrderRecordParamDto;
import com.okdeer.mall.activity.share.entity.ActivityShareOrderRecord;
import com.okdeer.mall.activity.share.service.ActivityShareOrderRecordApi;
import com.okdeer.mall.activity.share.service.ActivityShareOrderRecordService;

@Service(version = "1.0.0")
public class ActivityShareOrderRecordApiImpl implements ActivityShareOrderRecordApi {

	@Autowired
	private ActivityShareOrderRecordService activityShareOrderRecordService;

	@Override
	public List<ActivityShareOrderRecordDto> findList(
			ActivityShareOrderRecordParamDto activityShareOrderRecordParamDto) {
		ActivityShareOrderRecordParamBo activityShareOrderRecordParam = BeanMapper.map(activityShareOrderRecordParamDto,
				ActivityShareOrderRecordParamBo.class);
		List<ActivityShareOrderRecord> list = activityShareOrderRecordService.findList(activityShareOrderRecordParam);
		return BeanMapper.mapList(list, ActivityShareOrderRecordDto.class);
	}

}
