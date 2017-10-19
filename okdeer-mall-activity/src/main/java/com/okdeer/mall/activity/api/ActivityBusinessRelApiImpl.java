package com.okdeer.mall.activity.api;

import java.util.List;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.okdeer.base.common.utils.mapper.BeanMapper;
import com.okdeer.mall.activity.discount.entity.ActivityBusinessRel;
import com.okdeer.mall.activity.discount.service.ActivityBusinessRelApi;
import com.okdeer.mall.activity.discount.service.ActivityBusinessRelService;
import com.okdeer.mall.activity.dto.ActivityBusinessRelDto;

@Service(version="1.0.0",interfaceName="com.okdeer.mall.activity.discount.service.ActivityBusinessRelApi")
public class ActivityBusinessRelApiImpl implements ActivityBusinessRelApi {
	
	@Resource
	private ActivityBusinessRelService activityBusinessRelService;

	@Override
	public List<ActivityBusinessRelDto> findByActivityId(String activityId) {
		List<ActivityBusinessRel> relList = activityBusinessRelService.findByActivityId(activityId);
		List<ActivityBusinessRelDto> relDtoList = BeanMapper.mapList(relList, ActivityBusinessRelDto.class);
		return relDtoList;
	}

}
