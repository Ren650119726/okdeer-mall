package com.okdeer.mall.activity.serviceLimit;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.okdeer.mall.activity.serviceLimit.dto.StoreActivityLimitDto;
import com.okdeer.mall.activity.serviceLimit.service.ActivityServiceLimitApi;
import com.okdeer.mall.base.BaseServiceTest;

/**
 * ClassName: ActivityServiceLimitApiTest 
 * @Description: 限购活动api测试类
 * @author zengjizu
 * @date 2016年12月17日
 *
 * =================================================================================================
 *     Task ID			  Date			     Author		      Description
 * ----------------+----------------+-------------------+-------------------------------------------
 *
 */
public class ActivityServiceLimitApiTest extends BaseServiceTest {
	
	
	@Autowired
	private ActivityServiceLimitApi activityServiceLimitApi;
	
	/**
	 * @Description: 测试根据店铺id查询限购活动
	 * @author zengjizu
	 * @date 2016年12月17日
	 */
	@Test
	public void testFindStoreActivityLimit(){
		List<String> storeIdList = new ArrayList<>();
		storeIdList.add("8a8080a0578f596601578f81c7940016");
		storeIdList.add("2c91c0865639a2f2015639b10d800039");
		List<StoreActivityLimitDto> list = activityServiceLimitApi.findStoreActivityLimit(storeIdList);
		Assert.assertNotNull(list);
	}

}
