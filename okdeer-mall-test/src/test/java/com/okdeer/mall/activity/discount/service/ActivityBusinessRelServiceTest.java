package com.okdeer.mall.activity.discount.service;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.okdeer.mall.activity.dto.ActivityBusinessRelDto;
import com.okdeer.mall.base.BaseServiceTest;


public class ActivityBusinessRelServiceTest extends BaseServiceTest{
	
	@Resource
	private ActivityBusinessRelApi activityBusinessRelApi;

	@Test
	public void testFindByActivityId() {
		List<ActivityBusinessRelDto> relDtoList = activityBusinessRelApi.findByActivityId("8a8080e35f4d3750015f4d38ed890002");
		
	}

}
