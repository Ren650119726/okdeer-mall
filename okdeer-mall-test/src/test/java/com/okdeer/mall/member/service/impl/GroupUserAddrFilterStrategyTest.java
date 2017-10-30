package com.okdeer.mall.member.service.impl;


import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.okdeer.base.common.enums.WhetherEnum;
import com.okdeer.mall.activity.discount.entity.ActivityDiscount;
import com.okdeer.mall.activity.discount.enums.ActivityBusinessType;
import com.okdeer.mall.activity.discount.service.ActivityBusinessRelApi;
import com.okdeer.mall.activity.discount.service.ActivityDiscountApi;
import com.okdeer.mall.activity.dto.ActivityBusinessRelDto;
import com.okdeer.mall.base.BaseServiceTest;
import com.okdeer.mall.base.MockUtils;
import com.okdeer.mall.common.enums.AreaType;
import com.okdeer.mall.member.bo.UserAddressFilterCondition;
import com.okdeer.mall.member.member.entity.MemberConsigneeAddress;
import com.okdeer.mall.mock.MockFilePath;


public class GroupUserAddrFilterStrategyTest extends BaseServiceTest implements MockFilePath{
	
	@Resource
	private GroupUserAddrFilterStrategy groupUserAddrFilterStrategy;

	private List<MemberConsigneeAddress> userAddrList;
	
	@Mock
	private ActivityDiscountApi activityDiscountApi;
	
	@Mock
	private ActivityBusinessRelApi activityBusinessRelApi;
	
	@Override
	public void initMocks() throws Exception{
		this.userAddrList = MockUtils.getMockListData(MOCK_USER_ADDR_LIST_PATH, MemberConsigneeAddress.class);
		
		ReflectionTestUtils.setField(groupUserAddrFilterStrategy, "activityDiscountApi", activityDiscountApi);
		ReflectionTestUtils.setField(groupUserAddrFilterStrategy, "activityBusinessRelApi", activityBusinessRelApi);
	}
	
	@Test
	public void testIsOutRangeNonLimit() {
		// 团购活动不限范围
		ActivityDiscount actInfo = new ActivityDiscount();
		actInfo.setId("8a94e7545f136acc015f136acc380000");
		actInfo.setLimitRange(AreaType.national);
		UserAddressFilterCondition filterCondition = new UserAddressFilterCondition();
		filterCondition.setActivityInfo(actInfo);
		this.userAddrList.forEach(userAddr -> {
			assertEquals(false, groupUserAddrFilterStrategy.isOutRange(userAddr, filterCondition));
		});
	}

	@Test
	public void testIsOutRangeLimit() throws Exception{
		// 团购活动指定可以参加活动的地区
		ActivityDiscount actInfo = new ActivityDiscount();
		actInfo.setId("8a94e7545f136acc015f136acc380000");
		actInfo.setLimitRange(AreaType.area);
		actInfo.setLimitRangeType(WhetherEnum.not);
		
		// 活动业务关联关系
		List<ActivityBusinessRelDto> relList = Lists.newArrayList(); 
		// 限制湖北省
		ActivityBusinessRelDto provinceRel = new ActivityBusinessRelDto();
		provinceRel.setBusinessType(ActivityBusinessType.PROVINCE);
		provinceRel.setBusinessId("17");
		relList.add(provinceRel);
		
		// 限制深圳市
		ActivityBusinessRelDto cityRel = new ActivityBusinessRelDto();
		cityRel.setBusinessType(ActivityBusinessType.CITY);
		cityRel.setBusinessId("291");
		relList.add(cityRel);
		
		UserAddressFilterCondition filterCondition = new UserAddressFilterCondition();
		filterCondition.setActivityId("8a94e7545f136acc015f136acc380000");
		
		given(activityDiscountApi.findById(anyString())).willReturn(actInfo);
		given(activityBusinessRelApi.findByActivityId(anyString())).willReturn(relList);
		
		for(MemberConsigneeAddress userAddr : this.userAddrList){
			boolean expectResult = !userAddr.getProvinceId().equals("17") && !userAddr.getCityId().equals("291");
			assertEquals(expectResult, groupUserAddrFilterStrategy.isOutRange(userAddr, filterCondition));
		}
		
		// 团购活动指定不可以参加活动的区域
		filterCondition = new UserAddressFilterCondition();
		filterCondition.setActivityId("8a94e7545f136acc015f136acc380000");
		actInfo.setLimitRangeType(WhetherEnum.whether);
		given(activityDiscountApi.findById(anyString())).willReturn(actInfo);
		for(MemberConsigneeAddress userAddr : this.userAddrList){
			boolean expectResult = userAddr.getProvinceId().equals("17") || userAddr.getCityId().equals("291");
			assertEquals(expectResult, groupUserAddrFilterStrategy.isOutRange(userAddr, filterCondition));
		}
	}
}
