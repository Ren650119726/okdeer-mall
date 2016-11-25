package com.okdeer.mall.activity.coupons.mapper;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.okdeer.base.common.exception.ServiceException;
import com.okdeer.mall.Application;
import com.okdeer.mall.activity.coupons.entity.ActivityCouponsRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ActivityCouponsRecordBeforeMapperTest {

	@Autowired
	private ActivityCouponsRecordBeforeMapper activityCouponsRecordBeforeMapper;
	
	@Test
	public void testGetCopyRecords() {
		try {
			List<ActivityCouponsRecord> list = activityCouponsRecordBeforeMapper.getCopyRecords("8a94e446584906ee0158490c33f10008", new Date(), "15813820637");
			System.out.println(list.size());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
