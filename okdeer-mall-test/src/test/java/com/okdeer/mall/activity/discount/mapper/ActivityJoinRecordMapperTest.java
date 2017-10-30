package com.okdeer.mall.activity.discount.mapper;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;

import com.okdeer.mall.activity.bo.ActivityJoinRecParamBo;
import com.okdeer.mall.base.BaseServiceTest;


public class ActivityJoinRecordMapperTest extends BaseServiceTest{

	@Resource
	private ActivityJoinRecordMapper activityJoinRecordMapper;
	
	@Test
	public void testCountActivityJoinNum() {
		ActivityJoinRecParamBo paramBo = new ActivityJoinRecParamBo();
		paramBo.setJoinDate(new Date());
		activityJoinRecordMapper.countActivityJoinNum(paramBo);
	}

}
