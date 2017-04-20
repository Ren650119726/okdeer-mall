package com.okdeer.mall.activity.discount.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.enums.ActivityDiscountType;
import com.okdeer.mall.activity.dto.ActivityParamDto;
import com.okdeer.mall.base.BaseServiceTest;

@RunWith(Parameterized.class)
public class ActivityDiscountMapperTest extends BaseServiceTest{
	
	@Resource
	private ActivityDiscountMapper activityDiscountMapper;
	
	private ActivityParamDto paramDto;
	
	public ActivityDiscountMapperTest(ActivityParamDto paramDto){
		this.paramDto = paramDto;
	}
	
	@Parameters
	public static Collection<Object[]> initParam() {
		ActivityParamDto paramDto = new ActivityParamDto();
		paramDto.setPageNumber(1);
		paramDto.setPageSize(10);
		// 只查询有平台创建的活动。平台创建的活动storeId为0。
		paramDto.setStoreId("0");
		// 活动列表只查询满减、零钱包活动
		paramDto.setTypeList(Arrays.asList(new ActivityDiscountType[]{
				ActivityDiscountType.mlj,
				ActivityDiscountType.PIN_MONEY
		}));
		return Arrays.asList(new Object[][] { {paramDto} });
	}

	@Test
	public void testFindListByParam() {
		List<ActivityDiscount> actList = activityDiscountMapper.findListByParam(paramDto);
		assertEquals(12, actList.size());
	}

	@Test
	public void testFindNeedUpdateList() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateStatus() {
		fail("Not yet implemented");
	}

}
